package com.ksqeib.ksapi.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Cmdregister {
    private static SimpleCommandMap commandMap;
    private static HashMap<JavaPlugin, Command> clist = new HashMap<>();

    /**
     * 刷新内部commandMap
     */
    public static void refCommandMap() {
        //反射获取commandmap
        try {
            final Class<?> c = Bukkit.getServer().getClass();
            Field cf = c.getDeclaredField("commandMap");
            cf.setAccessible(true);
            commandMap = (SimpleCommandMap) cf.get(Bukkit.getServer());
        } catch (Exception e) {
            System.out.println("初始化CommandMap失败");
            e.printStackTrace();
        }
    }

    /**
     * 注册一个指令
     *
     * @param jp  插件主类
     * @param cmd 命令
     */
    public static void registercmd(JavaPlugin jp, Command cmd) {
        commandMap.register(jp.getName(), cmd);
        clist.put(jp, cmd);
    }

    /**
     * 获取所有通过ksAPI注册的指令列表
     *
     * @return 指令列表
     */
    public static HashMap<JavaPlugin, Command> getClist() {
        return clist;
    }
}
