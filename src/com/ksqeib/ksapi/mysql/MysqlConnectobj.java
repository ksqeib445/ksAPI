package com.ksqeib.ksapi.mysql;

import org.bukkit.Bukkit;

public class MysqlConnectobj {
    String url;
    String passwd;
    String username;
    @Override
    public boolean equals(Object obj) {
        if(!obj.getClass().equals(this.getClass()))return false;
        MysqlConnectobj e=(MysqlConnectobj)obj;
        if(e.toString().equals(toString()))return true;
        return false;
    }

    public MysqlConnectobj(String url, String passwd, String username) {
        this.url = url;
        this.passwd = passwd;
        this.username = username;
    }

    @Override
    public String toString() {
        return url+" "+username+" "+passwd;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
