/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Patrick
 */
public class Message extends AbstractMessage implements Serializable
{
	public String messageID = "";
	public String title = "";
	public String content = "";
	public String author = "";
	public Date dateOfChange = new Date("14/8/2015"); //"dd/M/yyyy"
        
        public Message(String tableID, String msgID, String title, String content, String author, Date date) {
            super(tableID);
            this.messageID = msgID;
            this.title = title;
            this.content = content;
            this.author = author;
            this.dateOfChange = date;
        }
        
        public Message(Message msg) {
            this(msg.tableID, msg.messageID, msg.title, msg.content, msg.author, msg.dateOfChange);
        }
}
