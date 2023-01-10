package com.nekozouneko.anni.file;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNITeam;
import com.nekozouneko.anni.util.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

public class ANNIMap {

    public static class TeamLocation {
        private String team;
        private SimpleLocation location;

        public TeamLocation(String team, SimpleLocation loc) {
            this.team = team;
            this.location = loc;
        }

        public TeamLocation(ANNITeam team, SimpleLocation loc) {
            this.team = team.name();
            this.location = loc;
        }

        public void setTeam(ANNITeam t) {
            this.team = t.name().toUpperCase();
        }

        public void setLocation(SimpleLocation loc) {
            this.location = loc;
        }

        public void setLocation(Location loc) {
            this.location = new SimpleLocation(loc);
        }

        public String getTeam() {
            return this.team;
        }

        public SimpleLocation getLocation() {
            return location;
        }
    }

    private String world;
    private String display;

    private Map<String, SimpleLocation> nexus;
    private Map<String, SimpleLocation> spawnpoints;

    public ANNIMap(String world, String display, Map<String, SimpleLocation> nexus, Map<String, SimpleLocation> spawnpoints) {
        this.world = world;
        this.display = display;
        this.nexus = nexus;
        this.spawnpoints = spawnpoints;
    }

    public String getWorld() {
        return world;
    }

    public String getDisplay() {
        return display;
    }

    public Map<String, SimpleLocation> getNexusList() {
        return nexus;
    }

    public Location getNexusLocation(ANNITeam t, boolean returnCopied) {
        if (returnCopied) {
            return nexus.get(t.name()).toLocation(ANNIPlugin.getGM().getGame().getCopiedMap());
        } else return nexus.get(t.name()).toLocation(Bukkit.getWorld(world));
    }

    public ANNITeam getNexusTeam(Location loc) {
        for (String t:nexus.keySet()) {
            try {
                Location changedWorld = getNexusLocation(ANNITeam.valueOf(t), true);
                if (changedWorld.equals(loc)) {
                    return ANNITeam.valueOf(t);
                } else if (getNexusLocation(ANNITeam.valueOf(t), false).equals(loc)) {
                    return ANNITeam.valueOf(t);
                }
            } catch (IllegalArgumentException ignored) {}
        }

        return null;
    }

    public Map<String, SimpleLocation> getSpawnPoints() {
        return spawnpoints;
    }

    public void setWorld(World w) {
        this.world = w.getName();
    }

    public void setWorld(String w) {
        this.world = w;
    }

    public void setDisplay(String name) {
        this.display = name;
    }

    public void setNexus(ANNITeam t, SimpleLocation sl) {
        if (!this.nexus.containsValue(sl)) {
            this.nexus.put(t.name(), sl);
        }
    }

    public void removeNexus(ANNITeam t) {
        this.nexus.remove(t.name());
    }

    public void setSpawnPoint(ANNITeam t, SimpleLocation point) {
        if (!this.spawnpoints.containsValue(point)) {
            this.spawnpoints.put(t.name(), point);
        }
    }

    public void removeSpawnPoint(ANNITeam t) {
        this.nexus.remove(t.name());
    }

    @Override
    public String toString() {
        return "ANNIMap[World="+ world +", Display="+ display +", Nexus="+ nexus +", SpawnPoints="+ spawnpoints +"]";
    }
}
