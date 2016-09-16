package com.github.rlf.littlebits.cloudapi;

import com.github.rlf.littlebits.async.Consumer;
import com.github.rlf.littlebits.model.Account;
import com.github.rlf.littlebits.model.Device;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simple apache http client implementation of the CloudAPI.
 */
public class HttpCloudAPI implements CloudAPI {
    private static final Logger LOG = Logger.getLogger(HttpCloudAPI.class.getName());
    private final String baseUrl;

    public HttpCloudAPI() {
        this(null);
    }

    public HttpCloudAPI(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl : "https://api-http.littlebitscloud.cc";
    }

    @Override
    public List<Device> getDevices(Account account) {
        HttpGet httpGet = new HttpGet(baseUrl + "/devices");
        httpGet.setHeader("Authorization", "Bearer " + account.getToken());
        httpGet.setHeader("Accept", "application/vnd.littlebits.v2+json");
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(httpGet)
        ) {
            if (response.getStatusLine().getStatusCode() == 200) {
                List<Device> devices = new ArrayList<>();
                HttpEntity entity = response.getEntity();
                JSONParser jsonParser = new JSONParser();
                try (Reader rdr = new InputStreamReader(entity.getContent())) {
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(rdr);
                    for (Object deviceObj : jsonArray) {
                        JSONObject deviceJson = (JSONObject) deviceObj;
                        String id = (String) deviceJson.get("id");
                        String label = (String) deviceJson.get("label");
                        Boolean connected = (Boolean) deviceJson.get("is_connected");
                        Device device = new Device(account, id, label);
                        device.setConnected(connected == Boolean.TRUE);
                        devices.add(device);
                    }
                } catch (ParseException e) {
                    throw new APIException(e, APIException.Reason.INVALID_CONTENT);
                }
                return devices;
            } else {
                throw new APIException(null, APIException.Reason.CONNECTION_ERROR);
            }
        } catch (IOException e) {
            throw new APIException(e);
        }
    }

    @Override
    public int readInput(Device device, Consumer<DeviceInput> consumer) {
        HttpGet httpGet = new HttpGet(baseUrl + "/devices/" + device.getId() + "/input");
        httpGet.setHeader("Authorization", "Bearer " + device.getAccount().getToken());
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/vnd.littlebits.v2+json");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet)
        ) {
            if (response.getStatusLine().getStatusCode() == 200) {
                readInput(response, consumer);
            }
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            throw new APIException(e);
        }
    }

    private boolean isCancelled() {
        return Thread.currentThread().isInterrupted();
    }

    private void readInput(CloseableHttpResponse response, Consumer<DeviceInput> consumer) throws IOException {
        HttpEntity entity = response.getEntity();
        JSONParser jsonParser = new JSONParser();
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            while (!isCancelled()) {
                String json = "";
                String sse;
                do {
                    sse = rdr.readLine();
                    if (sse.startsWith("data:")) {
                        json += sse.substring(5);
                    }
                } while (!"".equals(sse));
                Object deviceObj = jsonParser.parse(json);
                if (deviceObj == null) {
                    break;
                }
                JSONObject deviceJson = (JSONObject) deviceObj;
                LOG.finer("JSON: " + deviceJson.toJSONString());
                String type = (String) deviceJson.get("type");
                Object pct = deviceJson.get("percent");
                Object tstamp = deviceJson.get("timestamp");
                if ("input".equals(type) && pct instanceof Number && tstamp instanceof Number) {
                    consumer.accept(new DeviceInput(
                            ((Number) tstamp).longValue(), ((Number) pct).intValue())
                    );
                }
            }
        } catch (ParseException e) {
            throw new APIException(e, APIException.Reason.INVALID_CONTENT);
        }
    }

    @Override
    public int writeOutput(Device device, int amplitude) {
        HttpPost httpPost = new HttpPost(baseUrl + "/devices/" + device.getId() + "/output");
        httpPost.setHeader("Authorization", "Bearer " + device.getAccount().getToken());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/vnd.littlebits.v2+json");
        httpPost.setEntity(new StringEntity("{\"percent\":" + amplitude + ",\"duration_ms\":-1}\n", ContentType.APPLICATION_JSON));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)
        ) {
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            throw new APIException(e);
        }
    }

    @Override
    public int writeLabel(Device device, String label) {
        // TODO: 16/09/2016 - R4zorax: NOTE: Doesn't currently work - our OAuth scope isn't admin
        HttpPut httpMethod = new HttpPut(baseUrl + "/devices/" + device.getId());
        httpMethod.setHeader("Authorization", "Bearer " + device.getAccount().getToken());
        httpMethod.setHeader("Content-Type", "application/json");
        httpMethod.setHeader("Accept", "application/vnd.littlebits.v2+json");
        httpMethod.setEntity(new StringEntity("{\"label\":\"" + label + "\"}\n", ContentType.APPLICATION_JSON));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpMethod)
        ) {
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            throw new APIException(e);
        }
    }

    @Override
    public void shutdown() {
        // Do nothing (for now)
    }

}
