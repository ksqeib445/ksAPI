package com.ksqeib.ksapi.util;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Helper {

    public FileConfiguration hY;
    private Permission pe;

    public Helper(FileConfiguration hY, Permission pe) {
        this.hY = hY;
        this.pe = pe;
    }

    public void sendno(CommandSender cms, String label) {
        cms.sendMessage(hY.getString("help.head"));
        cms.sendMessage(hY.getString("help.start") + label + hY.getString("help.help"));
        cms.sendMessage(hY.getString("help.last"));
    }

    //打印帮助方法
    public void SendHelp(List<String> hsl, CommandSender cms, String label) {
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

    //打印页码
    private void PageSend(int page, int leng, CommandSender cms) {
        String str = hY.getString("help.page");
        String bstr = str.replace("[pagenow]", page + "").replace("[maxpage]", leng + "");
        cms.sendMessage(bstr);

    }

    //打印帮助页
    public void HelpPage(CommandSender cms, String label, String[] args) {

        cms.sendMessage(hY.getString("help.head"));


        //获取页标签
        List<String> oppages = hY.getStringList("help.oppages");
        List<String> compages = hY.getStringList("help.pages");
        //有多少个页标签
        int opleng = oppages.size();
        int comleng = compages.size();
        int allleng = opleng + comleng;

        if (args.length > 1) {
            int page = 1;
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                cms.sendMessage(hY.getString("error.notnum"));
            }

            if (page > 0 && page <= comleng) {
                //如果不是最后一页

                //获取这页
                List<String> nowPage = hY.getStringList("help.page" + page);
                //发送帮助信息
                SendHelp(nowPage, cms, label);
                //发送页码

                if (!pe.isp(cms, pe.oppage)) {
                    //如果不是op
                    PageSend(page, comleng, cms);
                } else {
                    PageSend(page, allleng, cms);
                }
            } else if (page > comleng) {
                //当给的数字多余最后一页，打印最后一页


                if (!pe.isp(cms, pe.oppage)) {
                    //如果是op
                    List<String> nowPage = hY.getStringList("help.page" + comleng);
                    SendHelp(nowPage, cms, label);

                    PageSend(page, comleng, cms);
                } else {
                    //若是op
                    List<String> nowPage = hY.getStringList("help.oppage" + (allleng - comleng));
                    if (page <= allleng) {
                        nowPage = hY.getStringList("help.oppage" + (page - comleng));
                    }
                    SendHelp(nowPage, cms, label);
                    PageSend(page, allleng, cms);
                }
            } else {
                //如果输入的数字清奇
                //获取第一页
                List<String> comPage = hY.getStringList("help." + compages.get(0));
                //发送
                SendHelp(comPage, cms, label);
            }
        } else {
            //如果输入的什么也不是
            //获取第一页
            List<String> comPage = hY.getStringList("help." + compages.get(0));
            //发送
            SendHelp(comPage, cms, label);
        }
        cms.sendMessage(hY.getString("help.last"));
    }

}
