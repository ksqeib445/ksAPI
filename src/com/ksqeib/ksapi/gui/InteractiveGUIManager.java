package com.ksqeib.ksapi.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.UUID;


public class InteractiveGUIManager  implements Listener {

    protected static HashMap<UUID, InteractiveGUI> guis = new HashMap<UUID, InteractiveGUI>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        UUID uuid = p.getUniqueId();
        int slot = event.getSlot();
        if (event.getCurrentItem() == null) return;
        if (!guis.containsKey(uuid)) return;
        event.setCancelled(true);
        if (event.getRawSlot() >= event.getInventory().getSize()) return;
        if (guis.get(uuid).getAction(slot) != null) {
            guis.get(uuid).getAction(slot).run();
            return;
        }
        if (event.getClick().isLeftClick()) {
            if (guis.get(uuid).getLAction(slot) != null) {
                guis.get(uuid).getLAction(slot).run();
            }
        }

        if (event.getClick().isRightClick()) {
            if (guis.get(uuid).getRAction(slot) != null) {
                guis.get(uuid).getRAction(slot).run();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        guis.remove(event.getPlayer().getUniqueId());
    }
}
