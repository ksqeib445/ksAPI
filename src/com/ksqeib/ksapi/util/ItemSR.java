package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * 对物品中的{0} {1} {2}进行替换
 */
public class ItemSR {
    Io io;
    public static ItemSR instanse;

    /**
     * 构造方法
     * @param io 需要一个io
     */
    protected ItemSR(Io io) {
        this.io = io;
        instanse = this;
    }

    /**
     * 进行替换
     * @param oit 物品
     * @param args 参数
     * @return 替换好的物品
     */
    public ItemStack rep(ItemStack oit, String[] args) {
        ItemMeta im = oit.getItemMeta();
        //设定名字替换
        if (im.hasDisplayName()) {
            im.setDisplayName(reps(oit.getItemMeta().getDisplayName(), args));
        }

        if (im.hasLore()) {
            //lore内容替换
            List<String> lores = im.getLore();
            for (int i = 0; i < lores.size(); i++) {
                String lore = lores.get(i);
                lores.set(i, reps(lore, args));
            }
            im.setLore(lores);
        }
        oit.setItemMeta(im);
        return oit;
    }

    private String reps(String str, String[] args) {
        String rp;
        if (str == null) {
            rp = "wrong";
        } else {
            rp = str.replace("&", "§");
        }
        if (args != null)
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    rp = rp.replace("{" + i + "}", "null");
                } else {
                    rp = rp.replace("{" + i + "}", args[i]);
                }
            }
        return rp;
    }

    /**
     * 替换一组物品
     * @param items 物品列表
     * @param args 参数
     * @return 替换好的物品列表
     */
    public ItemStack[] repmany(ItemStack[] items, String[] args) {
        int many = items.length;
        ItemStack[] repditems = new ItemStack[many];
        for (int i = 0; i < many; i++) {
            repditems[i] = rep(items[i], args);
        }
        return repditems;
    }
}
