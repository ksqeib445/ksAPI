package com.ksqeib.ksapi.mysql;

import org.bukkit.Bukkit;

import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Sqlitedatabase<T> extends KDatabase<T> {
    private final Type type;
    private final String url;
    private Boolean usual = true;
    private Connection conn;

    private String dbFilePath;

    public Sqlitedatabase(File datei, Type type, String tablename) {
        this.type = type;
//        try {
//            if(!datei.exists())datei.createNewFile();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        dbFilePath = datei.getAbsolutePath();
        url = "jdbc:sqlite:" + dbFilePath;
//        System.out.println(dbFilePath);
        this.tablename = tablename;
//        System.out.println("startcreate");
        initusual();
    }

    private void initDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ex) {
            System.out.println("数据库驱动程序错误:" + ex.getMessage());
        }
    }

    public void initusual() {
        try {
            initDriver();
            conn=createConnection();
            initTables(Class.forName(type.getTypeName()));
//            System.out.println("initedtable");
            this.initTable(conn);
            checkDuan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTable(Connection conn) throws SQLException {
        String createString = "CREATE TABLE IF NOT EXISTS %s(dbkey VARCHAR(128) PRIMARY KEY,";
        if (!usual) {
            createString = "CREATE TABLE IF NOT EXISTS %s(dbkey VARCHAR(128),";
        }
        int i = 0;
        for (String name : table.keySet()) {
            //初始化数据列表
            i++;
            createString = createString + name + " MEDIUMBLOB";
            if (i != table.size()) {
                createString += ",";
            } else {
                createString += ")";
            }
        }
        createString = String.format(createString, this.tablename);
//        System.out.println(createString);
        Statement pstmt = conn.createStatement();
        pstmt.execute(createString);
        pstmt.close();
    }

    @Override
    public Object getsthbysth(String by, String type, Object sign, Class objtype) {
        return null;
    }

    @Override
    public void saveone(String key, String arg, Object value) {
        try {
            if (value != null) {
                if (conn == null) return;
                ByteArrayInputStream biny = new ByteArrayInputStream(this.serialize(value).getBytes(StandardCharsets.UTF_8));
//                String insertstring = "INSERT INTO %s(dbkey," + arg + ") VALUES (?,?)";if(update)
                String insertstring="UPDATE %s SET " + arg + "= ? WHERE dbkey = ?";
                PreparedStatement pstmt = conn.prepareStatement(String.format(insertstring, this.tablename));
//                if(!update) {
//                    pstmt.setString(1, key);
//                    pstmt.setString(2, byteToStr(biny));
//                }else {
                    pstmt.setString(2, key);
                    pstmt.setString(1, byteToStr(biny));
//                }
                ///////
                pstmt.executeUpdate();
                pstmt.close();
            } else {
                Bukkit.getLogger().warning("Save wrong part " + arg + " at " + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delpart(String key, String arg) {
        try {
//                update 表名 set 字段=null where 字段=某值
            PreparedStatement pstmt = conn.prepareStatement(String.format("update %s set "+arg+"=null WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            pstmt.executeUpdate();
            pstmt.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Object loadonepart(String key, String part) {
        Object result = null;

        try {
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT "+part+" FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            /////
            if (rs.next()) {
                ///////
                InputStream input = rs.getBinaryStream(part);
                if (input != null) {
                    InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    String readed=br.readLine();
                    if(readed==null)return null;
                    result = this.deserialize(readed, table.get(part));

                }
            }

            pstmt.close();
        } catch (Exception var19) {
            var19.printStackTrace();
        }

        return (T) result;
    }

    @Override
    public T keyload(String key, T def) {
        Object result = def;

        try {
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            /////
            if (rs.next()) {
                Class cl = Class.forName(type.getTypeName());
//                Class pa = Class.forName(param.getTypeName());
                Method me = cl.getDeclaredMethod("fromkeyserizable", String.class);
                result = me.invoke(cl, key);
                ///////
                for (String keys : table.keySet()) {
                    //LOAD
                    InputStream input = rs.getBinaryStream(keys);
                    if (input != null) {
                        InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                        BufferedReader br = new BufferedReader(isr);
                        Object obj;
                        obj = this.deserialize(br.readLine(), table.get(keys));
                        if (obj == null) continue;
                        Field fi = getFielddeep(keys, cl, 0);
                        fi.setAccessible(true);
                        fi.set(result, obj);

                    }
                }
            }

            pstmt.close();
        } catch (Exception var19) {
            var19.printStackTrace();
        }

        return (T) result;
    }

    @Override
    public List<String> getColumnNames() {
        return null;
    }

    @Override
    public ArrayList<T> loadlist(String key, Boolean loaddelete) {
        return null;
    }

    @Override
    public void checkDuan() {

    }

    @Override
    public void addDuan(String name) {

    }

    @Override
    public Connection createConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    @Override
    public T load(String key, T def) {
        return null;
    }

    public String createInsert(String key, Set<String> keyset, HashMap<String, ByteArrayInputStream> binys) {
        String insertstring;
        if(has(key)){
            insertstring = "UPDATE %s SET ";
            int temp = 0;
            for (String keys : keyset) {
                //初始化数据列表
                insertstring = insertstring + keys;
                temp++;
                if (temp != binys.size()) {
                    insertstring += "= ?,";
                } else {
                    insertstring += "= ? ";
                }
            }
            insertstring+="WHERE dbkey=?";
        }else {
            insertstring = "INSERT INTO %s(dbkey,";
            int temp = 0;
            for (String keys : keyset) {
                //初始化数据列表
                insertstring = insertstring + keys;
                temp++;
                if (temp != binys.size()) {
                    insertstring += ",";
                } else {
                    insertstring += ") VALUES (?,";
                }
            }
            for (int i = 0; i < keyset.size(); i++) {
                //初始化数据列表
                insertstring = insertstring + "?";
                if (i != binys.size() - 1) {
                    insertstring += ",";
                } else {
                    insertstring += ")";
                }
            }
        }

        insertstring = String.format(insertstring, this.tablename);
//        System.out.println(insertstring);
        return insertstring;
    }

    @Override
    public void save(String key, T value) {
        try {
            if (value != null) {
                //变量名列表+Json列表
                HashMap<String, String> al = new HashMap<>();
                for (String keys : table.keySet()) {
                    //Load
                    Field fi = getFielddeep(keys, value, 0);
                    if (fi != null) {
                        fi.setAccessible(true);
                        Object obj = fi.get(value);
                        if (obj != null) {
                            al.put(fi.getName(), this.serialize(obj));
                        }
                    }

                }
//                转换成二进制列表
                HashMap<String, ByteArrayInputStream> binys = new HashMap<>();
                for (String ser : al.keySet()) {
                    binys.put(ser, new ByteArrayInputStream(al.get(ser).getBytes(StandardCharsets.UTF_8)));
                }
                //ADD!
                if(binys.size()==0)return;
                //变量名列表+二进制流列表
                Set<String> keyset = binys.keySet();
//                Bukkit.getLogger().warning(createInsert(key,keyset,binys));
                String insert=createInsert(key, keyset, binys);
                boolean update=has(key);
                PreparedStatement pstmt = conn.prepareStatement(insert);
                if(!update)
                pstmt.setString(1, key);
                int c = 2;
                if(update)c=1;
//                循环放入二进制
                for (String keys : keyset) {
                    pstmt.setString(c, byteToStr(binys.get(keys)));
                    c++;
                }
                if(update)pstmt.setString(c, key);
                ///////
                pstmt.executeUpdate();
                pstmt.close();
            } else {
                Bukkit.getLogger().warning("Save wrong data " + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void del(String key) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(String.format("DELETE FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            pstmt.executeUpdate();
            pstmt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public boolean haspart(String key,String name) {
//
//        boolean result = false;
//        Statement pstmt=null;
//        ResultSet rs=null;
//        try {
//            pstmt = conn.createStatement();
//            rs = pstmt.executeQuery("SELECT "+name+" FROM "+this.tablename+" WHERE dbkey = " +"\""+key+"\"");
//            if(rs.isClosed())return true;
//            rs.next();
//            result=rs.getString(1)!=null;
//            return result;
//        } catch (SQLException var15) {
//            var15.printStackTrace();
//        } finally {
//            try {
//                if(rs!=null)
//                rs.close();
//                if(pstmt!=null)
//                pstmt.close();
//            } catch (SQLException var14) {
//                var14.printStackTrace();
//            }
//
//        }
//
//        return result;
//    }

    @Override
    public boolean has(String key) {

        boolean result = false;

        try {
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT dbkey FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            result = rs.next();
            rs.close();
            pstmt.close();
            boolean var7 = result;
            return var7;
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

        return result;
    }

    @Override
    public Set<String> getKeys() {

        Set<String> keys = new HashSet();

        try {
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT dbkey FROM %s", this.tablename));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                keys.add(rs.getString("dbkey"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException var13) {
            var13.printStackTrace();
        }

        return keys;
    }

}
