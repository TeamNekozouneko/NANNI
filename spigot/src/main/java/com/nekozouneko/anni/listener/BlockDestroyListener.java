package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.game.ANNIBigMessage;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.AbstractGUILocationSelector;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.task.UpdateBossBar;
import com.nekozouneko.anni.util.BlockDestroyUtil;
import com.nekozouneko.anni.util.SimpleLocation;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockDestroyListener implements Listener {

    private final Random r = new Random();
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private final Map<Material, Integer> expRate = Collections.unmodifiableMap(
            new HashMap<Material, Integer>() {{
                put(Material.COAL_ORE, 3);
                put(Material.IRON_ORE, 5);
                put(Material.GOLD_ORE, 15);
                put(Material.REDSTONE_ORE, 1);
                put(Material.DIAMOND_ORE, 15);
                put(Material.EMERALD_ORE, 15);
                put(Material.LAPIS_ORE, 7);
            }}
    );

    @EventHandler(ignoreCancelled = true)
    public void onEvent(BlockBreakEvent e) {
        World w = e.getBlock().getWorld();
        Location loc = e.getBlock().getLocation();
        Material bt = e.getBlock().getType();

        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL && ANNIPlugin.getGM().getGame().isJoined(e.getPlayer())) {
            switch (bt) {
                case END_STONE:
                    onNexusDestroy(e);
                    break;
                case MELON:
                    e.setDropItems(false);
                    e.getPlayer().getInventory().addItem(new ItemStack(Material.MELON_SLICE, r.nextInt(9) + 1));

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.MELON), 100);
                    break;
                case COAL_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType())) {
                        e.getPlayer().getInventory().addItem(
                                new ItemStack(
                                        ANNIUtil.getOreMinedResult(bt),
                                        ANNIUtil.getFortuneOreDropItemAmounts(e.getPlayer())
                                )
                        );
                        e.getPlayer().giveExp(expRate.get(bt));
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.COAL_ORE), 140);
                    break;
                case IRON_ORE:
                case REDSTONE_ORE:
                case LAPIS_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType())) {
                        e.getPlayer().getInventory().addItem(
                                new ItemStack(
                                        ANNIUtil.getOreMinedResult(bt),
                                        ANNIUtil.getFortuneOreDropItemAmounts(e.getPlayer())
                                )
                        );
                        e.getPlayer().giveExp(expRate.get(bt));
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 200);
                    break;
                case GOLD_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType())) {
                        e.getPlayer().getInventory().addItem(
                                new ItemStack(
                                        ANNIUtil.getOreMinedResult(bt), ANNIUtil.getFortuneOreDropItemAmounts(e.getPlayer())
                                )
                        );
                        e.getPlayer().giveExp(expRate.get(bt));
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 300);
                    break;
                case DIAMOND_ORE:
                case EMERALD_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType()) && ANNIPlugin.getGM().getGame().getStatus().getPhaseId() >= 3) {
                        e.getPlayer().getInventory().addItem(
                                new ItemStack(
                                        ANNIUtil.getOreMinedResult(bt),
                                        ANNIUtil.getFortuneOreDropItemAmounts(e.getPlayer())
                                )
                        );
                        e.getPlayer().giveExp(expRate.get(bt));
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 600);
                    break;
                case GRAVEL:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    e.getPlayer().getInventory().addItem(
                            new ItemStack(
                                    Material.FLINT,
                                    ANNIUtil.getFortuneOreDropItemAmounts(e.getPlayer())
                            )
                    );

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 100);
                    break;
                case WHEAT:
                    Random r = new Random();
                    Ageable age = (Ageable) loc.getBlock().getBlockData();

                    if (age.getAge() >= 7) {
                        if (r.nextInt(101) <= 5) {
                            loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, 1));
                        }
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ()).getBlock().getType() == Material.FARMLAND) {
                                loc.getBlock().setType(Material.WHEAT);
                            }
                        }, 60);
                    }
                    break;
                case ACACIA_LOG:
                case BIRCH_LOG:
                case CRIMSON_STEM:
                case DARK_OAK_LOG:
                case JUNGLE_LOG:
                case SPRUCE_LOG:
                case OAK_LOG:
                case WARPED_STEM:
                case STRIPPED_ACACIA_LOG:
                case STRIPPED_BIRCH_LOG:
                case STRIPPED_CRIMSON_STEM:
                case STRIPPED_DARK_OAK_LOG:
                case STRIPPED_JUNGLE_LOG:
                case STRIPPED_SPRUCE_LOG:
                case STRIPPED_OAK_LOG:
                case STRIPPED_WARPED_STEM:
                    RegionContainer rc = ANNIPlugin.getWG().getPlatform().getRegionContainer();
                    RegionManager rm = rc.get(BukkitAdapter.adapt(e.getBlock().getWorld()));

                    if (rm != null) {
                        for (ProtectedRegion er : rm.getRegions().values()) {
                            if (er.getId().startsWith("anni_inf_log") && er.contains(
                                    e.getBlock().getLocation().getBlockX(),
                                    e.getBlock().getLocation().getBlockY(),
                                    e.getBlock().getLocation().getBlockZ()
                            )) {
                                e.setDropItems(false);
                                e.getPlayer().getInventory().addItem(new ItemStack(bt));
                                Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 100);
                            }
                        }
                    }
                    break;
                default:
                    final Material[] ignoreDestroy = new Material[] {
                            Material.ENCHANTING_TABLE,
                            Material.ENDER_CHEST,
                            Material.BEACON,
                            Material.NETHERITE_BLOCK
                    };

                    if (Arrays.asList(ignoreDestroy).contains(e.getBlock().getType())) {
                        e.setDropItems(false);
                        e.setExpToDrop(0);
                        e.setCancelled(true);
                        return;
                    }

                    for (SimpleLocation l : ANNIPlugin.getGM().getGame().getMap().getNexusList().values()) {
                        Location fl = l.toLocation(ANNIPlugin.getGM().getGame().getCopiedMap());

                        if (
                                ((fl.getX()-10d) <= e.getBlock().getLocation().getX()
                                        && (fl.getX()+10d) >= e.getBlock().getLocation().getX()) &&
                                        ((fl.getY()-10d) <= e.getBlock().getLocation().getY()
                                                && (fl.getY()+10d) >= e.getBlock().getLocation().getY()) &&
                                        ((fl.getZ()-10d) <= e.getBlock().getLocation().getZ()
                                                && (fl.getZ()+10d) >= e.getBlock().getLocation().getZ())
                                        && e.getPlayer().getGameMode() == GameMode.SURVIVAL
                        ) {
                            e.getPlayer().sendMessage(NChatColor.RED + "ネクサス付近は破壊できません。");
                            e.setCancelled(true);
                        }
                    }
                    break;
            }
        }

        AbstractGUILocationSelector g = MapEditor.glc.get(e.getPlayer().getUniqueId());
        if (g != null) {
            if (g instanceof NexusLocation) {
                g.edit(e.getBlock().getLocation());
                e.setCancelled(true);
            }
        }
    }

    private void onNexusDestroy(BlockBreakEvent e) {
        final ANNIGame g = ANNIPlugin.getGM().getGame();
        if (g.getStatus().getPhaseId() >= 2) {
            Player broker = e.getPlayer();
            Location loc = e.getBlock().getLocation();

            Team t = g.getMap().getNexusTeam(loc);
            if (t == null) {
                return;
            }
            if (t == g.getPlayerJoinedTeam(broker)) {
                broker.sendMessage(NChatColor.RED + "自陣のネクサスは破壊できません。");
                e.setDropItems(false);
                e.setCancelled(true);
                return;
            }
            int dam;

            if (g.getStatus().getPhaseId() >= 4) {
                dam = 2;
                g.damageNexusHealth(t, dam);
            } else {
                dam = 1;
                g.damageNexusHealth(t, dam);
            }

            g.getBossBar().setTitle(
                    broker.getDisplayName() + "が" + g.getScoreBoardTeam(t).getDisplayName() + "のネクサスにダメージを与えました!"
            );
            e.setDropItems(false);
            ANNIPlugin.getANNIDB().addNexusMinedCount(broker.getUniqueId());
            if (g.getNexusHealth(t) >= 1) {
                BlockDestroyUtil.nexusDestroyParticleSound(loc);

                Bukkit.getScheduler().runTaskLater(
                        plugin, () -> loc.getBlock().setType(Material.END_STONE), 3
                );
            } else if (g.getNexusHealth(t) <= 0){
                BlockDestroyUtil.finalNexusDestroyParticleSound(loc);

                Bukkit.getScheduler().runTaskLater(
                        plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3
                );
                g.loseTeam(t);

                for (Player p : g.getPlayers(t)) {
                    ANNIPlugin.getANNIDB().addLoseCount(p.getUniqueId());
                }

                if (g.getNotLostTeams().size() > 1) {
                    for (String s: ANNIBigMessage.createMessage(
                            UpdateBossBar.bigCharMap.get(t), g.getScoreBoardTeam(t).getColor().getChar(),
                            g.getScoreBoardTeam(t).getColor()+g.getScoreBoardTeam(t).getDisplayName()+"のネクサスが破壊されました。",
                            "§7by " + ANNIUtil.teamPrefixSuffixAppliedName(broker))
                    ) {
                        g.broadcast(s);
                    }
                } else {
                    try {
                        Team winTeam = g.getNotLostTeams().get(0);
                        org.bukkit.scoreboard.Team winBukkitTeam = g.getScoreBoardTeam(winTeam);

                        for (Player p : g.getPlayers(winTeam)) {
                            ANNIPlugin.getANNIDB().addWinCount(p.getUniqueId());
                        }

                        for (String s : ANNIBigMessage.createMessage(
                                UpdateBossBar.bigCharMap.get(winTeam), winBukkitTeam.getColor().getChar(),
                                winBukkitTeam.getColor() + winBukkitTeam.getDisplayName() + "の勝利",
                                "30秒後にロビーにテレポートします。"
                        )) {
                            g.broadcast(s);
                        }

                        g.lockTimer();
                        g.getBossBar().setTitle(UpdateBossBar.message.get(g.getStatus()) + " - " + ANNIUtil.toTimerFormat(g.getTimer()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    g.changeStatus(ANNIStatus.WAITING_RESTART);

                    new BukkitRunnable() {
                        int time = 30;

                        @Override
                        public void run() {
                            if (time <= 0) {
                                for (Player p : g.getPlayers()) {
                                    p.setGameMode(GameMode.ADVENTURE);
                                    p.getInventory().clear();
                                    ANNIPlugin.teleportToLobby(p);
                                }
                                g.restart();
                                cancel();
                            } else {
                                if (time > 5) {
                                    if (time % 5 == 0) {
                                        g.broadcast("再起動まであと" + time + "秒");
                                    }
                                } else {
                                    g.broadcast("再起動まであと" + time + "秒");
                                }

                                time--;
                            }
                        }
                    }.runTaskTimer(plugin, 20, 20);
                }
            } else {
                BlockDestroyUtil.nexusDestroyParticleSound(loc);

                Bukkit.getScheduler().runTaskLater(
                        plugin, () -> loc.getBlock().setType(Material.END_STONE), 3
                );
            }
            //Bukkit.getServer().getPluginManager().callEvent(new NexusAttackEvent(e.getPlayer(), g.getPlayerJoi));
        } else {
            Location fl = e.getBlock().getLocation();
            fl.setWorld(g.getCopiedMap());
            if (g.getMap().getNexusTeam(fl) != null) {
                if (
                        g.getMap().getNexusTeam(e.getBlock().getLocation())
                                == g.getPlayerJoinedTeam(e.getPlayer())
                ) {
                    e.getPlayer().sendMessage(NChatColor.RED + "自陣のネクサスは破壊できません。");
                } else {
                    e.getPlayer().sendMessage(NChatColor.RED + "ネクサスの破壊はフェーズ2以降です。");
                }

                e.setDropItems(false);
                e.setCancelled(true);
            }
        }
    }

}
