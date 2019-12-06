package com.ksqeib.ksapi;

import com.ksqeib.ksapi.command.Cmdregister;
import com.ksqeib.ksapi.command.Manage;
import com.ksqeib.ksapi.depend.DependManager;
import com.ksqeib.ksapi.gui.InteractiveGUIManager;
import com.ksqeib.ksapi.gui.InteractiveMoveGUIManager;
import com.ksqeib.ksapi.util.UtilManager;
import com.mc6m.manage.api.PluginCheck;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class KsAPI extends JavaPlugin {
    // 构造需要
    public static UtilManager um;
    public static KsAPI instance;
    public static int serverVersion;
    public static String serververStr;
    public static boolean debug;
    public static DependManager dependManager;

    @Override
    public void onLoad() {
        String[] vercalc = Bukkit.getBukkitVersion().split("-");
        String[] vercalc2 = vercalc[0].split("\\.");
        String[] vercalc3 = vercalc[1].split("\\.");
        serververStr = "v" + vercalc2[0] + "_" + vercalc2[1] + "_R" + vercalc3[1];
    }

    @Override
    public void onEnable() {
        init();
    }

    public void init() {
//初始化
        instance = this;
        new ArrayList<Integer>() {
            {
                    dependManager=new DependManager();
                    //加载
                    try {
                        serverVersion = getServerVersionType();
                    } catch (NoSuchMethodException ex) {
                        System.out.print("KSAPI版本兼容模块失效！");
                    }
                    PluginManager pm = Bukkit.getPluginManager();
                    um = new UtilManager(instance);
                    um.createalwaysneed(true);
                    um.createmulNBT();
                    um.getIo().loadaConfig("help",true);
                    um.createHelper("ksapi",um.getIo().getaConfig("help"));
                    Cmdregister.refCommandMap();
                    Cmdregister.registercmd(instance, new Manage("ksapi"));
                    pm.registerEvents(new InteractiveGUIManager(), instance);
                    pm.registerEvents(new InteractiveMoveGUIManager(), instance);
                    dependManager.checkSoft();
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            try {
                                Class.forName("org.sqlite.JDBC");
                            } catch (Exception ex) {
                                um.getTip().sendToConsole("数据库驱动程序错误:" + ex.getMessage(),null);
                            }
                        }
                    }.runTaskAsynchronously(instance);
                    um.getTip().getDnS(Bukkit.getConsoleSender(), "enable", null);
            }
        };


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

    public static DependManager getDependManager() {
        return dependManager;
    }
}
