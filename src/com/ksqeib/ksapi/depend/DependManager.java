package com.ksqeib.ksapi.depend;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import static org.bukkit.Bukkit.getServer;

public class DependManager {
    ActionBarManager actionBarManager = null;
    WorldBoardManager worldBoardManager=null;

    public DependManager() {

    }
    public void checkSoft(){
        PluginManager pm=getServer().getPluginManager();
        if (pm.getPlugin("ActionBarAPI") != null) actionBarManager=new ActionBarManager();
//        WorldBorder
        if (pm.getPlugin("WorldBorder") != null) worldBoardManager=new WorldBoardManager();
    }

    public void sendActionBar(Player p, String message) {
        if (actionBarManager != null) actionBarManager.sendActionBar(p, message);
    }
    public boolean isInsideBoard(Location loc){
        if(worldBoardManager==null)return true;
        return worldBoardManager.checkInisdeBorder(loc);
    }
}
