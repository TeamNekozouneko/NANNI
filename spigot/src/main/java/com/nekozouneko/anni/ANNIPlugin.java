package com.nekozouneko.anni;

import com.google.gson.Gson;
import com.nekozouneko.anni.command.ANNIAdminCommand;
import com.nekozouneko.anni.command.ANNICommand;
import com.nekozouneko.anni.file.ANNILobby;
import com.nekozouneko.anni.game.GameManager;
import com.nekozouneko.anni.game.MapManager;
import com.nekozouneko.anni.listener.*;
import com.nekozouneko.anni.task.UpdateBoard;
import fr.minuskube.netherboard.Netherboard;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ANNIPlugin extends JavaPlugin {

    private static ANNIPlugin instance;
    private static Netherboard nb;
    private static UpdateBoard boardTask;
    private static GameManager gm;
    private static MapManager mm;
    private static ANNILobby lobbyWorld;

    /* - Vault - */
    private static Economy eco = null;

    private static File mapDir;

    public static ANNIPlugin getInstance() {
        return instance;
    }

    public static Netherboard getNb() {
        return nb;
    }

    public static File getMapDir() {
        return mapDir;
    }

    public static GameManager getGM() {
        return gm;
    }

    public static MapManager getMM() {
        return mm;
    }

    public static ANNILobby getLobby() {
        return lobbyWorld;
    }

    public static Economy getVaultEconomy() {
        return eco;
    }

    @Override
    public void onEnable() {
        instance = this;

        /* ----- Initialize dir ----- */

        getLogger().info("Initializing plugin directories...");

        try {
            if (!(getDataFolder().exists())) {
                getDataFolder().mkdir();
            }

            mapDir = new File(getDataFolder(), "maps");

            if (!(mapDir.exists())) {
                mapDir.mkdir();
            }

            getLogger().info("Initialized plugin directories.");
        } catch (SecurityException se) {
            getLogger().severe(se.getMessage());
        }

        /* ----- Configuration -----  */

        getLogger().info("Loading configuration...");

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        getLogger().info("Configuration is successful loaded.");

        /* ----- Listener ----- */

        getLogger().info("Registering listener...");

        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new BlockDestroyListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);

        getLogger().info("Registered listener.");

        /* ----- Command ----- */

        getLogger().info("Registering command...");

        getCommand("nanni").setExecutor(new ANNICommand());
        getCommand("nanni-admin").setExecutor(new ANNIAdminCommand());

        getLogger().info("Registered Command.");

        /* ----- Recipe ----- */
        if (getConfig().getBoolean("anni.anni_recipe")) {
            registerRecipe();
        }

        /* ----- System ----- */
        getLogger().info("Initializing system...");

        mm = new MapManager();
        gm = new GameManager(
                getConfig().getInt("anni.min-players", 2),
                getConfig().getInt("anni.max-players", 100),
                getConfig().getInt("anni.rule", 2)
        );
        loadLobby();

        getLogger().info("Loading depends...");

        loadVaultEconomy();

        getLogger().info("Loaded depends!");

        /* ----- Task  ----- */

        getLogger().info("Initializing task...");

        nb = Netherboard.instance();

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

        gm.endGame(true);

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
            rec.setIngredient('c', Material.GOLDEN_APPLE);

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

}
