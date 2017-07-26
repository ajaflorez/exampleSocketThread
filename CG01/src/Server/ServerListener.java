package Server;

// Clase que recibe datos y los remite a todos los clientes conectados

import message.MessageData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Control;
import message.MessageConnection;

public class ServerListener extends Thread{    
    // InputStrean 
    private ClientData clientData;
    private ObjectInputStream clientInput;
    // Usuarios Conectados al server    
    //private ArrayList<ClientData> clients;
    
    private volatile boolean running;
    

    public ServerListener(ClientData clientData, ObjectInputStream clientInput) {
        this.clientData = clientData;        
        this.clientInput = clientInput;
        //this.clients = clients;
        this.running = true;
    }
    // Escucha los 
    @Override
    public void run() {        
        // Cliclo infinito que escucha los mensajes
        while(this.running) {
            try {                
                // Recibiendo mensaje
                Object object = this.clientInput.readObject();
                // Si el mensaje es un MessageData
                if(object instanceof MessageData) {
                    sendMessageClients((MessageData)object);
                }
                // Si un mensaje de conexion
                if(object instanceof MessageConnection) {
                    // Casting al object
                    MessageConnection messageReceive = (MessageConnection)object;
                    // Si envio un mensaje de FIN
                    if(messageReceive.getControl() == Control.FIN) {
                        this.endConnection();
                    }
                }
            } catch (IOException ex) {
                //this.endConnection();
                ex.printStackTrace();
                System.out.println("ERROR con client: " + this.clientData.getId());
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    private synchronized void sendMessageClients(MessageData message) {
        // Bucle para enviar el mensaje a todos los clients
        try {
            // Enviando mensaje a todos los clientes
            for(ClientData client : Server.clients) {
                client.getOutput().writeObject(message);
                client.getOutput().flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void endConnection() {
        try {
            // Creando mensaje de confirmación
            MessageConnection msgSend = new MessageConnection(this.clientData.getId(), "",Control.ACKFIN);
            // Copiando los datos
            Socket tmpSocket = this.clientData.getSocket();
            ObjectOutputStream tmpOutput = this.clientData.getOutput();
            
            // Removiendo el client de la lista
            Server.clients.remove(clientData);
            // Mensaje de desconexion
            System.out.println("Desconectado client[" + this.clientData.getId() + "]");
            
            // Enviando el mensaje de confirmación para finalizar
            tmpOutput.writeObject(msgSend);

            // Cerrando las conexiones
            tmpOutput.close();
            tmpSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }
    
}
