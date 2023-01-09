package com.nekozouneko.anni.command.shortcut;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0 && sender.hasPermission("nanni.command.points.other")) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
            if (Arrays.asList(Bukkit.getOfflinePlayers()).contains(op) || ANNIPlugin.getVaultEconomy().hasAccount(op)) {
                sender.sendMessage("§7" + args[0] + "の所持ポイント: §e"+ANNIPlugin.getVaultEconomy().getBalance(op));
            } else {
                sender.sendMessage("§c該当するプレイヤーまたはデータがないため取得できませんでした");
            }
        }
        else if (args.length > 0) {
            sender.sendMessage("§c権限が不足しています。");
        }
        else if (sender instanceof Player){
            Player self = (Player) sender;
            sender.sendMessage("§7あなたの所持ポイント: §e"+ANNIPlugin.getVaultEconomy().getBalance(self));
        }
        else {
            sender.sendMessage("§cプレイヤーとして実行するか、引数に所持ポイントを見たいプレイヤー名を入力してください。");
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> tab = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("nanni.command.points.other")) {
            for (OfflinePlayer offp : Bukkit.getOfflinePlayers()) {
                if (offp.getName() == null) continue;
                if (offp.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    tab.add(offp.getName());
                }
            }
        }

        return tab;
    }
}
