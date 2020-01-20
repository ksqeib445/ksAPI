package com.ksqeib.loader;

import com.ksqeib.loader.net.SrvConnect;

import java.util.Hashtable;


public class UpdateConfig {
    public static String host;
    public static int port;

    public static boolean srvConfig(String host, int port, String pluginname) {
        //对参数进行srv解析
        String query = "_" + pluginname + "._tcp." + host;
        Hashtable<String, String> ret = SrvConnect.resoveSrv(query);
        if (ret != null) {
            if (ret.size() != 0) {
                // 参数重设

                UpdateConfig.host = ret.get("host0");
                UpdateConfig.port = Integer.parseInt(ret.get("port0"));

                return true;

            }
        }
        return false;

    }
}
