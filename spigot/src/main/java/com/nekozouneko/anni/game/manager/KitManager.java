package com.nekozouneko.anni.game.manager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNIKit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private final Map<String, ANNIKit> loaded = new HashMap<>();

    public KitManager() {
        reload(ANNIPlugin.getKitDir());
    }

    public void load(File f) {
        Gson gson = new Gson();
        ANNIKit kit;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(f),
                        StandardCharsets.UTF_8
                )
        )) {
            kit = gson.fromJson(reader, ANNIKit.class);
        } catch (IOException | JsonSyntaxException e) {
            plugin.getLogger().warning("Failed load kit: " + f);
            plugin.getLogger().warning("Exception message: " + e.getMessage());
            return;
        }

        if (kit != null) {
            loaded.put(kit.getID(), kit);
            plugin.getLogger().info("Loaded kit: " + kit + ", File: " + f.getName());
        }
    }

    public void load(ANNIKit kit) {
        loaded.put(kit.getID(), kit);
    }

    public void loadAll(File dir) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".json")) load(f);
            }
        }
    }

    public void reload(File dir) {
        loaded.clear();
        loadAll(dir);
    }

    public void unload(String id) {
        loaded.remove(id);
    }

    public Map<String, ANNIKit> getLoadedKits() {
        return loaded;
    }

}
