/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.messageservice;

import java.util.concurrent.*;
import messages.Message;

/**
 *
 * @author tobias
 */
public abstract class AMessageService {
    
    protected String id = "";
    protected final CopyOnWriteArrayList<Message> messageList;
    protected boolean isOnline;

    public String getId() {
        return id;
    }

    public CopyOnWriteArrayList<Message> getMessageList() {
        return messageList;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public AMessageService(String id) {
        this.id = id;
        this.messageList = new CopyOnWriteArrayList<>();
        this.isOnline = false;
    }
}
