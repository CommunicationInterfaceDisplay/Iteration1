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
public abstract class AbstractMessage
{
	public String tableID = "";
        
        public AbstractMessage(String tableID) {
            this.tableID = tableID;
        }
}
