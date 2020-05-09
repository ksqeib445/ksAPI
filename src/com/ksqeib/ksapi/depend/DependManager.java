package com.ksqeib.ksapi.depend;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import static org.bukkit.Bukkit.getServer;

public class DependManager {
    ActionBarManager actionBarManager = null;
    WorldBoardManager worldBoardManager = null;

    /**
     * 检查前置
     */
    public void checkSoft() {
        PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("ActionBarAPI") != null) {
            actionBarManager = new ActionBarManager();
            KsAPI.um.getTip().sendToConsole("对接ActionBarAPI成功", null);
        }
//        WorldBorder
        if (pm.getPlugin("WorldBorder") != null) {
            worldBoardManager = new WorldBoardManager();
            KsAPI.um.getTip().sendToConsole("对接WorldBorder成功", null);
        }
    }

    /**
     * 发送ActionBar
     * @param p 玩家
     * @param message 内容
     */
    public void sendActionBar(Player p, String message) {
        if (actionBarManager != null) actionBarManager.sendActionBar(p, message);
    }

    /**
     * 判断是否在世界边界内
     * @param loc 坐标
     * @return
     */
    public boolean isInsideBoard(Location loc) {
        if (worldBoardManager == null) return true;
        return worldBoardManager.checkInisdeBorder(loc);
    }
}
