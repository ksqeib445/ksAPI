package com.ksqeib.ksapi.util;

import org.bukkit.Sound;

public class Musicg {

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

    public static void printlisttofile(Io io) {
        if (io.hasData) {
            Sound[] soundlist = Sound.values();
            for (int i = 0; i < soundlist.length; i++) {
                io.loadData("sound").set(i + "", soundlist[i].name());
            }
        }
    }
}
