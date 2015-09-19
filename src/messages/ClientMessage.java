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
public class ClientMessage extends AbstractMessage {
    
    public String UserID = "";

    public ClientMessage(String tableID, String userID) {
        super(tableID);
        this.UserID = userID;
    }
    
}

