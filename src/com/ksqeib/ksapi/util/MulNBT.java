package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    public Class<?> getCraftMetaItemStackClass() {
        return getBukkitClass("inventory.CraftMetaItem");
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

    /**
     * 判断nms物品是否有NBT标签
     *
     * @param obj NMSItemStack
     * @return NBTTagCompound
     */
    public Boolean nmsItemhasTag(Object obj) {
        try {
            return (Boolean) getItemSatckClass().getMethod("hasTag").invoke(obj, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取NMS物品的NBT标签
     *
     * @param obj NMS物品
     * @return
     */
    public Object nmsItemgetTag(Object obj) {
        try {
            return getItemSatckClass().getMethod("getTag").invoke(obj, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置NMS物品的NBT标签
     *
     * @param obj NMS物品
     * @param tag NBTTagCompound
     */
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

    /**
     * 建立一个对应类型的NBTBase
     *
     * @param data      数据
     * @param type      类型
     * @param typeclass 对应类型的基本类型class
     * @return NBTBase
     */
    public Object createNBTByType(Object data, String type, Class typeclass) {
        try {
            return getNBTClass("NBTTag" + type).getConstructor(typeclass).newInstance(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object createNBTByType(String type) {
        try {
            return getNBTClass("NBTTag" + type).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 强行获取NBTTag的数据 可以与getNBTMap(ItemStack item)搭配使用
     *
     * @param obj NBTTag对象
     * @return NBTBase中的数据
     */
    public Object getNBTTagData(Object obj) {
        try {
            String classname = obj.getClass().getSimpleName();
            for (Field fi : obj.getClass().getDeclaredFields()) {
                if (Modifier.isFinal(fi.getModifiers()) || Modifier.isStatic(fi.getModifiers())) continue;
                boolean todo = false;
                switch (classname) {
                    default:
                        if (fi.getName().equalsIgnoreCase("data")) todo = true;
                        break;
                    case "NBTTagList":
                        if (fi.getName().equalsIgnoreCase("list")) todo = true;
                        break;
                    case "NBTTagCompound":
                        if (fi.getName().equalsIgnoreCase("map")) todo = true;
                        break;
                    case "NBTTagLongArray":
                        if (fi.getName().equalsIgnoreCase("b")) todo = true;
                        break;
                }
                if (todo) {
                    fi.setAccessible(true);
                    return fi.get(obj);
                }
            }
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map getNBTTagCompundMap(Object obj) {
        try {
            Field fi = obj.getClass().getDeclaredField("map");
            fi.setAccessible(true);
            Map get = (Map) fi.get(obj);
//            if (!(get instanceof ConcurrentHashMap)) {
////                防止异步
//                return replaceNBTTagCompundMap(obj);
//            }
            return get;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    private Map replaceNBTTagCompundMap(Object obj) {
//        ConcurrentHashMap cmap = new ConcurrentHashMap();
//        try {
//            Field fi = obj.getClass().getDeclaredField("map");
//            fi.setAccessible(true);
//            Map get = (Map) fi.get(obj);
//            if (get.size() != 0) {
//                get.forEach((a, b) -> cmap.put(a, b));
//            }
//            fi.set(obj, cmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return cmap;
//    }

    private Object getNBTBaseofNBTTagCompound(String dataName, Object obj) {
        try {
            return getNBTTagCompoundClass().getMethod("get", String.class).invoke(obj, dataName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 只能读 不能修改 修改无效 用于获取NBT列表
     * Map String,NBTBase 不建议强转 使用getNBTTagData获取NBTBase中的数据
     *
     * @param item 传入的物品
     * @return 含有NBTBase的Map
     */
    public Map getNBTMap(ItemStack item) {
        Object nmsItem = asNMSCopy(item);
        Object compound = (nmsItemhasTag(nmsItem)) ? nmsItemgetTag(nmsItem)
                : null;
        if (compound == null) return new HashMap();
        return getNBTTagCompundMap(compound);
    }

    /**
     * 是否有所请求的NBT标签
     *
     * @param item 物品
     * @param type 标签
     * @return
     */
    public boolean hasNBTdataType(ItemStack item, String type) {
        if (item.getType() == Material.AIR) return false;
        return getMapByMeta(item).containsKey(type);
    }

    /**
     * 添加NBT数据
     *
     * @param item 物品
     * @param type NBTID
     * @param data 数据
     * @return 添加后的物品
     */
    public ItemStack addNBTdata(ItemStack item, String type, String data) {
        return addNBTdata(item, type, data, "String", String.class);
    }

    /**
     * @param item      传入的物品
     * @param id        修改的NBTID
     * @param data      数据
     * @param type      数据类型 Byte ByteArray Compound(需要get) Double End(未知) Float Int IntArray List Long LongArray Short String 均为基本类型
     * @param typeclass 数据类型的基本class 如String.class
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

    public ItemStack addNBTTag(ItemStack item, String id, Object tag) {
        Object nmsItem = asNMSCopy(item);
        boolean have = nmsItemhasTag(nmsItem);
        Object compound = have ? nmsItemgetTag(nmsItem)
                : getNBTTagCompoundInstance();
        if (!have) {
            nmsItemsetTag(nmsItem, compound);
        }
        getNBTTagCompundMap(compound).put(id, tag);
        return asBukkitCopy(nmsItem);
    }

    public Object getNbtTagCompound(ItemStack item) {
        Object nmsItem = asNMSCopy(item);
        return nmsItemhasTag(nmsItem) ? nmsItemgetTag(nmsItem) : getNBTTagCompoundInstance();
    }

    /**
     * 获取物品的String类型数据
     *
     * @param item 物品
     * @param type NBTID
     * @return 数据
     */
    public String getNBTdataStr(ItemStack item, String type) {
        return (String) getNBTdata(item, type);
    }

    /**
     * 获取NBT数据 直接返回对应的数据类型
     *
     * @param item 物品
     * @param type NBTID
     * @return 数据(需要强转
     */
    public Object getNBTdata(ItemStack item, String type) {
//        Object nmsItem = asNMSCopy(item);
//        if (!nmsItemhasTag(nmsItem)) return null;
//        Object itemtag = nmsItemgetTag(nmsItem);
//        Object nbtbase = getNBTBaseofNBTTagCompound(type, itemtag);
//        if (nbtbase == null) return null;
//        return getNBTTagData(nbtbase);
        Object itemtag = getNBTTag(item, type);
        if (itemtag == null) return null;
        return getNBTTagData(itemtag);
    }

    public Object getNBTTag(ItemStack item, String type) {
        if (!hasNBTdataType(item, type)) return null;
        return getMapByMeta(item).get(type);
    }

    /**
     * 移除NBT标签
     *
     * @param item 物品
     * @param type NBTID
     * @return 删除后的物品
     */
    public ItemStack removeNBTdata(ItemStack item, String type) {
        Object nmsItem = asNMSCopy(item);
        Object compound = (nmsItemhasTag(nmsItem)) ? nmsItemgetTag(nmsItem)
                : null;
        if (compound == null) return item;
        getNBTTagCompundMap(compound).remove(type);
        return asBukkitCopy(nmsItem);
    }

    public Map getMapByMeta(ItemStack item) {
        return getCraftMetalItemMap(item.getItemMeta());
    }

    public Map getCraftMetalItemMap(ItemMeta itemMeta) {
        if (itemMeta == null) return new HashMap();
        try {

            Field fi = getCraftMetaItemStackClass().getDeclaredField("unhandledTags");
            fi.setAccessible(true);
            Map get = (Map) fi.get(itemMeta);
            return get;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
