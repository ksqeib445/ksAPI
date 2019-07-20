package com.ksqeib.ksapi.command;

import com.ksqeib.ksapi.KsAPI;
import com.ksqeib.ksapi.util.Io;
import com.ksqeib.ksapi.util.Musicg;
import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
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
                            System.out.println("success");
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
                    case "sureload":
                        if (args.length > 1) {
                            if (UtilManager.plist.containsKey(args[1])) {
                                   UtilManager.plist.get(args[1]).reload();
                                    cms.sendMessage(args[1] + "插件强行重载成功");
                            } else {
                                cms.sendMessage("插件名称错误");
                            }
                        }
                        break;
                    case "plugins":
                        cms.sendMessage("装载的ksAPI插件有");
                        for (Map.Entry<String, UtilManager> en : UtilManager.plist.entrySet()) {
                            String name = en.getValue().jp.isEnabled() ? ChatColor.GREEN + en.getKey() : ChatColor.RED + en.getKey();
                            cms.sendMessage(name);
                        }
                        break;
                    case "commands":
                        cms.sendMessage("通过ksAPI注册的指令有");
                        cms.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l ");
                        for (Map.Entry<JavaPlugin, Command> entry : Cmdregister.clist.entrySet()) {
                            cms.sendMessage(ChatColor.GOLD + "注册插件:" + ChatColor.GRAY + entry.getKey().getDescription().getName() + ChatColor.GOLD + " VER:" + ChatColor.DARK_GRAY + entry.getKey().getDescription().getVersion());
                            Command c = entry.getValue();
                            String name = c.getName();
                            cms.sendMessage(ChatColor.GOLD + "名称:" + ChatColor.GREEN + name);
                            if (c.getAliases() != null && c.getAliases().size() != 0) {
                                cms.sendMessage(ChatColor.GOLD + "别名:");
                                for (String ali : c.getAliases()) {
                                    cms.sendMessage(ChatColor.GREEN + ali);
                                }
                            }
                            if (c.getDescription() != null && c.getDescription() != "")
                                cms.sendMessage(ChatColor.GOLD + "描述:" + ChatColor.GREEN + c.getDescription());
                            if (c.getLabel() != null && !c.getLabel().equals(name))
                                cms.sendMessage(ChatColor.GOLD + "Label:" + ChatColor.GREEN + c.getLabel());
                            if (c.getUsage() != null && !c.getUsage().equals("/" + name))
                                cms.sendMessage(ChatColor.GOLD + "用法:" + ChatColor.GREEN + c.getUsage());
                            if (c.getPermission() != null)
                                cms.sendMessage(ChatColor.GOLD + "权限:" + ChatColor.GREEN + c.getPermission());
                            if (c.getPermissionMessage() != null)
                                cms.sendMessage(ChatColor.GOLD + "权限信息:" + ChatColor.GREEN + c.getPermissionMessage());
                            cms.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l ");

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

    private String[] subCommands = {"debug", "about", "tsl", "commands", "plugins", "reload","sureload"};

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return Arrays.asList(subCommands);
    }
}
