package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ANNIGame {

    private final ANNIMap map;
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private ANNIStatus stat = ANNIStatus.WAITING;
    private final List<Player> players = new ArrayList<>();
    private final Map<com.nekozouneko.anni.Team, Team> teams = new HashMap<>();

    protected ANNIGame(ANNIMap map){
        if (map.getRule() <= map.getNexusList().size()) {
            throw new ExceptionInInitializerError("Needs "+ map.getRule() +" nexus but only has " + map.getNexusList().size());
        }

        this.map = map;
        initTeam();
    }

    private void initTeam() {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team red = sb.registerNewTeam("red_" + map.getWorld());
        Team blue = sb.registerNewTeam("blue_" + map.getWorld());
        Team yellow = sb.registerNewTeam("yellow_" + map.getWorld());
        Team green = sb.registerNewTeam("green_" + map.getWorld());
        Team spec = sb.registerNewTeam("spectator_" + map.getWorld());

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

    public ANNIMap getMap() {
        return map;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public ANNIStatus getStatus() {
        return stat;
    }

    public void changeStatus(ANNIStatus stat) {
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

        return true;
    }

    public boolean start(){
        if (canStart()) {


            return true;
        }

        return false;
    }

    public void end(boolean force) {
        teams.values().forEach(Team::unregister);
        stat = ANNIStatus.STOPPING;
    }

}
