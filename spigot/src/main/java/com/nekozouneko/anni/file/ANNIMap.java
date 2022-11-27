package com.nekozouneko.anni.file;

import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.util.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ANNIMap {

    public static class Nexus {
        private String team;
        private SimpleLocation location;

        public Nexus(String team, SimpleLocation loc) {
            this.team = team;
            this.location = loc;
        }

        public void setTeam(Team t) {
            this.team = t.name().toLowerCase();
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

    private List<Nexus> nexus;

    private int min;
    private int max;
    private int rule;

    public ANNIMap(String world, String display, List<Nexus> nexus, int min, int max, int rule) {
        this.world = world;
        this.display = display;
        this.nexus = nexus;
        this.min = min;
        this.max = max;
        this.rule = rule;
    }

    public ANNIMap(String world, String display, int min, int max, int rule, Nexus... nexus) {
        this.world = world;
        this.display = display;
        this.min = min;
        this.max = max;
        this.rule = rule;
        this.nexus = Arrays.asList(nexus);
    }

    public String getWorld() {
        return world;
    }

    public String getDisplay() {
        return display;
    }

    public List<Nexus> getNexusList() {
        return nexus;
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

    public void addNexus(Nexus... nexus) {
        Collections.addAll(this.nexus, nexus);
    }

    public void removeNexus(Nexus nexus) {
        this.nexus.remove(nexus);
    }

    public void setNexus(List<Nexus> nexus) {
        this.nexus = nexus;
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
