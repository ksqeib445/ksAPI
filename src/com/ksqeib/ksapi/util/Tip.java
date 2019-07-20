package com.ksqeib.ksapi.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Tip {
    public Boolean islist;
    public HashMap<String, List<String>> lmMap;
    public HashMap<String, String> mMap;
    public FileConfiguration messagefile;
    public boolean isnohead = false;
    public Io io;

    public Tip(Io io, boolean islist, String name) {
        this.io=io;
        this.islist = islist;
        messagefile = io.loadYamlFile(name, true);
        init();
    }
    public void init() {
        if (islist) {
            lmMap = Io.getAlllist(messagefile);
        } else {
            mMap = Io.getAll(messagefile);
        }
    }

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
            return mMap.get(m).replace("&", "§");
        }
    }

    public List<String> getMessageList(String m) {
        if (islist) {
            if(lmMap==null){
                Bukkit.getLogger().warning("严重！插件似乎未初始化完成！");
                new Exception().printStackTrace();
                return null;
            }
            List<String> strings=lmMap.get(m);
            if(strings==null){
                Bukkit.getLogger().warning("在读取列表语言"+m+"时发生了错误");
            }else {
                return strings;
            }
        }
        return null;
    }

    public void getDnS(CommandSender p, String m, String[] args) {
        if (!islist) {
            send(getMessage(m), p, args);
        } else {
            for (String mes :getMessageList(m)) {
                send(mes, p, args);
            }
        }
    }

    public void getDnS(Player p, String m, String[] args) {
        if (islist) {
            for (String mes : getMessageList(m)) {
                send(music(p, mes), p, args);
            }
        } else {
            send(musicc(p, m), p, args);
        }
    }

    public void getDnS(UUID uuid, String m, String[] args) {
        getDnS(Bukkit.getPlayer(uuid), m, args);
    }

    public void broadcastMessage(String first, String[] args) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(first, p, args);
        }
    }

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
        if (first.startsWith("=")) {
            //=判断
            p.sendMessage(first.substring(1));
        } else if (first.startsWith("+")) {
            if (isp) {
//                pl.sendActionBar(first.substring(1));
                ActionBar.sendActionBar(pl, first.substring(1));
            } else {
                p.sendMessage(first.substring(1));
            }
        } else if (first.startsWith("!")) {
            for (Player opl : Bukkit.getOnlinePlayers()) {
                music(opl, first.substring(1));
            }
        } else if (first.startsWith("-")) {
            sendwithhead(p, first.substring(1));
        } else {
            //判断title
            String[] bes = first.split("-;-");
            if (bes.length > 4) {
                if (isp) {
                    pl.sendTitle(bes[3], bes[4], Integer.parseInt(bes[0]), Integer.parseInt(bes[1]), Integer.parseInt(bes[2]));
                } else {
                    p.sendMessage(bes[3] + " " + bes[4]);
                }
            } else {
                sendwithhead(p, first);
            }
        }
    }

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
            ActionBar.sendActionBar(p, first.substring(1));
//            p.sendRawMessage(first.substring(1));
        } else if (first.startsWith("-")) {
            sendwithhead(p, first.substring(1));
        } else if (first.startsWith("!")) {
            Bukkit.getServer()
                    .broadcastMessage(first.substring(1));
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
            if (getMessage("mhead") == null) {
                p.sendMessage(in);
            } else
                p.sendMessage(getMessage("mhead") + in);
        }
    }

    public String musicc(Player p, String m) {
        return music(p, getMessage(m));
    }

    public String music(Player p, String nq) {
        String nh = nq;
        if (nq != null) {
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
    public void reload(){
        messagefile=io.loadYamlFile(messagefile.getCurrentPath(),true );
    }
}
