package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIConfig;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.file.ANNIKit;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.game.manager.GameManager;
import com.nekozouneko.anni.task.SuddenDeathTask;
import com.nekozouneko.anni.task.UpdateBossBar;
import com.nekozouneko.anni.util.SimpleLocation;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.*;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ANNIGame {

    private final GameManager gm;
    private ANNIMap map;
    private World copyWorld;
    private final int id = new Random().nextInt(0x1000000);
    private final String id16 = Integer.toHexString(id);
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private ANNIStatus stat = ANNIStatus.WAITING;
    private Long timer = -1L;
    private boolean lockTimer = false;

    private final Map<Player, com.nekozouneko.anni.Team> players = new HashMap<>();
    private final Map<com.nekozouneko.anni.Team, Team> teams = new HashMap<>();
    private final Map<UUID, String> kit = new HashMap<>();
    private final Map<com.nekozouneko.anni.Team, Boolean> losedTeams = new HashMap<>();
    public static final Map<com.nekozouneko.anni.Team, ItemStack[]> teamArmor = Collections.unmodifiableMap(
            new HashMap<com.nekozouneko.anni.Team, ItemStack[]>() {{
                put(com.nekozouneko.anni.Team.RED, ANNIUtil.createColorLeatherArmor(Color.RED));
                put(com.nekozouneko.anni.Team.BLUE, ANNIUtil.createColorLeatherArmor(Color.BLUE));
                put(com.nekozouneko.anni.Team.YELLOW, ANNIUtil.createColorLeatherArmor(Color.YELLOW));
                put(com.nekozouneko.anni.Team.GREEN, ANNIUtil.createColorLeatherArmor(Color.GREEN));
    }});
    private final KeyedBossBar bb = Bukkit.createBossBar(
            new NamespacedKey(plugin, id16), "待機中", BarColor.GREEN, BarStyle.SOLID
    );
    private final List<ProtectedRegion> regions = new ArrayList<>();

    private final Map<com.nekozouneko.anni.Team, Integer> nexusHealth = new HashMap<>();
    private BukkitRunnable bbbr;
    private BukkitRunnable suddenTask;
    private Map<UUID, TeamPlayerInventory> savedInv = new HashMap<>();

    private static class TeamPlayerInventory {
        private final UUID uuid;
        private final com.nekozouneko.anni.Team team;

        private final ItemStack[] armors;
        private final ItemStack offhand;
        private final ItemStack[] contents;
        private final ItemStack[] ender;

        public TeamPlayerInventory(com.nekozouneko.anni.Team team, Player player) {
            this.team = team;
            this.uuid = player.getUniqueId();
            this.armors = player.getInventory().getArmorContents();
            this.offhand = player.getInventory().getItemInOffHand();
            this.contents = player.getInventory().getContents();
            this.ender = player.getEnderChest().getContents();
        }

        public TeamPlayerInventory(com.nekozouneko.anni.Team team, UUID uuid, ItemStack[] armors, ItemStack offhand, ItemStack[] contents, ItemStack[] ender) {
            this.team = team;
            this.uuid = uuid;
            this.armors = armors;
            this.offhand = offhand;
            this.contents = contents;
            this.ender = ender;
        }

        public void set(Player p) {
            p.getInventory().setContents(contents);
            p.getInventory().setArmorContents(armors);
            p.getInventory().setItemInOffHand(offhand);
            p.getEnderChest().setContents(ender);
        }

        public void set() {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;

            set(p);
        }

        public UUID getUUID() {
            return uuid;
        }

        public Player getPlayer() {
            return Bukkit.getPlayer(uuid);
        }

        public com.nekozouneko.anni.Team getTeam() {
            return team;
        }
    }

    public ANNIGame(GameManager gm){
        this.gm = gm;

        initTeam();
        initNexus();
        randomMap();
        bbbr = new UpdateBossBar(this);
        bbbr.runTaskTimer(plugin, 20, 20);
    }

    private void initTeam() {
        ANNIConfig conf = ANNIPlugin.getANNIConf();
        Scoreboard sb = ANNIPlugin.getSb();
        Team red = sb.registerNewTeam(conf.redTeamID()+"_" + id16);
        Team blue = sb.registerNewTeam(conf.blueTeamID()+"_" + id16);
        Team yellow = sb.registerNewTeam(conf.yellowTeamID()+"_" + id16);
        Team green = sb.registerNewTeam(conf.greenTeamID()+"_"+ id16);
        Team spec = sb.registerNewTeam(conf.spectatorTeamID()+"_" + id16);

        red.setColor(conf.redTeamChatColor());
        blue.setColor(conf.blueTeamChatColor());
        yellow.setColor(conf.yellowTeamChatColor());
        green.setColor(conf.greenTeamChatColor());
        spec.setColor(conf.spectatorTeamChatColor());

        red.setCanSeeFriendlyInvisibles(true);
        blue.setCanSeeFriendlyInvisibles(true);
        yellow.setCanSeeFriendlyInvisibles(true);
        green.setCanSeeFriendlyInvisibles(true);

        red.setPrefix(NChatColor.replaceAltColorCodes(conf.redTeamPrefix()));
        blue.setPrefix(NChatColor.replaceAltColorCodes(conf.blueTeamPrefix()));
        yellow.setPrefix(NChatColor.replaceAltColorCodes(conf.yellowTeamPrefix()));
        green.setPrefix(NChatColor.replaceAltColorCodes(conf.greenTeamPrefix()));
        spec.setPrefix(NChatColor.replaceAltColorCodes(conf.spectatorTeamPrefix()));

        red.setDisplayName(NChatColor.replaceAltColorCodes(conf.redTeamDisplay()));
        blue.setDisplayName(NChatColor.replaceAltColorCodes(conf.blueTeamDisplay()));
        yellow.setDisplayName(NChatColor.replaceAltColorCodes(conf.yellowTeamDisplay()));
        green.setDisplayName(NChatColor.replaceAltColorCodes(conf.greenTeamDisplay()));
        spec.setDisplayName(NChatColor.replaceAltColorCodes(conf.spectatorTeamDisplay()));

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
        nexusHealth.put(com.nekozouneko.anni.Team.RED, ANNIPlugin.getANNIConf().nexusHealth());
        nexusHealth.put(com.nekozouneko.anni.Team.BLUE, ANNIPlugin.getANNIConf().nexusHealth());
        nexusHealth.put(com.nekozouneko.anni.Team.YELLOW, ANNIPlugin.getANNIConf().nexusHealth());
        nexusHealth.put(com.nekozouneko.anni.Team.GREEN, ANNIPlugin.getANNIConf().nexusHealth());
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

    public World getCopiedMap() {
        return copyWorld;
    }

    public void setCopiedMap(World world) {
        copyWorld = world;
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
        if (stat == ANNIStatus.PHASE_FOUR) phaseFour();
        if (stat == ANNIStatus.PHASE_FIVE) phaseFive();
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
            if (t != com.nekozouneko.anni.Team.NOT_JOINED) {
                if (players.get(p).isSpectator()) {
                    Team new1 = teams.get(t);
                    players.put(p, t);

                    if (old != null) old.removePlayer(p);
                    if (new1 != null) new1.addPlayer(p);

                    setKitToPlayer(p);
                    p.teleport(getTeamSpawnPoint(p));
                    p.setHealthScale(p.getHealthScale());
                    p.setSaturation(10.0f);
                    p.setTotalExperience(0);
                    p.setGameMode(GameMode.SURVIVAL);
                } else {
                    p.sendMessage("途中でチームを変更することはできません。");
                }
            } else {

                if (old != null) old.removePlayer(p);
                players.put(p, com.nekozouneko.anni.Team.NOT_JOINED);

                p.getInventory().clear();
                p.getEnderChest().clear();
                p.teleport(ANNIPlugin.getLobby().getLocation().toLocation());
                p.setHealthScale(p.getHealthScale());
                p.setSaturation(10.0f);
                p.setTotalExperience(0);
                p.setGameMode(GameMode.ADVENTURE);
            }
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
            broadcast(p.getName() + "§eがゲームに参加しました");

            if (savedInv.containsKey(p.getUniqueId())) {
                TeamPlayerInventory tpi = savedInv.get(p.getUniqueId());

                changeTeam(p, tpi.getTeam());
                tpi.set(p);

                savedInv.remove(p.getUniqueId());
            }
        }
    }

    public boolean isJoined(Player p) {
        return players.containsKey(p);
    }

    public void leave(Player p) {
        if (players.containsKey(p) && stat.getPhaseId() >= 1) {
            savedInv.put(p.getUniqueId(), new TeamPlayerInventory(players.get(p), p));
        }

        teams.values().forEach((t) -> {
            if (t.hasEntry(p.getName())) t.removeEntry(p.getName());
            if (t.hasPlayer(p)) t.removePlayer(p);
        });
        players.remove(p);
        p.getEnderChest().clear();
        p.getInventory().clear();
    }

    public void setKitToPlayer(Player p) {
        String kitId = kit.get(p.getUniqueId());
        if (kitId == null) kitId = "<default>";
        ANNIKit k = ANNIPlugin.getKM().getLoadedKits().get(kitId);
        if (k == null) k = ANNIPlugin.DEFAULT_KIT;

        final ItemStack[] defInv = k.getDecodedContent();

        com.nekozouneko.anni.Team t = players.get(p);

        if (!t.isSpectator()) {
            ItemStack[] armor = teamArmor.get(t);

            for (int i = 0; i < defInv.length; i++) {
                ItemStack ar = defInv[i];
                if (
                        ar != null &&
                        ((ar.getType() == Material.LEATHER_HELMET)
                        || (ar.getType() == Material.LEATHER_CHESTPLATE)
                        || (ar.getType() == Material.LEATHER_LEGGINGS)
                        || (ar.getType() == Material.LEATHER_BOOTS))
                ) {
                    Map<Enchantment,Integer> enc = ar.getEnchantments();
                    LeatherArmorMeta am = (LeatherArmorMeta) ar.getItemMeta();
                    am.setColor(t.getColor());
                    defInv[i].setItemMeta(am);


                    defInv[i].addEnchantments(enc);
                }
            }

            p.getInventory().setContents(defInv);
        }
    }

    public Location getTeamSpawnPoint(Player p) {
        SimpleLocation sl = getMap().getSpawnPoints().get(getPlayerJoinedTeam(p).name());
        if (sl != null) {
            return sl.toLocation(copyWorld);
        } else return copyWorld.getSpawnLocation();
    }

    /* ----- */

    public boolean canStart() {
        if (!(players.size() >= gm.getMinPlayers() && players.size() <= gm.getMaxPlayers())) return false;
        if (stat != ANNIStatus.WAITING) return false;

        return true;
    }

    public boolean start(){
        if (canStart()) {
            copyWorld(id16+"_"+getMap().getWorld());

            changeStatus(ANNIStatus.PHASE_ONE);

            for (Player p : players.keySet()) {
                if (players.get(p) == com.nekozouneko.anni.Team.NOT_JOINED) {
                    randomTeamJoin(p);
                }

                p.getEnderChest().clear();
                p.teleport(getTeamSpawnPoint(p));
                p.setGameMode(GameMode.SURVIVAL);
                p.setHealthScale(p.getHealthScale());
                p.setSaturation(10.0f);
                p.setTotalExperience(0);
                setKitToPlayer(p);
            }

            RegionContainer rc = ANNIPlugin.getWG().getPlatform().getRegionContainer();
            RegionManager rm = rc.get(BukkitAdapter.adapt(copyWorld));

            for (com.nekozouneko.anni.Team t : getTeams(true)) {
                Location nl = getMap().getNexusLocation(t, true);
                BlockVector3 vec3 = BlockVector3.at(nl.getX(), nl.getY(), nl.getZ());

                ProtectedCuboidRegion reg = new ProtectedCuboidRegion(t.name().toLowerCase()+"_nexus_"+id16, vec3, vec3);
                reg.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
                reg.setPriority(91217);

                if (rm != null) {
                    rm.addRegion(reg);
                    regions.add(reg);
                }
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

        for (Player p : players.keySet()) {
            p.getInventory().clear();
        }
        regions.clear();

        if (copyWorld != null) {
            RegionContainer cont = ANNIPlugin.getWG().getPlatform().getRegionContainer();
            RegionManager man = cont.get(BukkitAdapter.adapt(copyWorld));

            if (man != null) {
                for (ProtectedRegion reg : regions) {
                    man.removeRegion(reg.getId());
                }
            }

            Bukkit.unloadWorld(copyWorld, true);
            try {
                ANNIUtil.safeDeleteDir(copyWorld.getWorldFolder().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        players.clear();
        savedInv.clear();
    }

    public void pluginDisable() {
        getPlayers().forEach(ANNIPlugin::teleportToLobby);
        end(true);
        bb.setVisible(false);
        bb.removeAll();
        Bukkit.removeBossBar(new NamespacedKey(plugin, id16));
        bbbr.cancel();
    }

    public void copyWorld(String target) {
        ANNIPlugin.getInstance().getLogger().info("Copying World '"+ target +"'");
        World w = Bukkit.getWorld(getMap().getWorld());

        if (Bukkit.getWorld(target) != null) {
            ANNIPlugin.getInstance().getLogger().warning("World '"+target+"' is existing.");
            return;
        }

        if (w == null) return;

        try {
            Files.walkFileTree(
                    w.getWorldFolder().toPath(),
                    new ANNIUtil.copyDirectoryVisitor(
                            w.getWorldFolder().toPath(),
                            Paths.get("./"+target),
                            Arrays.asList("session.lock", "uid.dat")
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
            ANNIPlugin.getInstance().getLogger().warning("Error occurred");
            return;
        }

        World copied = WorldCreator.name(target).createWorld();

        if (copied != null) {
            setCopiedMap(copied);
            ANNIPlugin.getInstance().getLogger().info("Copying worldguard regions...");

            RegionContainer cont = ANNIPlugin.getWG().getPlatform().getRegionContainer();
            cont.reload();

            RegionManager man = cont.get(BukkitAdapter.adapt(w));
            RegionManager nman = cont.get(BukkitAdapter.adapt(copied));

            if (man != null && nman != null) {
                for (String rk : man.getRegions().keySet()) {
                    ProtectedRegion val = man.getRegions().get(rk);
                    ProtectedRegion nr;
                    if (val.getType() == RegionType.POLYGON) {
                        nr = new ProtectedPolygonalRegion(val.getId()+"_"+id16, val.getPoints(), val.getMinimumPoint().getY(), val.getMaximumPoint().getY());
                    } else if (val.getType() == RegionType.CUBOID){
                        nr = new ProtectedCuboidRegion(val.getId()+"_"+id16, val.getMinimumPoint(), val.getMaximumPoint());
                    } else continue;

                    nr.setFlags(val.getFlags());
                    nman.addRegion(nr);
                    regions.add(nr);
                    ANNIPlugin.getInstance().getLogger().info("Copied region ["+rk+"]");
                }
            }

            ANNIPlugin.getInstance().getLogger().info("Copied worldguard regions.");
        }

        ANNIPlugin.getInstance().getLogger().info("End copy world successful.");
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

            for (Player p : getPlayers(t)) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 2);
            }
        }
    }

    public Integer getNexusHealth(com.nekozouneko.anni.Team t) {
        if (t == null) throw new NullPointerException("Argument of Team is null.");
        if (t.isSpectator()) throw new IllegalArgumentException("Team Spectator not has nexus health.");

        return nexusHealth.get(t);
    }

    public String getNexusHealthForBoard(com.nekozouneko.anni.Team t) {
        Integer i = getNexusHealth(t);

        if (isLose(t) || i <= 0) {
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
        ANNIPlugin.getInstance().getLogger().info(m);
    }

    public void broadcast(String message, List<Player> exclude) {
        String m = NChatColor.replaceAltColorCodes(message);
        for (Player p:players.keySet()) {
            if (!exclude.contains(p)) p.sendMessage(m);
        }
        ANNIPlugin.getInstance().getLogger().info(m);
    }

    public void broadcast(com.nekozouneko.anni.Team team, String message) {
        Team t = teams.get(team);
        if (t == null) return;
        t.getEntries().forEach((s) -> {
            Player p = Bukkit.getPlayer(s);
            if (p == null) return;
            p.sendMessage(NChatColor.replaceAltColorCodes(message));
        });
        ANNIPlugin.getInstance().getLogger().info(NChatColor.replaceAltColorCodes(message));
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
                            "§7ダイヤ鉱石が採掘可能になります。"
                    )
            ) {
                broadcast(t, b);
            }
        }
    }

    private void phaseFour() {
        for (com.nekozouneko.anni.Team t : teams.keySet()) {
            for (String b :
                    ANNIBigMessage.createMessage(
                            '4', teams.get(t).getColor().getChar(), "フェーズ4が開始されました。",
                            "§7ネクサスへのダメージが2倍になります。"
                    )
            ) {
                broadcast(t, b);
            }
        }
    }

    private void phaseFive() {
        for (com.nekozouneko.anni.Team t : teams.keySet()) {
            for (String b :
                    ANNIBigMessage.createMessage(
                            '5', teams.get(t).getColor().getChar(), "フェーズ5が開始されました。",
                            "§c§lサドンデスが開始",
                            "§7ネクサスの体力が1になるまで減っていきます。"
                    )
            ) {
                broadcast(t, b);
            }
        }

        for (Player p : players.keySet()) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
        }

        if (suddenTask != null && !suddenTask.isCancelled()) {
            suddenTask.cancel();
        }

        suddenTask = new SuddenDeathTask(this);
        suddenTask.runTaskTimer(plugin, 100, 100);
    }

    //save inv

    public TeamPlayerInventory getSavedInventory(UUID id) {
        return savedInv.get(id);
    }

    public void removeSavedInventory(UUID id) {
        savedInv.remove(id);
    }

    // kit

    public void setKitId(UUID id, String kid) {
        kit.put(id, kid);
    }

    public String getKitId(UUID id) {
        return kit.get(id);
    }

    // region

}
