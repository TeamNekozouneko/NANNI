package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ANNIGame {

    private ANNIMap map;
    private final int id = new Random().nextInt(0x1000000);
    private final String id16 = Integer.toHexString(id);
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private ANNIStatus stat = ANNIStatus.WAITING;

    private final List<Player> players = new ArrayList<>();
    private final Map<com.nekozouneko.anni.Team, Team> teams = new HashMap<>();
    private final KeyedBossBar bb = Bukkit.createBossBar(
            new NamespacedKey(plugin, id16),
            null,
            BarColor.GREEN,
            BarStyle.SOLID,
            BarFlag.CREATE_FOG
    );

    private final Map<com.nekozouneko.anni.Team, Integer> nexusHealth = new HashMap<>();

    protected ANNIGame(ANNIMap map){
        if (map == null) throw new NullPointerException("Argument 'map' putted null!");
        if (map.getRule() <= map.getNexusList().size()) {
            stat = ANNIStatus.CANT_START;
        }

        this.map = map;
        initTeam();
        initNexus();
    }

    private void initTeam() {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team red = sb.registerNewTeam("red_" + id16);
        Team blue = sb.registerNewTeam("blue_" + id16);
        Team yellow = sb.registerNewTeam("yellow_" + id16);
        Team green = sb.registerNewTeam("green_" + id16);
        Team spec = sb.registerNewTeam("spectator_" + id16);

        red.setColor(ChatColor.RED);
        blue.setColor(ChatColor.BLUE);
        yellow.setColor(ChatColor.YELLOW);
        green.setColor(ChatColor.GREEN);
        spec.setColor(ChatColor.DARK_GRAY);

        red.setCanSeeFriendlyInvisibles(true);
        blue.setCanSeeFriendlyInvisibles(true);
        yellow.setCanSeeFriendlyInvisibles(true);
        green.setCanSeeFriendlyInvisibles(true);

        red.setPrefix(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.red.prefix", "&7[&cR&7] &r")
        ));
        blue.setPrefix(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.blue.prefix", "&7[&9B&7] &r")
        ));
        yellow.setPrefix(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.yellow.prefix", "&7[&eY&7] &r")
        ));
        green.setPrefix(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.green.prefix", "&7[&aG&7] &r")
        ));
        spec.setPrefix(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.red.prefix", "&7[&8SP&7] &r")
        ));

        red.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.red.display", "赤チーム")
        ));
        blue.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.red.display", "赤チーム")
        ));
        yellow.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.red.display", "赤チーム")
        ));
        green.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.red.display", "赤チーム")
        ));


        red.setAllowFriendlyFire(false);
        blue.setAllowFriendlyFire(false);
        yellow.setAllowFriendlyFire(false);
        green.setAllowFriendlyFire(false);

        teams.put(com.nekozouneko.anni.Team.RED, red);
        teams.put(com.nekozouneko.anni.Team.BLUE, blue);
        teams.put(com.nekozouneko.anni.Team.YELLOW, yellow);
        teams.put(com.nekozouneko.anni.Team.GREEN, green);
        teams.put(com.nekozouneko.anni.Team.SPECTATOR, spec);
    }

    private void initNexus() {
        nexusHealth.clear();
        nexusHealth.put(com.nekozouneko.anni.Team.RED, plugin.getConfig().getInt("anni.nexus.health", 100));
        nexusHealth.put(com.nekozouneko.anni.Team.BLUE, plugin.getConfig().getInt("anni.nexus.health", 100));
        nexusHealth.put(com.nekozouneko.anni.Team.YELLOW, plugin.getConfig().getInt("anni.nexus.health", 100));
        nexusHealth.put(com.nekozouneko.anni.Team.GREEN, plugin.getConfig().getInt("anni.nexus.health", 100));
    }

    public ANNIMap getMap() {
        return map;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public ANNIStatus getStatus() {
        return stat;
    }

    public int getId() {
        return id;
    }

    public String getId16() {
        return id16;
    }

    public BossBar getBossBar() {
        return bb;
    }

    public void changeStatus(ANNIStatus stat) {
        if (stat == ANNIStatus.CANT_START) return;
        this.stat = stat;

    }

    public void join(Player p) {
        if (!players.contains(p)) {
            Map<Team, Integer> members = new HashMap<>();
            teams.values().forEach((t) -> members.put(t, t.getSize()));
            players.add(p);
        }
    }

    public void leave(Player p) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        teams.values().forEach((t) -> {
            if (t.hasEntry(p.getName())) t.removeEntry(p.getName());
        });
        players.remove(p);
    }

    /* ----- */

    public boolean canStart() {
        if (!(players.size() >= map.getMin() && players.size() <= map.getMax())) return false;
        if (stat != ANNIStatus.WAITING) return false;

        return true;
    }

    public boolean start(){
        if (canStart()) {

            return true;
        }

        return false;
    }

    public void restart() {
        if (stat == ANNIStatus.CANT_START) return;
        end(true);
        stat = ANNIStatus.WAITING;
        initTeam();
        initNexus();
    }

    public void end(boolean force) {
        if (stat == ANNIStatus.CANT_START) return;
        if (force) {
            stat = ANNIStatus.STOPPING;
            teams.values().forEach(Team::unregister);
        } else {
            stat = ANNIStatus.WAITING;
            teams.values().forEach((t) -> {
                for (String e : t.getEntries()) t.removeEntry(e);
            });
        }

        players.clear();
    }

    /* ----- Game ----- */

    // Nexus

    public void addNexusHealth(com.nekozouneko.anni.Team t, Integer h) {

    }

    public void takeNexusHealth(com.nekozouneko.anni.Team t, Integer h) {

    }

    public void getNexusHealth(com.nekozouneko.anni.Team t) {

    }

}
