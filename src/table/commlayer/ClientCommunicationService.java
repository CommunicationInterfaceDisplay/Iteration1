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

    private final int port;
    private final ServerSocket serverSocket;

    public ClientCommunicationService(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
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
                ClientMessage msg = null;
                do {
                    out.writeObject(msg);
                    if (msg instanceof ClientLoginMessage) {
                        // übertragung aller nachrichten des Clients und blockiere
                        
                    } else if (msg instanceof ClientCommunicationMessage) {
                        // unterscheidung der typen
                    }
                } while (!(msg instanceof ClientLogoutMessage));
            } catch (IOException ex) {
                Logger.getLogger(TableCommunicationGateway.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
