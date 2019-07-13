package com.ksqeib.ksapi.command;

import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Cmdregister {
    public static CommandMap commandMap;
    public static HashMap<JavaPlugin,Command> clist = new HashMap<>();

    public static void getCommandMap() {
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

    public static void registercmd(JavaPlugin jp, Command cmd) {
        commandMap.register(jp.getDescription().getName(), cmd);
        clist.put(jp,cmd);
    }


}
