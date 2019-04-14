package com.ksqeib.ksapi.util;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.entity.Player;

public class ActionBar {
    public static boolean on=false;
    public static void sendActionBar(Player p, String message) {
        if(on)
        ActionBarAPI.sendActionBar(p, message);
    }
}
