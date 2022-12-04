package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.event.NexusAttackEvent;
import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.AbstractGUILocationSelector;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.util.BlockDestroyUtil;
import org.bukkit.*;
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
            }
        }
    }

    private void onNexusDestroy(BlockBreakEvent e) {
        if (ANNIPlugin.getGM().getGame().getStatus().getPhaseId() >= 2) {
            Location loc = e.getBlock().getLocation();

            e.setDropItems(false);
            BlockDestroyUtil.nexusDestroyParticleSound(loc);

            Bukkit.getScheduler().runTaskLater(
                    plugin, () -> loc.getBlock().setType(Material.END_STONE), 3
            );
            //Bukkit.getServer().getPluginManager().callEvent(new NexusAttackEvent(e.getPlayer(), ANNIPlugin.getGM().getGame().getPlayerJoi));
        } else {
            e.setCancelled(true);
        }
    }

}
