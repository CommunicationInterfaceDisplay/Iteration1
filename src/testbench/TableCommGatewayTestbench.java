/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testbench;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
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
}
