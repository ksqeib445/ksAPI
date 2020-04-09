package com.ksqeib.ksapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InteractiveGUI {

    private Inventory inventory;
    private HashMap<Integer, Runnable> actions = new HashMap<Integer, Runnable>();
    private HashMap<Integer, Runnable> ractions = new HashMap<Integer, Runnable>();
    private HashMap<Integer, Runnable> lactions = new HashMap<Integer, Runnable>();

    public InteractiveGUI(String originalname, int size) {
        String name = originalname;
        if (originalname != null)
            if (originalname.length() > 32) {
                name = originalname.substring(0, 32);
            }

        if (name == null || size == 0) return;
        inventory = Bukkit.createInventory(null, size,
                name);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setAction(int slot, Runnable runnable) {
        actions.put(slot, runnable);
    }

    public void setRightAction(int slot, Runnable runnable) {
        ractions.put(slot, runnable);
    }

    public void setLeftAction(int slot, Runnable runnable) {
        lactions.put(slot, runnable);
    }

    public void removeAction(int slot){
        actions.remove(slot);
    }

    public void removeLeftAction(int slot){
        lactions.remove(slot);
    }

    public void removeRightAction(int slot){
        ractions.remove(slot);
    }

    public void openInventory(Player p) {
        p.openInventory(inventory);
        InteractiveGUIManager.guis.put(p.getUniqueId(), this);
    }

    public Runnable getAction(int slot) {
        if (!actions.containsKey(slot)) return null;
        return actions.get(slot);
    }

    public Runnable getRAction(int slot) {
        if (!ractions.containsKey(slot)) return null;
        return ractions.get(slot);
    }

    public Runnable getLAction(int slot) {
        if (!lactions.containsKey(slot)) return null;
        return lactions.get(slot);
    }
}
