package com.ksqeib.ksapi.mysql;

import com.ksqeib.ksapi.manager.MysqlPoolManager;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class Kmysqldatabase<T> extends KDatabase<T> {
    private final Type type;
    private ConnectionPool pool = null;
    private Boolean usual = true;


    public Kmysqldatabase(String address, String dbName, String tablename, String userName, String password, Type type, Boolean primary, Type param) {
        this.type = type;
        this.tablename = tablename;
        if (pool == null) {
            String url = "jdbc:mysql://" + address + "/" + dbName + "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false";
            MysqlConnectobj mysqlConnectobj = new MysqlConnectobj(url, password, userName);
            pool = MysqlPoolManager.getPool(mysqlConnectobj);
        }
//        this.param=param;
        this.usual = primary;
        initusual();
    }

    public Type getType() {
        return type;
    }

    public String getTablename() {
        return tablename;
    }

    public ConnectionPool getPool() {
        return pool;
    }

    public Boolean getUsual() {
        return usual;
    }

    public HashMap<String, Type> getTable() {
        return table;
    }

    public void initusual() {
        Connection conn = this.createConnection();
        try {
            initTables(Class.forName(type.getTypeName()));
            this.initTable(conn);
            checkDuan();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
    }

    public Connection createConnection() {
        return this.pool.getConnection();
    }

    private void initTable(Connection conn) {
        String createString = "CREATE TABLE IF NOT EXISTS %s (dbkey CHAR(128) PRIMARY KEY,";
        if (!usual) {
            createString = "CREATE TABLE IF NOT EXISTS %s (dbkey CHAR(128),";
        }
        int i = 0;
        for (String name : table.keySet()) {
            //初始化数据列表
            i++;
            createString = createString + name + " MEDIUMBLOB";
            if (i != table.size()) {
                createString += ",";
            } else {
                createString += ") ENGINE=InnoDB   DEFAULT   CHARSET=utf8;";
            }
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(String.format(createString, this.tablename));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
        }
    }

    public void checkDuan() {
        List<String> duans = getColumnNames();
        ArrayList<String> needadd = new ArrayList<>();
        for (String name : table.keySet()) {
            if (!duans.contains(name)) {
                needadd.add(name);
            }
        }
        for (String name : needadd) {
            addDuan(name);
        }
//        for (String name : table.keySet()) {
//            duans.remove(name);
//        }
//        duans.remove("dbkey");
//        for (String willdel : duans) {
//            delDuan(willdel);
//        }

    }

    public void delDuan(String name) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            pstmt = conn.prepareStatement(String.format("alter table %s drop COLUMN " + name, this.tablename));
            pstmt.executeUpdate();
        } catch (SQLException var15) {
            var15.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }
    }

    public void addDuan(String name) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            pstmt = conn.prepareStatement(String.format("alter table %s ADD COLUMN " + name + " MEDIUMBLOB", this.tablename));
            pstmt.executeUpdate();
        } catch (SQLException var15) {
            var15.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

    }

    public ArrayList<T> loadlist(String key, Boolean loaddelete) {
        Connection conn = null;
        ArrayList<T> out = new ArrayList<>();
        if (!has(key)) return out;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            pstmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            rs = pstmt.executeQuery();
            /////
            Object result = null;
            while (rs.next()) {
                Class cl = Class.forName(type.getTypeName());
                if (cl.getConstructors().length > 0) {
                    //new Instance
                    for (Constructor c : cl.getDeclaredConstructors())
                        if (Modifier.isPrivate(c.getModifiers())) {
                            c.setAccessible(true);
                            result = c.newInstance();
                        }
                }
                if (result == null) continue;
                ///////
                for (String keys : table.keySet()) {
                    //LOAD
                    InputStream input = rs.getBinaryStream(keys);
                    if (input != null) {
                        InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                        BufferedReader br = new BufferedReader(isr);
                        Object obj;
                        obj = this.deserialize(br.readLine(), table.get(keys));
                        closeBufferedReader(br);
                        closeInputStreamReader(isr);
                        if (obj == null) continue;
                        Field fi = fitable.get(keys);
                        fi.set(result, obj);
                    }
                    closeInputStream(input);
                }
                out.add((T) result);
            }
            if (loaddelete)
                del(key);
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

        return out;
    }

    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = createConnection();
        PreparedStatement pStemt = null;
        String tableSql = "SELECT * FROM " + tablename + " LIMIT 1";
        try {

            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pStemt);
            closeConnection(conn);
        }
        return columnNames;
    }


    public T load(String key, T def) {
        Connection conn = null;
        Object result = def;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = this.createConnection();
//            LIMIT 1
            pstmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
            pstmt.setString(1, key);
            rs = pstmt.executeQuery();
            /////
            if (rs.next()) {
                Class cl = Class.forName(type.getTypeName());
                if (cl.getConstructors().length > 0) {
                    //new Instance
                    for (Constructor c : cl.getDeclaredConstructors()) {
                        if (c.getParameterCount() != 0) continue;
                        if (Modifier.isPrivate(c.getModifiers())) {
                            c.setAccessible(true);
                            result = c.newInstance();
                        } else if (Modifier.isPublic(c.getModifiers())) {
                            result = c.newInstance();
                        }
                    }
                }
                ///////
                for (String keys : table.keySet()) {
                    //LOAD
                    InputStream input = rs.getBinaryStream(keys);
                    if (input != null) {
                        InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                        BufferedReader br = new BufferedReader(isr);
                        Object obj;
                        obj = this.deserialize(br.readLine(), table.get(keys));
                        closeBufferedReader(br);
                        closeInputStreamReader(isr);
                        if (obj == null) continue;
                        Field fi = fitable.get(keys);
                        fi.set(result, obj);
                    }
                    closeInputStream(input);
                }
            }
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

        return (T) result;
    }

    public T keyload(String key, T def) {
        Connection conn = null;
        Object result = def;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = this.createConnection();
//            LIMIT 1
            pstmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
            pstmt.setString(1, key);
            rs = pstmt.executeQuery();
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
                        closeBufferedReader(br);
                        closeInputStreamReader(isr);
                        if (obj == null) continue;
                        Field fi = fitable.get(keys);
                        fi.set(result, obj);
                    }
                    closeInputStream(input);
                }
            }
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

        return (T) result;
    }

    public Object loadonepart(String key, String part) {
        Connection conn = null;
        Object result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = this.createConnection();
//            LIMIT 1
            pstmt = conn.prepareStatement(String.format("SELECT " + part + " FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
            pstmt.setString(1, key);
            rs = pstmt.executeQuery();
            /////
            if (rs.next()) {
                ///////
                InputStream input = rs.getBinaryStream(part);
                if (input != null) {
                    InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    String readed = br.readLine();
                    closeBufferedReader(br);
                    closeInputStreamReader(isr);
                    if (readed == null) return null;
                    result = this.deserialize(readed, table.get(part));
                }
                closeInputStream(input);
            }
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

        return result;

    }


    public String createInsert(String key, Set<String> keyset, HashMap<String, ByteArrayInputStream> binys) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO %s(dbkey,");
        int temp = 0;
        for (String keys : keyset) {
            //初始化数据列表
            stringBuilder.append(keys);
            temp++;
            if (temp != binys.size()) {
                stringBuilder.append(",");
            } else {
                stringBuilder.append(") VALUES (?,");
            }
        }
        for (int i = 0; i < keyset.size(); i++) {
            //初始化数据列表
            stringBuilder.append("?");
            if (i != binys.size() - 1) {
                stringBuilder.append(",");
            } else {
                stringBuilder.append(") ON DUPLICATE KEY UPDATE ");
            }
        }
        int q = 0;
        for (String keys : keyset) {
            //初始化数据列表
            q++;
            stringBuilder.append(keys);
            stringBuilder.append(" = VALUES(");
            stringBuilder.append(keys);
            stringBuilder.append(")");
            if (q != binys.size()) {
                stringBuilder.append(",");
            }
        }
        return String.format(stringBuilder.toString(), this.tablename);
    }

    @Override
    public void clearallpart(String partname) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            pstmt = conn.prepareStatement(String.format("update %s set " + partname + "=null", this.tablename));
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

    }

    public void delpart(String key, String arg) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
//                update 表名 set 字段=null where 字段=某值
            pstmt = conn.prepareStatement(String.format("update %s set " + arg + "=null WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

    }

    public void saveone(String key, String arg, Object value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            if (value != null) {
                if (conn == null) return;
                ByteArrayInputStream biny = new ByteArrayInputStream(this.serialize(value).getBytes(StandardCharsets.UTF_8));
                String insertstring = "INSERT INTO %s(dbkey," + arg + ") VALUES (?,?) ON DUPLICATE KEY UPDATE " + arg + " = VALUES(" + arg + ")";
                pstmt = conn.prepareStatement(String.format(insertstring, this.tablename));
                pstmt.setString(1, key);
                pstmt.setBinaryStream(2, biny);
                ///////
                pstmt.executeUpdate();
            } else {
                Bukkit.getLogger().warning("Save wrong part " + arg + " at " + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }
    }

    public void del(String key) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
//            new IOException("删除"+System.currentTimeMillis()).printStackTrace();

            pstmt = conn.prepareStatement(String.format("DELETE FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

    }

    public void save(String key, T value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            if (value != null) {
                //变量名列表+Json列表
                HashMap<String, String> al = new HashMap<>();
                for (String keys : table.keySet()) {
                    //Load
                    Field fi = fitable.get(keys);
                    if (fi != null) {
                        Object obj = fi.get(value);
                        if (obj != null) {
                            al.put(getKey(fi), this.serialize(obj));
                        }
                    }

                }
//                转换成二进制列表
                HashMap<String, ByteArrayInputStream> binys = new HashMap<>();
                for (String ser : al.keySet()) {
                    binys.put(ser, new ByteArrayInputStream(al.get(ser).getBytes(StandardCharsets.UTF_8)));
                }
                //ADD!

                //变量名列表+二进制流列表
                Set<String> keyset = binys.keySet();
//                Bukkit.getLogger().warning(createInsert(key,keyset,binys));

                pstmt = conn.prepareStatement(createInsert(key, keyset, binys));
                pstmt.setString(1, key);
                int c = 2;
//                循环放入二进制
                for (String keys : keyset) {
                    pstmt.setBinaryStream(c, binys.get(keys));
                    c++;
                }
                ///////
                pstmt.executeUpdate();
            } else {
                Bukkit.getLogger().warning("Save wrong data " + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

    }

    public Set<String> getKeys() {
        Set<String> keys = new HashSet();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = this.createConnection();
            pstmt = conn.prepareStatement(String.format("SELECT dbkey FROM %s", this.tablename));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                keys.add(rs.getString("dbkey"));
            }

        } catch (SQLException var13) {
            var13.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

        return keys;
    }

    public boolean has(String key) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = this.createConnection();
//            LIMIT 1
            pstmt = conn.prepareStatement(String.format("SELECT dbkey FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
            pstmt.setString(1, key);
            rs = pstmt.executeQuery();
            result = rs.next();
        } catch (SQLException var15) {
            var15.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

        return result;
    }

    @Override
    public String getDbkeyBySth(String by, Object sign) {
        Connection con = null;
        String ret =null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (sign == null) return null;
        try {
            con = createConnection();
            ByteArrayInputStream biny = new ByteArrayInputStream(this.serialize(sign).getBytes(StandardCharsets.UTF_8));
            ps = con.prepareStatement("SELECT dbkey FROM " + tablename + " WHERE " + by + " = ? LIMIT 1");
            ps.setBinaryStream(1, biny);
            rs = ps.executeQuery();

            while (rs.next()) {
                InputStream input = rs.getBinaryStream(1);
                if (input != null) {
                    InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    ret=br.readLine();
                    closeBufferedReader(br);
                    closeInputStreamReader(isr);
                }
                closeInputStream(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(ps);
            closeConnection(con);
        }
        return ret;
    }

    public List<?> getsthbysth(String by, String type, Object sign, Class objtype) {
        Connection con = null;
        ArrayList ret = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (sign == null) return null;
        try {
            con = createConnection();
            ByteArrayInputStream biny = new ByteArrayInputStream(this.serialize(sign).getBytes(StandardCharsets.UTF_8));
            ps = con.prepareStatement("SELECT " + type + " FROM " + tablename + " WHERE " + by + " = ?");
            ps.setBinaryStream(1, biny);
            rs = ps.executeQuery();

            while (rs.next()) {
                InputStream input = rs.getBinaryStream(1);
                if (input != null) {
                    InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    ret.add(this.deserialize(br.readLine(), objtype));
                    closeBufferedReader(br);
                    closeInputStreamReader(isr);
                }
                closeInputStream(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(ps);
            closeConnection(con);
        }
        return ret;
    }

    @Override
    public void clearDatabase() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            pstmt = conn.prepareStatement(String.format("delete from %s", this.tablename));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pstmt);
            closeConnection(conn);
        }

    }
}
