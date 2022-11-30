package com.nekozouneko.anni.file;

import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.util.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private List<TeamLocation> nexus;
    private List<TeamLocation> spawnpoints;

    private int min;
    private int max;
    private int rule;

    public ANNIMap(String world, String display, List<TeamLocation> nexus, List<TeamLocation> spawnpoints, int min, int max, int rule) {
        this.world = world;
        this.display = display;
        this.nexus = nexus;
        this.spawnpoints = spawnpoints;
        this.min = min;
        this.max = max;
        this.rule = rule;
    }

    public String getWorld() {
        return world;
    }

    public String getDisplay() {
        return display;
    }

    public List<TeamLocation> getNexusList() {
        return nexus;
    }

    public List<TeamLocation> getSpawnPoints() {
        return spawnpoints;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getRule() {
        return rule;
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

    public void addNexus(TeamLocation... nexus) {
        Collections.addAll(this.nexus, nexus);
    }

    public void removeNexus(TeamLocation nexus) {
        this.nexus.remove(nexus);
    }

    public void setNexus(List<TeamLocation> nexus) {
        this.nexus = nexus;
    }

    public void addSpawnPoints(TeamLocation... points) {

    }

    public void removeSpawnPoints(TeamLocation... points) {

    }

    public void setSpawnPoints(List<TeamLocation> points) {
        this.spawnpoints = points;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setRule(int rule) {
        this.rule = rule;
    }

}
