package com.ksqeib.ksapi.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.UUID;


public class InteractiveGUIManager implements Listener {

    protected static HashMap<UUID, InteractiveGUI> guis = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        UUID uuid = p.getUniqueId();
        int slot = event.getSlot();
        if (event.getCurrentItem() == null) return;
        if (!guis.containsKey(uuid)) return;
        event.setCancelled(true);
        if (event.getRawSlot() >= event.getInventory().getSize()) return;
        InteractiveGUI gui = guis.get(uuid);
        if (gui.isLock()) return;
        if (gui.getAction(slot) != null) {
            gui.getAction(slot).run();
        }
        if (event.getClick().isLeftClick()) {
            if (gui.getLAction(slot) != null) {
                gui.getLAction(slot).run();
            }
        }

        if (event.getClick().isRightClick()) {
            if (gui.getRAction(slot) != null) {
                gui.getRAction(slot).run();
            }
        }

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (gui.getShiftAction(slot) != null) {
                gui.getShiftAction(slot).run();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InteractiveGUI gui = guis.remove(event.getPlayer().getUniqueId());
        if (gui == null) return;
        if (gui.closeAction != null)
            gui.closeAction.run();
    }


}
