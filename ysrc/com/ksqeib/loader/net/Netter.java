package com.ksqeib.loader.net;

import com.ksqeib.loader.MainLoader;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.UUID;

public class Netter extends NP {
    private final static byte[] PROTOCOL_HEAD_ACK = {0x23, 0x04, 0x01, 0x34, 0x51, 0x33, 0x35, 0x18};

    public static final int NET_PROTOCOL_VERSION = 1;

    private String host;
    private int port;
    private Socket socket;

    private File jarFile;
    private String mainClass;

    private MainLoader ml;


    public Netter(MainLoader ml, String host, int port) {
        this.ml = ml;
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host + ":" + port;
    }

    public File getJarFile() {
        return jarFile;
    }

    public String getMainClass() {
        return mainClass;
    }


    public boolean start() throws IOException {
        //设置状态提示文本

        //发起连接
        ml.stat = 2;
        socket=new Socket();

        SocketAddress addr = new InetSocketAddress(host, port);
        try {
            socket.connect(addr,400);
        } catch (IOException e) {
            //失败文本
//            Bukkit.getLogger().warning("验证服务器连接失败！");
            return false;
        }

        //设置IO超时
        ml.stat = 3;
        socket.setSoTimeout(40000);


        //打开IO流
        ml.stat = 4;
        netIn = new DataInputStream(socket.getInputStream());
        netOut = new DataOutputStream(socket.getOutputStream());


        //测试协议
        if (!Ack(PROTOCOL_HEAD_ACK)) {
            ml.stat = 3;
//            System.("协议测试未通过，请检查端口是否被占用或者设置正确！" + "协议错误");
            return false;
        }

        //告诉服务端客户端的协议版本
        ml.stat = 4;
        writeInt(NET_PROTOCOL_VERSION);

        //如果协议版本服务端无法处理
        ml.stat = 5;
        if (!readBoolean()) {
            //读取服务端使用的协议版本
            String serverSNPVer = readString();
            Bukkit.getLogger().warning("服务端版本不支持，当前的客户端版本为 " + NET_PROTOCOL_VERSION + "\n服务端版本为" + serverSNPVer);
            return false;
        }

        ml.stat = 5;
        writeString(ml.mconfig.teststr);
        mainClass = readString();

        //接收文件长度
        ml.stat = 6;
        long fileLength = netIn.readLong();
        jarFile = File.createTempFile(UUID.randomUUID().toString(), ".jar");

        jarFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(jarFile);

        byte[] buf = new byte[4096];
        int ac = (int) (fileLength / buf.length);
        int bc = (int) (fileLength % buf.length);
        for (int c = 0; c < ac; c++) {
            netIn.readFully(buf);
            fos.write(buf, 0, buf.length);
        }

        for (int c = 0; c < bc; c++) {
            fos.write(netIn.readByte());
        }
        fos.close();
        ml.stat = 7;
        return true;
    }


    public Socket getSocket() {
        return socket;
    }

}
