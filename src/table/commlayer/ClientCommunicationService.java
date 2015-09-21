/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.commlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.ClientCommunicationMessage;
import messages.ClientLoginMessage;
import messages.ClientLogoutMessage;
import messages.ClientMessage;

/**
 *
 */
public class ClientCommunicationService extends Thread {

    private final ServerSocket serverSocket;

    public ClientCommunicationService() throws IOException {
        this.serverSocket = new ServerSocket(CommunicationConfiguration.ClientCommunicationSerivcePort);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket client = serverSocket.accept();
                
                new ClientCommunicationWorker(client).start();
            
            }
        } catch (IOException ex) {
            Logger.getLogger(TableCommunicationGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class ClientCommunicationWorker extends Thread {

        private final Socket client;
        private final ObjectInputStream in;
        private final ObjectOutputStream out;

        public ClientCommunicationWorker(Socket client) throws IOException {
            this.client = client;
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        }

        @Override
        public void run() {
            try {
                ClientMessage msg;
                do {
                    msg = (ClientMessage)in.readObject();
                    if (msg instanceof ClientLoginMessage) {
                        // @TODO
                        // übertragung aller nachrichten des Clients und blockiere diese, damit sich dieser User nicht nochmal verbinden kann
                        
                    } else if (msg instanceof ClientCommunicationMessage) {
                        // @TODO
                        // unterscheidung der typen, und je nachdem die Nachrichtenliste des Users anpassen
                    }
                } while (!(msg instanceof ClientLogoutMessage));
            } catch (IOException ex) {
                Logger.getLogger(TableCommunicationGateway.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientCommunicationService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
