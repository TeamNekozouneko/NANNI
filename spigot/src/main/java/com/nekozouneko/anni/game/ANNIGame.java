package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIConfig;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNITeam;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.file.ANNIKit;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.game.manager.GameManager;
import com.nekozouneko.anni.task.RestartTask;
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

    private final Map<Player, ANNITeam> players = new HashMap<>();
    private final Map<ANNITeam, Team> teams = new HashMap<>();
    private final Map<UUID, String> kit = new HashMap<>();
    private final Map<ANNITeam, Boolean> losedTeams = new HashMap<>();
    public static final Map<ANNITeam, ItemStack[]> teamArmor = Collections.unmodifiableMap(
            new HashMap<ANNITeam, ItemStack[]>() {{
                put(ANNITeam.RED, ANNIUtil.createColorLeatherArmor(Color.RED));
                put(ANNITeam.BLUE, ANNIUtil.createColorLeatherArmor(Color.BLUE));
                put(ANNITeam.YELLOW, ANNIUtil.createColorLeatherArmor(Color.YELLOW));
                put(ANNITeam.GREEN, ANNIUtil.createColorLeatherArmor(Color.GREEN));
    }});
    private final KeyedBossBar bb = Bukkit.createBossBar(
            new NamespacedKey(plugin, id16), "待機中", BarColor.GREEN, BarStyle.SOLID
    );
    private final List<ProtectedRegion> regions = new ArrayList<>();

    private final Map<ANNITeam, Integer> nexusHealth = new HashMap<>();
    private BukkitRunnable bbbr;
    private BukkitRunnable suddenTask;
    private Map<UUID, TeamPlayerInventory> savedInv = new HashMap<>();

    public static class TeamPlayerInventory {
        private final UUID uuid;
        private final ANNITeam team;

        private final ItemStack[] armors;
        private final ItemStack offhand;
        private final ItemStack[] contents;
        private final ItemStack[] ender;
        private boolean allowJoin = false;

        protected TeamPlayerInventory(ANNITeam team, Player player) {
            this.team = team;
            this.uuid = player.getUniqueId();
            this.armors = player.getInventory().getArmorContents();
            this.offhand = player.getInventory().getItemInOffHand();
            this.contents = player.getInventory().getContents();
            this.ender = player.getEnderChest().getContents();
        }

        protected TeamPlayerInventory(ANNITeam team, UUID uuid, ItemStack[] armors, ItemStack offhand, ItemStack[] contents, ItemStack[] ender) {
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

        public ANNITeam getTeam() {
            return team;
        }

        public boolean isAllowedJoin() {
            return allowJoin;
        }

        public void setAllowJoin(boolean v1) {
            allowJoin = v1;
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

        red.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        blue.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        yellow.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        green.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);

        teams.put(ANNITeam.RED, red);
        teams.put(ANNITeam.BLUE, blue);
        losedTeams.put(ANNITeam.RED, false);
        losedTeams.put(ANNITeam.BLUE, false);
        if (gm.getRuleType() == 4) {
            teams.put(ANNITeam.YELLOW, yellow);
            teams.put(ANNITeam.GREEN, green);
            losedTeams.put(ANNITeam.YELLOW, false);
            losedTeams.put(ANNITeam.GREEN, false);
        }
        teams.put(ANNITeam.SPECTATOR, spec);

    }

    private void initNexus() {
        nexusHealth.clear();
        nexusHealth.put(ANNITeam.RED, ANNIPlugin.getANNIConf().nexusHealth());
        nexusHealth.put(ANNITeam.BLUE, ANNIPlugin.getANNIConf().nexusHealth());
        nexusHealth.put(ANNITeam.YELLOW, ANNIPlugin.getANNIConf().nexusHealth());
        nexusHealth.put(ANNITeam.GREEN, ANNIPlugin.getANNIConf().nexusHealth());
        nexusHealth.put(ANNITeam.SPECTATOR, -1);
        nexusHealth.put(ANNITeam.NOT_JOINED, -1);
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

    public List<Player> getPlayers(ANNITeam filter) {
        List<Player> filtered = new ArrayList<>();
        players.forEach((p, t) -> {
            if (t == filter) {
                filtered.add(p);
            }
        });
        return filtered;
    }

    public ANNITeam getPlayerJoinedTeam(Player p) {
        return players.get(p);
    }

    public Team getScoreBoardTeam(ANNITeam t) {
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

    public List<ANNITeam> getTeams(boolean excludeSpec) {
        List<ANNITeam> s = new ArrayList<>();
        for (ANNITeam t:teams.keySet()) {
            if (!(excludeSpec && t.isSpectator())) s.add(t);
        }

        return s;
    }

    public void changeStatus(ANNIStatus stat) {
        if (stat == this.stat) return;
        if (stat == ANNIStatus.CANT_START) return;
        if (stat.getPhaseId() >= 1 && stat.getPhaseId() <= 4) {
            for (Player p : getPlayers()) {
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 2f);
            }
        }
        this.stat = stat;

        if (stat == ANNIStatus.PHASE_ONE) phaseOne();
        if (stat == ANNIStatus.PHASE_TWO) phaseTwo();
        if (stat == ANNIStatus.PHASE_THREE) phaseThree();
        if (stat == ANNIStatus.PHASE_FOUR) phaseFour();
        if (stat == ANNIStatus.PHASE_FIVE) phaseFive();
    }

    public boolean isAllowedChangeTeam(Player p) {
        return getStatus().getPhaseId() <= 0 || getPlayerJoinedTeam(p).isSpectator();
    }

    public void changeTeam(Player p, ANNITeam t) {
        if (!isAllowedChangeTeam(p)) return;

        Team old = teams.get(players.get(p));
        if (stat.getPhaseId() <= 0) {
            Team new1 = teams.get(t);
            players.put(p, t);

            if (old != null) old.removePlayer(p);
            if (new1 != null) new1.addPlayer(p);
        } else {
            if (t != ANNITeam.NOT_JOINED) {
                if (players.get(p).isSpectator()) {
                    Team new1 = teams.get(t);
                    players.put(p, t);

                    if (old != null) old.removePlayer(p);
                    if (new1 != null) new1.addPlayer(p);

                    if (!isLose(t)) {
                        setKitToPlayer(p);
                        p.teleport(getTeamSpawnPoint(p));
                        ANNIUtil.healPlayer(p);
                        p.setTotalExperience(0);
                        p.setLevel(0);
                        if (t == ANNITeam.SPECTATOR) p.setGameMode(GameMode.SPECTATOR);
                        else p.setGameMode(GameMode.SURVIVAL);
                    }
                } else {
                    p.sendMessage("§c途中でチームを変更することはできません。");
                }
            } else {
                if (old != null) old.removePlayer(p);
                players.put(p, ANNITeam.NOT_JOINED);

                p.getInventory().clear();
                p.getEnderChest().clear();
                p.teleport(ANNIPlugin.getLobby().getLocation().toLocation());
                ANNIUtil.healPlayer(p);
                p.setTotalExperience(0);
                p.setLevel(0);
                p.setGameMode(GameMode.ADVENTURE);
            }
        }
    }

    public ANNITeam randomTeam() {
        Map<ANNITeam, Integer> members = new HashMap<>();
        teams.keySet().forEach((t) -> members.put(t, teams.get(t).getSize()));
        ANNITeam t = ANNIUtil.balancingJoin(members);
        return t;
    }

    public void randomTeamJoin(Player p) {
        if (players.get(p).isSpectator()) {
            ANNITeam t = randomTeam();
            players.put(p, t);
            teams.get(t).addPlayer(p);
        }
    }

    public void join(Player p) {
        if (!players.containsKey(p)) {
            players.put(p, ANNITeam.NOT_JOINED);
            broadcast(p.getName() + "§eがゲームに参加しました");

            if (savedInv.containsKey(p.getUniqueId())) {
                TeamPlayerInventory tpi = savedInv.get(p.getUniqueId());

                changeTeam(p, tpi.getTeam());
                if (tpi.isAllowedJoin()) {
                    tpi.set(p);
                } else {
                    p.sendMessage("§cあなたが参加していたチームは負けているためマップには移動できません、ゲーム終了までお待ち下さい。");
                }

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

            if (getPlayerJoinedTeam(p) != null && !getPlayerJoinedTeam(p).isSpectator()) {
                ANNITeam t = getPlayerJoinedTeam(p);
                Team ts = getScoreBoardTeam(t);
                if (getPlayers(t).size() <= 1 && !isLose(t)) {
                    loseTeam(t);
                    getMap().getNexusLocation(t, true).getBlock().setType(Material.BEDROCK);

                    if (getNotLostTeams().size() > 1) {
                        for (
                                String bm : ANNIBigMessage.createMessage(
                                        UpdateBossBar.bigCharMap.get(t), ts.getColor().getChar(),
                                        ts.getColor()+ts.getDisplayName()+"§fのプレイヤーが全員退出したため、",
                                        ts.getDisplayName()+"は自動的に負けとなります"
                                )
                        ) broadcast(bm);
                    }
                    else {
                        try {
                            ANNITeam wt = getNotLostTeams().get(0);
                            Team wts = getScoreBoardTeam(wt);
                            for (
                                    String bm : ANNIBigMessage.createMessage(
                                    UpdateBossBar.bigCharMap.get(wt), wts.getColor().getChar(),
                                    wts.getColor() + wts.getDisplayName() + "§fの勝利",
                                    "§7※敵チームが全員退出したため、",
                                    "§7勝利としてカウントしません"
                            )
                            ) {
                                broadcast(bm);
                            }
                        } catch (Exception e) {e.printStackTrace();}
                    }
                    lockTimer();
                    new RestartTask(30, this).runTaskTimer(plugin, 20L, 20L);
                }
            }
        }

        teams.values().forEach((t) -> {
            if (t.hasEntry(p.getName())) t.removeEntry(p.getName());
            if (t.hasPlayer(p)) t.removePlayer(p);
        });
        players.remove(p);
        ANNIUtil.healPlayer(p);
        p.setTotalExperience(0);
        p.setLevel(0);
        p.getEnderChest().clear();
        p.getInventory().clear();
    }

    public void setKitToPlayer(Player p) {
        String kitId = ANNIPlugin.getANNIDB().getUsingKit(p.getUniqueId());
        if (kitId == null) kitId = "<default>";
        ANNIKit k = ANNIPlugin.getKM().getLoadedKits().get(kitId);
        if (k == null) k = ANNIPlugin.DEFAULT_KIT;

        final ItemStack[] defInv = k.getDecodedContent();

        ANNITeam t = players.get(p);

        if (!t.isSpectator()) {
            for (int i = 0; i < defInv.length; i++) {
                ItemStack ar = defInv[i];
                if (ar == null) continue;

                ItemMeta im = ar.getItemMeta();
                List<String> lr = im.hasLore() && im.getLore() != null ? im.getLore() : new ArrayList<>();

                if (lr.size() == 0) {
                    lr.add("§8Kit item");
                } else {
                    lr.add(" ");
                    lr.add("§8Kit item");
                }
                im.setLore(lr);
                ar.setItemMeta(im);

                if (
                        (ar.getType() == Material.LEATHER_HELMET)
                        || (ar.getType() == Material.LEATHER_CHESTPLATE)
                        || (ar.getType() == Material.LEATHER_LEGGINGS)
                        || (ar.getType() == Material.LEATHER_BOOTS)
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
        if (getPlayerJoinedTeam(p) == null) return copyWorld.getSpawnLocation();
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
                if (players.get(p) == ANNITeam.NOT_JOINED) {
                    randomTeamJoin(p);
                }

                p.getEnderChest().clear();
                p.teleport(getTeamSpawnPoint(p));
                if (!getPlayerJoinedTeam(p).isSpectator()) p.setGameMode(GameMode.SURVIVAL);
                else p.setGameMode(GameMode.SPECTATOR);
                ANNIUtil.healPlayer(p);
                p.setTotalExperience(0);
                p.setLevel(0);
                setKitToPlayer(p);
            }

            RegionContainer rc = ANNIPlugin.getWG().getPlatform().getRegionContainer();
            RegionManager rm = rc.get(BukkitAdapter.adapt(copyWorld));

            for (ANNITeam t : getTeams(true)) {
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

        for (ANNITeam t : getTeams(true)) {
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
            p.getEnderChest().clear();
            p.setTotalExperience(0);
            p.setLevel(0);
            ANNIUtil.healPlayer(p);
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

                    if (val.getId().startsWith("anni_inf_log")) {
                        nr.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
                    }
                    else nr.setFlags(val.getFlags());
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
        for (ANNITeam t : getTeams(true)) {
            if (isLose(t)) a++;
        }

        return a;
    }

    public void loseTeam(ANNITeam t) {
        if (!isLose(t)) {
            losedTeams.put(t, true);
        }
    }

    public boolean isLose(ANNITeam t) {
        if (t.isSpectator()) return false;
        Boolean b = losedTeams.get(t);
        if (b == null) b = false;

        return b;
    }

    public List<ANNITeam> getNotLostTeams() {
        List<ANNITeam> ts = new ArrayList<>();

        for (ANNITeam t : getTeams(true)) {
            if (!isLose(t)) ts.add(t);
        }

        return ts;
    }

    // Nexus

    public void healNexusHealth(ANNITeam t, Integer h) {
        if (t == null) return;

        if (!t.isSpectator()) {
            Integer hth = nexusHealth.get(t);
            nexusHealth.put(t, hth + h);
        }
    }

    public void damageNexusHealth(ANNITeam t, Integer h) {
        if (t == null) return;

        if (!t.isSpectator()) {
            Integer hth = nexusHealth.get(t);
            nexusHealth.put(t, hth - h);

            for (Player p : getPlayers(t)) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 2);
            }
        }
    }

    public Integer getNexusHealth(ANNITeam t) {
        if (t == null) throw new NullPointerException("Argument of Team is null.");
        if (t.isSpectator()) throw new IllegalArgumentException("Team Spectator not has nexus health.");

        return nexusHealth.get(t);
    }

    public String getNexusHealthForBoard(ANNITeam t) {
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

    public void broadcast(ANNITeam team, String message) {
        Team t = teams.get(team);
        if (t == null) return;
        t.getPlayers().forEach((ofp) -> {
            if (ofp.isOnline()) {
                Player p = (Player) ofp;
                p.sendMessage(NChatColor.replaceAltColorCodes(message));
            }
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
        for (ANNITeam t : teams.keySet()) {
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
        for (ANNITeam t : teams.keySet()) {
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
        for (ANNITeam t : teams.keySet()) {
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
        for (ANNITeam t : teams.keySet()) {
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
        for (ANNITeam t : teams.keySet()) {
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

}
