package com.ksqeib.ksapi;

import com.ksqeib.ksapi.command.Cmdregister;
import com.ksqeib.ksapi.command.Manage;
import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


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
        um= new UtilManager(this);
        um.createalwaysneed(true,false);
        Cmdregister.getCommandMap();
        Cmdregister.commandMap.register(getDescription().getName(),new Manage("ksapi"));
        instance = this;
    }

    @Override
    public void onDisable() {
        um.getIo().disabled();
        um.getTip().getDnS(Bukkit.getConsoleSender(), "disable", null);
    }
}
