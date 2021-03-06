package com.ksqeib.ksapi.util;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * 帮助者，制作一个好看的帮助页面，详情参考其他实现插件
 */
public class Helper {

    public FileConfiguration hY;
    private Permission pe;

    protected Helper(FileConfiguration hY, Permission pe) {
        this.hY = hY;
        this.pe = pe;
    }

    /**
     * 发送无任何指令时的帮助
     *
     * @param cms   发送者
     * @param label 命令label
     */
    public void sendno(CommandSender cms, String label) {
        cms.sendMessage(hY.getString("help.head"));
        cms.sendMessage(hY.getString("help.start") + label + hY.getString("help.help"));
        cms.sendMessage(hY.getString("help.last"));
    }

    /**
     * 打印帮助
     *
     * @param hsl   帮助列表
     * @param cms   发送者
     * @param label label
     */
    private void sendHelp(List<String> hsl, CommandSender cms, String label) {
        for (int i = 0; i < hsl.size(); i++) {
            //隔一行一送
            if (!((i % 2) == 0)) {
                //发送这个列表中的全部
                //发送使用说明
                cms.sendMessage(hsl.get(i));
            } else {
                //发送是什么指令
                cms.sendMessage(hY.getString("help.start") + label + hsl.get(i));
            }
        }
    }

    /**
     * 发送页码
     *
     * @param page 页码
     * @param leng 最大页码
     * @param cms  发送者
     */
    private void PageSend(int page, int leng, CommandSender cms) {
        String str = hY.getString("help.page");
        String bstr = str.replace("[pagenow]", page + "").replace("[maxpage]", leng + "");
        cms.sendMessage(bstr);
    }

    /**
     * 发送帮助页
     *
     * @param cms   发送者
     * @param label label
     * @param args  子参数(第二个参数为页码，第一个参数通常为help)
     */
    public void HelpPage(CommandSender cms, String label, String[] args) {
        cms.sendMessage(hY.getString("help.head"));
        //获取页标签
        List<String> oppages = hY.getStringList("help.oppages");
        List<String> compages = hY.getStringList("help.pages");
        //有多少个页标签
        int opleng = oppages.size();
        int comleng = compages.size();
        int allleng = opleng + comleng;
        int page = 1;
        boolean isop = pe.isPluginAdmin(cms);
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                cms.sendMessage(hY.getString("error.notnum"));
            }
        }
        //获取这页
        List<String> nowPage;
        if (isop) {
            if (page > allleng) {
                page = allleng;
            }
            if (page > comleng) {
                nowPage = hY.getStringList("help.oppage" + (page - comleng));
            } else {
                nowPage = hY.getStringList("help.page" + page);
            }
        } else {
            if (page > comleng) page = comleng;
            nowPage = hY.getStringList("help.page" + page);
        }
        //发送帮助信息
        sendHelp(nowPage, cms, label);
        //发送页码
        if (!isop) {
            //如果不是op
            PageSend(page, comleng, cms);
        } else {
            PageSend(page, allleng, cms);
        }
        cms.sendMessage(hY.getString("help.last"));
    }
}
