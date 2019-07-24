package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.entity.Entity;

/**
 * 实体管理类，还没完工
 */
public class EntityManage {
    public Class getCraftWorldClass() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + KsAPI.serververStr + ".CraftWorld");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class getEntityClass(String EntityName) {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + "." + EntityName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class getGenericAttributesClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".GenericAttributes");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class getMinecraftKeyClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".MinecraftKey");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class getattributeJumpStrengthClass() {
        try {
            return Class.forName("net.minecraft.server." + KsAPI.serververStr + ".EntityHorseAbstract.attributeJumpStrength");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Entity getBukkitEntity(Object object, String entityname) {

        Entity obj = null;
        try {
            obj = (Entity) getEntityClass(entityname).getMethod("getBukkitEntity").invoke(object);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return obj;
    }
}
