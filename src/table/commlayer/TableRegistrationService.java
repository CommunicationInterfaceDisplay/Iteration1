/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.commlayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tobias Tafelregistrierungsdienst (TRS)
 */
public class TableRegistrationService extends Thread {

    private DatagramSocket serverSocket;
    private final ConcurrentSkipListMap<String, InetAddress> tableRegister;
    private final String tableIdentifier;
    private final ThreadGroup tableCommunicationThreads;

    /**
     *
     * @param tableRegister
     * @param tableComThreads
     * @param tableIdentifier
     * @throws java.net.SocketException
     */
    public TableRegistrationService(ConcurrentSkipListMap<String, InetAddress> tableRegister, String tableIdentifier, ThreadGroup tableComThreads) throws SocketException {
        this.tableRegister = tableRegister;
        this.tableIdentifier = tableIdentifier;
        this.tableCommunicationThreads = tableComThreads;
        this.serverSocket = new DatagramSocket(CommunicationConfiguration.ClientCommunicationSerivcePort);
    }

    @Override
    public void run() {
        try {
            sendTableIdentifer();
        } catch (IOException ex) {
            Logger.getLogger(TableRegistrationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while (true) {
                byte[] receiveData = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String tableId = new String(receivePacket.getData());
                InetAddress tableIP = receivePacket.getAddress();

                System.out.println(new String(receivePacket.getData()));
                System.out.println(receivePacket.getAddress().getHostAddress());

                //System.out.println(tableRegister.values());
                if (tableRegister.containsKey(tableId) & tableRegister.containsValue(tableIP)) {
                    System.out.println("table schon angemeldet!");
                    Thread[] workers = null;
                    tableCommunicationThreads.enumerate(workers);
                    if (workers != null) {
                        for (Thread worker : workers) {
                            String name = worker.getName();
                            if (name.equals(tableId)) {
                                worker.interrupt();
                                break;
                            }
                        }
                    } 
                } else {
                    tableRegister.put(tableId, receivePacket.getAddress());
                }
                
                MessageSenderPart worker = new MessageSenderPart(tableId, this.tableCommunicationThreads, receivePacket.getAddress());
                worker.start();
            }
        } catch (SocketException ex) {
        } catch (IOException ex) {
        } finally {
            serverSocket.close();
        }
    }

    /**
     * Methode sendet den Table-Identifier per Broadcast
     *
     * @throws IOException
     */
    private void sendTableIdentifer() throws IOException {
        byte[] data = this.tableIdentifier.getBytes();

        serverSocket = new DatagramSocket();
        serverSocket.setBroadcast(true);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), CommunicationConfiguration.RegistrationServicePort);
        serverSocket.send(sendPacket);

        System.out.println("TableIdentifier wurde gesendet...");
    }
}
