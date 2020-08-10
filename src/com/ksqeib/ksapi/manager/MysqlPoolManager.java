package com.ksqeib.ksapi.manager;

import com.ksqeib.ksapi.mysql.ConnectionPool;
import com.ksqeib.ksapi.mysql.MysqlConnectobj;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.zaxxer.hikari.HikariConfig;

import java.util.HashMap;
import java.util.Map;

public class MysqlPoolManager {
    private static HashMap<MysqlConnectobj, ConnectionPool> connectionPools = new HashMap<>();

    /**
     * 获取一个数据库连接池
     * @param mysqlConnectobj 数据库连接对象
     * @return
     */
    public static ConnectionPool getPool(MysqlConnectobj mysqlConnectobj) {
        if (connectionPools.containsKey(mysqlConnectobj)) {
            return connectionPools.get(mysqlConnectobj);
        } else {
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
            config.setMaximumPoolSize(20);
            ConnectionPool pool = new ConnectionPool(config);
            connectionPools.put(mysqlConnectobj, pool);
//            Bukkit.getLogger().warning(mysqlConnectobj.toString());
            return pool;
        }
    }

    /**
     * 获取连接池列表 最好不要修改 可能会造成不可预料的后果
     * @return
     */
    public static Map<MysqlConnectobj,ConnectionPool> getConnectionPools(){
        return connectionPools;
    }

    /**
     * 使用常规方法获取
     * @param address 地址 如localhost:3306
     * @param dbName 数据库名
     * @param userName 用户
     * @param password 密码
     * @return
     */
    public static ConnectionPool getPool(String address, String dbName, String userName, String password) {

        String url = "jdbc:mysql://" + address + "/" + dbName + "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false";
        MysqlConnectobj mysqlConnectobj = new MysqlConnectobj(url, password, userName);
        return getPool(mysqlConnectobj);
    }

    public static void onDisable(){
        connectionPools.values().forEach(ConnectionPool::closePool);
    }
}
