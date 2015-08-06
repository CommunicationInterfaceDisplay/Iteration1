/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testbench;
import java.io.IOException;
import table.commlayer.Pipe;
import table.commlayer.TableCommunicationGateway;
/**
 *
 * @author tobias
 */
public class TableCommGatewayTestbench {
    
    public static void main(String[] args) {
        try {
            Pipe msgConPipe = new Pipe();
            Pipe msgPubPipe = new Pipe();
            TableCommunicationGateway gateway = new TableCommunicationGateway(msgConPipe, msgPubPipe);
            
            //...
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
