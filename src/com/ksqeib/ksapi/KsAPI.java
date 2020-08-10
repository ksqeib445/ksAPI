package com.ksqeib.ksapi;

import com.ksqeib.ksapi.command.Cmdregister;
import com.ksqeib.ksapi.command.Manage;
import com.ksqeib.ksapi.depend.DependManager;
import com.ksqeib.ksapi.gui.InteractiveGUIManager;
import com.ksqeib.ksapi.gui.InteractiveMoveGUIManager;
import com.ksqeib.ksapi.gui.TestCodeGUIManager;
import com.ksqeib.ksapi.manager.MysqlPoolManager;
import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class KsAPI extends JavaPlugin {
    // 构造需要
    public static UtilManager um;
    public static KsAPI instance;
    public static int serverVersion;
    public static String serververStr;
    public static boolean debug;
    public static boolean refalldatabase;
    public static DependManager dependManager;

    public static ConcurrentHashMap<String, String> item = new ConcurrentHashMap<>();

    public static int getServerVersionType() throws NoSuchMethodException {
        Method[] m = PlayerInventory.class.getDeclaredMethods();
        for (Method e : m) {
            if (e.toGenericString().contains("getItemInMainHand")) {
                return 3;
            }
        }
        if (Material.getMaterial("SLIME_BLOCK") != null) {
            return 2;
        } else {
            return 1;
        }
    }

    public static DependManager getDependManager() {
        return dependManager;
    }

    @Override
    public void onLoad() {
//        System.out.println(Bukkit.getBukkitVersion());
//        String[] vercalc = Bukkit.getBukkitVersion().split("-");
//        String[] vercalc2 = vercalc[0].split("\\.");
////        String[] vercalc3 = vercalc[1].split("\\.");
//        serververStr = "v" + vercalc2[0] + "_" + vercalc2[1] + "_R" + vercalc2[2];
        serververStr = Bukkit.getServer().getClass().getPackage().getName().substring(23);
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
                um.getIo().loadaConfig("config", true);
                um.createHelper("ksapi", um.getIo().getaConfig("help"));
                FileConfiguration fc = um.getIo().getaConfig("config");
                debug = fc.getBoolean("debug", false);
                refalldatabase = fc.getBoolean("refalldb", false);
                Cmdregister.refCommandMap();
                Cmdregister.registercmd(instance, new Manage("ksapi"));
                pm.registerEvents(new InteractiveGUIManager(), instance);
                pm.registerEvents(new InteractiveMoveGUIManager(), instance);
                pm.registerEvents(new TestCodeGUIManager(), instance);
                dependManager.checkSoft();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            Class.forName("org.sqlite.JDBC");
                        } catch (Exception ex) {
                            um.getTip().sendToConsole("数据库驱动程序错误:" + ex.getMessage());
                        }
                    }
                }.runTaskAsynchronously(instance);
                um.getTip().getDnS(Bukkit.getConsoleSender(), "enable");

//                item = getAll(um.getIo().loadYamlFile("itemname.yml", true));
            }
        };


    }

    @Override
    public void onDisable() {
        um.getIo().disabled();
        um.getTip().getDnS(Bukkit.getConsoleSender(), "disable");
        MysqlPoolManager.onDisable();
    }

    public static String getMaterialCN(Material material) {
        String type = material.name().toLowerCase();
        if (item.containsKey(type)) {
            return item.get(type);
        }
        return material.name();
    }
    public ConcurrentHashMap<String, String> getAll(FileConfiguration file) {
        ConcurrentHashMap<String, String> hash = new ConcurrentHashMap<>();
        for (String string : file.getValues(false).keySet()) {
            hash.put(string.toLowerCase(), file.getString(string).replace("&", "§"));
        }
        return hash;
    }
}
