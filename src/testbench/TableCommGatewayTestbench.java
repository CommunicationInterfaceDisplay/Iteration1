/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testbench;
import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.Message;
import table.commlayer.TableCommunicationGateway;
/**
 *
 * @author tobias
 */
public class TableCommGatewayTestbench {
    
    public static void main(String[] args) throws InterruptedException {
        try {
            TableCommunicationGateway gateway = new TableCommunicationGateway("TestServer");
            gateway.start();

            receiveBroadcastTransmission();
            Thread.sleep(1000);
            
            
            Message msg = new Message("0", "00000000", "title", "content...", "author", Date.from(Instant.now()));
            gateway.outgoingQueue.put(msg);
            Thread.sleep(1000);
            Message o;
            while ((o = (Message)gateway.incomingQueue.get()) != null) {
                System.out.println("Msg-ID: " + o.messageID);
            }
            
            Thread.sleep(1000);
            
            //...
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Test für das Senden des TableIdentifiers
     */
    private static void receiveBroadcastTransmission() {
        try {
                DatagramSocket socket = new DatagramSocket(10001);
            byte[] receiveData = new byte[1024];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            socket.getBroadcast();
            socket.receive(packet);
            
            System.out.println("Erhalte Broadcastmsg: " + new String(packet.getData()));
            
        } catch (SocketException ex) {
            Logger.getLogger(TableCommGatewayTestbench.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TableCommGatewayTestbench.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
