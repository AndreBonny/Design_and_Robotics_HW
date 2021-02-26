package Server;

import utils.ConnectionState;

import javax.microedition.io.StreamConnection;
import java.io.*;

public class MessageReceiver  implements  Runnable{

    private int sequenceNumber;
    private String message;
    private OutputStream out;
    private PrintWriter outPrinter;

    public MessageReceiver(int sequenceNumber, String message, StreamConnection connection) throws IOException
    {
        this.message = message;
        this.sequenceNumber = sequenceNumber;
        this.out = connection.openOutputStream();
        this.outPrinter = new PrintWriter(new OutputStreamWriter(out));
    }

    public void run()
    {
        System.out.println("Sequence number: "+sequenceNumber+"\ncontent: "+message);
        if(message.equals("EOF"))
        {
            ConnectionState.getInstance().voidConnection();
            outPrinter.write("ACK");
        }
        else
        {
            outPrinter.write("ACK");
            outPrinter.flush();
            outPrinter.close();
        }
    }
}
