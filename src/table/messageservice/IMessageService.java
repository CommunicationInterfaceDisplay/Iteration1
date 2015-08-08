/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.messageservice;

import java.util.ArrayList;
import messages.Message;

/**
 *
 * @author tobias
 */
public interface IMessageService{
	public String id = "";
	public ArrayList<Message> messageList = new ArrayList<>();
	public boolean isOnline = false;
}
