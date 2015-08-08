/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.util.Date;

/**
 *
 * @author Patrick
 */
public class Message extends AbstractMessage
{
	public String messageID = "";
	public String title = "";
	public String content = "";
	public String author = "";
	public Date dateOfChange = new Date("dd/M/yyyy");
}
