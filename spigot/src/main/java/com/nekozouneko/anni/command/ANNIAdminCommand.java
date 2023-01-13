package com.nekozouneko.anni.command;

import com.google.gson.Gson;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNITeam;
import com.nekozouneko.anni.file.ANNIKit;
import com.nekozouneko.anni.file.ANNILobby;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import com.nekozouneko.anni.game.manager.MapManager;

import com.nekozouneko.anni.gui.KitEditor;
import com.nekozouneko.anni.gui.KitMenu;
import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.gui.location.TeamSpawnPointLocation;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (args[1].equalsIgnoreCase("timer")) {
                    ANNIPlugin.getGM().getGame().setTimer(Long.parseLong(args[2]));
                }
                else if (args[1].equalsIgnoreCase("phase")) {
                    ANNIPlugin.getGM().getGame().changeStatus(ANNIStatus.valueOf(args[2]));
                }
            } else if (args[0].equalsIgnoreCase("kit")) {
                if (args[1].equalsIgnoreCase("add")) {
                    addKit(sender, command, label, args);
                } else if (args[1].equalsIgnoreCase("edit")) {
                    editKit(sender, command, label, args);
                }
                else if (args[1].equalsIgnoreCase("list")) {
                    listKit(sender, command, label, args);
                }
                else if (args[1].equalsIgnoreCase("remove")) {
                    removeKit(sender, command, label, args);
                }
            }
            else if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("nexus-health")) {
                    if (args.length >= 4) {
                        ANNITeam t = null;
                        Integer h = null;
                        try {
                            t = ANNITeam.valueOf(args[2].toUpperCase());
                            h = Integer.parseInt(args[3]);
                        } catch (IllegalArgumentException ignored) {
                            sender.sendMessage("§c使用方法: /anni-admin admin nexus-health <team> <health>");
                            return true;
                        }
                        ANNIPlugin.getGM().getGame().setNexusHealth(t, h);
                        for (ANNIGame.TeamPlayerInventory tpis : ANNIPlugin.getGM().getGame().getSavedInventories().values()) {
                            if (tpis.getTeam() == t) {
                                tpis.setAllowJoin(true);
                            }
                        }
                    } else {
                        sender.sendMessage("§c使用方法: /anni-admin admin nexus-health <team> <health>");
                    }
                }
                else if (args[1].equalsIgnoreCase("buff-item")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        ItemStack bbi = new ItemStack(Material.NETHER_STAR);

                        ItemMeta im = bbi.getItemMeta();
                        im.getPersistentDataContainer().set(
                                new NamespacedKey(ANNIPlugin.getInstance(), "buffmenu"),
                                PersistentDataType.BYTE, (byte)1
                        );
                        bbi.setItemMeta(im);
                        p.getInventory().addItem(bbi);
                    }
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
            arg = new AbstractMap.SimpleEntry<>(new String[]{"debug", "game", "kit", "lobby", "map"}, args.length);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("map")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"add", "edit", "list", "reload", "remove"}, args.length);
            } else if (args[0].equalsIgnoreCase("lobby")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"spawn"}, args.length);
            } else if (args[0].equalsIgnoreCase("kit")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"add", "edit", "list", "remove"}, args.length);
            } else if (args[0].equalsIgnoreCase("game")) {
                arg = new AbstractMap.SimpleEntry<>(new String[]{"max", "min", "nexus", "spawn"}, args.length);
            } else if (args[0].equalsIgnoreCase("debug")) {
                arg = new AbstractMap.SimpleEntry<>(new String[] {"timer", "phase"}, args.length);
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
        final ANNITeam t;
        final Player p = ((Player) sender);

        try {
            t = ANNITeam.valueOf(args[4]);
            if (t.isSpectator()) throw new IllegalArgumentException();
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
        final ANNITeam t;
        final Player p = ((Player) sender);

        try {
            t = ANNITeam.valueOf(args[4]);
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

                BufferedWriter wr = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                                        new File(ANNIPlugin.getInstance().getDataFolder(), "lobby.json")
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

    public static void addKit(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Please run as player.");
            return;
        }
        if (args.length < 5) {
            sender.sendMessage("使用方法: /"+label+" kit add <display> <shortId> <price>");
            return;
        }

        Player p = (Player) sender;
        String display = args[2];
        String shortId = args[3];
        double price;
        try {
            price = Double.parseDouble(args[4]);

            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage("価格が有効な数字ではありません。");
            return;
        }

        Random r = new Random();
        int i = r.nextInt(0x10000000);
        String id = String.format("%07x", i);

        while (ANNIPlugin.getKM().getLoadedKits().containsKey(id)) {
            i = r.nextInt(0x10000000);
            id = String.format("%07x", i);
        }

        ANNIKit k = new ANNIKit(display, id, shortId, p.getInventory().getContents(), price);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(new File(ANNIPlugin.getKitDir(), id+".json")),
                        StandardCharsets.UTF_8
                )
        )) {
            Gson gson = new Gson();
            gson.toJson(k, ANNIKit.class, writer);

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ANNIPlugin.getKM().load(k);
    }

    public static void editKit(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Please run as player.");
            return;
        }
        if (args.length < 3) {
            KitMenu.open((Player) sender, 1, true);
            return;
        }

        Player p = (Player) sender;
        ANNIKit k = ANNIPlugin.getKM().getLoadedKits().get(args[2]);

        if (k == null) {
            sender.sendMessage("Kit " + args[2] + " is not existing");
            return;
        }

        KitEditor.open(k, p);
    }

    public static void listKit(CommandSender sender, Command cmd, String label, String[] args) {
        Map<String, ANNIKit> kits = ANNIPlugin.getKM().getLoadedKits();

        sender.sendMessage(kits.size() + "個のキットがロード済み:");
        for (String key : kits.keySet()) {
            sender.sendMessage("- " + kits.get(key).getDisplayName() + " §8("+key+"§8)");
        }
    }

    public static void removeKit(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length >= 3)) {
            sender.sendMessage("§c使用方法: /annim kit remove <id>");
            return;
        }

        String kitId = args[2];

        if (!ANNIPlugin.getKM().getLoadedKits().containsKey(kitId)) {
            sender.sendMessage("§cそのようなキットは存在しません");
            return;
        }

        ANNIPlugin.getKM().unload(kitId);

        try {
            Files.deleteIfExists(new File(ANNIPlugin.getKitDir(), kitId + ".json").toPath());
        } catch (IOException ie) {ie.printStackTrace();}

        sender.sendMessage("操作は正常に終了しました。");
    }
}
