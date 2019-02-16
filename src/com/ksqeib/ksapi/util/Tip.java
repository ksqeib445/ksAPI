package com.ksqeib.ksapi.util;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class Tip {
    private Io io;
    public Tip(Io io){
        this.io=io;
    }
    public void getDnS(CommandSender p, String m, String args[]) {
        send(io.getMessage(m), p, args);
    }

    public void getDnS(Player p, String m, String args[]) {
        send(musicc(p, m), p, args);
    }

    public void getDnS(UUID uuid, String m, String args[]) {
        send(musicc(Bukkit.getPlayer(uuid), m), Bukkit.getPlayer(uuid), args);
    }

    public void send(String first, CommandSender p, String args[]) {
        Player pl = null;
        Boolean isp = false;
        if (p instanceof Player) {
            pl = (Player) p;
            isp = true;
        }
        if(p==null)return;
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
                pl.sendRawMessage(first.substring(1));
            } else {
                p.sendMessage(first.substring(1));
            }
        } else if (first.startsWith("!")) {
            for(Player opl:Bukkit.getOnlinePlayers()){
                music(opl,first.substring(1));
            }
        } else if (first.startsWith("-")) {
            p.sendMessage(io.getMessage("mhead") + first.substring(1));
        } else {
            //判断title
            String bes[] = first.split("-;-");
            if (bes.length > 4) {
                if (isp) {
                    pl.sendTitle(bes[3], bes[4], Integer.parseInt(bes[0]), Integer.parseInt(bes[1]), Integer.parseInt(bes[2]));
                } else {
                    p.sendMessage(bes[3] + " " + bes[4]);
                }
            } else {
                p.sendMessage(io.getMessage("mhead") + first);
            }
        }
    }

    public void send(String first, Player p, String args[]) {
        if(p==null)return;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                first = first.replace("{" + i + "}", args[i]);
            }
        }
        if (first.startsWith("=")) {
            //=判断
            p.sendMessage(first.substring(1));
        } else if (first.startsWith("+")) {
            ActionBarAPI.sendActionBar(p,first.substring(1));
//            p.sendRawMessage(first.substring(1));
        } else if (first.startsWith("-")) {
            p.sendMessage(io.getMessage("mhead") + first.substring(1));
        } else if (first.startsWith("!")) {
            Bukkit.getServer()
                    .broadcastMessage(first.substring(1));
        } else {
            //判断title
            String bes[] = first.split("-;-");
            if (bes.length > 4) {
                p.sendTitle(bes[3], bes[4], Integer.parseInt(bes[0]), Integer.parseInt(bes[1]), Integer.parseInt(bes[2]));
            } else {
                p.sendMessage(io.getMessage("mhead") + first);
            }
        }
    }
    public String musicc(Player p,String m){
        return music(p,io.getMessage(m));
    }
    public String music(Player p, String nq) {
        String nh = nq;
        if (nq != null) {
            String nqs[] = nq.split("=.=");
            if (nqs != null) {
                if (nqs.length > 3) {
                    p.playSound(p.getLocation(), Musicg.getSound(nqs[0]), Float.parseFloat(nqs[1]), Float.parseFloat(nqs[2]));
                    nh = nqs[3];
                }
            }
        }
        return nh;
    }
}
