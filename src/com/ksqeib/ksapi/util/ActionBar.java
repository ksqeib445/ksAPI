package com.ksqeib.ksapi.util;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.entity.Player;

/**
 * 注册ActionBarAPI的小东西
 */
public class ActionBar {
    public static boolean on = false;

    /**
     * 发送ActionBar(没有ActionBarAPI不会成功)
     * @param p 玩家
     * @param message 消息
     */
    public static void sendActionBar(Player p, String message) {
        if (on)
            ActionBarAPI.sendActionBar(p, message);
    }
}
