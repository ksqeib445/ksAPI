package com.ksqeib.ksapi.loader;

import com.ksqeib.ksapi.loader.memory.MConfig;
import com.ksqeib.ksapi.loader.net.Netter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;


public class MainLoader {
    public String host;
    public int port;
    public MConfig mconfig;
    public int stat = 0;

    public MainLoader(MConfig mcs) {
        this.mconfig = mcs;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        new MainLoader(new MConfig("127.0.0.1", 37717, false, "testing", "test")).runtest();
    }

    public void runtest() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        //创建窗口

        //获取host
        host = mconfig.host;
        port = mconfig.port;
        //src更新
        if (mconfig.src) {
            if (UpdateConfig.srvConfig(host, port)) {
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


            URL url = jar.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
            Class<?> mc = classLoader.loadClass(main);

            Object coreObj = mc.newInstance();

            Method coreMain = mc.getDeclaredMethod("starttest", Socket.class, String.class, int.class, MConfig.class);

            coreMain.invoke(coreObj, net.getSocket(), host, port, mconfig);

            classLoader.close();
        }
    }


}
