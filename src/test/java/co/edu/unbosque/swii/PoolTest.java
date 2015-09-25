/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.pool2.BaseObjectPool;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Alejandro
 */
//averiguar patron template
public class PoolTest {

    public static final String pwd = "df_qsWM_UW";

    @Test(expectedExceptions = org.postgresql.util.PSQLException.class,
            expectedExceptionsMessageRegExp = ".*too many connections.*"
    )
    public void soloDebeCrear5Conexiones() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo8_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            pool.borrowObject();
        }
    }

    @Test
    public void aprendiendoAControlarLasConexiones() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo8_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            Connection c = pool.borrowObject();
            pool.returnObject(c);
        }
    }

    @Test
    //Nota: al retornar la coneccion cerrada, el metodo passivateObject se encarga de realizar
    //un rollback que deja a la coneccion lista para utilizarce nuevamente
    public void quePasaCuandoSeCierraUnaConexionAntesDeRetornarla() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo8_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        Connection c = pool.borrowObject();
        c.close();
        pool.returnObject(c);
        c = pool.borrowObject();
        Statement s = c.createStatement();
        s.execute("Select 1");
    }

    @Test
    public void quePasaCuandoSeRetornaUnaconexionContransaccionIniciada() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo8_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        Connection c = pool.borrowObject();
        c.setAutoCommit(false);
        Statement s = c.createStatement();
        s.execute("create table Cliente(nombre text, identificacion integer PRIMARY KEY);");
        s.execute("insert into Cliente values('david', '1')");
        pool.returnObject(c);
        c=pool.borrowObject();
        s.execute("insert into Cliente values('david2', '2')");
    }

    @Test(threadPoolSize = 5)
    public void midaTiemposParaInsertar1000RegistrosConSingleton() throws SQLException, Exception {
        long ti =System.currentTimeMillis();
        Connection sc = SingletonConnection.getConnection();
        Statement s = sc.createStatement();
        for(int cont=0;cont<1000;cont++){
            s.execute("insert into Prueb values('a', '"+cont+"')");
        }
        long tf=System.currentTimeMillis();
        System.out.println(tf-ti);
    }
    
    @Test
    public void datosTablaPrueb(){
        
    }

    //@Test(threadPoolSize = 5, invocationCount = 5)
    @Test(threadPoolSize = 5)
    public void midaTiemposParaInsertar1000RegistrosConObjectPool() throws Exception {
        long ti =System.currentTimeMillis();
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo8_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        Connection c;
        for(int cont=0;cont<1000;){
            c=pool.borrowObject();
            pool.returnObject(c);
        }
        ConnectionHandler ch = new ConnectionHandler(pool);
        ch.ingresarRegistros(1000);
        long tf=System.currentTimeMillis();
        System.out.println(tf-ti);
    }
    
    @Test(expectedExceptions = org.postgresql.util.PSQLException.class)
    public void quePasaCuandoUnaConnecionCreaUnaTablaSinHacerCommitYOtraLaUtilizaHaciendoCommit() throws Exception{
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo8_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        Connection c1 = pool.borrowObject();
        c1.setAutoCommit(false);
        Connection c2 = pool.borrowObject();
        Statement s1 = c1.createStatement();
        s1.execute("create table Cliente(nombre text, identificacion integer PRIMARY KEY);");
        Statement s2 = c2.createStatement();
        s2.execute("insert INTO Cliente Values('x', '1');");    
    }
    
    @Test (expectedExceptions = java.lang.IllegalStateException.class)
    public void quePasaCuandoSeIntentaRegresarUnaConnecionCreadaPorFueraDeLaFabrica() throws Exception{
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo8_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        Connection c1 = DriverManager.getConnection("jdbc:postgresql://aretico.com:5432/software_2", "grupo8_5", pwd);
        pool.returnObject(c1);
    }
}
