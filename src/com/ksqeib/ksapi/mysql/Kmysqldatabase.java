package com.ksqeib.ksapi.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.zaxxer.hikari.HikariConfig;
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

//    public Kmysqldatabase(String address, String dbName, String tablename, String userName, String password, Type type) {
//        this.type = type;
//        this.tablename = tablename;
//        if (pool == null) {
//            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
//            HikariConfig config = new HikariConfig();
//            ds.setURL("jdbc:mysql://" + address + "/" + dbName+ "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false");
//            config.setJdbcUrl("jdbc:mysql://" + address + "/" + dbName+ "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false");
//            ds.setUser(userName);
//            config.setUsername(userName);
//            ds.setPassword(password);
//            config.setPassword(password);
//            ds.setCharacterEncoding("UTF-8");
//            ds.setUseUnicode(true);
//            ds.setAutoReconnectForPools(true);
//            ds.setAutoReconnect(true);
//            ds.setAutoReconnectForConnectionPools(true);
//            config.setDataSource(ds);
//            pool = ConnectionPool.getPool(config);
//        }
//        param=null;
//        usual=true;
//        initusual();
//    }
//    public Kmysqldatabase(String address, String dbName, String tablename, String userName, String password, Type type,Boolean primary) {
//        this.type = type;
//        this.tablename = tablename;
//        if (pool == null) {
//            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
//            HikariConfig config = new HikariConfig();
//            ds.setURL("jdbc:mysql://" + address + "/" + dbName+ "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false");
//            config.setJdbcUrl("jdbc:mysql://" + address + "/" + dbName+ "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false");
//            ds.setUser(userName);
//            config.setUsername(userName);
//            ds.setPassword(password);
//            config.setPassword(password);
//            ds.setCharacterEncoding("UTF-8");
//            ds.setUseUnicode(true);
//            ds.setAutoReconnectForPools(true);
//            ds.setAutoReconnect(true);
//            ds.setAutoReconnectForConnectionPools(true);
//            config.setDataSource(ds);
//            pool = ConnectionPool.getPool(config);
//        }
////        param=null;
//        this.usual=primary;
//        initusual();
//    }

    public Kmysqldatabase(String address, String dbName, String tablename, String userName, String password, Type type, Boolean primary, Type param) {
        this.type = type;
        this.tablename = tablename;
        if (pool == null) {
            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
            HikariConfig config = new HikariConfig();
            ds.setURL("jdbc:mysql://" + address + "/" + dbName + "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false");
            config.setJdbcUrl("jdbc:mysql://" + address + "/" + dbName + "?autoReconnect=true&useUnicode=true&amp&characterEncoding=UTF-8&useSSL=false");
            ds.setUser(userName);
            config.setUsername(userName);
            ds.setPassword(password);
            config.setPassword(password);
            ds.setCharacterEncoding("UTF-8");
            ds.setUseUnicode(true);
            ds.setAutoReconnectForPools(true);
            ds.setAutoReconnect(true);
            ds.setAutoReconnectForConnectionPools(true);
            config.setDataSource(ds);
            pool = new ConnectionPool(config);
        }
//        this.param=param;
        this.usual = primary;
        initusual();
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

    private void initTable(Connection conn) throws SQLException {
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
        PreparedStatement pstmt = conn.prepareStatement(String.format(createString, this.tablename));
        pstmt.executeUpdate();
        pstmt.close();
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

    }

    public void addDuan(String name) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.createConnection();
            pstmt = conn.prepareStatement(String.format("alter table %s ADD COLUMN " + name + " MEDIUMBLOB", this.tablename));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException var15) {
            var15.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var14) {
                var14.printStackTrace();
            }

        }

    }

    public ArrayList<T> loadlist(String key, Boolean loaddelete) {
        Connection conn = null;
        ArrayList<T> out = new ArrayList<>();
        if (!has(key)) return out;
        try {
            conn = this.createConnection();
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
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
                        if (obj == null) continue;
                        Field fi = fitable.get(keys);
                        fi.set(result, obj);
                    }
                }
                out.add((T) result);
            }
            if (loaddelete)
                del(key);
            pstmt.close();
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var18) {
                var18.printStackTrace();
            }

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
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            closeConnection(conn);
        }
        return columnNames;
    }


    public T load(String key, T def) {
        Connection conn = null;
        Object result = def;

        try {
            conn = this.createConnection();
//            LIMIT 1
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
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
                        if (obj == null) continue;
                        Field fi = fitable.get(keys);
                        fi.set(result, obj);

                    }
                }
            }

            pstmt.close();
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var18) {
                var18.printStackTrace();
            }

        }

        return (T) result;
    }

    public T keyload(String key, T def) {
        Connection conn = null;
        Object result = def;

        try {
            conn = this.createConnection();
//            LIMIT 1
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
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
                        Field fi = fitable.get(keys);
                        fi.set(result, obj);

                    }
                }
            }

            pstmt.close();
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var18) {
                var18.printStackTrace();
            }

        }

        return (T) result;
    }

    public Object loadonepart(String key, String part) {
        Connection conn = null;
        Object result = null;

        try {
            conn = this.createConnection();
//            LIMIT 1
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT " + part + " FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            /////
            if (rs.next()) {
                ///////
                InputStream input = rs.getBinaryStream(part);
                if (input != null) {
                    InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    String readed = br.readLine();
                    if (readed == null) return null;
                    result = this.deserialize(readed, table.get(part));

                }
            }

            pstmt.close();
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var18) {
                var18.printStackTrace();
            }

        }

        return (T) result;

    }


    public String createInsert(String key, Set<String> keyset, HashMap<String, ByteArrayInputStream> binys) {
        String insertstring = "INSERT INTO %s(dbkey,";
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
                insertstring += ") ON DUPLICATE KEY UPDATE ";
            }
        }
        int q = 0;
        for (String keys : keyset) {
            //初始化数据列表
            q++;
            insertstring = insertstring + keys + " = VALUES(" + keys + ")";
            if (q != binys.size()) {
                insertstring += ",";
            } else {
                insertstring += "";
            }
        }
        insertstring = String.format(insertstring, this.tablename);
        return insertstring;
    }

    @Override
    public void clearallpart(String partname) {
        Connection conn = null;
        try {
            conn = this.createConnection();
            PreparedStatement pstmt = conn.prepareStatement(String.format("update %s set " + partname + "=null", this.tablename));
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var14) {
                var14.printStackTrace();
            }

        }

    }

    public void delpart(String key, String arg) {
        Connection conn = null;
        try {
            conn = this.createConnection();
//                update 表名 set 字段=null where 字段=某值
            PreparedStatement pstmt = conn.prepareStatement(String.format("update %s set " + arg + "=null WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var14) {
                var14.printStackTrace();
            }

        }

    }

    public void saveone(String key, String arg, Object value) {
        Connection conn = null;

        try {
            conn = this.createConnection();
            if (value != null) {
                if (conn == null) return;
                ByteArrayInputStream biny = new ByteArrayInputStream(this.serialize(value).getBytes(StandardCharsets.UTF_8));
                String insertstring = "INSERT INTO %s(dbkey," + arg + ") VALUES (?,?) ON DUPLICATE KEY UPDATE " + arg + " = VALUES(" + arg + ")";
                PreparedStatement pstmt = conn.prepareStatement(String.format(insertstring, this.tablename));
                pstmt.setString(1, key);
                pstmt.setBinaryStream(2, biny);
                ///////
                pstmt.executeUpdate();
                pstmt.close();
            } else {
                Bukkit.getLogger().warning("Save wrong part " + arg + " at " + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void del(String key) {
        Connection conn = null;
        try {
            conn = this.createConnection();
            PreparedStatement pstmt = conn.prepareStatement(String.format("DELETE FROM %s WHERE dbkey = ?", this.tablename));
            pstmt.setString(1, key);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var14) {
                var14.printStackTrace();
            }

        }

    }

    public void save(String key, T value) {
        Connection conn = null;

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

                //变量名列表+二进制流列表
                Set<String> keyset = binys.keySet();
//                Bukkit.getLogger().warning(createInsert(key,keyset,binys));

                PreparedStatement pstmt = conn.prepareStatement(createInsert(key, keyset, binys));
                pstmt.setString(1, key);
                int c = 2;
//                循环放入二进制
                for (String keys : keyset) {
                    pstmt.setBinaryStream(c, binys.get(keys));
                    c++;
                }
                ///////
                pstmt.executeUpdate();
                pstmt.close();
            } else {
                Bukkit.getLogger().warning("Save wrong data " + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var14) {
                var14.printStackTrace();
            }

        }

    }

    public Set<String> getKeys() {
        Set<String> keys = new HashSet();
        Connection conn = null;

        try {
            conn = this.createConnection();
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT dbkey FROM %s", this.tablename));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                keys.add(rs.getString("dbkey"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var12) {
                var12.printStackTrace();
            }

        }

        return keys;
    }

    public boolean has(String key) {
        boolean result = false;
        Connection conn = null;

        try {
            conn = this.createConnection();
//            LIMIT 1
            PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT dbkey FROM %s WHERE dbkey = ? LIMIT 1", this.tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            result = rs.next();
            rs.close();
            pstmt.close();
            boolean var7 = result;
            return var7;
        } catch (SQLException var15) {
            var15.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var14) {
                var14.printStackTrace();
            }

        }

        return result;
    }

    public Object getsthbysth(String by, String type, Object sign, Class objtype) {
        Connection con = null;
        Object ret = null;
        PreparedStatement ps = null;
        if (sign == null) return sign;
        try {
            con = createConnection();
            String s;
            s = "SELECT " + type + " FROM " + tablename + " WHERE " + by + " = ?";
            ByteArrayInputStream biny = new ByteArrayInputStream(this.serialize(sign).getBytes(StandardCharsets.UTF_8));
            ps = con.prepareStatement(s);
            ps.setBinaryStream(1, biny);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                InputStream input = rs.getBinaryStream(1);
                if (input != null) {
                    InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    ret = this.deserialize(br.readLine(), objtype);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return ret;
    }
}
