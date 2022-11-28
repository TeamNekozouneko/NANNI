package com.nekozouneko.anni.game;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNIMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MapManager {

    private final Gson gson = new Gson();
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private final Map<String, ANNIMap> loadedMap = new HashMap<>();

    public MapManager() {
        reload(ANNIPlugin.getMapDir());
    }

    public void load(File f) throws JsonSyntaxException {
        if (f == null) throw new IllegalArgumentException();

        try {
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(f),
                            StandardCharsets.UTF_8
                    )
            );

            ANNIMap am = gson.fromJson(r, ANNIMap.class);
            if (am == null) throw new JsonSyntaxException("");

            loadedMap.put(am.getWorld(), am);
        } catch (IOException ie) {
        }
    }

    public void loadAll(File dir) {
        if (dir.listFiles() == null) return;

        try {
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith(".json")) {
                    load(f);
                }
            }
        } catch (NullPointerException npe) {
            return;
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        }
    }

    public void reload(File dir) {
        loadedMap.clear();
        loadAll(dir);
    }

    public boolean unload(String w) {
        try {
            loadedMap.remove(w);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public Map<String, ANNIMap> getMaps() {
        return loadedMap;
    }

    public void putMap(ANNIMap map) {
        loadedMap.put(map.getWorld(), map);
    }

    public ANNIMap getMap(String k) {
        return loadedMap.get(k);
    }

}
