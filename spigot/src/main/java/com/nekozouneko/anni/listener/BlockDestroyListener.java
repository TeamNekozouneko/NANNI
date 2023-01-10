package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.ANNITeam;
import com.nekozouneko.anni.game.ANNIBigMessage;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.AbstractGUILocationSelector;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.task.RestartTask;
import com.nekozouneko.anni.task.UpdateBossBar;
import com.nekozouneko.anni.util.BlockDestroyUtil;
import com.nekozouneko.anni.util.SimpleLocation;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BlockDestroyListener implements Listener {

    private final Random r = new Random();
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private final Map<Material, Integer> expRate = Collections.unmodifiableMap(
            new HashMap<Material, Integer>() {{
                put(Material.COAL_ORE, 5);
                put(Material.IRON_ORE, 8);
                put(Material.GOLD_ORE, 23);
                put(Material.REDSTONE_ORE, 2);
                put(Material.DIAMOND_ORE, 23);
                put(Material.EMERALD_ORE, 23);
                put(Material.LAPIS_ORE, 11);
            }}
    );

    @EventHandler(ignoreCancelled = true)
    public void onEvent(BlockBreakEvent e) {
        World w = e.getBlock().getWorld();
        Location loc = e.getBlock().getLocation();
        Material bt = e.getBlock().getType();

        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL && ANNIPlugin.getGM().getGame().isJoined(e.getPlayer())) {
            if (e.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                e.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                e.getPlayer().sendMessage(NChatColor.RED + "ブロックの破壊により透明化が解除されました");
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 0);
            }

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
                                e.getBlock().getDrops(
                                        e.getPlayer().getInventory().getItemInMainHand(),
                                        e.getPlayer()
                                ).toArray(new ItemStack[0])
                        );
                        e.getPlayer().giveExp(expRate.get(bt));
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.COBBLESTONE), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.COAL_ORE), 140);
                    break;
                case IRON_ORE:
                case REDSTONE_ORE:
                case LAPIS_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType())) {
                        e.getPlayer().getInventory().addItem(
                                e.getBlock().getDrops(
                                        e.getPlayer().getInventory().getItemInMainHand(),
                                        e.getPlayer()
                                ).toArray(new ItemStack[0])
                        );
                        e.getPlayer().giveExp(expRate.get(bt));
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.COBBLESTONE), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 200);
                    break;
                case GOLD_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType())) {
                        e.getPlayer().getInventory().addItem(
                                e.getBlock().getDrops(
                                        e.getPlayer().getInventory().getItemInMainHand(),
                                        e.getPlayer()
                                ).toArray(new ItemStack[0])
                        );
                        e.getPlayer().giveExp(expRate.get(bt));
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.COBBLESTONE), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 300);
                    break;
                case DIAMOND_ORE:
                case EMERALD_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIPlugin.getGM().getGame().getStatus().getPhaseId() >= 3) {
                        if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType())) {
                            e.getPlayer().getInventory().addItem(
                                    e.getBlock().getDrops(
                                            e.getPlayer().getInventory().getItemInMainHand(),
                                            e.getPlayer()
                                    ).toArray(new ItemStack[0])
                            );

                            e.getPlayer().giveExp(expRate.get(bt));
                        }
                    }
                    else {
                        e.getPlayer().sendMessage("§cダイヤ/エメラルド鉱石の採掘はフェーズ3以降となります");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.COBBLESTONE), 3);
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

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.COBBLESTONE), 1);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 100);
                    break;
                case WHEAT:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    Random r = new Random();
                    Ageable age = (Ageable) loc.getBlock().getBlockData();

                    if (age.getAge() >= 7) {
                        if (r.nextInt(101) <= 3) {
                            loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, 1));
                        }
                        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.WHEAT, new Random().nextInt(3)+1));
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ()).getBlock().getType() == Material.FARMLAND) {
                            loc.getBlock().setType(Material.WHEAT);
                        }
                    }, 60);

                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.WHEAT_SEEDS));
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
                                BlockData bd = e.getBlock().getBlockData().clone();

                                e.setDropItems(false);
                                e.getPlayer().getInventory().addItem(new ItemStack(bt));
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    loc.getBlock().setType(bt);
                                    loc.getBlock().setBlockData(bd);
                                }, 100);
                            }
                        }
                    }
                    break;
                default:
                    final Material[] ignoreDestroy = new Material[] {
                            Material.ENCHANTING_TABLE,
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

            ANNITeam t = g.getMap().getNexusTeam(loc);
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

            if (g.getPlayerJoinedTeam(broker) != null && !g.getPlayerJoinedTeam(broker).isSpectator()) {
                for (Player p : g.getPlayers(g.getPlayerJoinedTeam(broker))) {
                    ANNIPlugin.getVaultEconomy().depositPlayer(p, 2.);
                    p.sendMessage("§a+2 Nekozouneko Anni Point §7(NAP)");
                }
            }

            g.getBossBar().setTitle(
                    broker.getDisplayName() + "が" + g.getScoreBoardTeam(t).getDisplayName() + "のネクサスにダメージを与えました!"
            );
            g.getBossBar().setProgress(1.);
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
                        ANNITeam winTeam = g.getNotLostTeams().get(0);
                        org.bukkit.scoreboard.Team winBukkitTeam = g.getScoreBoardTeam(winTeam);

                        for (Player p : g.getPlayers(winTeam)) {
                            ANNIPlugin.getANNIDB().addWinCount(p.getUniqueId());
                            ANNIPlugin.getVaultEconomy().depositPlayer(p, 750);
                            p.sendMessage("§a+750 Nekozouneko Anni Point §7(NAP)");
                        }

                        for (String s : ANNIBigMessage.createMessage(
                                UpdateBossBar.bigCharMap.get(winTeam), winBukkitTeam.getColor().getChar(),
                                winBukkitTeam.getColor() + winBukkitTeam.getDisplayName() + "の勝利",
                                "30秒後にロビーにテレポートします。"
                        )) {
                            g.broadcast(s);
                        }

                        g.lockTimer();
                        g.getBossBar().setTitle(UpdateBossBar.message.get(g.getStatus()) + (g.getStatus() != ANNIStatus.PHASE_FIVE ? (" - " + ANNIUtil.toTimerFormat(g.getTimer())) : ""));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    g.changeStatus(ANNIStatus.WAITING_RESTART);

                    new RestartTask(30, g).runTaskTimer(plugin, 20, 20);
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
