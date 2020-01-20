package com.ksqeib.ksapi.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;


public class InteractiveMoveGUIManager implements Listener {

    protected static HashMap<UUID, InteractiveMoveGUI> guis = new HashMap<UUID, InteractiveMoveGUI>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        UUID uuid = p.getUniqueId();
        int slot = event.getSlot();
        if (event.getCurrentItem() == null) return;
        if (!guis.containsKey(uuid)) return;
        InteractiveMoveGUI img = guis.get(uuid);
        if (!(slot >= img.getCmstart() || slot <= img.getCmend())) return;
        event.setCancelled(true);
        if (event.getRawSlot() >= event.getInventory().getSize()) return;
        if (img.getAction(slot) != null) {
            img.getAction(slot).run();
            return;
        }
        if (event.getClick().isLeftClick()) {
            if (img.getLAction(slot) != null) {
                img.getLAction(slot).run();
            }
        }

        if (event.getClick().isRightClick()) {
            if (img.getRAction(slot) != null) {
                img.getRAction(slot).run();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uid = event.getPlayer().getUniqueId();
        if (guis.containsKey(uid)) {
            InteractiveMoveGUI img = guis.get(uid);
            Inventory inv = img.getInventory();
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item != null) {
                    if (!(i >= img.getCmstart() || i <= img.getCmend())) {
                        event.getPlayer().getInventory().addItem(item);
                    }
                }
            }
            guis.remove(uid);
        }
    }
}
