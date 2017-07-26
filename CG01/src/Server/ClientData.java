package Server;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientData {
    private int id;
    private Socket socket;
    private ObjectOutputStream output;
    private String HostAddress;
    
    private volatile static int idInc = 1;

    public ClientData(Socket socket, ObjectOutputStream output, String HostAddress) {
        this.id = idInc;
        this.socket = socket;
        this.output = output;
        this.HostAddress = HostAddress;
        idInc++;
    }

    public int getId() {
        return id;
    }
    
    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public String getHostAddress() {
        return HostAddress;
    }

}
