/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unbosque.swii;

import java.sql.Connection;
import org.apache.commons.pool2.ObjectPool;

/**
 *
 * @author nanito
 */
public class ConnectionHandler extends Thread{
    ObjectPool<Connection> op;
    Thread t1;

    public ConnectionHandler(ObjectPool<Connection> pool) {
        
    }

    void ingresarRegistros(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void run(){
        
    }
    
}
