package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * 提示类
 */
public class Tip {
    public boolean isnohead = false;
    public Io io;
    private String head;
    private String filename;
    private LinkedHashMap<String, Object> tips = new LinkedHashMap<>();
    private static final UnaryOperator<String> repcolor = x -> x.replace("&", "§");

    /**
     * 初始化类
     *
     * @param io     io
     * @param islist 是否是列表的
     * @param name   读取的信息列表名称(不会自动加.yml)
     */
    protected Tip(Io io, boolean islist, String name) {
        this.io = io;
        this.filename = name;
        init();
    }

    protected Tip(Io io, String name) {
        this.io = io;
        this.filename = name;
        init();
    }

    /**
     * 初始化(创建后会自动调用)
     */
    public void init() {
        File file = io.loadPluginFile(filename, true);
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> tip = null;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            tip = yaml.load(fileInputStream);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        if (tip == null) return;
        initMap(tip, true, new StringBuilder());
        if (tips.containsKey("mhead")) {
            head = getMessage("mhead");
        } else {
            head = null;
        }
    }

    private List<String> translateList(List<?> list) {
        if (list.size() == 0) return new ArrayList<>();
//                转换
        List<String> listb = new ArrayList<>();
        for (Object obj : list) {
            listb.add(String.valueOf(obj));
        }
        listb.replaceAll(repcolor);
        return listb;
    }

    private void initMap(Map<?, ?> map, boolean root, StringBuilder sb) {
        for (Map.Entry<?, ?> en : map.entrySet()) {
            if (en.getValue() instanceof List<?>) {
                List<String> val = translateList((List<?>) en.getValue());
                if (root) {
                    tips.put(String.valueOf(en.getKey()), val);
                } else {
                    sb.append(en.getKey());
                    tips.put(sb.toString(), val);
                }
            } else if (en.getValue() instanceof Map<?, ?>) {
                initMap((Map<?, ?>) en.getValue(), false, new StringBuilder(sb.append(en.getKey()).append(".").toString()));
            } else {
                String val = String.valueOf(en.getValue()).replace("&", "§");
                if (root) {
                    tips.put(String.valueOf(en.getKey()), val);
                } else {
                    sb.append(en.getKey());
                    tips.put(sb.toString(), val);
                }
            }
        }
    }

    /**
     * 获取一条提示信息(List会自动只获取第一条)
     *
     * @param m 信息id
     * @return 信息
     */
    public String getMessage(String m) {
        if (!tips.containsKey(m)) {
            Bukkit.getLogger().warning("在读取语言" + m + "时发生了一个空指针！");
            showDetial();
            return "";
        }
        Object val = tips.get(m);
        if (val instanceof List<?>) {
            List<String> mes = (List<String>) val;
            if (mes.size() != 0) {
                return mes.get(0);
            } else {
                return "";
            }
        } else {
            return String.valueOf(val);
        }
    }

    private void showDetial() {
        Bukkit.getLogger().warning("细节：" + io.getPluginName() + " " + filename);
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取提示信息列表(混合使用)
     *
     * @param m 信息id
     * @return 信息列表
     */
    public List<String> getMessageList(String m) {
        if (tips == null) {
            new NullPointerException("插件似乎未初始化完成").printStackTrace();
            return new ArrayList<>();
        }
        if (!tips.containsKey(m)) {
            Bukkit.getLogger().warning("在读取列表语言" + m + "时发生了错误");
            showDetial();
        }
        return (List<String>) tips.get(m);
    }

    /**
     * 发送一条提示消息
     *
     * @param p    被发送者
     * @param m    内部消息id
     * @param args {0} {1} {2}这种要被替换的
     */
    public void getDnS(CommandSender p, String m, String... args) {
        objsend(tips.get(m), p, args);
    }

    private void objsend(Object get, CommandSender p, String... args) {
        if (get instanceof String) {
            send((String) get, p, args);
        } else if (get instanceof List<?>) {
            for (String mes : (List<String>) get) {
                send(mes, p, args);
            }
        } else {
            showDetial();
        }
    }

    private void objsendWithMusic(Object get, Player p, String... args) {
        if (get instanceof String) {
            send(music(p, (String) get), p, args);
        } else if (get instanceof List<?>) {
            for (String mes : (List<String>) get) {
                send(music(p, mes), p, args);
            }
        } else {
            showDetial();
        }
    }

    /**
     * 发送一条提示消息
     *
     * @param p    被发送者
     * @param m    内部消息id
     * @param args {0} {1} {2}这种要被替换的
     */
    public void getDnS(Player p, String m, String... args) {
        objsendWithMusic(tips.get(m), p, args);
    }

    /**
     * 发送一条提示消息
     *
     * @param uuid 被发送者uuid
     * @param m    内部消息id
     * @param args {0} {1} {2}这种要被替换的
     */
    public void getDnS(UUID uuid, String m, String... args) {
        getDnS(Bukkit.getPlayer(uuid), m, args);
    }

    public void broadcastMessage(String first, String... args) {
        if (args != null)
            for (int i = 0; i < args.length; i++) {
                first = first.replace("{" + i + "}", args[i]);
            }
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(music(p, first), p);
        }
    }

    public void getBroadcast(String in, String... args) {
        Object get = tips.get(in);
        if (get instanceof String) {
            broadcastMessage((String) get, args);
        } else if (get instanceof List<?>) {
            for (String mes : (List<String>) get) {
                broadcastMessage(mes, args);
            }
        } else {
            showDetial();
        }
    }

    /**
     * 发送一条信息
     *
     * @param first 内容
     * @param p     被发送者
     * @param args  {0} {1} {2}这种要被替换的
     */
    public void send(String first, CommandSender p, String... args) {
        Player pl = null;
        boolean isp = false;
        if (p instanceof Player) {
            pl = (Player) p;
            isp = true;
        }
        first = getString(first, p == null, args);
        if (isp) {
            send(first, pl);
        } else {
            sendToConsole(first);
        }
    }

    /**
     * 发送一条信息
     *
     * @param first 内容
     * @param p     被发送者
     * @param args  {0} {1} {2}这种要被替换的
     */
    public void send(String first, Player p, String... args) {
        first = getString(first, p == null, args);
        if (first == null) return;
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

    private String getString(String first, boolean b, String[] args) {
        if (first == null) return null;
        if (b) return null;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                first = first.replace("{" + i + "}", args[i]);
            }
        }
        return first;
    }

    public void sendToConsole(String first, String... args) {
        CommandSender ccs = Bukkit.getConsoleSender();
        first = getString(first, false, args);
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
        if (head == null) {
            p.sendMessage(in);
        } else
            p.sendMessage(head + in);
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
            if (nh.startsWith("!")) return nq;
            String[] nqs = nq.split("=.=");
            if (nqs.length > 3) {
                p.playSound(p.getLocation(), Musicg.getSound(nqs[0]), Float.parseFloat(nqs[1]), Float.parseFloat(nqs[2]));
                nh = nqs[3];
            }
        }
        return nh;
    }

    /**
     * 重载
     */
    public void reload() {
        init();
    }
}
