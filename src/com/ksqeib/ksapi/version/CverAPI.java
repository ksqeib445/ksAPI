package com.ksqeib.ksapi.version;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CverAPI {

    public static LivingEntity getLivingEntityByEntityId(UUID id) {
        for (World w : Bukkit.getWorlds()) {
            for (LivingEntity e : w.getLivingEntities()) {
                if (e.getUniqueId().equals(id)) {
                    return e;
                }
            }
        }
        return null;
    }

    //优化完成
    public static String getFileVersion() {
        if (KsAPI.serverVersion == 3) {
            return "-1_9";
        } else {
            return "-1_8";
        }
    }

    //优化完成
    public static ItemStack getItemInHand(LivingEntity p) {
        if (KsAPI.serverVersion == 3 && p.getEquipment() != null) {
            return p.getEquipment().getItemInMainHand();
        }
        if (p.getType() == EntityType.PLAYER && p.getEquipment() != null) {
            return ((Player) p).getItemInHand();
        } else {
            return p.getEquipment().getItemInHand();
        }
    }

    public static ItemStack getItemInOffHand(LivingEntity p) {
        if (KsAPI.serverVersion == 3) {
            return p.getEquipment().getItemInOffHand();
        }
        return new ItemStack(Material.AIR);
    }

    //优化完成
    public static void setItemInHand(Player p, ItemStack i) {
        if (KsAPI.serverVersion == 3) {
            p.getInventory().setItemInMainHand(i);
        } else {
            p.getInventory().setItemInHand(i);
        }
    }

    //优化完成
    public static double getMaxHealth(LivingEntity p) {
        if (KsAPI.serverVersion == 3) {
            return p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        }
        return p.getMaxHealth();
    }

    //优化完成
    public static void setMaxHealth(LivingEntity p, double m) {
        if (KsAPI.serverVersion == 3) {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(m);
        } else {
            p.setMaxHealth(m);
        }
    }

    //优化完成
    public static HashMap getHashMap(String s1, YamlConfiguration config, int i, String s2) {
        HashMap<String, Double> hashmap = new HashMap<>();
        if (config.get(i + "." + s1 + "." + s2) != null) {
            Double value = config.getDouble(i + "." + s1 + "." + s2);
            hashmap.put(s1, value);
        } else {
            hashmap.put(s1, 0D);
        }
        return hashmap;
    }

    public static List<ItemStack> removeDurability(List<ItemStack> items) {
        for (ItemStack item : items) {
            if (!item.getItemMeta().spigot().isUnbreakable()) {
                short nj = item.getDurability();
                if (nj - 1 > 0) {
                    item.setDurability((short) (nj - 1));
                } else {
                    item.setType(Material.AIR);
                }
            }
        }
        return items;
    }
}
