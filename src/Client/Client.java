package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class Client {

    /**
     * Starts the Bluetooth devices discovery, once the other devices are found they are
     * printed on screen.
     * Then search from the devices found the one which exposes a services with the UUID of the
     * server
     *
     * @throws BluetoothStateException
     * @throws InterruptedException
     */
    private static String startDiscovery() throws BluetoothStateException, InterruptedException {
        DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();

        System.out.println("Starting device discovery...");
        agent.startInquiry(DiscoveryAgent.GIAC, new DeviceDiscoveryService());


        // Wait for the thread to finish the search
        synchronized (Client.class) {
            Client.class.wait();
        }
        String myServiceUUID = "bdb48a12781d11eb94390242ac";
        UUID thisUUID = new UUID(myServiceUUID, false);
        String urlFromUUID = agent.selectService(thisUUID, ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
        System.out.println("Found URL: " + urlFromUUID);
        return urlFromUUID;
    }

    /**
     * Opens up a connection to the specified address
     *
     * @param address the address to which connect
     * @throws IOException
     */
    private static void openConnection(String address) throws IOException {
        // Tries to open the connection.
        StreamConnection connection = (StreamConnection) Connector.open(address);
        if (connection == null) {
            System.err.println("Could not open connection to address: " + address);
            System.exit(1);
        }

        // Initializes the streams.
        OutputStream output = connection.openOutputStream();
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(isr);

        // Starts the listening service for incoming messages, this will only run on a single thread
        // right now the server will send only "ACK" or "EOF"
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new ClientMessageReceiver(connection));

        // Main loop of the program: reads a string and sends to the Bluetooth
        // device.
        System.out.println("\nConnection opened, type in console and press enter to send a message to: " + address + "\n");
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        while (true) {
            String toSend = reader.readLine();
            byte[] toSendBytes = toSend.getBytes(StandardCharsets.US_ASCII);
            output.write(toSendBytes);
            System.out.println("[" + localDevice.getFriendlyName() + " -> " + localDevice.getBluetoothAddress() + "]: " + toSend);
        }
    }


    /**
     * Main of the client, it will display some information on the
     * client device and then it will try to connect to the server
     *
     * @param args standard main input, here it's unused
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // Prints some information at startup about this device.
        LocalDevice local = LocalDevice.getLocalDevice();
        System.out.println("----------- LOCAL DEVICE INFORMATION -----------");
        System.out.println("Address: " + local.getBluetoothAddress());
        System.out.println("Name: " + local.getFriendlyName());

        // Search for bluetooth devices in the proximity and find the one with the same
        // in my case that is //btspp://9CB6D0FF1C58:4;authenticate=false;encrypt=false;master=false
        String serverURL = startDiscovery();
        openConnection(serverURL);

    }
}

