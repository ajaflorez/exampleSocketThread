
package message;

import java.io.Serializable;

public class MessageData implements Serializable{
    private static final long serialVersionUID = 1L;
    private static int idInc = 1;
    private int id;
    private String apellido;
    private String nombres;
    private boolean aprobo;

    public MessageData(String apellido, String nombres, boolean aprobo) {
        this.id = idInc;
        this.apellido = apellido;
        this.nombres = nombres;
        this.aprobo = aprobo;
        idInc++;
    }
    public MessageData() {
        this.id = idInc;
        this.apellido = "FLORES";
        this.nombres = "JUAN";
        this.aprobo = true;
        idInc++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public boolean isAprobo() {
        return aprobo;
    }

    public void setAprobo(boolean aprobo) {
        this.aprobo = aprobo;
    }

    @Override
    public String toString() {
        return this.id + ": " + this.apellido + " " + this.nombres + " - " + this.isAprobo();
    }
    
}
