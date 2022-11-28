package com.nekozouneko.anni.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.Random;

public class BlockDestroyUtil {

    public static void nexusDestroyParticleSound(Location loc) {
        Random r = new Random();
        World w = loc.getWorld();
        double x = loc.getX() + 0.5;
        double y = loc.getY() + 0.5;
        double z = loc.getZ() + 0.5;
        w.spawnParticle(
                Particle.SMOKE_NORMAL, x, y, z,
                100, 0.1, 0.1, 0.1, 0.1
        );
        w.spawnParticle(
                Particle.LAVA, x, y, z,
                50, 0.25, 0.25, 0.25, 0.1
        );
        w.playSound(loc, Sound.BLOCK_ANVIL_PLACE, 1, r.nextFloat());
    }

    public static void finalNexusDestroyParticleSound(Location loc) {
        final Random r = new Random();
        World w = loc.getWorld();
        double x = loc.getX() + 0.5;
        double y = loc.getY() + 0.5;
        double z = loc.getZ() + 0.5;

        w.spawnParticle(
                Particle.SMOKE_NORMAL, x, y, z,
                100, 0.1, 0.1, 0.1, 0.1
        );
        w.spawnParticle(
                Particle.LAVA, x, y, z,
                50, 0.25, 0.25, 0.25, 0.1
        );
        w.spawnParticle(
                Particle.EXPLOSION_HUGE, x, y, z,
                3, 3f, 3f, 3f, 0.1
        );
        w.playSound(loc, Sound.BLOCK_ANVIL_PLACE, 1, r.nextFloat());
        w.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 100f, 0f);
    }

}
