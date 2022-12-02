package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ANNIGame {

    private final GameManager gm;
    private ANNIMap map;
    private final int id = new Random().nextInt(0x1000000);
    private final String id16 = Integer.toHexString(id);
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private ANNIStatus stat = ANNIStatus.WAITING;

    private final Map<Player, com.nekozouneko.anni.Team> players = new HashMap<>();
    private final Map<com.nekozouneko.anni.Team, Team> teams = new HashMap<>();
    private final KeyedBossBar bb = Bukkit.createBossBar(
            new NamespacedKey(plugin, id16),
            "待機中",
            BarColor.GREEN,
            BarStyle.SOLID,
            BarFlag.CREATE_FOG
    );

    private final Map<com.nekozouneko.anni.Team, Integer> nexusHealth = new HashMap<>();
    private final Map<UUID, ItemStack[]> inventories = new HashMap<>();

    protected ANNIGame(GameManager gm){
        this.gm = gm;

        initTeam();

        initNexus();
        randomMap();
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
                plugin.getConfig().getString("anni.teams.spectator.prefix", "&7[&8SP&7] &r")
        ));

        red.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.red.display", "赤チーム")
        ));
        blue.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.blue.display", "青チーム")
        ));
        yellow.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.yellow.display", "黄チーム")
        ));
        green.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.green.display", "緑チーム")
        ));
        spec.setDisplayName(NChatColor.replaceAltColorCodes(
                plugin.getConfig().getString("anni.teams.spectator.display", "観戦チーム")
        ));


        red.setAllowFriendlyFire(false);
        blue.setAllowFriendlyFire(false);
        yellow.setAllowFriendlyFire(false);
        green.setAllowFriendlyFire(false);

        teams.put(com.nekozouneko.anni.Team.RED, red);
        teams.put(com.nekozouneko.anni.Team.BLUE, blue);
        if (gm.getRuleType() == 4) {
            teams.put(com.nekozouneko.anni.Team.YELLOW, yellow);
            teams.put(com.nekozouneko.anni.Team.GREEN, green);
        }
        teams.put(com.nekozouneko.anni.Team.SPECTATOR, spec);
    }

    private void initNexus() {
        nexusHealth.clear();
        nexusHealth.put(com.nekozouneko.anni.Team.RED, plugin.getConfig().getInt("anni.nexus.health", 100));
        nexusHealth.put(com.nekozouneko.anni.Team.BLUE, plugin.getConfig().getInt("anni.nexus.health", 100));
        nexusHealth.put(com.nekozouneko.anni.Team.YELLOW, plugin.getConfig().getInt("anni.nexus.health", 100));
        nexusHealth.put(com.nekozouneko.anni.Team.GREEN, plugin.getConfig().getInt("anni.nexus.health", 100));
        nexusHealth.put(com.nekozouneko.anni.Team.SPECTATOR, -1);
    }

    public void randomMap() {
        Map<String, ANNIMap> maps = ANNIPlugin.getMM().getMaps();
        int siz = maps.values().size();
        Random r = new Random();
        if (siz <= 1) {
            this.map = null;
        } else {
            int a = r.nextInt(siz);
            this.map = (ANNIMap) maps.values().toArray()[a];
        }
    }

    public ANNIMap getMap() {
        return map;
    }

    public List<Player> getPlayers() {
        return Arrays.asList(players.keySet().toArray(new Player[0]));
    }

    public List<Player> getPlayers(com.nekozouneko.anni.Team filter) {
        List<Player> filtered = new ArrayList<>();
        players.forEach((p, t) -> {
            if (t == filter) {
                filtered.add(p);
            }
        });
        return filtered;
    }

    public com.nekozouneko.anni.Team getPlayerJoinedTeam(Player p) {
        return players.get(p);
    }

    public Team getScoreBoardTeam(com.nekozouneko.anni.Team t) {
        return teams.get(t);
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

    public GameManager getManager() {
        return gm;
    }

    public void setMap(ANNIMap map) {
        this.map = map;
    }

    public void changeStatus(ANNIStatus stat) {
        if (stat == ANNIStatus.CANT_START) return;
        this.stat = stat;

    }

    public void changeTeam(Player p, com.nekozouneko.anni.Team t) {
        Team old = teams.get(players.get(p));
        players.put(p, t);

        old.removeEntry(p.getName());
        teams.get(t).addEntry(p.getName());
    }

    public com.nekozouneko.anni.Team randomTeam() {
        Map<com.nekozouneko.anni.Team, Integer> members = new HashMap<>();
        teams.keySet().forEach((t) -> members.put(t, teams.get(t).getSize()));
        com.nekozouneko.anni.Team t = ANNIUtil.balancingJoin(members);
        return t;
    }

    public void join(Player p) {
        if (!players.containsKey(p)) {
            Map<com.nekozouneko.anni.Team, Integer> members = new HashMap<>();
            teams.keySet().forEach((t) -> members.put(t, teams.get(t).getSize()));
            com.nekozouneko.anni.Team t = ANNIUtil.balancingJoin(members);
            players.put(p, ANNIUtil.balancingJoin(members));
            teams.get(t).addEntry(p.getName());
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
        if (!(players.size() >= gm.getMinPlayers() && players.size() <= gm.getMaxPlayers())) return false;
        if (stat != ANNIStatus.WAITING) return false;

        return true;
    }

    public boolean start(){
        if (canStart()) {
            changeStatus(ANNIStatus.PHASE_ONE);

            // red


            return true;
        }

        return false;
    }

    public void restart() {
        end(true);
        stat = ANNIStatus.WAITING;
        initTeam();
        initNexus();
        randomMap();
    }

    public void end(boolean force) {
        if (stat == ANNIStatus.CANT_START) return;
        if (force) {
            stat = ANNIStatus.STOPPING;
            teams.values().forEach(Team::unregister);

            for (Team ts : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                if (ts.getName().matches("(red|yellow|blue|green|spectator)_([0-9a-fA-F]{1,6})")) {
                    ts.unregister();
                }
            }
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
        if (t == null) return;

        if (t != com.nekozouneko.anni.Team.SPECTATOR) {
            Integer hth = nexusHealth.get(t);
            nexusHealth.put(t, hth + h);
        }
    }

    public void takeNexusHealth(com.nekozouneko.anni.Team t, Integer h) {
        if (t == null) return;

        if (t != com.nekozouneko.anni.Team.SPECTATOR) {
            Integer hth = nexusHealth.get(t);
            nexusHealth.put(t, hth - h);
        }
    }

    public Integer getNexusHealth(com.nekozouneko.anni.Team t) {
        if (t == null) throw new NullPointerException("Argument of Team is null.");
        if (t == com.nekozouneko.anni.Team.SPECTATOR) throw new IllegalArgumentException("Team Spectator not has nexus health.");

        return nexusHealth.get(t);
    }

    // messages

    public void broadcast(String message) {
        String m = NChatColor.replaceAltColorCodes(message);
        for (Player p:players.keySet()) {
            p.sendMessage(m);
        }
    }

    public void broadcast(com.nekozouneko.anni.Team team, String message) {
        Team t = teams.get(team);
        if (t == null) return;
        t.getEntries().forEach((s) -> {
            Player p = Bukkit.getPlayer(s);
            if (p == null) return;
            p.sendMessage(NChatColor.replaceAltColorCodes(message));
        });
    }

}
