/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.proxy;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author tobias
 * 
 * Eine Implementierung eines Monitors mit threadsicherer Queue
 * @param <T>
 */
public class MonitoredQueue<T> {
    private final ConcurrentLinkedQueue queue;
    
    public MonitoredQueue() {
        queue = new ConcurrentLinkedQueue();
    }
    
    public void put(T o) {
        System.out.println("Opening...");
        synchronized (queue) {
            queue.add(o);
            queue.notifyAll();
            System.out.println("New Element and notify all!");
        }
        System.out.println("Closing...");
    }
    
    public Object get() throws InterruptedException { 
        synchronized (queue) {
            while (queue.isEmpty()) {
                System.out.println("queue is empty...");
                queue.wait();
                System.out.println("Waiting...");
            }
            T o = (T)queue.peek();
        
            return o;
        }
        
    }
}
