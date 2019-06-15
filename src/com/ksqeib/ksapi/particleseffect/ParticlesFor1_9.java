package com.ksqeib.ksapi.particleseffect;

import org.bukkit.Particle;

public class ParticlesFor1_9 {

    public static Particle getParticle(int id) {
        return Particle.values()[id - 1];
    }
}
