package com.nekozouneko.anni.file;

import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.util.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ANNIMap {

    public static class TeamLocation {
        private String team;
        private SimpleLocation location;

        public TeamLocation(String team, SimpleLocation loc) {
            this.team = team;
            this.location = loc;
        }

        public TeamLocation(Team team, SimpleLocation loc) {
            this.team = team.name();
            this.location = loc;
        }

        public void setTeam(Team t) {
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

    public void setNexus(Team t,  SimpleLocation sl) {
        if (!this.nexus.containsValue(sl)) {
            this.nexus.put(t.name(), sl);
        }
    }

    public void removeNexus(Team t) {
        this.nexus.remove(t.name());
    }

    public void setSpawnPoint(Team t, SimpleLocation point) {
        if (!this.spawnpoints.containsValue(point)) {
            this.spawnpoints.put(t.name(), point);
        }
    }

    public void removeSpawnPoint(Team t) {
        this.nexus.remove(t.name());
    }

    @Override
    public String toString() {
        return "ANNIMap[World="+ world +", Display="+ display +", Nexus="+ nexus +", SpawnPoints="+ spawnpoints +"]";
    }
}
