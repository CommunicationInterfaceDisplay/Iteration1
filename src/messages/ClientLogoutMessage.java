/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

/**
 *
 * @author tobias
 * 
 * Der Zugriff auf die Nachrichten wird wieder freigeben. 
 * Bei Timeout der Verbindung geschieht um übrigen das selbe...
 */
public class ClientLogoutMessage extends ClientMessage {

    public ClientLogoutMessage(String tableID, String userID) {
        super(tableID, userID);
    }
    
}
