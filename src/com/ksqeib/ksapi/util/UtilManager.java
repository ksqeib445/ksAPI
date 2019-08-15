package com.ksqeib.ksapi.util;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * 对工具类进行管理！务必使用！
 */
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
    @Getter
    InventoryControl inventoryControl;

    /**
     *构造方法
     * @param jp 插件主类
     */
    public UtilManager(JavaPlugin jp) {
        this.jp = jp;
        plist.put(jp.getDescription().getName(), this);
    }

    /**
     * 创建快速开发总是需要的
     * 会创建 Io ItemSR Tip(默认加载message.ymk Permission
     * @param hasdata io是否有目录树数据
     */
    public void createalwaysneed(Boolean hasdata) {
        createio(hasdata);
        createitemsr();
        createtip(false, "message.yml");
        createperm();
    }
    /**
     * 创建快速开发总是需要的
     * 会创建 Io(没有目录树数据) ItemSR Tip(默认加载message.ymk Permission
     */
    public void createalwaysneed() {
        createio(false);
        createitemsr();
        createtip(false, "message.yml");
        createperm();
    }

    /**
     * 创建一个Io
     * @param hasdata 是否有目录树数据
     */
    public void createio(Boolean hasdata) {
        io = new Io(jp, hasdata);
    }

    /**
     * 创建一个io(没有目录树数据)
     */
    @Deprecated
    public void createio() {
        io = new Io(jp);
    }

    /**
     * 创建NBT管理类
     */
    public void createmulNBT() {
        mulNBT = new MulNBT();
    }

    public void createinvControl(){inventoryControl=new InventoryControl();}

    /**
     * 创建实体管理类
     */
    public void createentityManage() {
        entityManage = new EntityManage();
    }

    /**
     * 创建物品管理类
     * @return 是否创建成功(没有io会创建失败)
     */
    public boolean createitemsr() {
        if (io != null) {
            itemsr = new ItemSR(io);
            return true;
        }
        return false;
    }
    /**
     * 创建提示信息管理类
     * @return 是否创建成功(没有io会创建失败)
     */
    public boolean createtip(boolean islist, String name) {
        if (io != null) {
            tip = new Tip(io, islist, name);
            return true;
        }
        return false;
    }

    /**
     * 创建权限管理类
     */
    public void createperm() {
        perm = new Permission(jp);
    }

    /**
     * 创建一个帮助者
     * @param command 命令名称
     * @param hy help.yml
     */
    public void createHelper(String command, FileConfiguration hy) {
        if (perm == null) {
            createperm();
        }
        helpers.put(command, new Helper(hy, perm));
    }

    /**
     * 获取一个帮助者
     * @param command 命令名称
     * @return 帮助者
     */
    public Helper getHelper(String command) {
        return helpers.get(command);
    }

    /**
     * 重载全部可以重载的工具类
     */
    public void reload(){
        if(io!=null)io.reload();
        if(tip!=null)tip.reload();
    }
}
