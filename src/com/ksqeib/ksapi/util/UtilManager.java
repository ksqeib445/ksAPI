package com.ksqeib.ksapi.util;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class UtilManager {
    public static HashMap<String, UtilManager> plist = new HashMap<>();
    public JavaPlugin jp;
    private HashMap<String, Helper> helpers = new HashMap<>();
    @Getter
    Io io;
    @Getter
    ItemSR itemsr;
    @Getter
    Permission perm;
    @Getter
    Tip tip;
    @Getter
    MulNBT mulNBT;
    @Getter
    EntityManage entityManage;

    public UtilManager(JavaPlugin jp) {
        this.jp = jp;
        plist.put(jp.getDescription().getName(), this);
    }

    public void createalwaysneed(Boolean hasdata) {
        createio(hasdata);
        createitemsr();
        createtip(false, "message.yml");
        createperm();
    }

    public void createalwaysneed() {
        createio(false);
        createitemsr();
        createtip(false, "message.yml");
        createperm();
    }

    public void createio(Boolean hasdata) {
        io = new Io(jp, hasdata);
    }

    public void createio() {
        io = new Io(jp);
    }

    public void createmulNBT() {
        mulNBT = new MulNBT();
    }

    public void createentityManage() {
        entityManage = new EntityManage();
    }
    public boolean createitemsr() {
        if (io != null) {
            itemsr = new ItemSR(io);
            return true;
        }
        return false;
    }

    public boolean createtip(boolean islist, String name) {
        if (io != null) {
            tip = new Tip(io, islist, name);
            return true;
        }
        return false;
    }

    public boolean createtip(boolean islist, FileConfiguration messagefile) {
        if (messagefile != null) {
            tip = new Tip( islist, messagefile);
            return true;
        }
        return false;
    }

    public void createperm() {
        perm = new Permission(jp);
    }

    public void initsql() {

        new BukkitRunnable() {

            @Override
            public void run() {
                //待写
            }
        }.runTaskAsynchronously(jp);
    }

    public void createHelper(String command, FileConfiguration hy) {
        if(perm==null){
            createperm();
        }
        helpers.put(command, new Helper(hy, perm));
    }

    public Helper getHelper(String command) {
        return helpers.get(command);
    }
}
