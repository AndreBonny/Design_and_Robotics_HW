package Server;

import utils.ConnectionState;

import javax.microedition.io.StreamConnection;
import java.io.*;

public class MessageReceiver  implements  Runnable{

    private int sequenceNumber;
    private String message;
    private OutputStream out;
    private PrintWriter outPrinter;

    /**
     * Constructor of the class, each new instance is aimed to be a short one (receive message, perform some
     * operations and then die)
     * This can be used as a staring point to implement a Command Pattern with small tweaks on the run method
     * @param sequenceNumber each incoming message will have a number to keep the order of the messages, the idea is to
     *                       execute the commands in the same order they arrive in
     * @param message the incoming message, here will only be a simple string but this can be changed into a json file
     *                which will need to be sent to a parser
     * @param connection contains info about the connection on which the message was sent, in the unlikely case where there are
     *                   more than on active connection (this project was designed with only one connection in mind), the output
     *                   message will be sent on the right channel
     * @throws IOException in case the write operation goes wrong
     */
    public MessageReceiver(int sequenceNumber, String message, StreamConnection connection, OutputStream out)
    {
        this.message = message;
        this.sequenceNumber = sequenceNumber;
        this.out = out;
        this.outPrinter = new PrintWriter(new OutputStreamWriter(out));
    }

    public void run()
    {
        System.out.println("Sequence number: "+sequenceNumber+"\ncontent: "+message);
        if(message.equals("EOF"))
        {
            ConnectionState.getInstance().voidConnection();
            outPrinter.write("EOF");
        }
        else
        {
            outPrinter.write("ACK, recieved:"+message);
            outPrinter.flush();
            outPrinter.close();
        }
    }
}
