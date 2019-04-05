package com.ksqeib.ksapi.loader.memory;

public class MConfig {
    public String host;
    public int port;
    public boolean src;
    public String teststr;
    public String pluginname;

    public MConfig(String host, int port, boolean src, String teststr, String pluginname) {
        this.host = host;
        this.port = port;
        this.src = src;
        this.teststr = teststr;
        this.pluginname = pluginname;
    }
}

