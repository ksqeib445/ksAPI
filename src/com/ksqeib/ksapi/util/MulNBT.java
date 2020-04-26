package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 暴力的nbt管理类，不多 bb
 */
public class MulNBT<T> {

    public Class<?> getNBTClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + KsAPI.serververStr + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getNBTReadLimiterClass() {
        return getNBTClass("NBTReadLimiter");
    }

    public Object getNBTReadLimiter() {
        Object obj = null;
        try {
            obj = getNBTReadLimiterClass().getField("a").get(null);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return obj;
    }

    public Class<?> getNBTTagCompoundClass() {
        return getNBTClass("NBTTagCompound");
    }

    public Object getNBTTagCompoundInstance() {
        Object obj = null;
        try {
            obj = getNBTTagCompoundClass().newInstance();
        } catch (ReflectiveOperationException e2) {
            e2.printStackTrace();
        }
        return obj;
    }

    public Class<?> getCraftItemStackClass() {
        return getBukkitClass("inventory.CraftItemStack");
    }

    private Object asNMSCopy(Object object) {
        Object obj = null;
        try {
            obj = getCraftItemStackClass().getMethod("asNMSCopy", ItemStack.class).invoke(null, object);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return obj;
    }

    public ItemStack asBukkitCopy(Object object) {
        ItemStack obj = null;
        try {
            obj = (ItemStack) getCraftItemStackClass().getMethod("asBukkitCopy", getItemSatckClass()).invoke(null, new Object[]{object});
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return obj;
    }

    public Object asCraftMirror(Object object) {
        Object obj = null;
        try {
            obj = getCraftItemStackClass().getMethod("asCraftMirror", getItemSatckClass()).invoke(null, object);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return obj;
    }

    public Object asCraftCopy(Object object) {
        Object obj = null;
        try {
            obj = getCraftItemStackClass().getMethod("asCraftCopy", ItemStack.class).invoke(null, object);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return obj;
    }

    public Class<?> getItemSatckClass() {
        return getNBTClass("ItemStack");
    }

    public Class<?> getNBTBaseClass() {
        return getNBTClass("NBTBase");
    }

    public Boolean nmsItemhasTag(Object obj) {
        try {
            return (Boolean) getItemSatckClass().getMethod("hasTag").invoke(obj, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object nmsItemgetTag(Object obj) {
        try {
            return getItemSatckClass().getMethod("getTag").invoke(obj, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void nmsItemsetTag(Object obj, Object tag) {
        try {
            getItemSatckClass().getMethod("setTag", getNBTTagCompoundClass()).invoke(obj, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object createNBTByType(Object data, String type) {
        return createNBTByType(data, type, data.getClass());
    }

    public Object createNBTByType(Object data, String type, Class typeclass) {
        try {
            return getNBTClass("NBTTag" + type).getConstructor(typeclass).newInstance(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getNBTTagData(Object obj) {
        try {
            return getFieldData(obj, "data");
        } catch (NoSuchFieldException e) {
            try {
                return getFieldData(obj, "b");
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getFieldData(Object obj, String name) throws NoSuchFieldException, IllegalAccessException {
        Field fi = obj.getClass().getDeclaredField(name);
        fi.setAccessible(true);
        return fi.get(obj);
    }

    private Map getNBTTagCompundMap(Object obj) {
        try {
            Field fi = obj.getClass().getDeclaredField("map");
            fi.setAccessible(true);
            return (Map) fi.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object getNBTBaseofNBTTagCompound(String dataName, Object obj) {
        try {
            return getNBTTagCompoundClass().getMethod("get", String.class).invoke(obj, dataName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map getNBTMap(ItemStack item) {
        Object nmsItem = asNMSCopy(item);
        Object compound = (nmsItemhasTag(nmsItem)) ? nmsItemgetTag(nmsItem)
                : null;
        if (compound == null) return new HashMap();
        return getNBTTagCompundMap(compound);
    }

    public boolean hasNBTdataType(ItemStack item, String type) {
        return getNBTMap(item).containsKey(type);
    }

    public ItemStack addNBTdata(ItemStack item, String type, String data) {
        Object nmsItem = asNMSCopy(item);
        boolean have = nmsItemhasTag(nmsItem);
        Object compound = have ? nmsItemgetTag(nmsItem)
                : getNBTTagCompoundInstance();
        if (!have) {
            nmsItemsetTag(nmsItem, compound);
        }
        getNBTTagCompundMap(compound).put(type, createNBTByType(data, "String"));
        return asBukkitCopy(nmsItem);
    }

    /**
     * @param item 传入的物品
     * @param id   修改的NBTID
     * @param data 数据
     * @param type 数据类型
     * @param typeclass 数据类型的基本class
     * @return
     */
    public ItemStack addNBTdata(ItemStack item, String id, Object data, String type, Class typeclass) {
        Object nmsItem = asNMSCopy(item);
        boolean have = nmsItemhasTag(nmsItem);
        Object compound = have ? nmsItemgetTag(nmsItem)
                : getNBTTagCompoundInstance();
        if (!have) {
            nmsItemsetTag(nmsItem, compound);
        }
        getNBTTagCompundMap(compound).put(id, createNBTByType(data, type, typeclass));
        return asBukkitCopy(nmsItem);
    }

    public String getNBTdataStr(ItemStack item, String type) {
        Object nmsItem = asNMSCopy(item);
        if (!nmsItemhasTag(nmsItem)) return null;
        Object itemtag = nmsItemgetTag(nmsItem);
        Object nbtbase = getNBTBaseofNBTTagCompound(type, itemtag);
        if (nbtbase == null) return null;
        return (String) getNBTTagData(nbtbase);
    }

    public Object getNBTdata(ItemStack item, String type) {
        Object nmsItem = asNMSCopy(item);
        if (!nmsItemhasTag(nmsItem)) return null;
        Object itemtag = nmsItemgetTag(nmsItem);
        Object nbtbase = getNBTBaseofNBTTagCompound(type, itemtag);
        if (nbtbase == null) return null;
        return getNBTTagData(nbtbase);
    }

    public ItemStack removeNBTdata(ItemStack item, String type) {
        Object nmsItem = asNMSCopy(item);
        Object compound = (nmsItemhasTag(nmsItem)) ? nmsItemgetTag(nmsItem)
                : null;
        if (compound == null) return item;
        getNBTTagCompundMap(compound).remove(type);
        return asBukkitCopy(nmsItem);
    }

    private Object doMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] types = new Class<?>[args.length];
            Object[] objs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
                objs[i] = args[i];
            }
            Method method = obj.getClass().getMethod(methodName, types);
            return method.invoke(obj, objs);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }
        return null;
    }

}
