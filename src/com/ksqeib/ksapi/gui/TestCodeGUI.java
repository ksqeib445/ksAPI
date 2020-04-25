package com.ksqeib.ksapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TestCodeGUI {
    private Inventory inventory;
    private Runnable okac;
    private Runnable failac;
    private int yytime = 0;
    private Material totest;
    private int loc = -1;

    public void setInventory(String originalname, int size) {
        String name = originalname;
        if (originalname != null)
            if (originalname.length() > 32) {
                name = originalname.substring(0, 32);
            }

        if (name == null || size == 0) return;
        inventory = Bukkit.createInventory(null, size,
                name);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void init() {
        if (totest == null)
            totest = Material.SIGN;
        if (inventory == null) {
            setInventory(ChatColor.BLACK + "点击界面中" + ChatColor.DARK_RED + "牌子" + ChatColor.BLACK + "继续下一步", 27);
        }
        if (loc == -1)
            loc = TestCodeGUIManager.randInt(0, inventory.getSize() - 1);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
        }
        inventory.setItem(loc, new ItemStack(totest));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Runnable getOkac() {
        return okac;
    }

    public void setOkac(Runnable okac) {
        this.okac = okac;
    }

    public int getYytime() {
        return yytime;
    }

    public void setYytime(int yytime) {
        this.yytime = yytime;
    }

    public void addYytime() {
        yytime++;
    }

    public Material getTotest() {
        return totest;
    }

    public void setTotest(Material totest) {
        this.totest = totest;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public Runnable getFailac() {
        return failac;
    }

    public void setFailac(Runnable failac) {
        this.failac = failac;
    }

    public void openInventory(Player p) {
        p.closeInventory();
        p.openInventory(inventory);
        TestCodeGUIManager.guis.put(p.getUniqueId(), this);
    }

}
