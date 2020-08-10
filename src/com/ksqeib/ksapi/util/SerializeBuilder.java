/*
 * Copyright (c) 2018-2020 ksqeib. All rights reserved.
 * @author ksqeib <ksqeib@dalao.ink> <https://github.com/ksqeib445>
 * @create 2020/08/01 20:24:32
 *
 * ksAPI/ksAPI/Serializer.java
 */

package com.ksqeib.ksapi.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.ksqeib.ksapi.mysql.serializer.KSeri;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;

public class SerializeBuilder {

    /**
     * 创建json,反射序列号专用的
     *
     * @param json
     * @param cl
     * @param context
     * @param value
     */
    public static void jsonCreate(JsonObject json, Class cl, JsonSerializationContext context, Object value) {
        HashMap<String, Type> table = new HashMap<>();
        try {
            initTables(table, cl);
            for (String keys : table.keySet()) {
                //Load
                Field fi = getObjFielddeep(keys, value, 0);
                if (fi != null) {
                    fi.setAccessible(true);
                    Object obj = fi.get(value);
                    if (obj != null) {
                        KSeri zj = fi.getAnnotation(KSeri.class);
                        if (zj == null) {
                            json.add(fi.getName(), context.serialize(obj));
                        } else {
                            json.add(zj.value(), context.serialize(obj));
                        }
                    }
                }

            }
        } catch (Exception e) {
            Bukkit.getLogger().warning(value.getClass().getTypeName());
        }
    }

    public static void onekeySetField(Class cl, Object result, JsonObject json, JsonDeserializationContext context) {
        try {
            HashMap<String, Type> table = new HashMap<>();
            initTables(table, cl);
            for (String keys : table.keySet()) {
                //LOAD
                Field fi = getFielddeep(keys, cl, 0);
                fi.setAccessible(true);
                JsonElement obj = json.get(keys);
                if (obj != null)
                    fi.set(result, context.deserialize(obj, table.get(keys)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Field getObjFielddeep(String key, Object value, int i) {
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

    public static Field getFielddeep(String key, Class cl, int i) {
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

    public static void initTables(HashMap<String, Type> table, Class cl) {
        for (Field fi : cl.getDeclaredFields()) {
            if (!Modifier.isTransient((fi.getModifiers())) && !Modifier.isStatic(fi.getModifiers())) {
                KSeri zj = fi.getAnnotation(KSeri.class);
                if (zj == null) {
                    table.put(fi.getName(), fi.getGenericType());
                } else {
                    table.put(zj.value(), fi.getGenericType());
                }
            }
        }
        if (cl.getSuperclass() != null) {
            initTables(table, cl.getSuperclass());
        }
    }

    public static void initTablesSuKseri(HashMap<String, Type> table, Class cl) {
        for (Field fi : cl.getDeclaredFields()) {
            if (!Modifier.isTransient((fi.getModifiers())) && !Modifier.isStatic(fi.getModifiers())) {
                KSeri zj = fi.getAnnotation(KSeri.class);
                if (zj == null) {
                    continue;
                } else {
                    table.put(zj.value(), fi.getGenericType());
                }
            }
        }
        if (cl.getSuperclass() != null) {
            initTables(table, cl.getSuperclass());
        }
    }

    public static void jsonCreateByMap(JsonObject json, JsonSerializationContext context, Object value, HashMap<String, Type> table) {
        try {
            for (String keys : table.keySet()) {
                //Load
                Field fi = getObjFielddeep(keys, value, 0);
                if (fi != null) {
                    fi.setAccessible(true);
                    Object obj = fi.get(value);
                    if (obj != null) {
                        KSeri zj = fi.getAnnotation(KSeri.class);
                        if (zj == null) {
                            json.add(fi.getName(), context.serialize(obj));
                        } else {
                            json.add(zj.value(), context.serialize(obj));
                        }
                    }
                }

            }
        } catch (Exception e) {
            Bukkit.getLogger().warning(value.getClass().getTypeName());
        }
    }

    public static void setFieldByMap(Object result, JsonObject json, JsonDeserializationContext context, HashMap<String, Type> table) {
        try {
            for (String keys : table.keySet()) {
                //LOAD
                Field fi = getFielddeep(keys, result.getClass(), 0);
                fi.setAccessible(true);
                JsonElement obj = json.get(keys);
                if (obj != null)
                    fi.set(result, context.deserialize(obj, table.get(keys)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
