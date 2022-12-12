package com.nekozouneko.anni.command;

import com.nekozouneko.anni.ANNIPlugin;
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
        if (args.length == 1) {
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
                    KitMenu.open((Player) sender, 1);
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
}
