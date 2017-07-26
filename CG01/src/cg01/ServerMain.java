package cg01;

import Server.Server;
import java.util.Scanner;
import message.MessageData;

public class ServerMain {

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        
        Scanner leer = new Scanner(System.in);
        String texto;
        
        while(server.isRunning()) {
            texto = leer.nextLine();
            if(texto.equals("STOP")) {
                server.endConnectionClients();
                server.endListener();
            }      
        }
        System.out.println("SERVER STOP ..!");
        
    }
    
}
