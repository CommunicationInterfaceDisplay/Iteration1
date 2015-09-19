/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

/**
 *
 * @author tobias
 */
public class ClientCommunicationMessage extends ClientMessage {
    
    private final CommunicationType type;
    private final Message message;

    public ClientCommunicationMessage(String tableID, String userID, CommunicationType type, Message msg) {
        super(tableID, userID);
        this.type = type;
        this.message = msg;
    }
    
    
}
