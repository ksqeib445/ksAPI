package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class MulNBT {

    public Class<?> getNBTTagCompoundClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".NBTTagCompound");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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

    public Class<?> getNBTReadLimiterClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".NBTReadLimiter");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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

    public Class<?> getCraftItemStackClass() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + KsAPI.serververStr + ".inventory.CraftItemStack");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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

    public Object asNMSCopy(Object object) {
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

    public Object getItemStack(Object object) {

        Object obj = null;
        try {
            obj = getItemSatckClass().getConstructor(new Class[]{getNBTTagCompoundClass()}).newInstance(object);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return obj;
    }

    public Class<?> getItemSatckClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".ItemStack");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getNBTBaseClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".NBTBase");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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

    public void compoundset(Object obj, String dataName, Object data) {
        try {
            getNBTTagCompoundClass().getMethod("set", String.class, getNBTBaseClass()).invoke(obj, dataName, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNBTTagStringClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".NBTTagString");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object createNBTTagString(String data) {
        try {
            return getNBTTagStringClass().getConstructor(String.class).newInstance(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getNBTTagString(Object obj) {
        try {
            Field fi = getNBTTagStringClass().getDeclaredField("data");
            fi.setAccessible(true);
            return (String) (fi.get(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getNBTBaseofNBTTagCompound(String dataName, Object obj) {
        try {
            return getNBTTagCompoundClass().getMethod("get", String.class).invoke(obj, dataName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStack addNBTdata(ItemStack item, String type, String data) {
        Object nmsItem = asNMSCopy(item);
        Object compound = (nmsItemhasTag(nmsItem)) ? nmsItemgetTag(nmsItem)
                : getNBTTagCompoundInstance();

        compoundset(compound, type, createNBTTagString(data));
        nmsItemsetTag(nmsItem, compound);
        return asBukkitCopy(nmsItem);
    }

    public String getNBTdataStr(ItemStack item, String type) {
        Object nmsItem = asNMSCopy(item);
        if (!nmsItemhasTag(nmsItem)) return null;
        Object itemtag = nmsItemgetTag(nmsItem);
        Object nbtbase = getNBTBaseofNBTTagCompound(type, itemtag);
        if (nbtbase == null) return null;
        return getNBTTagString(nbtbase);
    }

}
