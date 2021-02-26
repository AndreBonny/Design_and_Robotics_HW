package Client;
import java.io.IOException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 * This is just an implementation of the DiscoveryListener with the methods
 * needed for this application
 */
public class DeviceDiscoveryService implements DiscoveryListener {

    public void servicesDiscovered(int transID, ServiceRecord[] services) {}


    public void serviceSearchCompleted(int transID, int respCode) {}

    public void inquiryCompleted(int discType) {
        System.out.println("Device discovery completed!");

        // Notify the main thread that the search has ended
        synchronized (Client.class) {
            Client.class.notify();
        }
    }


    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        String address = btDevice.getBluetoothAddress();
        try {
            String name = btDevice.getFriendlyName(false);
            System.out.println("New device discovered: [" + address + " - " + name + "], URL: ");
        } catch (IOException e) {
            System.err.println("Error while retrieving name for device [" + address + "]");
            e.printStackTrace();
        }
    }
}