/*
 * Copyright (c) 2018-2020 ksqeib. All rights reserved.
 * @author ksqeib <ksqeib@dalao.ink> <https://github.com/ksqeib445>
 * @create 2020/07/30 15:35:50
 *
 * ksAPI/ksAPI/ConnectionPool.java
 */

package com.ksqeib.ksapi.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class ConnectionPool {
    private HikariDataSource ds;

    /**
     * 连接池构造方法
     *
     * @param config HikariConfig
     */
    public ConnectionPool(HikariConfig config) {
        this.ds = new HikariDataSource(config);
    }

    /**
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

    /**
     * @return 获取一个连接
     */
    public void closePool(){
        ds.close();
    }
}
