/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testbench;
import java.io.IOException;
import messages.Message;
import table.proxy.Pipe;
import table.commlayer.TableCommunicationGateway;
/**
 *
 * @author tobias
 */
public class TableCommGatewayTestbench {
    
    public static void main(String[] args) throws InterruptedException {
        try {
            Pipe msgConPipe = new Pipe();
            Pipe msgPubPipe = new Pipe();
            TableCommunicationGateway gateway = new TableCommunicationGateway(msgConPipe, msgPubPipe);
            gateway.start();
            Thread.sleep(1000);
            Message msg = new Message();
            msgPubPipe.put(msg);
            msgPubPipe.put(msg);
            msgPubPipe.put(msg);
            Thread.sleep(1000);
            msgPubPipe.put(msg);
            //...
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
