/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.commlayer;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author tobias
 */
public class ServerCommunicationWorker extends Thread {

    private final Socket workerSocket;
    
    public ServerCommunicationWorker(String name, ThreadGroup group, InetAddress address) throws IOException {
        super(group, name);
        this.workerSocket = new Socket(address, CommunicationConfiguration.MssageTransportServicePort);
    }

    @Override
    public void run() {
        
    }

}
