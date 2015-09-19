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
 * Diese Nachricht sorgt dafür, das alle Nachrichten des Users an den Client gesendet werden
 * und der Zugriff auf diese Nachrichten gesperrt werden.
 */
public class ClientLoginMessage extends ClientMessage {

    public ClientLoginMessage(String tableID, String userID) {
        super(tableID, userID);
    }
    
}
