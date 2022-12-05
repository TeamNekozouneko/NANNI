package com.nekozouneko.anni.command;

import com.google.gson.Gson;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.file.ANNILobby;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.game.MapManager;

import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.gui.location.TeamSpawnPointLocation;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
                    addMap(sender, command, label, args);
                } else if (args[1].equalsIgnoreCase("edit")) {
                    if (args[3].equalsIgnoreCase("nexus")) {
                        setNexusLocation(sender, command, label, args);
                    } else if (args[3].equalsIgnoreCase("spawn")) {
                        setTeamSpawnPointLocation(sender, command, label, args);
                    }
                } else if (args[1].equalsIgnoreCase("list")) {
                    listMap(sender, command, label, args);
                }
            } else if (args[0].equalsIgnoreCase("lobby")) {
                if (args[1].equalsIgnoreCase("spawn")) {
                    setSpawnOfLobby(sender, command, label, args);
                }
            } else if (args[0].equalsIgnoreCase("game")) {
                if (args[1].equalsIgnoreCase("min")) {
                    plugin.getConfig().set("anni.min-players", Integer.parseInt(args[2]));
                    plugin.saveConfig();
                } else if (args[1].equalsIgnoreCase("max")) {
                    plugin.getConfig().set("anni.max-players", Integer.parseInt(args[2]));
                    plugin.saveConfig();
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        Map.Entry<String[], Integer> arg = new AbstractMap.SimpleEntry<>(new String[0], args.length);

        if (args.length == 1) {
            arg = new AbstractMap.SimpleEntry<>(new String[]{"admin", "game", "kit", "lobby", "map"}, args.length);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("map")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"add", "edit", "list", "reload", "remove"}, args.length);
            } else if (args[0].equalsIgnoreCase("lobby")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"spawn"}, args.length);
            } else if (args[0].equalsIgnoreCase("kit")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"add"}, args.length);
            } else if (args[0].equalsIgnoreCase("game")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"max", "min", "nexus", "spawn"}, args.length);
            } else if (args[0].equalsIgnoreCase("admin")) {
                arg = new AbstractMap.SimpleEntry<>(new String[] {"end", "set-status"}, args.length);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("game")) {
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("map") && args[1].equalsIgnoreCase("edit")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"display", "nexus", "rule"}, args.length);
            }
        } else if (args.length == 5) {
            if (
                    args[0].equalsIgnoreCase("map") &&
                    args[1].equalsIgnoreCase("edit")
            ) {
                if (args[3].equalsIgnoreCase("nexus")) {
                    arg = new AbstractMap.SimpleEntry<>(new String[]{"RED", "BLUE", "YELLOW", "GREEN"}, args.length);
                } else if (args[3].equalsIgnoreCase("spawn")) {
                    arg = new AbstractMap.SimpleEntry<>(new String[]{"RED","BLUE","YELLOW","GREEN","SPECTATOR",}, args.length);
                }
            }
        }

        for (String a : arg.getKey()) {
            if (a.toLowerCase().startsWith(args[arg.getValue()-1].toLowerCase())) {
                tab.add(a);
            }
        }

        return tab;
    }

    public static void addMap(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(NChatColor.RED + "使用方法: /" + label + " map add <world> [display]");
            return;
        }

        final World w = Bukkit.getWorld(args[2]);
        final String d;
        if (args.length >= 4) {
            d = args[3];
        } else {
            d = args[2];
        }

        if (w == null) {
            sender.sendMessage(NChatColor.RED + args[2] + " というワールドは存在しません");
            return;
        }

        if (ANNIPlugin.getMM().getMaps().containsKey(args[2])) {
            sender.sendMessage(NChatColor.RED + "すでに " + args[2] + " というマップは追加されています。");
        }

        ANNIMap map = new ANNIMap(w.getName(), d, new HashMap<>(), new HashMap<>());

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

            sender.sendMessage(NChatColor.GREEN + "マップ " + map.getDisplay() + " が追加されました。");
            ANNIPlugin.getMM().load(new File(ANNIPlugin.getMapDir(), args[2] + ".json"));
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(NChatColor.RED + "ファイルに書き込む際エラーが発生しました。コンソールを確認してください。");
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

    public static void setNexusLocation(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 4) {
            sender.sendMessage(NChatColor.RED + "チームを指定してください");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(NChatColor.RED + "プレイヤーとして実行してください。");
            return;
        }

        final World w = Bukkit.getWorld(args[2]);
        final Team t;
        final Player p = ((Player) sender);

        try {
            t = Team.valueOf(args[4]);
            if (t == Team.SPECTATOR) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            sender.sendMessage(NChatColor.RED + "存在しないチームか無効なチームです。");
            return;
        }
        if (w == null) {
            sender.sendMessage(args[2] + " というワールドは存在しません。");
            return;
        }

        MapEditor.glc.put(p.getUniqueId(), new NexusLocation(ANNIPlugin.getMM().getMap(w.getName()), t, p));
        sender.sendMessage("ネクサスをクリックして設定します。 (指定チーム: " + t.name() + ")");
    }

    public static void setTeamSpawnPointLocation(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 4) {
            sender.sendMessage(NChatColor.RED + "チームを指定してください");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(NChatColor.RED + "プレイヤーとして実行してください。");
            return;
        }

        final World w = Bukkit.getWorld(args[2]);
        final Team t;
        final Player p = ((Player) sender);

        try {
            t = Team.valueOf(args[4]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(NChatColor.RED + "存在しないチームか無効なチームです。");
            return;
        }
        if (w == null) {
            sender.sendMessage(args[2] + " というワールドは存在しません。");
            return;
        }

        MapEditor.glc.put(p.getUniqueId(), new TeamSpawnPointLocation(ANNIPlugin.getMM().getMap(w.getName()), t, p));
        sender.sendMessage("スポーン地点にブロックを設置して設定します。 (指定チーム: " + t.name() + ")");
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
