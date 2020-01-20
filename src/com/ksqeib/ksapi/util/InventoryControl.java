package com.ksqeib.ksapi.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryControl {

    public boolean hasEnoughItems(Player p, ItemStack[] itemStacks) {
        for (ItemStack item : itemStacks) {
            int need = item.getAmount();
            if (hasItem(p, item) < need) return false;
        }
        return true;
    }

    public void delItems(Player p, ItemStack[] itemStacks) {

        for (ItemStack item : itemStacks) {
            int need = item.getAmount();
            delItem(p, item, need);
        }
    }

    public int hasItem(Player p, ItemStack item) {
        int many = 0;
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack get = p.getInventory().getItem(i);
            if (get == null) continue;
            if (get.isSimilar(item)) {
                many += get.getAmount();
            }
        }
        return many;
    }

    public void delItem(Player p, ItemStack item, int amount) {
        if (hasItem(p, item) >= amount) {
            int willamount = amount;
            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack get = p.getInventory().getItem(i);
                if (get == null) continue;
                if (get.isSimilar(item)) {
                    if (get.getAmount() > willamount) {
                        get.setAmount(get.getAmount() - willamount);
                        willamount = 0;
                    } else if (get.getAmount() == willamount) {
                        p.getInventory().setItem(i, null);
                        willamount = 0;
                    } else {
                        p.getInventory().setItem(i, null);
                        willamount -= get.getAmount();
                    }
                }
            }
        }
    }
}
