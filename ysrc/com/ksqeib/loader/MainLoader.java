package com.ksqeib.loader;

import com.ksqeib.ksapi.KsAPI;
import com.ksqeib.loader.memory.MConfig;
import com.ksqeib.loader.net.Netter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URLClassLoader;
import java.util.List;


public class MainLoader {
    public String host;
    public int port;
    public MConfig mconfig;
    public int stat = 0;

    public MainLoader(MConfig mcs) {
        this.mconfig = mcs;
    }
//
//    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
//        new MainLoader(new MConfig("yz1.ksmc.fun", 30010,true, "kingdomsplus", "kingdomsplus")).runtest();
//    }

    public void runtest() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        //创建窗口

        //获取host
        host = mconfig.host;
        port = mconfig.port;
        //src更新
        if (mconfig.src) {
            if (UpdateConfig.srvConfig(host, port,mconfig.teststr)) {
                host = UpdateConfig.host;
                port = UpdateConfig.port;
            }
        }
        //包括握手验证和协议验证
        stat = 1;
        Netter net = new Netter(this, host, port);
        if (net.start()) {


            stat = 20;
            File jar = net.getJarFile();
            String main = net.getMainClass();
            try {
                Bukkit.getPluginManager().loadPlugin(jar);
            }catch (Exception e){
                e.printStackTrace();
            }
            Class<?> mc = Class.forName(main);
            Object coreObj = mc.newInstance();
            Method coreMain = mc.getDeclaredMethod("startserver", Socket.class);

            coreMain.invoke(coreObj, net.getSocket());

        }else {
            throw new IOException("无法链接到验证服务器"+stat);
        }
    }


}
