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
 * 
 */
public enum CommunicationType {

    /**
     * Die beigefügte Nachricht ist eine neue und soll hinzugefügt werden
     */
    Add,

    /**
     * Die beigefügte Nachricht soll gelöscht werden, benötigt an sich nur MessageID
     */
    Remove,

    /**
     * Die beigefügte Nachricht soll eine bestehende Überschreiben
     */
    Replace
}
