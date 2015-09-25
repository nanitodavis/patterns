/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.Statement;
import org.apache.commons.pool2.ObjectPool;

/**
 *
 * @author nanito
 */
public class ConnectionHandler extends Thread{
    ObjectPool<Connection> pool;
    Connection con;
    Statement s;
    Thread thread1;

    public ConnectionHandler(ObjectPool<Connection> pool) {
        this.pool=pool;
    }

    void ingresarRegistros(int i) throws Exception {
        con=pool.borrowObject();
        s.execute("insert into Prueb Values ('a', '"+i+"')");
        pool.returnObject(con);
    }
    
    public void run(){
        
    }
    
}
