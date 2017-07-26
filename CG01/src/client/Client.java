package client;

import message.MessageData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Control;
import message.MessageConnection;

public class Client extends Thread{
    private int idClient;
    // host e ip
    private InetAddress inetAddress; 
    // Socket de conexion
    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    
    private String hostServer;
    private int portServer;
    
    private boolean running;

    public Client() {
        this.hostServer = "10.10.102.11";
        this.portServer = 65432;
        this.initialize();
    }
    public Client(String host, int port) {
        this.hostServer = host;
        this.portServer = port;
        this.initialize();
    }
    private void initialize() {
        this.idClient = 0;
        this.running = true;
        try {
            // Obteniendo el InetAddress
            this.inetAddress = Inet4Address.getLocalHost();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        this.startConnection();
    }
    public synchronized void sendMessage(Object message) throws IOException {
        this.clientOutput.writeObject(message);
    }
    /*public void sendMessageConnection(MessageConnection message) throws IOException {
        this.clientOutput.writeObject(message);
    }*/
    public void startConnection() {
        try {
            // Crear un socket de conexión al servidor
            this.clientSocket = new Socket(hostServer, portServer);
            // Crea los Streams
            this.clientInput = new ObjectInputStream(this.clientSocket.getInputStream());
            this.clientOutput = new ObjectOutputStream(this.clientSocket.getOutputStream());
            
            // Creando mensaje para establecer conexion
            MessageConnection messageSend = new MessageConnection(this.idClient, this.inetAddress.getHostAddress(), Control.SYN);
            // Enviando mensaje al servidor para conectarse
            //this.clientOutput.writeObject(messageSend);            
            this.sendMessage(messageSend);

            // Esperando la confirmación del servidor, recibiendo el mensaje
            Object object = this.clientInput.readObject();
            // si es un mensaje de conexion
            if(object instanceof MessageConnection) {
                // Casting al objeto recibido
                MessageConnection messageReceive = (MessageConnection)object;
                if(messageReceive.getControl() == Control.ACK) {
                    this.idClient = messageReceive.getId();
                    //-----------------------------
                    System.out.println("Conectado con SERVER IP: " + messageReceive.getHostAddress() + ", ID: " + this.idClient);
                    //-----------------------------                
                }                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    public void endConnection() {
        // Creando mensaje para finalizar la conexion
        MessageConnection msgEnd = new MessageConnection(this.idClient, "", Control.FIN);
        try {
            // ENvianado mensaje para finalizar conexion
            //this.clientOutput.writeObject(msgEnd);
            this.sendMessage(msgEnd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void run() {
        while(this.running) {
            try {
                // Esperando mensaje del servidor, recibiendo el mensaje
                Object object = this.clientInput.readObject();
                // si es un mensaje de data
                if(object instanceof MessageData) {
                    MessageData messageData = (MessageData)object;
                    System.out.println(messageData);
                }
                // si es un mensaje de conexion
                if(object instanceof MessageConnection) {
                    MessageConnection messageConnection = (MessageConnection)object;
                    // Si en una confirmación de FIN
                    if(messageConnection.getControl() == Control.ACKFIN) {
                        this.running = false;
                        this.clientOutput.close();
                        this.clientInput.close();
                        this.clientSocket.close();
                        System.out.println("Desconectado con Servidor ... !");
                    }
                    // Si en un mensaje de FIN
                    if(messageConnection.getControl() == Control.FIN) {
                        this.endConnection();
                    }
                }
            } catch (IOException ex) {
                /*this.running = false;
                try {
                    this.clientOutput.close();
                    this.clientInput.close();
                    this.clientSocket.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.out.println("SERVIDOR DESAPARECIO ...!!!");
                System.out.println("Desconectado con Servidor ... !");*/
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getIdClient() {
        return idClient;
    }
    
}
