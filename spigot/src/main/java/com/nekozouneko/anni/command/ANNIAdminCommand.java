package com.nekozouneko.anni.command;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ANNIAdminCommand implements CommandExecutor, TabCompleter {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // pass
        } else if (args.length == 1) {
            // pass
        } else {
            if (args[0].equalsIgnoreCase("map")) {
                if (args[1].equalsIgnoreCase("add")) {
                    if (args.length >= 4) {
                        addMap(sender, command, label, args);
                    }
                } else if (args[1].equalsIgnoreCase("edit")) {

                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();

        if (args.length == 1) {
            final String[] arg = new String[] {"map"};

            for (String a : arg) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    tab.add(a);
                }
            }
        } else if (args.length == 2) {
            final String[] arg = new String[] {"add", "edit", "list", "remove"};

            for (String a : arg) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                    tab.add(a);
                }
            }
        }
        return tab;
    }

    public static void addMap(CommandSender sender, Command cmd, String label, String[] args) {
        final World w = Bukkit.getWorld(args[2]);
        final String d = args[3];

        if (w == null) {
            sender.sendMessage("World " + args[2] + " is not existing.");
            return;
        }

        ANNIMap map = new ANNIMap(w.getName(), d, Collections.emptyList(), 2, 100, 2);

        try {
            Gson gson = new Gson();

            BufferedWriter wr = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(new File(ANNIPlugin.getMapDir(), args[2] + ".json")),
                            StandardCharsets.UTF_8
                    )
            );

            gson.toJson(map, ANNIMap.class, wr);

            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage("IO Exception called");
        }

    }
}
