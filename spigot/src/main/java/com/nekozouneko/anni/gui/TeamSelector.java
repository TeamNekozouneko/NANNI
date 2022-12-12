package com.nekozouneko.anni.gui;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.game.ANNIBigMessage;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.task.UpdateBossBar;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public final class TeamSelector {

    private static final Material[] clickToCloseItems = new Material[] {
            Material.RED_WOOL,
            Material.BLUE_WOOL,
            Material.YELLOW_WOOL,
            Material.GREEN_WOOL,

            Material.WHITE_CONCRETE,
            Material.ENDER_PEARL
    };

    private TeamSelector() {}

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, "チームを選択");

        Team t = ANNIPlugin.getGM().getGame().getPlayerJoinedTeam(p);
        org.bukkit.scoreboard.Team st = ANNIPlugin.getGM().getGame().getScoreBoardTeam(t);
        String tn = (t != null && st != null) ? st.getDisplayName() + "" : "無所属";

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(NChatColor.AQUA + p.getName() + "の情報");
        headMeta.setLore(Arrays.asList(
                "§f所持ポイント: §e" + ANNIPlugin.getVaultEconomy().getBalance(p),
                "§f所属: §e" + tn
        ));
        headMeta.setOwningPlayer(p);
        head.setItemMeta(headMeta);

        ItemStack redTeam = new ItemStack(Material.RED_WOOL);
        ItemMeta redTeamMeta = redTeam.getItemMeta();
        redTeamMeta.setDisplayName(NChatColor.RED + "赤チーム");
        redTeam.setItemMeta(redTeamMeta);

        ItemStack blueTeam = new ItemStack(Material.BLUE_WOOL);
        ItemMeta blueTeamMeta = blueTeam.getItemMeta();
        blueTeamMeta.setDisplayName(NChatColor.BLUE + "青チーム");
        blueTeam.setItemMeta(blueTeamMeta);

        ItemStack greenTeam = new ItemStack(Material.GREEN_WOOL);
        ItemMeta greenTeamMeta = greenTeam.getItemMeta();
        greenTeamMeta.setDisplayName(NChatColor.GREEN + "緑チーム");
        greenTeam.setItemMeta(greenTeamMeta);

        ItemStack yellowTeam = new ItemStack(Material.YELLOW_WOOL);
        ItemMeta yellowTeamMeta = yellowTeam.getItemMeta();
        yellowTeamMeta.setDisplayName(NChatColor.YELLOW + "黄チーム");
        yellowTeam.setItemMeta(yellowTeamMeta);

        ItemStack randomTeam = new ItemStack(Material.WHITE_CONCRETE);
        ItemMeta randomTeamMeta = randomTeam.getItemMeta();
        randomTeamMeta.setDisplayName(NChatColor.GREEN + "ランダム");
        randomTeam.setItemMeta(randomTeamMeta);

        ItemStack specTeam = new ItemStack(Material.ENDER_PEARL);
        ItemMeta specTeamMeta = specTeam.getItemMeta();
        specTeamMeta.setDisplayName(NChatColor.GRAY + "観戦");
        specTeam.setItemMeta(specTeamMeta);

        inv.setItem(0, head);
        inv.setItem(2, redTeam);
        inv.setItem(3, blueTeam);
        if (ANNIPlugin.getGM().getRuleType() == 4) {
            inv.setItem(4, greenTeam);
            inv.setItem(5, yellowTeam);
        }
        inv.setItem(7, randomTeam);
        inv.setItem(8, specTeam);

        p.openInventory(inv);
    }

    public static void handle(InventoryClickEvent e) {
        e.setCancelled(true);

        final ItemStack clicked = e.getCurrentItem();
        final Player p = ((Player) e.getView().getPlayer());
        final ANNIGame g = ANNIPlugin.getGM().getGame();

        if (clicked == null) return;

        if (e.getRawSlot() < 9) {
            Team requestedTeam = null;

            switch (clicked.getType()) {
                case RED_WOOL:
                    requestedTeam = Team.RED;
                    break;
                case BLUE_WOOL:
                    requestedTeam = Team.BLUE;
                    break;
                case YELLOW_WOOL:
                    requestedTeam = Team.YELLOW;
                    break;
                case GREEN_WOOL:
                    requestedTeam = Team.GREEN;
                    break;
                case WHITE_CONCRETE:
                    requestedTeam = g.randomTeam();
                    break;
                case ENDER_PEARL:
                    requestedTeam = Team.SPECTATOR;
                    break;
                default: break;
            }

            if (requestedTeam != null && !g.isLose(requestedTeam)) {
                org.bukkit.scoreboard.Team st = g.getScoreBoardTeam(requestedTeam);
                g.changeTeam(p, requestedTeam);
                Character c = UpdateBossBar.bigCharMap.get(requestedTeam);
                if (c != null && !requestedTeam.isSpectator() && g.getStatus().getPhaseId() >= 1) {
                    for (String s : ANNIBigMessage.createMessage(c, st.getColor().getChar(),
                            st.getDisplayName() + "§fに参加しました。",
                            "§7現在フェーズ " + g.getStatus().getPhaseId() + " です。"
                    )) {
                        p.sendMessage(s);
                    }
                } else {
                    p.sendMessage(st.getColor()+st.getDisplayName()+"§eに参加しました。");
                }
            } else if (g.isLose(requestedTeam)) {
                org.bukkit.scoreboard.Team st = g.getScoreBoardTeam(requestedTeam);
                p.sendMessage(st.getColor() + st.getDisplayName() + "§rはすでに負けているため参加できません。別のチームに参加してください。");
                return;
            }

            if (
                    g.getPlayerJoinedTeam(p) != Team.NOT_JOINED
                    && g.getStatus().getPhaseId() >= 1
            ) {
                p.teleport(g.getTeamSpawnPoint(p));
            }
        }

        if (Arrays.asList(clickToCloseItems).contains(clicked.getType())) e.getView().close();
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return (e.getInventory().getSize() == 9) && (e.getView().getTitle().equals("チームを選択"));
    }

}
