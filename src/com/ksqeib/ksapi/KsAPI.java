package com.ksqeib.ksapi;

import com.ksqeib.ksapi.command.Cmdregister;
import com.ksqeib.ksapi.command.Manage;
import com.ksqeib.ksapi.depend.DependManager;
import com.ksqeib.ksapi.gui.InteractiveGUIManager;
import com.ksqeib.ksapi.gui.InteractiveMoveGUIManager;
import com.ksqeib.ksapi.util.UtilManager;
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

    public static void warn(UtilManager ym) {
        for (int i = 0; i < 21; i++) {
            if (ym.getTip() != null) {
                ym.getTip().sendToConsole("如果插件使用中出现任何问题", null);
                ym.getTip().sendToConsole("如读取语言出现空指针 请先删除语言文件重启服务器", null);
                ym.getTip().sendToConsole("如版本迭代发现功能不符 请先删除旧配置文件重试", null);
                if (ym.jp.getDescription().getAuthors().size() == 0) return;
                StringBuilder kfz = new StringBuilder();
                for (String name : ym.jp.getDescription().getAuthors()) {
                    kfz.append(name).append(",");
                }
                ym.getTip().sendToConsole("如果无法解决 请联系插件开发者{0}", new String[]{kfz.toString()});
            }
        }
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

    public static DependManager getDependManager() {
        return dependManager;
    }

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
                dependManager = new DependManager();
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
                um.getIo().loadaConfig("help", true);
                um.createHelper("ksapi", um.getIo().getaConfig("help"));
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
                            um.getTip().sendToConsole("数据库驱动程序错误:" + ex.getMessage(), null);
                        }
                    }
                }.runTaskAsynchronously(instance);
                um.getTip().getDnS(Bukkit.getConsoleSender(), "enable", null);
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    warn(um);
                }, 200L);
                warn(um);
            }
        };


    }

    @Override
    public void onDisable() {
        um.getIo().disabled();
        um.getTip().getDnS(Bukkit.getConsoleSender(), "disable", null);
    }
}
