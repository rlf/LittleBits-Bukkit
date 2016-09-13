package com.github.rlf.bitcloud.cloudapi;

import com.github.rlf.bitcloud.async.Scheduler;
import com.github.rlf.bitcloud.event.AccountAdded;
import com.github.rlf.bitcloud.event.EventManager;
import com.github.rlf.bitcloud.model.Account;
import com.github.rlf.bitcloud.model.Device;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simple apache http client implementation of the CloudAPI.
 */
public class HttpCloudAPI implements CloudAPI, Listener {
    private static final Logger log = Logger.getLogger(HttpCloudAPI.class.getName());

    private String baseUrl = "https://api-http.littlebitscloud.cc";
    private Scheduler scheduler;
    private EventManager eventManager;

    public HttpCloudAPI(Scheduler scheduler, EventManager eventManager) {
        this.scheduler = scheduler;
        this.eventManager = eventManager;
        eventManager.registerListener(this);
    }

    @Override
    public List<Device> getDevices(Account account) throws APIException {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Trying to access CloudAPI on primary thread");
        }
        HttpGet httpGet = new HttpGet(baseUrl + "/devices");
        httpGet.setHeader("Authorization", "Bearer " + account.getToken());
        httpGet.setHeader("Accept", "application/vnd.littlebits.v2+json");
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(httpGet);
        ) {
            if (response.getStatusLine().getStatusCode() == 200) {
                account.getDevices().clear();
                HttpEntity entity = response.getEntity();
                JSONParser jsonParser = new JSONParser();
                try (Reader rdr = new InputStreamReader(entity.getContent())) {
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(rdr);
                    for (Object deviceObj : jsonArray) {
                        JSONObject deviceJson = (JSONObject) deviceObj;
                        String id = (String) deviceJson.get("id");
                        String label = (String) deviceJson.get("label");
                        Device device = new Device(account, id, label);
                        account.add(device);
                    }
                } catch (ParseException e) {
                    throw new APIException(e, APIException.Reason.INVALID_CONTENT);
                }
            } else {
                throw new APIException(null, APIException.Reason.CONNECTION_ERROR);
            }
        } catch (IOException e) {
            throw new APIException(e);
        }
        return null;
    }

    @EventHandler
    public void on(AccountAdded e) {
        final Account account = e.getAccount();
        scheduler.async(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Device> devices = getDevices(account);
                    // TODO: R4zorax - 14-09-2016: fire some events
                } catch (Exception e) {
                    log.info("Error retrieving devices for account " + account + ": " + e);
                }
            }
        });
    }
}
