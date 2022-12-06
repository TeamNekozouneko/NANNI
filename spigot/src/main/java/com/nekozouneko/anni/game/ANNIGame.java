package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.task.UpdateBossBar;
import com.nekozouneko.anni.util.SimpleLocation;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
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
    private Long timer = -1L;
    private boolean lockTimer = false;

    private final Map<Player, com.nekozouneko.anni.Team> players = new HashMap<>();
    private final Map<com.nekozouneko.anni.Team, Team> teams = new HashMap<>();
    private final Map<com.nekozouneko.anni.Team, Boolean> losedTeams = new HashMap<>();
    public static final Map<com.nekozouneko.anni.Team, ItemStack[]> teamArmor = Collections.unmodifiableMap(
            new HashMap<com.nekozouneko.anni.Team, ItemStack[]>() {{
        put(com.nekozouneko.anni.Team.RED, ANNIUtil.createColorLeatherArmor(Color.RED));
        put(com.nekozouneko.anni.Team.BLUE, ANNIUtil.createColorLeatherArmor(Color.BLUE));
        put(com.nekozouneko.anni.Team.YELLOW, ANNIUtil.createColorLeatherArmor(Color.YELLOW));
        put(com.nekozouneko.anni.Team.GREEN, ANNIUtil.createColorLeatherArmor(Color.GREEN));
    }}
    );
    private final KeyedBossBar bb = Bukkit.createBossBar(
            new NamespacedKey(plugin, id16),
            "待機中",
            BarColor.GREEN,
            BarStyle.SOLID,
            BarFlag.CREATE_FOG
    );

    private final Map<com.nekozouneko.anni.Team, Integer> nexusHealth = new HashMap<>();
    private BukkitRunnable bbbr;

    protected ANNIGame(GameManager gm){
        this.gm = gm;

        initTeam();

        initNexus();
        randomMap();
        bbbr = new UpdateBossBar(this);
        bbbr.runTaskTimer(plugin, 20, 20);
    }

    private void initTeam() {
        Scoreboard sb = ANNIPlugin.getSb();
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
                plugin.getConfig().getString("anni.teams.spectator.display", "観戦者")
        ));

        red.setAllowFriendlyFire(false);
        blue.setAllowFriendlyFire(false);
        yellow.setAllowFriendlyFire(false);
        green.setAllowFriendlyFire(false);

        teams.put(com.nekozouneko.anni.Team.RED, red);
        teams.put(com.nekozouneko.anni.Team.BLUE, blue);
        losedTeams.put(com.nekozouneko.anni.Team.RED, false);
        losedTeams.put(com.nekozouneko.anni.Team.BLUE, false);
        if (gm.getRuleType() == 4) {
            teams.put(com.nekozouneko.anni.Team.YELLOW, yellow);
            teams.put(com.nekozouneko.anni.Team.GREEN, green);
            losedTeams.put(com.nekozouneko.anni.Team.YELLOW, false);
            losedTeams.put(com.nekozouneko.anni.Team.GREEN, false);
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
        nexusHealth.put(com.nekozouneko.anni.Team.NOT_JOINED, -1);
    }

    public void randomMap() {
        Map<String, ANNIMap> maps = ANNIPlugin.getMM().getMaps();
        int siz = maps.values().size();
        Random r = new Random();
        if (siz == 0) {
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

    public List<com.nekozouneko.anni.Team> getTeams(boolean excludeSpec) {
        List<com.nekozouneko.anni.Team> s = new ArrayList<>();
        for (com.nekozouneko.anni.Team t:teams.keySet()) {
            if (!(excludeSpec && t.isSpectator())) s.add(t);
        }

        return s;
    }

    public void changeStatus(ANNIStatus stat) {
        if (stat == this.stat) return;
        if (stat == ANNIStatus.CANT_START) return;
        this.stat = stat;

        if (stat == ANNIStatus.PHASE_ONE) phaseOne();
        if (stat == ANNIStatus.PHASE_TWO) phaseTwo();
        if (stat == ANNIStatus.PHASE_THREE) phaseThree();
    }

    public void changeTeam(Player p, com.nekozouneko.anni.Team t) {
        if (stat.getPhaseId() <= 0) {
            Team old = teams.get(players.get(p));
            Team new1 = teams.get(t);
            players.put(p, t);

            if (old != null) old.removePlayer(p);
            if (new1 != null) new1.addPlayer(p);
        } else {
            Team old = teams.get(players.get(p));
            Team new1 = teams.get(t);
            players.put(p, t);

            if (old != null) old.removePlayer(p);
            if (new1 != null) new1.addPlayer(p);

            setDefaultKitToPlayer(p);
            p.teleport(getTeamSpawnPoint(p));
        }
    }

    public com.nekozouneko.anni.Team randomTeam() {
        Map<com.nekozouneko.anni.Team, Integer> members = new HashMap<>();
        teams.keySet().forEach((t) -> members.put(t, teams.get(t).getSize()));
        com.nekozouneko.anni.Team t = ANNIUtil.balancingJoin(members);
        return t;
    }

    public void randomTeamJoin(Player p) {
        if (players.get(p).isSpectator()) {
            com.nekozouneko.anni.Team t = randomTeam();
            players.put(p, t);
            teams.get(t).addPlayer(p);
        }
    }

    public void join(Player p) {
        if (!players.containsKey(p)) {
            players.put(p, com.nekozouneko.anni.Team.NOT_JOINED);
            /*if (stat.getPhaseId() <= 0) {
                Map<com.nekozouneko.anni.Team, Integer> members = new HashMap<>();
                teams.keySet().forEach((t) -> members.put(t, teams.get(t).getSize()));
                com.nekozouneko.anni.Team t = ANNIUtil.balancingJoin(members);
                players.put(p, t);
                teams.get(t).addPlayer(p);
            }*/
        }
    }

    public boolean isJoined(Player p) {
        return players.containsKey(p);
    }

    public void leave(Player p) {
        teams.values().forEach((t) -> {
            if (t.hasEntry(p.getName())) t.removeEntry(p.getName());
            if (t.hasPlayer(p)) t.removePlayer(p);
        });
        players.remove(p);
    }

    public void setDefaultKitToPlayer(Player p) {
        final ItemStack[] defInv = new ItemStack[36];
        defInv[0] = new ItemStack(Material.WOODEN_SWORD);
        defInv[1] = new ItemStack(Material.GOLDEN_PICKAXE);
        defInv[2] = new ItemStack(Material.WOODEN_AXE);
        defInv[3] = new ItemStack(Material.WOODEN_SHOVEL);


        com.nekozouneko.anni.Team t = players.get(p);

        ItemStack[] armor = teamArmor.get(t);
        if (armor == null) armor = new ItemStack[0];

        if (!t.isSpectator()) {
            p.getInventory().setContents(defInv);
        }
        p.getInventory().setArmorContents(armor);
    }

    public Location getTeamSpawnPoint(Player p) {
        SimpleLocation sl = getMap().getSpawnPoints().get(getPlayerJoinedTeam(p).name());
        if (sl != null) {
            return sl.toLocation(Bukkit.getWorld(getMap().getWorld()));
        } else return Bukkit.getWorld(getMap().getWorld()).getSpawnLocation();
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

            for (Player p : players.keySet()) {
                if (players.get(p) == com.nekozouneko.anni.Team.NOT_JOINED) {
                    randomTeamJoin(p);
                }

                p.teleport(getTeamSpawnPoint(p));
                setDefaultKitToPlayer(p);
            }

            return true;
        }

        return false;
    }

    public void restart() {
        end(false);
        stat = ANNIStatus.WAITING;
        initNexus();
        randomMap();

        for (com.nekozouneko.anni.Team t : getTeams(true)) {
            losedTeams.put(t, false);
        }

        unlockTimer();
    }

    public void end(boolean force) {
        if (stat == ANNIStatus.CANT_START) return;
        if (force) {
            stat = ANNIStatus.STOPPING;
            teams.values().forEach(Team::unregister);

            for (Team ts : ANNIPlugin.getSb().getTeams()) {
                if (ts.getName().matches("(red|yellow|blue|green|spectator)_([0-9a-fA-F]{1,6})")) {
                    ts.unregister();
                }
            }
        } else {
            stat = ANNIStatus.WAITING;
            teams.values().forEach((t) -> {
                for (String e : t.getEntries()) t.removeEntry(e);
                for (OfflinePlayer p : t.getPlayers()) t.removePlayer(p);
            });
        }
        players.clear();
    }

    public void pluginDisable() {
        end(true);
        bb.setVisible(false);
        bb.removeAll();
        Bukkit.removeBossBar(new NamespacedKey(plugin, id16));
        bbbr.cancel();
    }

    /* ----- Game ----- */

    public int getLosedTeams() {
        int a = 0;
        for (com.nekozouneko.anni.Team t : getTeams(true)) {
            if (isLose(t)) a++;
        }

        return a;
    }

    public void loseTeam(com.nekozouneko.anni.Team t) {
        if (!isLose(t)) {
            losedTeams.put(t, true);
        }
    }

    public boolean isLose(com.nekozouneko.anni.Team t) {
        Boolean b = losedTeams.get(t);
        if (b == null) b = false;

        return b;
    }

    public List<com.nekozouneko.anni.Team> getNotLostTeams() {
        List<com.nekozouneko.anni.Team> ts = new ArrayList<>();

        for (com.nekozouneko.anni.Team t : getTeams(true)) {
            if (!isLose(t)) ts.add(t);
        }

        return ts;
    }

    // Nexus

    public void healNexusHealth(com.nekozouneko.anni.Team t, Integer h) {
        if (t == null) return;

        if (!t.isSpectator()) {
            Integer hth = nexusHealth.get(t);
            nexusHealth.put(t, hth + h);
        }
    }

    public void damageNexusHealth(com.nekozouneko.anni.Team t, Integer h) {
        if (t == null) return;

        if (!t.isSpectator()) {
            Integer hth = nexusHealth.get(t);
            nexusHealth.put(t, hth - h);
        }
    }

    public Integer getNexusHealth(com.nekozouneko.anni.Team t) {
        if (t == null) throw new NullPointerException("Argument of Team is null.");
        if (t.isSpectator()) throw new IllegalArgumentException("Team Spectator not has nexus health.");

        return nexusHealth.get(t);
    }

    public String getNexusHealthForBoard(com.nekozouneko.anni.Team t) {
        Integer i = getNexusHealth(t);
        boolean isLose = losedTeams.get(t);

        if (isLose || i <= 0) {
            return "§c✖";
        } else {
            return i.toString();
        }
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

    // timer

    public long takeTimer() {
        return takeTimer(1);
    }

    public long takeTimer(long l) {
        if (!lockTimer) this.timer -= l;
        return this.timer;
    }

    public long addTimer() {
        return addTimer(1);
    }

    public long addTimer(long l) {
        if (!lockTimer) this.timer += l;
        return this.timer;
    }

    public long setTimer(long l) {
        if (!lockTimer) this.timer = l;
        return this.timer;
    }

    public long getTimer() {
        return timer;
    }

    public void lockTimer() {
        lockTimer = true;
    }

    public void unlockTimer() {
        lockTimer = false;
    }

    public boolean isTimerLocked() {
        return lockTimer;
    }

    // phase

    private void phaseOne() {
        for (com.nekozouneko.anni.Team t : teams.keySet()) {
            for (String b :
                    ANNIBigMessage.createMessage(
                            '1', teams.get(t).getColor().getChar(), "フェーズ1が開始されました。",
                            "§7あなたは"+teams.get(t).getColor()+teams.get(t).getDisplayName()+"§7に参加しました。"
                    )
            ) {
                broadcast(t, b);
            }
        }
    }

    private void phaseTwo() {
        for (com.nekozouneko.anni.Team t : teams.keySet()) {
            for (String b :
                    ANNIBigMessage.createMessage(
                            '2', teams.get(t).getColor().getChar(), "フェーズ2が開始されました。",
                            "§7ネクサスが破壊可能になります。"
                    )
            ) {
                broadcast(t, b);
            }
        }
    }

    private void phaseThree() {
        for (com.nekozouneko.anni.Team t : teams.keySet()) {
            for (String b :
                    ANNIBigMessage.createMessage(
                            '3', teams.get(t).getColor().getChar(), "フェーズ3が開始されました。",
                            "§7中央のダイヤ鉱石が採掘可能になります。"
                    )
            ) {
                broadcast(t, b);
            }
        }
    }

}
