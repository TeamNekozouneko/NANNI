package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.event.NexusAttackEvent;
import com.nekozouneko.anni.game.ANNIBigMessage;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.AbstractGUILocationSelector;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.task.UpdateBossBar;
import com.nekozouneko.anni.util.BlockDestroyUtil;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BlockDestroyListener implements Listener {

    private final Random r = new Random();
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    @EventHandler
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
                /*case END_STONE_BRICKS:
                    e.setDropItems(false);
                    BlockDestroyUtil.finalNexusDestroyParticleSound(loc);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                    break;*/
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
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(bt), 300);
                    break;
                case DIAMOND_ORE:
                case EMERALD_ORE:
                    e.setDropItems(false);
                    e.setExpToDrop(0);
                    if (ANNIUtil.isMineableOre(bt, e.getPlayer().getInventory().getItemInMainHand().getType())) {
                        e.getPlayer().getInventory().addItem(
                                new ItemStack(
                                        ANNIUtil.getOreMinedResult(bt),
                                        ANNIUtil.getFortuneOreDropItemAmounts(e.getPlayer())
                                )
                        );
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
                default:
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
        e.setCancelled(true);
        e.setDropItems(false);

        final ANNIGame g = ANNIPlugin.getGM().getGame();
        if (g.getStatus().getPhaseId() >= 2) {
            Player broker = e.getPlayer();
            Location loc = e.getBlock().getLocation();

            Team t = g.getMap().getNexusTeam(loc);
            if (t == null) {
                e.setDropItems(true);
                e.setCancelled(false);
                return;
            };
            if (t == g.getPlayerJoinedTeam(broker)) {
                broker.sendMessage(NChatColor.RED + "自陣のネクサスは破壊できません。");
                return;
            }

            if (g.getStatus().getPhaseId() >= 4) {
                g.damageNexusHealth(t, 2);
            } else {
                g.damageNexusHealth(t, 1);
            }

            g.getBossBar().setTitle(
                    broker.getDisplayName() + "が" + g.getScoreBoardTeam(t).getDisplayName() + "のネクサスにダメージを与えました!"
            );
            if (g.getNexusHealth(t) >= 1) {
                BlockDestroyUtil.nexusDestroyParticleSound(loc);

                Bukkit.getScheduler().runTaskLater(
                        plugin, () -> loc.getBlock().setType(Material.END_STONE), 3
                );
            } else {
                BlockDestroyUtil.finalNexusDestroyParticleSound(loc);

                Bukkit.getScheduler().runTaskLater(
                        plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3
                );

                g.loseTeam(t);

                if (g.getNotLostTeams().size() > 1) {
                    for (String s: ANNIBigMessage.createMessage(
                            UpdateBossBar.bigCharMap.get(t), g.getScoreBoardTeam(t).getColor().getChar(),
                            g.getScoreBoardTeam(t).getColor()+g.getScoreBoardTeam(t).getDisplayName()+"のネクサスが破壊されました。",
                            "§7by " + ANNIUtil.teamPrefixSuffixAppliedName(broker))
                    ) {
                        g.broadcast(s);
                    }
                } else {
                    Team winTeam = g.getNotLostTeams().get(0);
                    org.bukkit.scoreboard.Team winBukkitTeam = g.getScoreBoardTeam(winTeam);

                    for (String s:ANNIBigMessage.createMessage(
                        UpdateBossBar.bigCharMap.get(winTeam), winBukkitTeam.getColor().getChar(),
                        winBukkitTeam.getColor()+winBukkitTeam.getDisplayName()+"の勝利",
                        "自動的にロビーにテレポートします。"
                    )) {
                        g.broadcast(s);
                    }
                }
            }
            //Bukkit.getServer().getPluginManager().callEvent(new NexusAttackEvent(e.getPlayer(), g.getPlayerJoi));
        } else {
            if (g.getMap().getNexusTeam(e.getBlock().getLocation()) != null) {
                if (
                        g.getMap().getNexusTeam(e.getBlock().getLocation())
                                == g.getPlayerJoinedTeam(e.getPlayer())
                ) {
                    e.getPlayer().sendMessage(NChatColor.RED + "自陣のネクサスは破壊できません。");
                    return;
                }

                e.setCancelled(true);
            } else {
                e.setCancelled(false);
                e.setDropItems(true);
            }
        }
    }

}
