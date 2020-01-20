package com.ksqeib.ksapi.depend;


import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.entity.Player;

public class ActionBarManager {
    public void sendActionBar(Player p, String mes) {
        ActionBarAPI.sendActionBar(p, mes);
    }
}
