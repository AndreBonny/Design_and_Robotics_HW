package Client;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;


public class ClientMessageReceiver implements Runnable {


    private StreamConnection connection;


    public ClientMessageReceiver(StreamConnection connection) {
        this.connection = connection;
    }


    public void run() {
        // Opens the connection. If this fails, the whole listening service
        // fails.
        InputStream input = null;
        RemoteDevice device = null;
        try {
            input = new BufferedInputStream(connection.openInputStream());
            device = RemoteDevice.getRemoteDevice(connection);
        } catch (IOException e) {
            System.err.println("Listening service failed. Incoming messages won't be displayed.");
            e.printStackTrace();
            return;
        }

        // Main loop of the thread, reads incoming message
        // and prints it.
        while (true) {
            byte buffer[] = new byte[1024];
            int bytesRead;
            try {
                bytesRead = input.read(buffer);
                String incomingMessage = new String(buffer, 0, bytesRead);
                System.out.println("[" + device.getFriendlyName(false) + " -> " + device.getBluetoothAddress() + "]: "+ incomingMessage);
            } catch (IOException e) {
                // Don't rethrow this exception so if one message is lost, the
                // service continues listening.
                System.err.println("Error while reading the incoming message.");
                e.printStackTrace();
            }
        }

    }

}