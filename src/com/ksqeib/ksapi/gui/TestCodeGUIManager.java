package com.ksqeib.ksapi.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class TestCodeGUIManager implements Listener {

    protected static HashMap<UUID, TestCodeGUI> guis = new HashMap<UUID, TestCodeGUI>();
    private static Material[] mats = Material.values();
    private static Random rm = new Random();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        UUID uuid = p.getUniqueId();
        int slot = event.getSlot();
        if (!guis.containsKey(uuid)) return;
        event.setCancelled(true);
        if (event.getRawSlot() >= event.getInventory().getSize()) return;
        TestCodeGUI gui = guis.get(uuid);
        if (slot < 0 || slot >= gui.getInventory().getSize()) return;
        if (gui.getOkac() == null) return;
        if (slot == gui.getLoc()) {
            gui.getOkac().run();
            return;
        }
        ItemStack item = event.getInventory().getItem(slot);
        if (item == null || item.getType() == gui.getTotest()) {
            gui.getOkac().run();
            return;
        }
        if (gui.getFailac() != null)
            gui.getFailac().run();
        gui.addYytime();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        guis.remove(event.getPlayer().getUniqueId());
    }


    public static Material randomMaterial() {
        return mats[randInt(0, mats.length - 1)];
    }

    public static int randInt(int min, int max) {
        int randomNum;
        if (min > max) {
            randomNum = rm.nextInt((min - max) + 1) + max;
        } else if (min == max) {
            randomNum = min;
        } else {
            randomNum = rm.nextInt((max - min) + 1) + min;
        }

        return randomNum;
    }
}
