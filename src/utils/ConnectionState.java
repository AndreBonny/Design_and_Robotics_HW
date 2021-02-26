package utils;

public class ConnectionState {

    private boolean isOn;
    private static ConnectionState instance;

    private ConnectionState()
    {
        this.isOn = true;
    }

    public static ConnectionState getInstance()
    {
        if(instance == null)
            instance = new ConnectionState();
        return instance;
    }

    public boolean isAlive()
    {
        return isOn;
    }

    public void voidConnection()
    {
        isOn = false;
    }
}
