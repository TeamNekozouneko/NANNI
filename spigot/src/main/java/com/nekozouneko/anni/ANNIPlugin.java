package com.nekozouneko.anni;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.nekozouneko.anni.command.*;
import com.nekozouneko.anni.database.*;
import com.nekozouneko.anni.file.ANNIKit;
import com.nekozouneko.anni.file.ANNILobby;
import com.nekozouneko.anni.game.manager.*;
import com.nekozouneko.anni.listener.*;
import com.nekozouneko.anni.task.UpdateBoard;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.WorldGuard;

import fr.minuskube.netherboard.Netherboard;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ANNIPlugin extends JavaPlugin {

    /* ANNI System */
    private static ANNIPlugin instance;

    // board update
    private static UpdateBoard boardTask;

    // manager
    private static GameManager gm;
    private static MapManager mm;
    private static KitManager km;

    // lobby & configuration & database
    private static ANNILobby lobbyWorld;
    private static ANNIConfig config;
    private static ANNIDatabase db;

    /* integration & depends */
    private static Netherboard nb;
    private static Scoreboard sb;
    private static WorldEdit we;
    private static WorldGuard wg;
    private static Economy eco = null;

    public final static ANNIKit DEFAULT_KIT;

    /* Directories */

    private static File mapDir;
    private static File kitDir;

    static {
        ItemStack[] inv = new ItemStack[41];

        inv[0] = new ItemStack(Material.WOODEN_SWORD);
        inv[1] = new ItemStack(Material.STONE_PICKAXE);
        inv[2] = new ItemStack(Material.WOODEN_AXE);
        inv[3] = new ItemStack(Material.WOODEN_SHOVEL);
        inv[8] = new ItemStack(Material.BREAD, 32);

        inv[36] = new ItemStack(Material.LEATHER_BOOTS);
        inv[37] = new ItemStack(Material.LEATHER_LEGGINGS);
        inv[38] = new ItemStack(Material.LEATHER_CHESTPLATE);
        inv[39] = new ItemStack(Material.LEATHER_HELMET);

        DEFAULT_KIT = new ANNIKit("デフォルト", "<default>", inv, 0.0);
    }

    public static ANNIPlugin getInstance() {
        return instance;
    }

    public static Netherboard getNb() {
        return nb;
    }

    public static Scoreboard getSb() {
        return sb;
    }

    public static File getMapDir() {
        return mapDir;
    }

    public static File getKitDir() {
        return kitDir;
    }

    public static GameManager getGM() {
        return gm;
    }

    public static MapManager getMM() {
        return mm;
    }

    public static KitManager getKM() {
        return km;
    }

    public static ANNILobby getLobby() {
        return lobbyWorld;
    }

    public static Economy getVaultEconomy() {
        return eco;
    }

    public static ANNIConfig getANNIConf() {
        return config;
    }

    public static ANNIDatabase getANNIDB() {
        return db;
    }

    public static WorldEdit getWE() {
        return we;
    }

    public static WorldGuard getWG() {
        return wg;
    }

    public static void teleportToLobby(Player player) {
        player.teleport(getLobby().getLocation().toLocation());
    }

    @Override
    public void onEnable() {
        instance = this;
        nb = Netherboard.instance();
        sb = getServer().getScoreboardManager().getNewScoreboard();

        /* ----- Initialize dir ----- */

        getLogger().info("Initializing plugin directories...");

        try {
            if (!(getDataFolder().exists())) {
                getDataFolder().mkdir();
            }

            mapDir = new File(getDataFolder(), "maps");
            kitDir = new File(getDataFolder(), "kits");

            if (!(mapDir.exists())) {
                mapDir.mkdir();
            }

            if (!(kitDir.exists())) {
                kitDir.mkdir();
            }

            getLogger().info("Initialized plugin directories.");
        } catch (SecurityException se) {
            getLogger().severe(se.getMessage());
        }

        /* ----- Configuration -----  */

        getLogger().info("Loading configuration...");

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        config = new ANNIConfig(this);

        getLogger().info("Configuration is successful loaded.");

        /* ----- Listener ----- */

        getLogger().info("Registering listener...");

        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new BlockDestroyListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new CraftItemListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(), this);

        getLogger().info("Registered listener.");

        /* ----- Command ----- */

        getLogger().info("Registering command...");

        getCommand("nanni").setExecutor(new ANNICommand());
        getCommand("nanni-admin").setExecutor(new ANNIAdminCommand());

        getLogger().info("Registered Command.");

        /* ----- Recipe ----- */
        if (config.isEnabledCustomRecipe()) {
            registerRecipe();
        }

        /* ----- System ----- */
        getLogger().info("Initializing system...");

        getLogger().info("- Loading managers/lobby (1/3)");

        initDefaultKit();

        mm = new MapManager();
        gm = new GameManager(
                config.MinPlayers(),
                config.MaxPlayers(),
                config.ANNIRule()
        );
        km = new KitManager();
        loadLobby();

        getLogger().info("- Loading database... (2/3)");

        if (config.DatabaseType().equalsIgnoreCase("MySQL")) {
            db = null;
        } else db = new ANNISQLiteDatabase();

        for (Player p : getServer().getOnlinePlayers()) {
            db.checkToInitPlayerData(p.getUniqueId());
        }

        getLogger().info("- Loading depends... (3/3)");

        loadVaultEconomy();
        loadWorldEdit();
        loadWorldGuard();
        loadPlaceholderAPI();

        /* ----- Task  ----- */

        getLogger().info("Initializing task...");

        boardTask = new UpdateBoard(nb);
        boardTask.runTaskTimer(this, 5, 5);

        getLogger().info("Initialized task.");

    }

    @Override
    public void onDisable() {
        if (boardTask != null) {
            boardTask.stop();
            boardTask = null;
        }

        gm.getGame().pluginDisable();
        db.close();

        unregisterRecipe();
    }

    private void registerRecipe() {
        NamespacedKey egaid = new NamespacedKey(
                this,
                "enchanted_golden_apple"
        );

        if (getServer().getRecipe(egaid) == null) {
            ShapedRecipe rec = new ShapedRecipe(
                    new NamespacedKey(
                            this,
                            "enchanted_golden_apple"
                    ),
                    new ItemStack(
                            Material.ENCHANTED_GOLDEN_APPLE
                    )
            );

            rec.shape("bbb", "bcb", "bbb");
            rec.setIngredient('b', Material.GOLD_BLOCK);
            rec.setIngredient('c', Material.APPLE);

            getServer().addRecipe(rec);
        }
    }

    private void unregisterRecipe() {
        NamespacedKey ega_r = new NamespacedKey(this, "enchanted_golden_apple");

        if (Bukkit.getServer().getRecipe(ega_r) != null) getServer().removeRecipe(ega_r);
    }

    public void loadLobby() {
        final File lobbyFile = new File(getDataFolder(), "lobby.json");
        if (lobbyFile.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(getDataFolder(), "lobby.json")),
                    StandardCharsets.UTF_8
            ))) {
                Gson gson = new Gson();

                lobbyWorld = gson.fromJson(br, ANNILobby.class);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                lobbyWorld = null;
            }
        }
    }

    public boolean loadVaultEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

    public void loadWorldEdit() {
        we = WorldEdit.getInstance();

    }

    public void loadWorldGuard() {
        wg = WorldGuard.getInstance();
    }

    public void loadPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ANNIExpansion().register();
        }
    }

    public void initDefaultKit() {
        Gson gson = new Gson();
        File kdf = new File(kitDir, "default.json");
        ANNIKit kd;

        if (kdf.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(kdf),
                            StandardCharsets.UTF_8
                    )
            )) {
                kd = gson.fromJson(reader, ANNIKit.class);
            } catch (IOException ignored) {
                kd = DEFAULT_KIT;
            } catch (JsonSyntaxException e) {
                createDefaultKitFile();
                kd = DEFAULT_KIT;
            }

            if (kd == null) createDefaultKitFile();
        } else createDefaultKitFile();
    }

    private void createDefaultKitFile() {
        Gson gson = new Gson();
        File kdf = new File(kitDir, "default.json");

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(kdf),
                        StandardCharsets.UTF_8
                )
        )) {
            gson.toJson(DEFAULT_KIT, ANNIKit.class, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
