/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.proxy;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author tobias
 * 
 * Eine Implementierung eines Monitors mit threadsicherer Queue
 */
public class Monitor {
    private ConcurrentLinkedQueue queue;
    Semaphore monitorSemaphore;
    
    public Monitor() {
        queue = new ConcurrentLinkedQueue();
        monitorSemaphore = new Semaphore(1);
    }
    
    public void put(Object o) {
        monitorSemaphore.acquireUninterruptibly();
        queue.add(o);
        monitorSemaphore.release();
    }
    
    public Object get() {
        monitorSemaphore.acquireUninterruptibly();
        Object o = queue.peek();
        monitorSemaphore.release();
        
        return o;
    }
}
