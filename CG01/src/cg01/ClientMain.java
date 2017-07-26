package cg01;

import message.MessageData;
import client.Client;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMain {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();

        /*MessageData message = new MessageData("MOROCO", "ANTONIO", false);
        client.sendMessage(message);

        Scanner leer = new Scanner(System.in);
        String apellido;
        String nombres;            
        while(client.isRunning()) {
            apellido = leer.nextLine();
            if(client.isRunning()) {
                if(apellido.equals("STOP")) {
                    client.endConnection();
                    break;
                }
                nombres = leer.nextLine();                
                MessageData message2 = new MessageData(apellido, nombres, true);
                client.sendMessage(message2);
            }
            else {
                break;
            }

        }*/
        Random random = new Random();
        int i = 0;
        boolean b = false;
        MessageData message;
        int id = client.getIdClient();
                
        while(client.isRunning()) {
            i = random.nextInt(100)+1;
            b = random.nextBoolean();
            message = new MessageData(id + ": MOROCO", "JUAN", b);
            try {
                client.sendMessage(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
