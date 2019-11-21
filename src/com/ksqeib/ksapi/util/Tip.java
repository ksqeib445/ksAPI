package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 提示类
 */
public class Tip {

    public static ConsoleCommandSender ccs = Bukkit.getConsoleSender();
    public Boolean islist;
    public HashMap<String, List<String>> lmMap;
    public HashMap<String, String> mMap;
    public FileConfiguration messagefile;
    public boolean isnohead = false;
    public Io io;

    /**
     * 初始化类
     *
     * @param io     io
     * @param islist 是否是列表的
     * @param name   读取的信息列表名称(不会自动加.yml)
     */
    protected Tip(Io io, boolean islist, String name) {
        this.io = io;
        this.islist = islist;
        messagefile = io.loadYamlFile(name, true);
        init();
    }

    /**
     * 初始化(创建后会自动调用)
     */
    public void init() {
        if (islist) {
            lmMap = Io.getAlllist(messagefile);
        } else {
            mMap = Io.getAll(messagefile);
        }
    }

    /**
     * 获取一条提示信息(List会自动只获取第一条)
     *
     * @param m 信息id
     * @return 信息
     */
    public String getMessage(String m) {
        if (islist) {
            List<String> mes = lmMap.get(m);
            if (mes == null) {
                Bukkit.getLogger().warning("在读取语言" + m + "时发生了一个空指针！");
            }
            if (mes.size() != 0) {
                return mes.get(0).replace("&", "§");
            } else {
                return "";
            }
        } else {
            String mes = mMap.get(m);
            if (mes == null) {
                Bukkit.getLogger().warning("在读取语言" + m + "时发生了一个空指针！");
                return "";
            } else {
                return mes;
            }
        }
    }

    /**
     * 获取提示信息列表(只有list模式能用)
     *
     * @param m 信息id
     * @return 信息列表
     */
    public List<String> getMessageList(String m) {
        if (islist) {
            if (lmMap == null) {
                Bukkit.getLogger().warning("严重！插件似乎未初始化完成！");
                new Exception().printStackTrace();
                return null;
            }
            List<String> strings = lmMap.get(m);
            if (strings == null) {
                Bukkit.getLogger().warning("在读取列表语言" + m + "时发生了错误");
            } else {
                return strings;
            }
        }
        return null;
    }

    /**
     * 发送一条提示消息
     *
     * @param p    被发送者
     * @param m    内部消息id
     * @param args {0} {1} {2}这种要被替换的
     */
    public void getDnS(CommandSender p, String m, String[] args) {
        if (!islist) {
            send(getMessage(m), p, args);
        } else {
            for (String mes : getMessageList(m)) {
                send(mes, p, args);
            }
        }
    }

    /**
     * 发送一条提示消息
     *
     * @param p    被发送者
     * @param m    内部消息id
     * @param args {0} {1} {2}这种要被替换的
     */
    public void getDnS(Player p, String m, String[] args) {
        if (islist) {
            for (String mes : getMessageList(m)) {
                send(music(p, mes), p, args);
            }
        } else {
            send(music(p, getMessage(m)), p, args);
        }
    }

    /**
     * 发送一条提示消息
     *
     * @param uuid 被发送者uuid
     * @param m    内部消息id
     * @param args {0} {1} {2}这种要被替换的
     */
    public void getDnS(UUID uuid, String m, String[] args) {
        getDnS(Bukkit.getPlayer(uuid), m, args);
    }

    /**
     * 发一条全服
     *
     * @param first 内容
     */
    private void broadcastMessage(String first) {
        Bukkit.broadcastMessage(first);
        for (Player p : Bukkit.getOnlinePlayers()) {
            music(p, first);
        }
    }

    public void broadcastMessage(String first, String[] args) {
        if (args != null)
            for (int i = 0; i < args.length; i++) {
                first = first.replace("{" + i + "}", args[i]);
            }
        Bukkit.broadcastMessage(first);
        for (Player p : Bukkit.getOnlinePlayers()) {
            music(p, first);
        }
    }

    /**
     * 发送一条信息
     *
     * @param first 内容
     * @param p     被发送者
     * @param args  {0} {1} {2}这种要被替换的
     */
    public void send(String first, CommandSender p, String[] args) {
        Player pl = null;
        Boolean isp = false;
        if (p instanceof Player) {
            pl = (Player) p;
            isp = true;
        }
        if (first == null) return;
        if (p == null) return;
        if (args != null)
            for (int i = 0; i < args.length; i++) {
                first = first.replace("{" + i + "}", args[i]);
            }
        if (isp) {
            send(first, pl, null);
        } else {
            sendToConsole(first, null);
        }
    }

    /**
     * 发送一条信息
     *
     * @param first 内容
     * @param p     被发送者
     * @param args  {0} {1} {2}这种要被替换的
     */
    public void send(String first, Player p, String[] args) {
        if (p == null) return;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                first = first.replace("{" + i + "}", args[i]);
            }
        }
        if (first.startsWith("=")) {
            //=判断
            p.sendMessage(first.substring(1));
        } else if (first.startsWith("+")) {
            KsAPI.getDependManager().sendActionBar(p, first.substring(1));
        } else if (first.startsWith("-")) {
            sendwithhead(p, first.substring(1));
        } else if (first.startsWith("!")) {
            broadcastMessage(first.substring(1));
        } else {
            //判断title
            String[] bes = first.split("-;-");
            if (bes.length > 4) {
                p.sendTitle(bes[3], bes[4], Integer.parseInt(bes[0]), Integer.parseInt(bes[1]), Integer.parseInt(bes[2]));
            } else {
                sendwithhead(p, first);
            }
        }
    }

    public void sendToConsole(String first, String[] args) {
        if (first == null) return;
        if (ccs == null) return;
        if (args != null)
            for (int i = 0; i < args.length; i++) {
                first = first.replace("{" + i + "}", args[i]);
            }
        if (first.startsWith("=")) {
            //=判断
            ccs.sendMessage(first.substring(1));
        } else if (first.startsWith("+")) {
            ccs.sendMessage(first.substring(1));
        } else if (first.startsWith("!")) {
            broadcastMessage(first.substring(1));
        } else if (first.startsWith("-")) {
            sendwithhead(ccs, first.substring(1));
        } else {
            //判断title
            String[] bes = first.split("-;-");
            if (bes.length > 4) {
                ccs.sendMessage(bes[3] + " " + bes[4]);
            } else {
                sendwithhead(ccs, first);
            }
        }
    }

    /**
     * 带有消息头的发送
     *
     * @param p  被发送者
     * @param in 内容
     */
    public void sendwithhead(CommandSender p, String in) {
        if (isnohead) return;
        if (islist) {
            List<String> head = lmMap.get("mhead");
            if (head == null) {
                p.sendMessage(in);
            } else
                for (int i = 0; i < head.size(); i++) {
                    String get = head.get(i);
                    if (i == head.size() - 1) {
                        get += in;
                    }
                    p.sendMessage(get);
                }
        } else {
            String head = mMap.get("mhead");
            if (head == null) {
                p.sendMessage(in);
            } else
                p.sendMessage(head + in);
        }
    }

    /**
     * 对消息中的音乐进行处理
     *
     * @param p  目标玩家
     * @param nq 内容
     * @return 处理后的消息
     */
    public String music(Player p, String nq) {
        String nh = nq;
        if (nq != null) {
            if(nh.startsWith("!"))return nq;
            String[] nqs = nq.split("=.=");
            if (nqs != null) {
                if (nqs.length > 3) {
                    p.playSound(p.getLocation(), Musicg.getSound(nqs[0]), Float.parseFloat(nqs[1]), Float.parseFloat(nqs[2]));
                    nh = nqs[3];
                }
            }
        }
        return nh;
    }

    /**
     * 重载
     */
    public void reload() {
        messagefile = io.loadYamlFile(messagefile.getCurrentPath(), true);
    }
}
