/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.ksqeib.ksapi.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ksqeib.ksapi.mysql.serializer.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.ksqeib.ksapi.KsAPI.um;

/**
 * 数据库爹类(雾)
 *
 * @param <T> 储存的东西的反省
 */
public abstract class KDatabase<T> {

    public final static GsonBuilder builder = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC).enableComplexMapKeySerialization()
            .registerTypeAdapter(Location.class, new LocationSerializer())
            .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
            .registerTypeAdapter(ItemStack[].class, new ItemStackArraySerializer())
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .registerTypeAdapter(UUIDListSerializer.class, new UUIDListSerializer());
    protected HashMap<String, Type> table = new HashMap<>();
    protected HashMap<String, Field> fitable = new HashMap<>();
    protected String tablename;
    private Gson gson;

    /**
     * 注册序列化专家
     *
     * @param clazz 要序列化的类
     * @param obj   序列化用的类
     */
    public static void registerTypeAdapter(Class<?> clazz, Object obj) {
        synchronized (builder) {
            builder.registerTypeAdapter(clazz, obj);
        }
    }

    /**
     * 将字节变成String
     *
     * @param biny 字节
     * @return String
     */
    public static String byteToStr(ByteArrayInputStream biny) {
        StringBuffer out = new StringBuffer();
        try {
            byte[] b = new byte[4096];
            for (int n; (n = biny.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    /**
     * 关闭连接
     *
     * @param conn 连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                um.getTip().send("数据库连接关闭失败", Bukkit.getConsoleSender(), null);
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                um.getTip().send("preparedStatement关闭失败", Bukkit.getConsoleSender(), null);
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
                um.getTip().send("ResultSet关闭失败", Bukkit.getConsoleSender(), null);
            }
        }
    }

    public static void closeInputStreamReader(InputStreamReader inputStreamReader) {
        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                um.getTip().send("InputStreamReader关闭失败", Bukkit.getConsoleSender(), null);
            }
        }
    }

    public static void closeBufferedReader(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                um.getTip().send("BufferedReader关闭失败", Bukkit.getConsoleSender(), null);
            }
        }
    }

    public static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                um.getTip().send("inputStream关闭失败", Bukkit.getConsoleSender(), null);
            }
        }
    }

    /**
     * 初始化表
     *
     * @param cl 储存用的类
     */
    protected void initTables(Class cl) {
        for (Field fi : cl.getDeclaredFields()) {
            if (Modifier.isTransient((fi.getModifiers()))) continue;
            if (Modifier.isStatic(fi.getModifiers())) continue;
            if (Modifier.isFinal(fi.getModifiers())) continue;
            KSeri zj = fi.getAnnotation(KSeri.class);
            if (zj == null) continue;
            String name = zj.value();
            fi.setAccessible(true);
            table.put(name, fi.getGenericType());
            fitable.put(name, fi);
        }
        if (cl.getSuperclass() != null) {
            initTables(cl.getSuperclass());
        }

    }

    public Field getFieldByMap(String name) {
        return fitable.get(name);
    }

    /**
     * 通过某个id获取某种东西
     *
     * @param by      第一个id
     * @param type    第一个数据
     * @param sign    第二个id
     * @param objtype 读出来的数据类型
     * @return
     */
    public abstract Object getsthbysth(String by, String type, Object sign, Class objtype);

    /**
     * 保存单个数据
     *
     * @param key   数据库id
     * @param arg   数据id
     * @param value 数据
     */
    public abstract void saveone(String key, String arg, Object value);

    /**
     * 删除单段数据
     *
     * @param key 数据库id
     * @param arg 数据id
     */
    public abstract void delpart(String key, String arg);

    /**
     * 清除所有 该id字段
     *
     * @param partname id
     */
    public abstract void clearallpart(String partname);

    /**
     * 加载一个字段
     *
     * @param key  数据库id
     * @param part 字段id
     * @return
     */
    public abstract Object loadonepart(String key, String part);

    /**
     * 使用key加载 要求储存类必须实现 fromkeyserizable(String)
     *
     * @param key key
     * @param def 读取不到的默认数据
     * @return
     */
    public abstract T keyload(String key, T def);

    /**
     * 反射一级一级获取一个field
     *
     * @param key
     * @param value
     * @param i
     * @return
     */
    public Field getFielddeep(String key, T value, int i) {
        Field fi = null;
        Class cl = null;
        try {
            cl = Class.forName(value.getClass().getTypeName());
            fi = cl.getDeclaredField(key);
        } catch (NoSuchFieldException nc) {
            i++;
            if (i != 3)
                fi = getFielddeep(key, cl.getSuperclass(), i);
        } catch (ClassNotFoundException e1) {
        }
        return fi;
    }

    public String getKey(Field value) {
        String key = "";
        for (Map.Entry<String, Field> entry : fitable.entrySet()) {
            if (value.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        return key;
    }

    /**
     * 反射一级一级获取一个field
     *
     * @param key
     * @param cl
     * @param i
     * @return
     */
    public Field getFielddeep(String key, Class cl, int i) {
        Field fi = null;
        try {
            fi = cl.getDeclaredField(key);
        } catch (NoSuchFieldException nc) {
            i++;
            if (i != 3)
                fi = getFielddeep(key, cl.getSuperclass(), i);
        }
        return fi;
    }

    /**
     * 获取数据库字段列表
     *
     * @return 字段列表
     */
    public abstract List<String> getColumnNames();

    /**
     * 加载一组数据
     *
     * @param key        数据酷id
     * @param loaddelete 加载后是否删除
     * @return
     */
    public abstract ArrayList<T> loadlist(String key, Boolean loaddelete);

    /**
     * 检查字段
     */
    public abstract void checkDuan();

    /**
     * 添加字段
     *
     * @param name 字段名
     */
    public abstract void addDuan(String name);

    /**
     * 创建连接
     *
     * @return 连接
     */
    public abstract Connection createConnection();

    /**
     * Deserialize the data from the database and return
     *
     * @param key the key of the data
     * @param def default value to be used if data was not found.
     * @return the deserialized data
     */
    public abstract T load(String key, T def);

    /**
     * Serialize the data and put it into the database.
     *
     * @param key   the key to pair the data with
     * @param value the data to be saved
     */
    public abstract void save(String key, T value);

    public abstract void del(String key);

    /**
     * Check if the key exists in the database
     *
     * @param key the key to check
     * @return true if exists; false if not
     */
    public abstract boolean has(String key);

    /**
     * get list of all keys in this database. The operation time of this method
     * can be longer depends on the amount of data saved in the data. Make sure
     * to use it asynchronous manner or only once on initialization.
     *
     * @return
     */
    public abstract Set<String> getKeys();

    /**
     * Serialize the object using the class type of object itself.
     *
     * @param obj
     * @return serialized string
     */
    public String serialize(Object obj) {
        if (gson == null)
            gson = builder.create();

        return gson.toJson(obj);
    }

    /**
     * Serialize the object using specified class type.
     *
     * @param obj
     * @param clazz
     * @return serialzied string
     */
    public String serialize(Object obj, Type clazz) {
        if (gson == null)
            gson = builder.create();

        return gson.toJson(obj, clazz);
    }

    /**
     * Deserialize the serialized string into the specified type of object.
     *
     * @param ser
     * @param clazz
     * @return deserialized object
     */
    public Object deserialize(String ser, Type clazz) {
        if (gson == null)
            gson = builder.create();

        return gson.fromJson(ser, clazz);
    }

    public abstract void clearDatabase();


}