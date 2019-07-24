package com.ksqeib.ksapi.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

/**
 * 连接池，以前是龙猫写的，现在就是一个套了HikariPool的类
 */
public class ConnectionPool {
    private HikariDataSource ds;

    /**
     * 连接池构造方法
     * @param config HikariConfig
     */
    public ConnectionPool(HikariConfig config) {
        this.ds = new HikariDataSource(config);
    }

    /**
     *
     * @return 获取一个连接
     */
    public Connection getConnection() {
        try {
            return this.ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
