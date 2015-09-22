/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import java.util.concurrent.ConcurrentSkipListSet;
import table.messageservice.AMessageService;
import table.messageservice.TableMessageService;
import table.messageservice.UserMessageService;



/**
 *
 * @author tobias
 */
public class Table extends AMessageService
{
    /**
     * Singleton-Instance
     */
    private static Table instance;
    private static final Object singletonLock = new Object();
    private static Table getInstance() {
        synchronized (singletonLock) {
            if (instance == null) {
                instance = new Table("");
            }
        }
        return instance;
    }
    // -- //
    
    private final ConcurrentSkipListSet<TableMessageService> tableMessageServices;
    
    private final ConcurrentSkipListSet<UserMessageService> userMessageServices;

    public ConcurrentSkipListSet<TableMessageService> getTableMessageServices() {
        return tableMessageServices;
    }

    public ConcurrentSkipListSet<UserMessageService> getUserMessageServices() {
        return userMessageServices;
    }
    
    public Table(String id) {
        super(id);
        this.tableMessageServices = new ConcurrentSkipListSet<>();
        this.userMessageServices = new ConcurrentSkipListSet<>();
    }
}
