package Server;

import utils.ConnectionState;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.util.concurrent.*;

public class Server {


    /**
     * This method is called by the main, it contains the main loop of the server and will start new threads
     * to deal with the incoming message. The threads are handled by a ThreadPoolExecutor.
     * This server is designed to only handle one client that can even send messages at a high frequency
     * @throws IOException when there are problems in the open of the streamConnectionNotifier
     */
    private void startServer() throws IOException
    {
        // The thread factory can be changed when needed
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2) );

        String myServiceName = "simpleService";
        // This UUID can be changed and should be set as unique,
        // remember that with a short uuid the attribute shortUUID should be set as true
        String myServiceUUID = "bdb48a12781d11eb94390242ac";
        UUID thisUUID = new UUID(myServiceUUID, false);
        //UUID thisUUID = new UUID("8848",true);
        String connURL = "btspp://localhost:"+thisUUID+";"+"name="+myServiceName;
        System.out.println("This device URL: "+connURL);


        StreamConnectionNotifier streamConnectionNotifier = (StreamConnectionNotifier)Connector.open(connURL);
        System.out.println("Waiting for new connection...");
        //this method blocks the thread
        StreamConnection streamConnection = streamConnectionNotifier.acceptAndOpen();

        // New client connection accepted we will handle its messages
        RemoteDevice rd = RemoteDevice.getRemoteDevice(streamConnection);
        System.out.println("Remote device address: "+ rd.getBluetoothAddress());
        System.out.println("Remote device name: "+ rd.getFriendlyName(true));

        ConnectionState connectionState = ConnectionState.getInstance();
        InputStream input = streamConnection.openInputStream();
        OutputStream output =  streamConnection.openOutputStream();

        int messageCount = 0;

        System.out.println("Starting to listen...");
        // Main loop
        while (connectionState.isAlive())
        {
            byte buffer[] = new byte[1024];
            int bytes_read = input.read( buffer );
            String received = new String(buffer, 0, bytes_read);
            executor.execute(new MessageReceiver(messageCount, received, streamConnection,output));
            messageCount++;
        }

        input.close();
        streamConnectionNotifier.close();

    }

    /**
     * Main class of the Server, it will display some info on the device it is run on.
     * It will then start the server
     * @param args standard command line input
     */
    public static void main(String[] args){
        try {
            //display local device address and name
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            localDevice.setDiscoverable(DiscoveryAgent.GIAC);
            System.out.println("This device address: " + localDevice.getBluetoothAddress());
            System.out.println("This device name: " + localDevice.getFriendlyName());

            Server server = new Server();
            server.startServer();
        }catch (IOException e)
        {
            System.out.println("Invoked IO exception");
        }
        catch (Exception other)
        {
            System.out.println("Other exception invoked: ");
            other.printStackTrace();;
        }

    }


}
