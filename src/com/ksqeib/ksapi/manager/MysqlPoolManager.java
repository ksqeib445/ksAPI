package com.ksqeib.ksapi.manager;

import com.ksqeib.ksapi.mysql.ConnectionPool;
import com.ksqeib.ksapi.mysql.MysqlConnectobj;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class MysqlPoolManager {
    private static HashMap<MysqlConnectobj,ConnectionPool> connectionPools=new HashMap<>();

    public static ConnectionPool getPool(MysqlConnectobj mysqlConnectobj){
        if(connectionPools.containsKey(mysqlConnectobj)){
            return connectionPools.get(mysqlConnectobj);
        }else {
            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
            HikariConfig config = new HikariConfig();
            ds.setURL(mysqlConnectobj.url);
            config.setJdbcUrl(mysqlConnectobj.url);
            ds.setUser(mysqlConnectobj.username);
            config.setUsername(mysqlConnectobj.username);
            ds.setPassword(mysqlConnectobj.passwd);
            config.setPassword(mysqlConnectobj.passwd);
            ds.setCharacterEncoding("UTF-8");
            ds.setUseUnicode(true);
            ds.setAutoReconnectForPools(true);
            ds.setAutoReconnect(true);
            ds.setAutoReconnectForConnectionPools(true);
            config.setDataSource(ds);
            config.setMaximumPoolSize(Runtime.getRuntime().availableProcessors()*2<config.getMinimumIdle()?config.getMinimumIdle():Runtime
            .getRuntime().availableProcessors()*2);
            ConnectionPool pool = new ConnectionPool(config);
            connectionPools.put(mysqlConnectobj,pool);
//            Bukkit.getLogger().warning(mysqlConnectobj.toString());
            return pool;
        }
    }
    public static ConnectionPool getPool(String address, String dbName, String userName, String password){

        String url="jdbc:mysql://" + address + "/" + dbName + "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false";
        MysqlConnectobj mysqlConnectobj=new MysqlConnectobj(url,password,userName);
        return getPool(mysqlConnectobj);
    }
}
