package com.nekozouneko.anni;

import com.nekozouneko.anni.command.ANNIAdminCommand;
import com.nekozouneko.anni.command.ANNICommand;
import com.nekozouneko.anni.listener.BlockDestroyListener;
import com.nekozouneko.anni.listener.InventoryClickListener;
import com.nekozouneko.anni.listener.PlayerKillListener;
import com.nekozouneko.anni.listener.PlayerTeleportListener;
import com.nekozouneko.anni.task.UpdateBoard;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ANNIPlugin extends JavaPlugin {

    private static ANNIPlugin instance;
    private static Netherboard nb;
    private static BukkitRunnable boardTask;

    public static ANNIPlugin getInstance() {
        return instance;
    }

    public static Netherboard getNb() {
        return nb;
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

            File mapsDir = new File(getDataFolder(), "maps");

            if (!(mapsDir.exists())) {
                mapsDir.mkdir();
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

        getLogger().info("Registered listener.");

        /* ----- Command ----- */

        getLogger().info("Registering command...");

        getCommand("nanni").setExecutor(new ANNICommand());
        getCommand("nanni-admin").setExecutor(new ANNIAdminCommand());

        getLogger().info("Registered Command.");
    }

    @Override
    public  void onDisable() {
        ((UpdateBoard) boardTask).stop();
        boardTask = null;


    }

}
