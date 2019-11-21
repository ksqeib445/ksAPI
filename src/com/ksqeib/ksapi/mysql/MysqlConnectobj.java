package com.ksqeib.ksapi.mysql;

public class MysqlConnectobj {
    public String url;
    public String passwd;
    public String username;
    @Override
    public boolean equals(Object obj) {
        if(!obj.getClass().equals(this.getClass()))return false;
        MysqlConnectobj e=(MysqlConnectobj)obj;
        if(e.toString().equals(toString()))return true;
        return false;
    }

    public MysqlConnectobj(String url, String passwd, String username) {
        url=url.replace("127.0.0.1","localhost");
        url=url.replace(":3306","");
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
