package com.ksqeib.ksapi.loader.net;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 此类包含了常用的传输方法,主要用于被继承
 * NP(NetPrococol)
 *
 * @author innc-table
 */
public abstract class NP {
    protected DataInputStream netIn;
    protected DataOutputStream netOut;

    public void writeFile(File file) throws IOException {
        //写出文件长度
        try (FileInputStream fileIn = new FileInputStream(file)) {
            //写出文件长度
            netOut.writeLong(file.length());

            byte[] buffer = new byte[1024 * 4];
            int len = 0;
            while ((len = fileIn.read(buffer)) != -1) {
                netOut.write(buffer, 0, len);
            }
        }
    }

    public void readFile(File file, long length) throws IOException {
        file.createNewFile();

        if (length != 0) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buf = new byte[4096];
                int rcount = (int) (length / buf.length);
                int dyv = (int) (length % buf.length);
                int cp = 0;
                for (int c = 0; c < rcount; c++) {
                    netIn.readFully(buf);

                    fos.write(buf, 0, buf.length);
                }

                for (int c = 0; c < dyv; c++) {
                    fos.write(netIn.readByte());
                }
            }
        }
    }

    public boolean Ack(byte[] ack) throws IOException {
        netOut.write(ack);
        byte[] inres = new byte[ack.length];
        netIn.read(inres);
        return Arrays.equals(inres, ack);
    }

    public void writeString(String str) throws IOException {
        //final int max = 1024;

        //这里似乎不增加程度也没影响
        final int max = 10240;

        byte[] bytes = str.getBytes(Charset.forName("UTF-8"));

        //告诉客户端数组的长度
        netOut.writeInt(bytes.length);
        int at = bytes.length / max;

        int bt = bytes.length % max;

        for (int c = 0; c < at; c++) {
            netOut.write(bytes, c * max, max);
        }
        netOut.write(bytes, at * max, bt);
    }

    public String readString() throws IOException {
        //final int maxTransportLength = 1024;

        //这里增加长度 因为有的文件中文名过长就会导致有的中文乱码问题
        final int maxTransportLength = 10240;

        StringBuilder sb = new StringBuilder();

        //接受服务端发来的数组的长度
        int bytesLength = netIn.readInt();

        int at = bytesLength / maxTransportLength;
        int bt = bytesLength % maxTransportLength;

        byte[] buffer = new byte[maxTransportLength];
        for (int c = 0; c < at; c++) {
            netIn.readFully(buffer);
            sb.append(new String(buffer, Charset.forName("UTF-8")));
        }

        netIn.readFully(buffer, 0, bt);

        sb.append(new String(buffer, 0, bt, Charset.forName("UTF-8")));

        return sb.toString();
    }

    public void writeInt(int value) throws IOException {
        netOut.writeInt(value);
    }

    public int readInt() throws IOException {
        return netIn.readInt();
    }

    public void writeBoolean(boolean bool) throws IOException {
        netOut.writeBoolean(bool);
    }

    public boolean readBoolean() throws IOException {
        return netIn.readBoolean();
    }

    public void writeBytes(byte[] bytes) throws IOException {
        //final int maxTransportLength = 1024;

        //这里增加长度 因为有的文件中文名过长就会导致有的中文乱码问题
        final int maxTransportLength = 10240;

        //告诉客户端数组的长度
        netOut.writeInt(bytes.length);

        int at = bytes.length / maxTransportLength;
        int bt = bytes.length % maxTransportLength;

        for (int c = 0; c < at; c++) {
            netOut.write(bytes, c * maxTransportLength, (c + 1) * maxTransportLength);
        }
        netOut.write(bytes, at * maxTransportLength, bt);
    }

    public byte[] readByteArray() throws IOException {
        //final int maxTransportLength = 1024;

        //这里增加长度 因为有的文件中文名过长就会导致有的中文乱码问题
        final int maxTransportLength = 10240;

        //接受服务端发来的数组的长度
        int bytesLength = netIn.readInt();

        int at = bytesLength / maxTransportLength;
        int bt = bytesLength % maxTransportLength;


        ByteArrayOutputStream bout = new ByteArrayOutputStream(bytesLength);

        byte[] buffer = new byte[maxTransportLength];
        for (int c = 0; c < at; c++) {
            netIn.readFully(buffer);
            bout.write(buffer);
        }

        netIn.readFully(buffer, 0, bt);
        bout.write(buffer, 0, bt);

        return bout.toByteArray();
    }
}
