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
 * @param <T>
 */
public class Monitor<T> {
    private final ConcurrentLinkedQueue queue;
    //Semaphore monitorSemaphore;
    
    public Monitor() {
        queue = new ConcurrentLinkedQueue();
        //monitorSemaphore = new Semaphore(1);
    }
    
    public void put(T o) {
        //monitorSemaphore.acquireUninterruptibly();
        System.out.println("Opening...");
        synchronized (queue) {
            
            queue.add(o);
            queue.notifyAll();
            System.out.println("New Element and notify all!");
        }
        System.out.println("Closing...");
        //monitorSemaphore.release();
    }
    
    public Object get() throws InterruptedException {
        //monitorSemaphore.acquireUninterruptibly();
        
        synchronized (queue) {
            while (queue.isEmpty()) {
                System.out.println("queue is empty...");
                queue.wait();
                System.out.println("Waiting...");
            }
            T o = (T)queue.peek();
            //monitorSemaphore.release();
        
            return o;
        }
        
    }
}
