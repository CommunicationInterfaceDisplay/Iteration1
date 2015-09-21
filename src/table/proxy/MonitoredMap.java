/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.proxy;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 *
 * @author tobias
 * 
 * Eine Implementierung eines Monitors mit threadsicherer Map
 * @param <K> Key
 * @param <T>
 */
public class MonitoredMap<K, T> {
    private final ConcurrentSkipListMap map;
    
    public MonitoredMap() {
        map = new ConcurrentSkipListMap();
    }
    
    public void put(K k, T t) {
        //System.out.println("Opening...");
        synchronized (map) {
            map.put(k, t);
            map.notifyAll();
            //System.out.println("New Element and notify all!");
        }
        //System.out.println("Closing...");
    }
    
    public boolean containsKey(K k) {
        boolean isContained;
        synchronized (map) {
            isContained = map.containsKey(k);
        }
        return isContained;
    }
    
    public void remove(K k) {
        synchronized (map) {
            map.remove(k);
        }
    }
    
//    public Object get() throws InterruptedException { 
//        synchronized (map) {
//            while (map.isEmpty()) {
//                System.out.println("queue is empty...");
//                map.wait();
//                System.out.println("Waiting...");
//            }
//            T o = (T)map.();
//        
//            return o;
//        }
//    }
}
