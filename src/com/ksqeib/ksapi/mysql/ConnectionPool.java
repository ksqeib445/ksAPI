package com.ksqeib.ksapi.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class ConnectionPool {
    private HikariDataSource ds;

    public static ConnectionPool getPool(HikariConfig config) {
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new ConnectionPool(config);
    }

    public ConnectionPool(HikariConfig config) {
        this.ds = new HikariDataSource(config);
    }
    public Connection getConnection(){
        try {
            return this.ds.getConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
