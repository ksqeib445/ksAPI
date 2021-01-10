package com.ksqeib.ksapi.gui;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class InteractiveMoveGUI extends InteractiveGUI{

    private Collection<Integer> canmove = new ArrayList<>();

    public InteractiveMoveGUI(String name, int size) {
        super(name,size);
    }

    public void openInventory(Player p) {
        p.openInventory(inventory);
        InteractiveMoveGUIManager.guis.put(p.getUniqueId(), this);
    }

    public void setCM(int start, int end) {
        for (int i = start; i <= end; i++) {
            canmove.add(i);
        }
    }

    public void setCM(int i) {
        canmove.add(i);
    }

    public void removeCM(int start, int end) {
        for (int i = start; i <= end; i++) {
            canmove.remove(i);
        }
    }

    public void removeCM(int i) {
        canmove.remove(i);
    }

    public boolean isCanMove(int i) {
        return canmove.contains(i);
    }

}
