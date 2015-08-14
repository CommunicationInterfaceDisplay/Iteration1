/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.proxy;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 *
 * @author tobias
 * @version 0.5
 * 
 * Die Klasse Pipe soll dazu dienen, Objekte über Streams Thread-übergreifend zu steuern
 */
public class Pipe {
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    
    public Pipe() throws IOException {
        PipedOutputStream src = new PipedOutputStream();
        PipedInputStream snk = new PipedInputStream();
        src.connect(snk);
        out = new ObjectOutputStream(src);
        in = new ObjectInputStream(snk);
    }
    
    public void put(Object msg) throws IOException {
        out.writeObject(msg);
    }
    
    public Object get() throws IOException, ClassNotFoundException {
        return in.readObject();
    }
        
}
