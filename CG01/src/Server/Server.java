package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Control;
import message.MessageConnection;

public class Server extends Thread{
    // host e ip
    private InetAddress inetAddress;    
    // numero de puerto y máximo de conexiones
    private int port;
    private int maxConn;    
    // Socket del servidor
    private ServerSocket serverSocket;
    // Array de clients conectados    
    protected static volatile ArrayList<ClientData> clients;
    
    private volatile boolean running;
    
    public Server() {
        this.port = 65432;
        this.maxConn = 5;
        this.initialize();
    }
    public Server(int port, int maxConn) {
        this.port = port;
        this.maxConn = maxConn;
        this.initialize();
    }
    private void initialize() {
        try {
            // Obteniendo el InetAddress
            this.inetAddress = Inet4Address.getLocalHost();          
            // Creando en socket del server, que espera lo requerimientos de los clients
            this.serverSocket = new ServerSocket(port, maxConn);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        clients = new ArrayList<>();
        this.running = true;
    }
    // Función que escucha las conexiones de los clientes
    public void listener() {
        try {
            // Listener de conexion del usuario, espera las conexiones desde los clients
            Socket clientSocket = this.serverSocket.accept();
            // crear el outputStream del client
            ObjectOutputStream clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            // creando el inputStream del client
            ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
            // recibiendo mensaje de conexión del Client
            Object object = clientInput.readObject();
            // Si el mensaje recibido es un messageSyn
            if(object instanceof MessageConnection) {
                // Casting al object
                MessageConnection messageReceive = (MessageConnection)object;
                if(messageReceive.getControl() == Control.SYN) {
                    // Creando un nuevo ClientData
                    ClientData clientData = new ClientData(clientSocket, clientOutput, messageReceive.getHostAddress());
                    // Agregando un nuevo ClientData a clients
                    clients.add(clientData);
                    // Crea un hilo que escuche los objetos enviado por el client
                    ServerListener serverListener = new ServerListener(clientData, clientInput);
                    serverListener.start();
                    // creando el mensaje de respuesta ACK al cliente
                    MessageConnection messageResponse = new MessageConnection(clientData.getId(), 
                            this.inetAddress.getHostAddress(), Control.ACK);
                    // enviando el mensaje al cliente
                    clientOutput.writeObject(messageResponse);
                    //-----------------------------
                    System.out.println("Conectado con Client[" + clientData.getId() + "] IP: " + 
                            messageReceive.getHostAddress());
                    //-----------------------------
                }
            }                
        } catch (IOException ex) {
            try {
                this.serverSocket.close();
            } catch (IOException ex1) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        //------------
        System.out.println("Server IP: " + this.inetAddress.getHostAddress() + 
                ", NAME: " + this.inetAddress.getHostName());
        
        while(this.running) {
            this.listener();
        }
    }
    public void endListener() {
        //this.running = false;
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }  
    public void endConnectionClients() {
        
        // Bucle para enviar el mensaje a todos los clients
        try {
            // Enviando mensaje a todos los clientes
            for(ClientData client : clients) {
                // Creando mensaje para finalizar la conexion
                MessageConnection msgEnd = new MessageConnection(client.getId(), "", Control.FIN);
                // Enviando mensaje
                client.getOutput().writeObject(msgEnd);
            }
            this.running = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }
    
}
