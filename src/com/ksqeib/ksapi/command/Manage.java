package com.ksqeib.ksapi.command;

import com.ksqeib.ksapi.KsAPI;
import com.ksqeib.ksapi.gui.TestCodeGUI;
import com.ksqeib.ksapi.manager.MysqlPoolManager;
import com.ksqeib.ksapi.mysql.ConnectionPool;
import com.ksqeib.ksapi.mysql.MysqlConnectobj;
import com.ksqeib.ksapi.util.Io;
import com.ksqeib.ksapi.util.Musicg;
import com.ksqeib.ksapi.util.Tip;
import com.ksqeib.ksapi.util.UtilManager;
import fun.ksmc.util.MemMgmt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Manage extends Command {
    private String[] subCommands = {"debug", "about", "tsl", "commands", "plugins", "reload", "sureload"};

    public Manage(String name) {
        super(name);
    }

    /**
     * ks内部作用的管理指令
     *
     * @param cms
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean execute(CommandSender cms, String label, String[] args) {
        Io io = KsAPI.um.getIo();
        Tip tip = KsAPI.um.getTip();
        if (KsAPI.um.getPerm().isPluginAdmin(cms)) {
            if (args.length > 0) {
                switch (args[0]) {
                    default:
                    case "info":

                        Runtime run = Runtime.getRuntime();
                        tip.send("运行中的bukkit线程数量: " + Bukkit.getScheduler().getPendingTasks().size(), cms);
                        tip.send("bukkit工作线程数量: " + Bukkit.getScheduler().getActiveWorkers().size(), cms);
                        tip.send("处理器数量: " + run.availableProcessors(), cms);
                        tip.send("空闲内存: " + MemMgmt.getMemSize(run.freeMemory()), cms);
                        tip.send("内存分配: " + MemMgmt.getMemSize(run.totalMemory()) + "/" + MemMgmt.getMemSize(run.maxMemory()), cms);
                        tip.send("线程数: " + Thread.getAllStackTraces().keySet().size(), cms);
                        tip.send("[" + MemMgmt.getMemoryBar(50, run) + "]", cms);
                        tip.send("BY KSQEIB", cms);
                        break;

                    case "help":
                        KsAPI.um.getHelper("ksapi").HelpPage(cms, label, args);
                        break;
                    case "mysql":
                        Map<MysqlConnectobj, ConnectionPool> connectionPoolMap = MysqlPoolManager.getConnectionPools();
                        tip.send("当前连接池数量:" + connectionPoolMap.size(), cms);
                        for (Map.Entry<MysqlConnectobj, ConnectionPool> en : connectionPoolMap.entrySet()) {
                            tip.send(en.getKey().toString(), cms);
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
                                    tip.send(args[1] + "插件重载成功", cms);
                                } catch (Exception e) {
                                    tip.send("该插件可能不支持重载", cms);
                                }
                            } else {
                                tip.send("插件名称错误", cms);
                            }
                        }
                        break;
                    case "sureload":
                        if (args.length > 1) {
                            if (UtilManager.plist.containsKey(args[1])) {
                                UtilManager.plist.get(args[1]).reload();
                                tip.send(args[1] + "插件强行重载成功", cms);
                            } else {
                                tip.send("插件名称错误", cms);
                            }
                        }
                        break;
                    case "sureinject":
                        if (args.length > 1) {
                            String n = args[1];
                            if (UtilManager.plist.containsKey(n)) {
                                JavaPlugin jp = UtilManager.plist.get(n).jp;
                                String where = jp.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                                Bukkit.getPluginManager().disablePlugin(jp);
                                try {
                                    tip.send(n + "插件目录:" + where, cms);
                                    Plugin target = Bukkit.getPluginManager().loadPlugin(new File(where));
                                    target.onLoad();
                                    target.onEnable();
                                } catch (InvalidPluginException e) {
                                    tip.send(n + "插件存在错误", cms);
                                } catch (InvalidDescriptionException e) {
                                    tip.send(n + "插件描述有误", cms);
                                }
                                tip.send(n + "插件强行物理重载成功", cms);
                            } else {
                                tip.send("插件名称错误", cms);
                            }
                        }
                        break;
                    case "plugins":
                        tip.send("装载的ksAPI插件有", cms);
                        for (Map.Entry<String, UtilManager> en : UtilManager.plist.entrySet()) {
                            String name = en.getValue().jp.isEnabled() ? ChatColor.GREEN + en.getKey() : ChatColor.RED + en.getKey();
                            tip.send(name + ChatColor.AQUA + " VER:" + en.getValue().jp.getDescription().getVersion(), cms);
                        }
                        break;
                    case "commands":
                        tip.send("通过ksAPI注册的指令有", cms);
                        tip.send("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l ", cms);
                        for (Map.Entry<JavaPlugin, Command> entry : Cmdregister.getClist().entrySet()) {
                            tip.send(ChatColor.GOLD + "注册插件:" + ChatColor.GRAY + entry.getKey().getDescription().getName() + ChatColor.GOLD + " VER:" + ChatColor.DARK_GRAY + entry.getKey().getDescription().getVersion(), cms);
                            Command c = entry.getValue();
                            String name = c.getName();
                            tip.send(ChatColor.GOLD + "名称:" + ChatColor.GREEN + name, cms);
                            if (c.getAliases() != null && c.getAliases().size() != 0) {
                                tip.send(ChatColor.GOLD + "别名:", cms);
                                for (String ali : c.getAliases()) {
                                    tip.send(ChatColor.GREEN + ali, cms);
                                }
                            }
                            if (c.getDescription() != null && c.getDescription() != "")
                                tip.send(ChatColor.GOLD + "描述:" + ChatColor.GREEN + c.getDescription(), cms);
                            if (c.getLabel() != null && !c.getLabel().equals(name))
                                tip.send(ChatColor.GOLD + "Label:" + ChatColor.GREEN + c.getLabel(), cms);
                            if (c.getUsage() != null && !c.getUsage().equals("/" + name))
                                tip.send(ChatColor.GOLD + "用法:" + ChatColor.GREEN + c.getUsage(), cms);
                            if (c.getPermission() != null)
                                tip.send(ChatColor.GOLD + "权限:" + ChatColor.GREEN + c.getPermission(), cms);
                            if (c.getPermissionMessage() != null)
                                tip.send(ChatColor.GOLD + "权限信息:" + ChatColor.GREEN + c.getPermissionMessage(), cms);
                            tip.send("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l ", cms);

                        }
                        break;
                    case "test":
                        if (cms instanceof Player) {
                            Player p = (Player) cms;
                            TestCodeGUI testCodeGUI = new TestCodeGUI();
                            testCodeGUI.init();
                            testCodeGUI.setOkac(() -> {
                                p.sendMessage("SU");
                            });
                            testCodeGUI.setFailac(() -> {
                                p.sendMessage("FA");
                            });
                            testCodeGUI.openInventory(p);
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
                            boolean su = false;
                            switch (args[1].toLowerCase()) {
                                default: {
                                    KsAPI.um.getTip().getDnS(cms, "subwrong");
                                    break;
                                }
                                case "disio": {
                                    KsAPI.um.getIo().disabled();
                                    su = true;
                                    break;
                                }
                                case "printmusic": {
                                    Musicg.printlisttofile(KsAPI.um.getIo());
                                    su = true;
                                    break;
                                }
                                case "setitem": {
                                    KsAPI.um.getIo().loadData("item").set("hand", p.getInventory().getItemInMainHand());
                                    if (args.length > 2) {
                                        KsAPI.um.getIo().loadData("item").set(args[2], p.getInventory().getItemInMainHand());
                                    }
                                    KsAPI.um.getIo().disabled();
                                    su = true;
                                    break;
                                }
                                case "copyinv": {
                                    Inventory inv = snedp.getOpenInventory().getTopInventory();
                                    FileConfiguration yaml = KsAPI.um.getIo().loadData(args[2]);
                                    for (int i = 0; i < inv.getSize(); i++) {
                                        yaml.set(i + "", inv.getItem(i));
                                    }
                                    su = true;
                                    break;
                                }
                                case "unbreak": {
                                    ItemStack item = new ItemStack(p.getInventory().getItemInMainHand());
                                    ItemMeta im = item.getItemMeta();
                                    im.setUnbreakable(true);
                                    item.setItemMeta(im);
                                    p.getInventory().addItem(item);
                                    su = true;
                                    break;
                                }
                                case "nbttest": {
                                    ItemStack hand = new ItemStack(p.getInventory().getItemInMainHand());
                                    hand = KsAPI.um.getMulNBT().addNBTdata(hand, "cr", "test");
                                    hand = KsAPI.um.getMulNBT().addNBTdata(hand, "fuck", "艹", "String", String.class);
                                    hand = KsAPI.um.getMulNBT().addNBTdata(hand, "dd", 2d, "Double", double.class);
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().hasNBTdataType(hand, "cr")));
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().hasNBTdataType(hand, "fuck")));
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().hasNBTdataType(hand, "dd")));
                                    p.sendMessage((String) KsAPI.um.getMulNBT().getNBTdata(hand, "cr"));
                                    p.sendMessage((String) KsAPI.um.getMulNBT().getNBTdata(hand, "fuck"));
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().getNBTdata(hand, "dd")));
                                    hand = KsAPI.um.getMulNBT().removeNBTdata(hand, "cr");
                                    hand = KsAPI.um.getMulNBT().removeNBTdata(hand, "fuck");
                                    hand = KsAPI.um.getMulNBT().removeNBTdata(hand, "dd");
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().hasNBTdataType(hand, "cr")));
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().hasNBTdataType(hand, "fuck")));
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().hasNBTdataType(hand, "dd")));
                                    p.sendMessage((String) KsAPI.um.getMulNBT().getNBTdata(hand, "cr"));
                                    p.sendMessage((String) KsAPI.um.getMulNBT().getNBTdata(hand, "fuck"));
                                    p.sendMessage(String.valueOf(KsAPI.um.getMulNBT().getNBTdata(hand, "dd")));
                                    p.getInventory().addItem(hand);
                                    su = true;
                                    break;
                                }
                                case "nbtinfo": {
                                    ItemStack hand = new ItemStack(p.getInventory().getItemInMainHand());
                                    soutNbtBaseName(KsAPI.um.getMulNBT().getNbtTagCompound(hand), cms, "!!!!!!!!");
                                    su = true;
                                    break;
                                }
                                case "addlore": {
                                    ItemStack hand = new ItemStack(p.getInventory().getItemInMainHand());
                                    ItemMeta im = hand.getItemMeta();
                                    List<String> lore = im.getLore();
                                    if (lore == null) lore = new ArrayList<>();
                                    lore.forEach(p::sendMessage);
                                    if (args.length > 2) {
                                        lore.add(0, "§0 " + args[2].replace("&", "§"));
                                    }
                                    im.setLore(lore);
                                    hand.setItemMeta(im);
                                    p.getInventory().addItem(hand);
                                    su = true;
                                    break;
                                }
                            }
                            if (su) {
                                KsAPI.um.getTip().send("Success", cms);
                            } else {
                                KsAPI.um.getTip().send("Fail", cms);
                            }
                        }
                }
            } else {
                KsAPI.um.getHelper("ksapi").sendno(cms, label);
            }
        } else {
            KsAPI.um.getTip().send("BY KSQEIB", cms);
        }
        return true;
    }

    private void soutNbtBaseName(Object obj, CommandSender cms, String disname) {
        String name = obj.getClass().getSimpleName();
        if (name.equalsIgnoreCase("NBTTagCompound")) {
            cms.sendMessage("===========" + disname + "===============");
            Map<?, ?> getmap = KsAPI.um.getMulNBT().getNBTTagCompundMap(obj);
            getmap.forEach((k, v) -> soutNbtBaseName(v, cms, k.toString()));
            cms.sendMessage("===========" + disname + "===============");
        } else {
            Object get = KsAPI.um.getMulNBT().getNBTTagData(obj);
            if (get instanceof List) {
                cms.sendMessage(disname + ":");
                int i = 0;
                for (Object getobj : (List<?>) get) {
                    soutNbtBaseName(getobj, cms, "-" + i);
                    i++;
                }
            } else
                cms.sendMessage(disname + ":" + name + ":" + get.toString());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return Arrays.asList(subCommands);
    }
}
