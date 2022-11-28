package com.nekozouneko.anni;

import com.nekozouneko.anni.command.ANNIAdminCommand;
import com.nekozouneko.anni.command.ANNICommand;
import com.nekozouneko.anni.game.GameManager;
import com.nekozouneko.anni.game.MapManager;
import com.nekozouneko.anni.listener.*;
import com.nekozouneko.anni.task.UpdateBoard;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ANNIPlugin extends JavaPlugin {

    private static ANNIPlugin instance;
    private static Netherboard nb;
    private static BukkitRunnable boardTask;
    private static GameManager gm;
    private static MapManager mm;

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

        /* ----- Scoreboard  ----- */

        getLogger().info("Initializing scoreboard...");

        nb = Netherboard.instance();

        boardTask = new UpdateBoard(nb);
        boardTask.runTaskTimer(this, 5, 5);

        getLogger().info("Initialized scoreboard.");

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
        registerRecipe();

        /* ----- System ----- */
        getLogger().info("Initializing system...");

        gm = new GameManager();
        mm = new MapManager();

    }

    @Override
    public  void onDisable() {
        ((UpdateBoard) boardTask).stop();
        boardTask = null;
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

}
