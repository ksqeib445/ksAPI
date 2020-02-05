package com.ksqeib.ksapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class ScrollerGUI extends InteractiveGUI {

    public ArrayList<Inventory> pages = new ArrayList<Inventory>();
    public UUID id;
    public int currpage = 0;
    public Player p;

    public ScrollerGUI(ArrayList<ItemStack> items, String name, Player p) {
        super(null,0);
        this.id = UUID.randomUUID();
        this.p=p;
        Inventory page = getBlankPage(name);

        for (int i = 0; i < items.size(); i++) {
            if (page.firstEmpty() == 46) {
                pages.add(page);
                page = getBlankPage(name);
                page.addItem(items.get(i));
            } else {
                page.addItem(items.get(i));
            }
        }

        setAction(45,this::lastpage);
        setAction(53,this::nextPage);
        pages.add(page);
        setInventory(pages.get(currpage));
        openInventory(p);
    }
    public void lastpage(){
        if (currpage > 0) {
            currpage -= 1;
            setInventory(pages.get(currpage));
            openInventory(p);
        }
    }

    public void nextPage(){
        if (currpage >= pages.size() - 1) {
            return;
        } else {
            currpage += 1;
            setInventory(pages.get(currpage));
            openInventory(p);
        }
    }

    private Inventory getBlankPage(String name) {
        Inventory page = Bukkit.createInventory(null, 54, name);

//        ItemStack nextpage = ItemStorger.scroller("nextpage");
//        ItemStack prevpage = ItemStorger.scroller("prevpage");

//        page.setItem(53, nextpage);
//        page.setItem(45, prevpage);
        return page;
    }
}
