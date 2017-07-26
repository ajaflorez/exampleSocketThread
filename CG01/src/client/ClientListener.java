package client;

import message.MessageData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Control;
import message.MessageConnection;

public class ClientListener implements Runnable{
    private int id;
    // host e ip
    private InetAddress inetAddress; 
    // Socket de conexion
    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    
    private String hostServer;
    private int portServer;
    
    private boolean running;

    public ClientListener() {
        this.hostServer = "localhost";
        this.portServer = 65432;
        this.initialize();
    }
    public ClientListener(String host, int port) {
        this.hostServer = host;
        this.portServer = port;
        this.initialize();
    }
    private void initialize() {
        this.id = 0;
        try {
            // Obteniendo el InetAddress
            this.inetAddress = Inet4Address.getLocalHost();
            // Crear un socket de conexión al servidor
            this.clientSocket = new Socket(hostServer, portServer);
            // Crea los Streams
            this.clientInput = new ObjectInputStream(this.clientSocket.getInputStream());
            this.clientOutput = new ObjectOutputStream(this.clientSocket.getOutputStream());
            
            // Creando mensaje para establecer conexion
            MessageConnection messageSend = new MessageConnection(this.id, this.inetAddress.getHostAddress(), 
                    this.inetAddress.getHostName(), Control.SYN);
            // Enviando mensaje al servidor para conectarse
            this.clientOutput.writeObject(messageSend);            

            // Esperando la confirmación del servidor, recibiendo el mensaje
            Object object = this.clientInput.readObject();
            // si es un mensaje de conexion
            if(object instanceof MessageConnection) {
                // Casting al objeto recibido
                MessageConnection messageReceive = (MessageConnection)object;
                if(messageReceive.getControl() == Control.ACK) {
                    this.id = messageReceive.getId();
                    //-----------------------------
                    System.out.println("Conectado con SERVER IP: " + messageReceive.getHostAddress() + ", ID: " + this.id);
                    //-----------------------------                
                }                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.running = true;
    }
    public void sendMessage(MessageData message) {
        try {
             // Enviando mensaje al servidor para enviar a los demas clients
            this.clientOutput.writeObject(message);            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void endConnection() {
        this.running = false;
        // creando un mensaje para terminar la conexión
        MessageConnection msgEnd = new MessageConnection(this.id, this.hostServer, "", Control.FIN);
        try {
            // Enviando mensaje al servidor para terminas la conexion
            this.clientOutput.writeObject(msgEnd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }         
    }

    @Override
    public void run() {
        
        while(this.running) {
            try {
                // Recibiendo el mensaje
                Object object = this.clientInput.readObject();
                
                if(object instanceof MessageData) {
                    MessageData message = (MessageData)object;
                    System.out.println(message);
                } 
                
                // si es un mensaje de conexion
                if(object instanceof MessageConnection) {
                    // Casting al objeto recibido
                    MessageConnection messageReceive = (MessageConnection)object;
                    if(messageReceive.getControl() == Control.ACK) {                    
                        //-----------------------------
                        System.out.println("terminando conexión");
                        //-----------------------------         
                        
                    }               
                    
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } 
        }
    }
    
}
