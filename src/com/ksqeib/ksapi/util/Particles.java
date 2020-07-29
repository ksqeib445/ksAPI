package com.ksqeib.ksapi.util;

import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Particles {
    public void test(Player p){
        PacketPlayOutWorldParticles packet=new PacketPlayOutWorldParticles();
        CraftPlayer cp=(CraftPlayer)p;
        cp.getHandle().playerConnection.sendPacket(packet);
    }
}
