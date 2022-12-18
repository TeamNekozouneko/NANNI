package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.AbstractGUILocationSelector;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.gui.location.TeamSpawnPointLocation;
import com.nekozouneko.anni.util.SimpleLocation;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockPlaceListener implements Listener {

    private final List<Material> denyPlace = Arrays.asList(
            Material.IRON_ORE, Material.GOLD_ORE,
            Material.COAL_ORE, Material.LAPIS_ORE,
            Material.DIAMOND_ORE, Material.EMERALD_ORE,
            Material.REDSTONE_ORE,
            Material.BEDROCK, Material.OBSIDIAN,
            Material.CRYING_OBSIDIAN
    );

    @EventHandler
    public void onEvent(BlockPlaceEvent e) {

        if (ANNIPlugin.getGM().getGame().isJoined(e.getPlayer())) {
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
                    e.getPlayer().sendMessage(NChatColor.RED + "ネクサス付近は設置できません。");
                    e.setCancelled(true);
                }
            }

            if (denyPlace.contains(e.getBlock().getType())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("そのブロックを設置することは許可されていません");
            }
        }

        AbstractGUILocationSelector g = MapEditor.glc.get(e.getPlayer().getUniqueId());
        if (g != null) {
            if (g instanceof TeamSpawnPointLocation) {
                g.edit(e.getBlock().getLocation());
                e.setCancelled(true);
            }
        }
    }

}
