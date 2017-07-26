package message;

import java.io.Serializable;

public class MessageConnection implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String HostAddress;
    private Control control;

    public MessageConnection(int id, String HostAddress, Control control) {
        this.id = id;
        this.HostAddress = HostAddress;
        this.control = control;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHostAddress() {
        return HostAddress;
    }

    public void setHostAddress(String HostAddress) {
        this.HostAddress = HostAddress;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

        
}
