package com.ksqeib.ksapi.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 权限管理类，似乎废弃了
 */
public class Permission {
    public String pluginn = "插件名";
    String oppage = "oppage";

    protected Permission(JavaPlugin jp) {
        pluginn = jp.getName();
    }

    public boolean isp(CommandSender cms, String pr) {
        return cms.isOp() || cms.hasPermission(pluginn + pr);
    }

    public boolean isp(Player p, String pr) {
        return p.isOp() || p.hasPermission(pluginn + pr);
    }


}
