package com.ksqeib.ksapi.gui;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;


public class InteractiveMoveGUIManager implements Listener {

    protected static HashMap<UUID, InteractiveMoveGUI> guis = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        UUID uuid = p.getUniqueId();
        if (!guis.containsKey(uuid)) return;
        if (!invokeComp(event.getClickedInventory(), guis.get(uuid).getInventory())) return;
        int slot = event.getSlot();
        if (event.getCurrentItem() == null) return;
        InteractiveMoveGUI img = guis.get(uuid);
        if (img.isLock()) {
            event.setCancelled(true);
            return;
        }
        if (img.isCanMove(slot)) return;
        event.setCancelled(true);
        if (event.getRawSlot() >= event.getInventory().getSize()) return;
        if (img.getAction(slot) != null) {
            img.getAction(slot).run();
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
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (img.getShiftAction(slot) != null) {
                img.getShiftAction(slot).run();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uid = event.getPlayer().getUniqueId();
        InteractiveMoveGUI img = guis.remove(uid);
        if (img == null) return;
        Inventory inv = img.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (!img.isCanMove(i)) continue;
            inv.setItem(i, null);
            event.getPlayer().getInventory().addItem(item);
        }
        if (img.closeAction != null) img.closeAction.run();
    }

    private boolean invokeComp(Inventory a, Inventory b) {
        if (a == null || b == null) return false;
        try {
            Class<?> cl = Class.forName("org.bukkit.craftbukkit." + KsAPI.serververStr + ".inventory.CraftInventory");
            Field fi = cl.getDeclaredField("inventory");
            fi.setAccessible(true);
            return fi.get(a) == fi.get(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
