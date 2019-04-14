package com.ksqeib.ksapi;

import com.ksqeib.ksapi.command.Cmdregister;
import com.ksqeib.ksapi.command.Manage;
import com.ksqeib.ksapi.util.ActionBar;
import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.Map;


public class KsAPI extends JavaPlugin {
    // 构造需要
    public static UtilManager um;
    public static KsAPI instance;
    @Override
    public void onEnable() {
        init();
        um.getTip().getDnS(Bukkit.getConsoleSender(), "enable", null);

    }

    public void init() {
//初始化
        //加载
        PluginManager pm=Bukkit.getPluginManager();
        um= new UtilManager(this);
        um.createalwaysneed(true,false);
        Cmdregister.getCommandMap();
        Cmdregister.commandMap.register(getDescription().getName(),new Manage("ksapi"));
        instance = this;
        if(pm.getPlugin("ActionBarAPI") != null) ActionBar.on=true;

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

    @Override
    public void onDisable() {
        um.getIo().disabled();
        um.getTip().getDnS(Bukkit.getConsoleSender(), "disable", null);
    }
}
