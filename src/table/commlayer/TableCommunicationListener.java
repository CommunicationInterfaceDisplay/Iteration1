/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.commlayer;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author tobias
 */
public class TableCommunicationListener extends Thread {
    
    private final ServerSocket listenerSocket;
    private final ThreadGroup tableCommGroup;
    
    public TableCommunicationListener(ThreadGroup group) throws IOException {
        this.tableCommGroup = group;
        this.listenerSocket = new ServerSocket(CommunicationConfiguration.MssageTransportServicePort);
    }

    @Override
    public void run() {
        int counter = 0;
        while (true) {
            try {
                Socket socket = listenerSocket.accept();
                // in eine liste einfügen
                Worker w = new Worker(tableCommGroup , socket, Integer.toString(counter));
                
            } catch (IOException ex) {
                Logger.getLogger(TableCommunicationListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public class Worker extends Thread {
        
        private final Socket socket;
        
        public Worker(ThreadGroup group, Socket socket, String counter) {
            super(group, counter);
            this.socket = socket;
        }

        @Override
        public void run() {
            /*
            1. Füge die Tafel das Register ein
            2. Empfange Nachrichten der Gegenstelle und mache diese in die IngomingQueue
            3. Sende die eigenen Nachrichten an die Gegenstelle
            4. 
            */
        }
    }
}
