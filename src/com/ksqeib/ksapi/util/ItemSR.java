package com.ksqeib.ksapi.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemSR {
    Io io;
    public static ItemSR instanse;

    public ItemSR(Io io) {
        this.io = io;
        instanse = this;
    }

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

    String reps(String str, String[] args) {
        String rp;
        if (str == null) {
            rp = "wrong";
        } else {
            rp = str.replace("&", "§");
//            rp =rp.replace(true+"",Kingdoms.getLang().getmes("open")).replace(false+"",Kingdoms.getLang().getmes("close"));
//            rp=rp.replace("true",Kingdoms.getLang().getmes("open")).replace("false",Kingdoms.getLang().getmes("close"));
        }
        if (args != null)
            for (int i = 0; i < args.length; i++) {
                rp = rp.replace("{" + i + "}", args[i]);
            }
        return rp;
    }

    ItemStack[] repmany(ItemStack[] items, String[] args) {
        int many = items.length;
        ItemStack[] repditems = new ItemStack[many];
        for (int i = 0; i < many; i++) {
            repditems[i] = rep(items[i], args);
        }
        return repditems;
    }
}
