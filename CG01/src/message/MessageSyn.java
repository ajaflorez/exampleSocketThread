package message;

import java.io.Serializable;

public class MessageSyn implements Serializable{
    private static final long serialVersionUID = 1L;
    private String HostAddress;
    private String HostName;

    public MessageSyn(String HostAddress, String HostName) {
        this.HostAddress = HostAddress;
        this.HostName = HostName;
    }

    public String getHostAddress() {
        return HostAddress;
    }

    public void setHostAddress(String HostAddress) {
        this.HostAddress = HostAddress;
    }

    public String getHostName() {
        return HostName;
    }

    public void setHostName(String HostName) {
        this.HostName = HostName;
    }
    
    
    
}
