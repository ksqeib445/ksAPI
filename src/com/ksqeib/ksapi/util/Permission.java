package com.ksqeib.ksapi.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 权限管理类，似乎废弃了
 */
public class Permission {
    private final String admin = "admin";
    public String pluginn;

    protected Permission(JavaPlugin jp) {
        pluginn = jp.getName();
    }

    public boolean isp(CommandSender cms, String pr) {
        return cms.isOp() || cms.hasPermission(pluginn + pr);
    }

    public boolean isp(Player p, String pr) {
        return p.isOp() || p.hasPermission(pluginn + pr);
    }

    public boolean ispWithPoint(CommandSender cms, String pr) {
        return isp(cms, "." + pr);
    }

    public boolean isPluginAdmin(CommandSender cms) {
        return isp(cms, "." + admin);
    }

    public void setPluginn(String pluginn) {
        this.pluginn = pluginn;
    }
}
