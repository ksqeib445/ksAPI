package com.ksqeib.ksapi.command;

import com.ksqeib.ksapi.KsAPI;
import com.ksqeib.ksapi.util.Io;
import com.ksqeib.ksapi.util.Musicg;
import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Map;

public class Manage extends Command {
    public Manage(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender cms, String label, String[] args) {
        Io io = KsAPI.um.getIo();
        if (cms.isOp() || cms.hasPermission("ksapi.manage")) {
            if (args.length > 0) {
                switch (args[0]) {
                    default:
                    case "about":
                        KsAPI.um.getTip().send("BY KSQEIB", cms, null);
                        if (args.length >= 2) {
                            KsAPI.um.getTip().send("PL:" + KsAPI.instance.getName(), cms, null);
                            KsAPI.um.getTip().send("VER" + KsAPI.instance.getDescription().getVersion(), cms, null);
                        }
                        break;
                    case "tsl":
                        if (args.length == 3) {
                            io.toStringListAndSave(Io.getAll(io.loadData(args[1])), args[2]);
                            System.out.println("sucess");
                        }
                        break;
                    case "reload":
                        if (args.length > 1) {
                            if (UtilManager.plist.containsKey(args[1])) {
                                try {
                                    Class<?> c = UtilManager.plist.get(args[1]).jp.getClass();
                                    Method me = c.getMethod("reload");
                                    me.invoke(UtilManager.plist.get(args[1]).jp);
                                    cms.sendMessage(args[1] + "插件重载成功");
                                } catch (Exception e) {
                                    cms.sendMessage("该插件可能不支持重载");
                                }
                            } else {
                                cms.sendMessage("插件名称错误");
                            }
                        }
                        break;
                    case "plugins":
                        cms.sendMessage("装载的ksAPI插件有");
                        for (String name : UtilManager.plist.keySet()) {
                            cms.sendMessage(name);
                        }
                        break;
                    case "commands":
                        cms.sendMessage("通过ksAPI注册的指令有");
                        cms.sendMessage("==============================================================");
                        for (Map.Entry<JavaPlugin,Command> entry : Cmdregister.clist.entrySet()) {
                            cms.sendMessage("注册插件:" + entry.getKey().getDescription().getName()+" VER:"+entry.getKey().getDescription().getVersion());
                            Command c=entry.getValue();
                            cms.sendMessage("名称:" + c.getName());
                            if(c.getAliases()!=null&&c.getAliases().size()!=0){
                                cms.sendMessage("别名:");
                                for(String ali:c.getAliases()){
                                    cms.sendMessage(ali);
                                }
                            }
                            if(c.getDescription()!=null&&c.getDescription()!="")
                            cms.sendMessage("描述:" + c.getDescription());
                            if(c.getLabel()!=null)
                            cms.sendMessage("Label:" + c.getLabel());
                            if(c.getUsage()!=null)
                            cms.sendMessage("用法:" + c.getUsage());
                            if(c.getPermission()!=null)
                            cms.sendMessage("权限:" + c.getPermission());
                            if(c.getPermissionMessage()!=null)
                            cms.sendMessage("权限信息:" + c.getPermissionMessage());
                            if(c.getTimingName()!=null)
                            cms.sendMessage("TimingName:" + c.getTimingName());
                            cms.sendMessage("==============================================================");

                        }
                        break;
                    case "debug":
                        if (args.length >= 2) {
                            Player p = null;
                            if (cms instanceof Player) {
                                p = (Player) cms;
                            }
                            Player snedp = p;
                            if (args.length >= 4) {
                                snedp = Bukkit.getPlayer(args[3]);
                            }
                            Boolean su = false;
                            switch (args[1].toLowerCase()) {
                                default:
                                    KsAPI.um.getTip().getDnS(cms, "subwrong", null);
                                    break;
                                case "disio":
                                    KsAPI.um.getIo().disabled();
                                    su = true;
                                    break;
                                case "printmusic":
                                    Musicg.printlisttofile(KsAPI.um.getIo());
                                    su = true;
                                    break;
                                case "setitem":
                                    KsAPI.um.getIo().loadData("item").set("hand", p.getInventory().getItemInMainHand());
                                    if (args.length > 2) {
                                        KsAPI.um.getIo().loadData("item").set(args[2], p.getInventory().getItemInMainHand());
                                    }
                                    su = true;
                                    break;
                                case "copyinv":
                                    Inventory inv = snedp.getOpenInventory().getTopInventory();
                                    FileConfiguration yaml = KsAPI.um.getIo().loadData(args[2]);
                                    for (int i = 0; i < inv.getSize(); i++) {
                                        yaml.set(i + "", inv.getItem(i));
                                    }
                                    su = true;
                                    break;
                            }
                            if (su) {
                                KsAPI.um.getTip().send("Success", cms, null);
                            } else {
                                KsAPI.um.getTip().send("Fail", cms, null);
                            }
                        }
                }
            }
        } else {
            KsAPI.um.getTip().getDnS(cms, "subwrong", null);
        }
        return true;
    }
}
