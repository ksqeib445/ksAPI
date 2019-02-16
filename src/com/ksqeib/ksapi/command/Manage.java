package com.ksqeib.ksapi.command;

import com.ksqeib.ksapi.KsAPI;
import com.ksqeib.ksapi.util.Musicg;
import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;
import java.util.List;

public class Manage extends Command {
    public Manage(String name) {
        super(name);
    }

    public Manage(String name, String doc, String usage, List<String> alies) {
        super(name, doc, usage, alies);
    }

    @Override
    public boolean execute(CommandSender cms, String label, String[] args) {
        if (cms.isOp()||cms.hasPermission("ksapi.manage")) {
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
                    case "reload":
                        if (args.length > 1) {
                            if (UtilManager.plist.containsKey(args[1])) {
                                try {
                                    Class<?> c = UtilManager.plist.get(args[1]).jp.getClass();
                                    Method me=c.getMethod("reload");
                                    if(me==null){
                                        Bukkit.getConsoleSender().sendMessage("该插件可能不支持重载");
                                    }else {
                                       me .invoke(UtilManager.plist.get(args[1]).jp);
                                       Bukkit.getConsoleSender().sendMessage(args[1]+"插件重载成功");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
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
                                case "senddmes":
                                    String argss[] = new String[args.length - 4];
                                    for (int i = 0; i < argss.length; i++) {
                                        argss[i] = args[i + 4];
                                    }
                                    KsAPI.um.getTip().send(KsAPI.um.getTip().musicc(snedp, args[2].replace("&", "§")), snedp, argss);
                                    su = true;
                                    break;
                                case "sendmes":
                                    String sends = args[2];
                                    for (int i = 4; i < args.length; i++) {
                                        sends = sends + " " + args[i];
                                    }
                                    KsAPI.um.getTip().send(KsAPI.um.getTip().music(snedp, sends.replace("&", "§")), snedp, null);
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
