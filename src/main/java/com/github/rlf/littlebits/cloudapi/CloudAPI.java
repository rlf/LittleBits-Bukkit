package com.github.rlf.littlebits.cloudapi;

import com.github.rlf.littlebits.async.Consumer;
import com.github.rlf.littlebits.model.Account;
import com.github.rlf.littlebits.model.Device;

import java.util.List;

/**
 * An abstraction of the CloudAPI
 */
public interface CloudAPI {
    /**
     * Returns a list of devices registered to the supplied account.
     * @param account A valid littlebits account
     * @return A list of devices, or <code>null</code>.
     */
    List<Device> getDevices(Account account);

    /**
     * Reads the input of the given device continuously (blocks).
     * @param device   A valid device.
     * @param consumer The consumer that will receive the stream of input.
     * @return An HTTP error code if/when the reading is interrupted/aborted.
     */
    int readInput(Device device, Consumer<DeviceInput> consumer);

    /**
     * Writes the amplitude (percentage) to the given device.
     * @param device     A valid littleBits device
     * @param amplitude  The amplitude (0-100) to be output by the device.
     * @return An HTTP error code if/when the reading is interrupted/aborted.
     */
    int writeOutput(Device device, int amplitude);

    /**
     * Writes a new label to the given device.
     * @param device     A valid littleBits device.
     * @param label      A new label for the device.
     * @return An HTTP error code if/when the reading is interrupted/aborted.
     */
    int writeLabel(Device device, String label);

    /**
     * Shutsdown the service (stops the blocking threads).
     */
    void shutdown();

    class DeviceInput {
        private final long timestamp;
        private final int percentage;

        public DeviceInput(long timestamp, int percentage) {
            this.timestamp = timestamp;
            this.percentage = percentage;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getPercentage() {
            return percentage;
        }
    }
}
