package com.ksqeib.ksapi.command;

import com.google.common.collect.HashBiMap;
import net.minecraft.server.v1_12_R1.MethodProfiler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Cmdregister {
    private static CommandMap commandMap;
    private static HashMap<JavaPlugin,Command> clist = new HashMap<>();

    /**
     * 刷新内部commandMap
     */
    public static void refCommandMap() {
        //反射获取commandmap
        try {
            final Class<?> c = Bukkit.getServer().getClass();
            for (final Method method : c.getDeclaredMethods())
                if (method.getName().equals("getCommandMap"))
                    commandMap = (CommandMap) method.invoke(Bukkit.getServer(), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册一个指令
     * @param jp 插件主类
     * @param cmd 命令
     */
    public static void registercmd(JavaPlugin jp, Command cmd) {
        commandMap.register(jp.getName(), cmd);
        clist.put(jp,cmd);
    }

    /**
     * 获取所有通过ksAPI注册的指令列表
     * @return 指令列表
     */
    public static HashMap<JavaPlugin, Command> getClist() {
        return clist;
    }
}
