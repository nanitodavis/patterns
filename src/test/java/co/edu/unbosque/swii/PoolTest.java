/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.Statement;
import org.apache.commons.pool2.BaseObjectPool;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
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
        for (int i = 0; i < 3; i++) {
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

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConSingleton() {

    }

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConObjectPool() {

    }
}
