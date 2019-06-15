package com.ksqeib.ksapi;

import com.ksqeib.ksapi.command.Cmdregister;
import com.ksqeib.ksapi.command.Manage;
import com.ksqeib.ksapi.util.ActionBar;
import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;


public class KsAPI extends JavaPlugin {
    // 构造需要
    public static UtilManager um;
    public static KsAPI instance;
    public static int serverVersion;

    @Override
    public void onEnable() {
        init();
        um.getTip().getDnS(Bukkit.getConsoleSender(), "enable", null);

    }

    public void init() {
//初始化
        //加载
        try {
            serverVersion = getServerVersionType();
        } catch (NoSuchMethodException ex) {
            System.out.print("KSAPI版本兼容模块失效！");
        }
        PluginManager pm = Bukkit.getPluginManager();
        um = new UtilManager(this);
        um.createalwaysneed(true);
        Cmdregister.getCommandMap();
        Cmdregister.commandMap.register(getDescription().getName(), new Manage("ksapi"));
        instance = this;
        if (pm.getPlugin("ActionBarAPI") != null) ActionBar.on = true;
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (Exception ex) {
                    System.out.println("数据库驱动程序错误:" + ex.getMessage());
                }
            }
        }.runTaskAsynchronously(this);
    }

    public static int getServerVersionType() throws NoSuchMethodException {
        int sv = 0;
        Method[] m = PlayerInventory.class.getDeclaredMethods();
        for (Method e : m) {
            if (e.toGenericString().contains("getItemInMainHand")) {
//                1.9+
                sv = 3;
                return sv;
            }
        }
        if (sv == 0) {
            if (Material.getMaterial("SLIME_BLOCK") != null) {
//                1.8
                sv = 2;
                return sv;
            } else {
//                1.7
                sv = 1;
                return sv;
            }
        }
        return sv;
    }

    @Override
    public void onDisable() {
        um.getIo().disabled();
        um.getTip().getDnS(Bukkit.getConsoleSender(), "disable", null);
    }
}
