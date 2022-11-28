package com.nekozouneko.anni.command;

import com.google.gson.Gson;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNILobby;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.game.MapManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

                } else if (args[1].equalsIgnoreCase("list")) {
                    listMap(sender, command, label, args);
                }
            } else if (args[0].equalsIgnoreCase("lobby")) {
                if (args[1].equalsIgnoreCase("spawn")) {
                    setSpawnOfLobby(sender, command, label, args);
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();

        if (args.length == 1) {
            final String[] arg = new String[] {"kit", "lobby", "map"};

            for (String a : arg) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    tab.add(a);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("map")) {
                final String[] arg = new String[]{"add", "edit", "list", "reload", "remove"};

                for (String a : arg) {
                    if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                        tab.add(a);
                    }
                }
            } else if (args[0].equalsIgnoreCase("lobby")) {
                final String[] arg = new String[]{"spawn"};
            } else if (args[0].equalsIgnoreCase("kit")) {

            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("map") && args[1].equalsIgnoreCase("edit")) {
                final String[] arg = new String[]{"display", "nexus", "rule"};

                for (String a : arg) {
                    if (a.toLowerCase().startsWith(args[3].toLowerCase())) {
                        tab.add(a);
                    }
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

    public static void listMap(CommandSender sender, Command cmd, String label, String[] args) {
        MapManager mm = ANNIPlugin.getMM();

        sender.sendMessage("Maps: ");
        for (String k : mm.getMaps().keySet()) {
            ANNIMap mp = mm.getMaps().get(k);
            sender.sendMessage("- " + mp.getDisplay() + " (" + mp.getWorld() + ")");
        }
    }

    public static void setSpawnOfLobby(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            try {
                ANNILobby annil = new ANNILobby(p.getLocation());
                final Gson gson = new Gson();
                ANNIPlugin ap = ANNIPlugin.getInstance();

                BufferedWriter wr = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                                        new File(ap.getDataFolder(), "lobby.json")
                                ),
                                StandardCharsets.UTF_8
                        )
                );
                gson.toJson(annil, ANNILobby.class, wr);

                wr.flush();
                wr.close();

                ANNIPlugin.getInstance().loadLobby();

                sender.sendMessage("ロビーを以下の座標に設定しました:");
                sender.sendMessage(
                        "W: "
                        + p.getLocation().getWorld().getName()
                        + " X: " + p.getLocation().getX()
                        + " Y: " + p.getLocation().getY()
                        + " Z: " + p.getLocation().getZ()
                        + " YW: " + p.getLocation().getYaw()
                        + " P: " + p.getLocation().getPitch()
                );
            } catch (IOException ie) {}
        }
    }
}
