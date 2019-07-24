package com.ksqeib.ksapi.util;

import org.bukkit.Sound;

/**
 * 音乐播放类
 */
public class Musicg {

    /**
     * 获取一种声音
     * @param name 音乐id
     * @return 声音
     */
    public static Sound getSound(String name) {
        Sound[] soundlist = Sound.values();
        for (int i = 0; i < soundlist.length; i++) {
            if (soundlist[i].name().equalsIgnoreCase("BLOCK_" + name)) {
                return Sound.valueOf("BLOCK_" + name);
            }
            if (soundlist[i].name().equalsIgnoreCase(name)) {
                return Sound.valueOf(name);
            }
        }
        return null;
    }

    /**
     * 把所有的音乐id输出到sound.yml 要求开启目录树储存
     * @param io io
     */
    public static void printlisttofile(Io io) {
        if (io.hasData) {
            Sound[] soundlist = Sound.values();
            for (int i = 0; i < soundlist.length; i++) {
                io.loadData("sound").set(i + "", soundlist[i].name());
            }
        }
    }
}
