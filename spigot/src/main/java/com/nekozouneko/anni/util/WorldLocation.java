package com.nekozouneko.anni.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldLocation {

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final double yaw;
    private final double pitch;

    public WorldLocation(String world, double x, double y, double z) {
        this(world, x, y, z, 0d, 0d);
    }

    public WorldLocation(String world, double x, double y, double z, double yaw, double pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public WorldLocation(Location loc) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public String getWorld() {
        return world;
    }

    public World getBukkitWorld() {
        return Bukkit.getWorld(world);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

}
