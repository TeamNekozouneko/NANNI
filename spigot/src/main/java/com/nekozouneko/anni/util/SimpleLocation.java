package com.nekozouneko.anni.util;

import org.bukkit.Location;
import org.bukkit.World;

public class SimpleLocation {

    private final double x;
    private final double y;
    private final double z;

    public SimpleLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SimpleLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
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

    public Location toLocation(World w) {
        return new Location(w, x, y, z);
    }
}
