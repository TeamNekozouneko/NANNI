package com.nekozouneko.anni.command;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.database.ANNIDatabase;
import com.nekozouneko.anni.game.ANNIBigMessage;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.gui.KitMenu;
import com.nekozouneko.anni.gui.TeamSelector;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ANNICommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            for (String b : ANNIBigMessage.createMessage('N', 'b',
                    "&bN&c&lA&9&lN&e&lN&a&lI&7 (Nekozouneko Annihilation)",
                    "", "",
                    "Ver. " + ANNIPlugin.getInstance().getDescription().getVersion(),
                    "&7(C) 2022 Team Nekozouneko,",
                    "&7                  Apache-2.0 License"
            )) {
                sender.sendMessage(b);
            }
        }
        else if (args.length == 1) {
            switch (args[0]) {
                case "join":
                    ANNIPlugin.getGM().join((Player) sender);
                    break;
                case "leave":
                    ANNIPlugin.getGM().leaveFromGame((Player) sender);
                    if (((Player) sender).getLocation().getWorld() != ANNIPlugin.getLobby().getLocation().getBukkitWorld()) {
                        ANNIPlugin.teleportToLobby((Player) sender);
                    }
                    break;
                case "team":
                    if (ANNIPlugin.getGM().isJoined((Player) sender)) {
                        TeamSelector.open((Player) sender);
                    }
                    else sender.sendMessage("参加していないためチームを選択することはできません");
                    break;
                case "kit":
                    KitMenu.open((Player) sender, 1, false);
                    break;
                case "status":
                    statusCommand(sender, command, label, args);
                    break;
                default:
                    sender.sendMessage("そんなサブコマンドないよ");
                    break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            String[] arg = new String[] {"join", "leave", "team"};

            for (String a : arg) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    tab.add(a);
                }
            }
        } else {
        }
        return (tab == null || tab.size() == 0) ? null : tab;
    }

    private void statusCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player tar;
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage("§cプレイヤーとして実行しない場合、情報を見る対象を指定してください");
                return;
            }

            tar = Bukkit.getPlayer(args[1]);
            if (tar == null) {
                sender.sendMessage("§cそのようなプレイヤーは存在しません。");
                return;
            }
        } else tar = (Player) sender;

        ANNIDatabase db = ANNIPlugin.getANNIDB();
        int k = db.getKillCount(tar.getUniqueId());
        int de = db.getDeathCount(tar.getUniqueId());
        int w = db.getWinCount(tar.getUniqueId());
        int l = db.getLoseCount(tar.getUniqueId());
        int ttl = w+l;
        String late = String.format("%.2f", ANNIUtil.KDCalc(k, de));

        float wlatec = (float)ttl/100f;

        sender.sendMessage("§7§l| §r"+tar.getName() + "のANNI内の統計");
        sender.sendMessage("§7§l| §8§o("+tar.getUniqueId()+")");
        sender.sendMessage("§7§l| §6K/D レート: §7" + late + " ("+k+" kill / "+de+" death)");
        sender.sendMessage("§7§l| §6勝利/敗北数: §7" + db.getWinCount(tar.getUniqueId()) + " / " + db.getLoseCount(tar.getUniqueId()) + " (勝率: "+String.format("%.1f", w/wlatec)+"%)");
    }
}
