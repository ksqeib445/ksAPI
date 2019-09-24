package com.ksqeib.ksapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InteractiveMoveGUI{

    private Inventory inventory;
    private HashMap<Integer, Runnable> actions = new HashMap<Integer, Runnable>();
    private HashMap<Integer, Runnable> ractions = new HashMap<Integer, Runnable>();
    private HashMap<Integer, Runnable> lactions = new HashMap<Integer, Runnable>();
    private int cmstart=0;
    private int cmend=0;

    public InteractiveMoveGUI(String name, int size) {
        if(name==null||size==0)return;
        inventory = Bukkit.createInventory(null, size,
                name);
    }

    public Inventory getInventory() {
        return inventory;
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

    public void openInventory(Player p) {
        p.openInventory(inventory);
        InteractiveMoveGUIManager.guis.put(p.getUniqueId(), this);
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

    public void setCM(int start,int end){
        this.cmstart=start;
        this.cmend=end;
    }

    public int getCmstart() {
        return cmstart;
    }

    public int getCmend() {
        return cmend;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
