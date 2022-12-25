package com.nekozouneko.anni;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ANNIConfig {

    private static final int LATEST_CONFIG_VERSION = 1;
    private static final Map<String, Object> DEFAULT_CONFIG = Collections.unmodifiableMap(
            new HashMap<String, Object>() {{
                put("database.type", "SQLite");

                put("database.sqlite-file", "anni.db");

                put("database.mysql-address", "localhost");
                put("database.mysql-port", 3306);
                put("database.mysql-user", "root");
                put("database.mysql-password", "");

                put("anni.timezone", "Asia/Tokyo");
                put("anni.max-players", 120);
                put("anni.min-players", 2);
                put("anni.rule", 2);

                put("anni.features.custom-recipe", true);
                put("anni.features.shop.enabled", true);
                put("anni.features.shop.default-purchase-type", "VAULT");

                put("anni.nexus.health", 100);
                put("anni.nexus.material", "END_STONE");

                put("anni.teams.red.id", "red");
                put("anni.teams.red.display", "赤チーム");
                put("anni.teams.red.prefix", "&7[&cR&7]");
                put("anni.teams.red.team-color", "c");

                put("anni.teams.blue.id", "blue");
                put("anni.teams.blue.display", "青チーム");
                put("anni.teams.blue.prefix", "&7[&9B&7]");
                put("anni.teams.blue.team-color", "9");

                put("anni.teams.yellow.id", "yellow");
                put("anni.teams.yellow.display", "黄チーム");
                put("anni.teams.yellow.prefix", "&7[&eY&7]");
                put("anni.teams.yellow.team-color", "e");

                put("anni.teams.green.id", "green");
                put("anni.teams.green.display", "緑チーム");
                put("anni.teams.green.prefix", "&7[&aG&7]");
                put("anni.teams.green.team-color", "a");

                put("anni.teams.spectator.id", "spectator");
                put("anni.teams.spectator.display", "観戦者");
                put("anni.teams.spectator.prefix", "&7[&8SP&7]");
                put("anni.teams.spectator.team-color", "8");

                put("config-version", 1);

    }});
    private static final Map<String, ChatColor> colorMap = new HashMap<String, ChatColor>() {{
        put("0", ChatColor.BLACK);
        put("1", ChatColor.DARK_BLUE);
        put("2", ChatColor.DARK_GREEN);
        put("3", ChatColor.DARK_AQUA);
        put("4", ChatColor.DARK_RED);
        put("5", ChatColor.DARK_PURPLE);
        put("6", ChatColor.GOLD);
        put("7", ChatColor.GRAY);
        put("8", ChatColor.DARK_GRAY);
        put("9", ChatColor.BLUE);
        put("a", ChatColor.GREEN);
        put("b", ChatColor.AQUA);
        put("c", ChatColor.RED);
        put("d", ChatColor.LIGHT_PURPLE);
        put("e", ChatColor.YELLOW);
        put("f", ChatColor.WHITE);
        put("r", ChatColor.RESET);
    }};

    private final ANNIPlugin plugin;
    private FileConfiguration conf;

    /* Configuration */

    private String DATABASE_TYPE;
    private String DATABASE_SQLITE_FILE;
    private String DATABASE_MYSQL_ADDRESS;
    private int DATABASE_MYSQL_PORT;
    private String DATABASE_MYSQL_USER;
    private String DATABASE_MYSQL_PASSWORD;

    private String ANNI_TIMEZONE;
    private int ANNI_MAX_PLAYERS;
    private int ANNI_MIN_PLAYERS;
    private int ANNI_RULE;

    private boolean ANNI_FEATURES_CUSTOM_RECIPE;
    private boolean ANNI_FEATURES_SHOP;

    private int ANNI_NEXUS_HEALTH;
    private String ANNI_NEXUS_MATERIAL;

    private String ANNI_TEAMS_RED_ID;
    private String ANNI_TEAMS_RED_DISPLAY;
    private String ANNI_TEAMS_RED_PREFIX;
    private String ANNI_TEAMS_RED_TEAM_COLOR;

    private String ANNI_TEAMS_BLUE_ID;
    private String ANNI_TEAMS_BLUE_DISPLAY;
    private String ANNI_TEAMS_BLUE_PREFIX;
    private String ANNI_TEAMS_BLUE_TEAM_COLOR;

    private String ANNI_TEAMS_YELLOW_ID;
    private String ANNI_TEAMS_YELLOW_DISPLAY;
    private String ANNI_TEAMS_YELLOW_PREFIX;
    private String ANNI_TEAMS_YELLOW_TEAM_COLOR;

    private String ANNI_TEAMS_GREEN_ID;
    private String ANNI_TEAMS_GREEN_DISPLAY;
    private String ANNI_TEAMS_GREEN_PREFIX;
    private String ANNI_TEAMS_GREEN_TEAM_COLOR;

    private String ANNI_TEAMS_SPECTATOR_ID;
    private String ANNI_TEAMS_SPECTATOR_DISPLAY;
    private String ANNI_TEAMS_SPECTATOR_PREFIX;
    private String ANNI_TEAMS_SPECTATOR_TEAM_COLOR;

    private int CONFIG_VERSION;

    protected ANNIConfig(ANNIPlugin plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();

        this.conf = plugin.getConfig();

        reload();

        if (conf.getInt("config-version", LATEST_CONFIG_VERSION) < LATEST_CONFIG_VERSION) update();
    }

    public void reload() {
        this.conf = plugin.getConfig();
        conf.options().copyDefaults(true);

        DATABASE_TYPE = conf.getString("database.type", "SQLite").toUpperCase();
        DATABASE_SQLITE_FILE = conf.getString("database.sqlite-file");
        DATABASE_MYSQL_ADDRESS = conf.getString("database.mysql-address");
        DATABASE_MYSQL_PORT = conf.getInt("database.mysql-port");
        DATABASE_MYSQL_USER = conf.getString("database.mysql-user");
        DATABASE_MYSQL_PASSWORD = conf.getString("database.mysql-password");

        ANNI_TIMEZONE = conf.getString("anni.timezone");
        ANNI_MAX_PLAYERS = conf.getInt("anni.max-players");
        ANNI_MIN_PLAYERS = conf.getInt("anni.min-players");
        ANNI_RULE = conf.getInt("anni.rule");

        ANNI_FEATURES_CUSTOM_RECIPE = conf.getBoolean("anni.features.custom-recipe");
        ANNI_FEATURES_SHOP = conf.getBoolean("anni.features.shop");

        ANNI_NEXUS_HEALTH = conf.getInt("anni.nexus.health");
        ANNI_NEXUS_MATERIAL = conf.getString("anni.nexus.material");

        ANNI_TEAMS_RED_ID = conf.getString("anni.teams.red.id");
        ANNI_TEAMS_RED_DISPLAY = conf.getString("anni.teams.red.display");
        ANNI_TEAMS_RED_PREFIX = conf.getString("anni.teams.red.prefix");
        ANNI_TEAMS_RED_TEAM_COLOR = conf.getString("anni.teams.red.team-color");

        ANNI_TEAMS_BLUE_ID = conf.getString("anni.teams.blue.id");
        ANNI_TEAMS_BLUE_DISPLAY = conf.getString("anni.teams.blue.display");
        ANNI_TEAMS_BLUE_PREFIX = conf.getString("anni.teams.blue.prefix");
        ANNI_TEAMS_BLUE_TEAM_COLOR = conf.getString("anni.teams.blue.team-color");

        ANNI_TEAMS_YELLOW_ID = conf.getString("anni.teams.yellow.id");
        ANNI_TEAMS_YELLOW_DISPLAY = conf.getString("anni.teams.yellow.display");
        ANNI_TEAMS_YELLOW_PREFIX = conf.getString("anni.teams.yellow.prefix");
        ANNI_TEAMS_YELLOW_TEAM_COLOR = conf.getString("anni.teams.yellow.team-color");

        ANNI_TEAMS_GREEN_ID = conf.getString("anni.teams.green.id");
        ANNI_TEAMS_GREEN_DISPLAY = conf.getString("anni.teams.green.display");
        ANNI_TEAMS_GREEN_PREFIX = conf.getString("anni.teams.green.prefix");
        ANNI_TEAMS_GREEN_TEAM_COLOR = conf.getString("anni.teams.green.team-color");

        ANNI_TEAMS_SPECTATOR_ID = conf.getString("anni.teams.spectator.id");
        ANNI_TEAMS_SPECTATOR_DISPLAY = conf.getString("anni.teams.spectator.display");
        ANNI_TEAMS_SPECTATOR_PREFIX = conf.getString("anni.teams.spectator.prefix");
        ANNI_TEAMS_SPECTATOR_TEAM_COLOR = conf.getString("anni.teams.spectator.team-color");

        CONFIG_VERSION = conf.getInt("config-version");

        configValidation();
    }

    public void configValidation() {
        // checks

        if (!(DATABASE_TYPE.equalsIgnoreCase("SQLite") || DATABASE_TYPE.equalsIgnoreCase("MySQL"))) {
            plugin.getLogger().info("Illegal value detected: (Key: database.type, Original: " + DATABASE_TYPE + ", New: SQLite");
            DATABASE_TYPE = "SQLite";
            conf.set("database.type", "SQLite");
        }

        if (DATABASE_SQLITE_FILE.matches(".*?([/:*?\"<>|\\\\]+?).*?")) {
            plugin.getLogger().info("Illegal value detected: (Key: database.sqlite-file, Original: " + DATABASE_SQLITE_FILE + ", New: anni.db");
            DATABASE_SQLITE_FILE = "anni.db";
            conf.set("database.sqlite-file", "anni.db");
        }

        if (DATABASE_MYSQL_ADDRESS.matches("^.*?://?.*?") || DATABASE_MYSQL_ADDRESS.matches(".*?:[0-9]*?$")) {
            plugin.getLogger().info("Illegal value detected: (Key: database.mysql-address, Original: " + DATABASE_MYSQL_ADDRESS + ", New: localhost");
            DATABASE_MYSQL_ADDRESS = "localhost";
            conf.set("database.mysql-address", "localhost");
        }

        if (!(DATABASE_MYSQL_PORT <= 65535 && DATABASE_MYSQL_PORT > 0)) {
            plugin.getLogger().info("Illegal value detected: (Key: database.mysql-port, Original: " + DATABASE_MYSQL_PORT + ", New: 3306");
            DATABASE_MYSQL_PORT = 3306;
            conf.set("database.mysql-port", 3306);
        }

        // anni

        if (!(ANNI_RULE == 2 || ANNI_RULE == 4)) {
            plugin.getLogger().info("Illegal value detected: (Key: anni.rule, Original: " + ANNI_RULE + ", New: 2");
            ANNI_RULE = 2;
            conf.set("anni.rule", 2);
        }

        if (ANNI_RULE > ANNI_MIN_PLAYERS) {
            plugin.getLogger().info("Illegal value detected: (Key: anni.min-players, Original: " + ANNI_MIN_PLAYERS + ", New: "+ANNI_RULE);
            ANNI_MIN_PLAYERS = ANNI_RULE;
            conf.set("anni.min-players", ANNI_RULE);
        }

        if (ANNI_MAX_PLAYERS < ANNI_MIN_PLAYERS) {
            plugin.getLogger().info("Illegal value detected: (Key: anni.max-players, Original: " + ANNI_MAX_PLAYERS + ", New: "+ANNI_MIN_PLAYERS);
            ANNI_MAX_PLAYERS = ANNI_MIN_PLAYERS;
            conf.set("anni.max-players", ANNI_MIN_PLAYERS);
        }

        // anni.nexus

        if (ANNI_NEXUS_HEALTH < 1) {
            plugin.getLogger().info("Illegal value detected: (Key: anni.nexus.health, Original: " + ANNI_NEXUS_HEALTH + ", New: 100");
            ANNI_NEXUS_HEALTH = 100;
            conf.set("anni.nexus.health", 100);
        }

        try {
            Material.valueOf(ANNI_NEXUS_MATERIAL);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().info("Illegal value detected: (Key: anni.nexus.material, Original: " + ANNI_NEXUS_MATERIAL + ", New: END_STONE");
            ANNI_NEXUS_MATERIAL = "END_STONE";
            conf.set("anni.nexus.material", "END_STONE");
        }

        save();
    }

    public void update() {
        File dataDir = plugin.getDataFolder();
        File confFile = new File(dataDir, "config.yml");
        File oldFile = new File(dataDir, "config.yml."+(System.currentTimeMillis()/1000)+".update");

        try {
            Files.move(confFile.toPath(), oldFile.toPath());

            YamlConfiguration oldYaml = YamlConfiguration.loadConfiguration(oldFile);
            oldYaml.options().copyDefaults(true);

            plugin.saveDefaultConfig();
            plugin.reloadConfig();

            Configuration newYaml = plugin.getConfig();
            newYaml.options().copyDefaults(true);

            for (String key : oldYaml.getValues(true).keySet()) {
                Object val = oldYaml.getValues(true).get(key);

                if (val instanceof MemorySection) continue;
                plugin.getLogger().info("Updating config: key: " + key + ", value: " + val);
                newYaml.set(key, val);
            }

            newYaml.set("config-version", LATEST_CONFIG_VERSION);

            plugin.saveConfig();
            reload();
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("----------");
            plugin.getLogger().severe("Configuration update is failed");
        }
    }

    public void save() {
        conf.set("database.type", DATABASE_TYPE);
        conf.set("database.sqlite-file", DATABASE_SQLITE_FILE);
        conf.set("database.mysql-address", DATABASE_MYSQL_ADDRESS);
        conf.set("database.mysql-port", DATABASE_MYSQL_PORT);
        conf.set("database.mysql-user", DATABASE_MYSQL_USER);
        conf.set("database.mysql-password", DATABASE_MYSQL_PASSWORD);

        conf.set("anni.timezone", ANNI_TIMEZONE);
        conf.set("anni.max-players", ANNI_MAX_PLAYERS);
        conf.set("anni.min-players", ANNI_MIN_PLAYERS);
        conf.set("anni.rule", ANNI_RULE);

        conf.set("anni.features.custom-recipe", ANNI_FEATURES_CUSTOM_RECIPE);
        conf.set("anni.features.shop", ANNI_FEATURES_SHOP);

        conf.set("anni.nexus.health", ANNI_NEXUS_HEALTH);
        conf.set("anni.nexus.material", ANNI_NEXUS_MATERIAL);

        conf.set("anni.teams.red.id", ANNI_TEAMS_RED_ID);
        conf.set("anni.teams.red.display", ANNI_TEAMS_RED_DISPLAY);
        conf.set("anni.teams.red.prefix", ANNI_TEAMS_RED_PREFIX);
        conf.set("anni.teams.red.team-color", ANNI_TEAMS_RED_TEAM_COLOR);

        conf.set("anni.teams.blue.id", ANNI_TEAMS_BLUE_ID);
        conf.set("anni.teams.blue.display", ANNI_TEAMS_BLUE_DISPLAY);
        conf.set("anni.teams.blue.prefix", ANNI_TEAMS_BLUE_PREFIX);
        conf.set("anni.teams.blue.team-color", ANNI_TEAMS_BLUE_TEAM_COLOR);

        conf.set("anni.teams.yellow.id", ANNI_TEAMS_YELLOW_ID);
        conf.set("anni.teams.yellow.display", ANNI_TEAMS_YELLOW_DISPLAY);
        conf.set("anni.teams.yellow.prefix", ANNI_TEAMS_YELLOW_PREFIX);
        conf.set("anni.teams.yellow.team-color", ANNI_TEAMS_YELLOW_TEAM_COLOR);

        conf.set("anni.teams.green.id", ANNI_TEAMS_GREEN_ID);
        conf.set("anni.teams.green.display", ANNI_TEAMS_GREEN_DISPLAY);
        conf.set("anni.teams.green.prefix", ANNI_TEAMS_GREEN_PREFIX);
        conf.set("anni.teams.green.team-color", ANNI_TEAMS_GREEN_TEAM_COLOR);

        conf.set("anni.teams.spectator.id", ANNI_TEAMS_SPECTATOR_ID);
        conf.set("anni.teams.spectator.display", ANNI_TEAMS_SPECTATOR_DISPLAY);
        conf.set("anni.teams.spectator.prefix", ANNI_TEAMS_SPECTATOR_PREFIX);
        conf.set("anni.teams.spectator.team-color", ANNI_TEAMS_SPECTATOR_TEAM_COLOR);
        try {
            conf.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {e.printStackTrace();}
    }

    public String DatabaseType() {
        return DATABASE_TYPE;
    }

    public String DatabaseType(String value) {
        DATABASE_TYPE = value;
        return DATABASE_TYPE;
    }

    public String SQLiteFile() {
        return DATABASE_SQLITE_FILE;
    }

    public String SQLiteFile(String value) {
        DATABASE_SQLITE_FILE = value;
        return DATABASE_SQLITE_FILE;
    }

    public String MySQLAddress() {
        return DATABASE_MYSQL_ADDRESS;
    }

    public String MySQLAddress(String value) {
        DATABASE_MYSQL_ADDRESS = value;
        return DATABASE_MYSQL_ADDRESS;
    }

    public int MySQLPort() {
        return DATABASE_MYSQL_PORT;
    }

    public int MySQLPort(int value) {
        DATABASE_MYSQL_PORT = value;
        return DATABASE_MYSQL_PORT;
    }

    public String MySQLUser() {
        return DATABASE_MYSQL_USER;
    }

    public String MySQLUser(String value) {
        DATABASE_MYSQL_USER = value;
        return DATABASE_MYSQL_USER;
    }

    public String MySQLPassword() {
        return DATABASE_MYSQL_PASSWORD;
    }

    public String MySQLPassword(String value) {
        DATABASE_MYSQL_PASSWORD = value;
        return DATABASE_MYSQL_PASSWORD;
    }

    public String TimeZone() {
        return ANNI_TIMEZONE;
    }

    public String TimeZone(String value) {
        ANNI_TIMEZONE = value;
        return ANNI_TIMEZONE;
    }

    public int MaxPlayers() {
        return ANNI_MAX_PLAYERS;
    }

    public int MaxPlayers(int value) {
        ANNI_MAX_PLAYERS = value;
        return ANNI_MAX_PLAYERS;
    }

    public int MinPlayers() {
        return ANNI_MIN_PLAYERS;
    }

    public int MinPlayers(int value) {
        ANNI_MIN_PLAYERS = value;
        return ANNI_MIN_PLAYERS;
    }

    public int ANNIRule() {
        return ANNI_RULE;
    }

    public int ANNIRule(int value) {
        ANNI_RULE = value;
        return ANNI_RULE;
    }

    public boolean isEnabledCustomRecipe() {
        return ANNI_FEATURES_CUSTOM_RECIPE;
    }

    public boolean isEnabledCustomRecipe(boolean value) {
        ANNI_FEATURES_CUSTOM_RECIPE = value;
        return ANNI_FEATURES_CUSTOM_RECIPE;
    }

    public boolean isEnabledShop() {
        return ANNI_FEATURES_SHOP;
    }

    public int nexusHealth() {
        return ANNI_NEXUS_HEALTH;
    }

    public int nexusHealth(int value) {
        ANNI_NEXUS_HEALTH = value;
        return value;
    }

    public Material nexusMaterial() {
        return Material.valueOf(ANNI_NEXUS_MATERIAL);
    }

    public Material nexusMaterial(String value) throws IllegalArgumentException {
        ANNI_NEXUS_MATERIAL = value;
        return Material.valueOf(ANNI_NEXUS_MATERIAL);
    }

    public Material nexusMaterial(Material value) {
        ANNI_NEXUS_MATERIAL = value.name();
        return Material.valueOf(ANNI_NEXUS_MATERIAL);
    }

    public String redTeamID() {
        return ANNI_TEAMS_RED_ID;
    }

    public String redTeamID(String value) {
        ANNI_TEAMS_RED_ID = value;
        return ANNI_TEAMS_RED_ID;
    }

    public String redTeamDisplay() {
        return ANNI_TEAMS_RED_DISPLAY;
    }

    public String redTeamDisplay(String value) {
        ANNI_TEAMS_RED_DISPLAY = value;
        return ANNI_TEAMS_RED_DISPLAY;
    }

    public String redTeamPrefix() {
        return ANNI_TEAMS_RED_PREFIX;
    }

    public String redTeamPrefix(String value) {
        ANNI_TEAMS_RED_PREFIX = value;
        return ANNI_TEAMS_RED_PREFIX;
    }

    public String redTeamColor() {
        return ANNI_TEAMS_RED_TEAM_COLOR;
    }

    public ChatColor redTeamChatColor() {
        return colorMap.getOrDefault(ANNI_TEAMS_RED_TEAM_COLOR, ChatColor.RESET);
    }

    public String redTeamColor(String value) {
        ANNI_TEAMS_RED_TEAM_COLOR = value;
        return ANNI_TEAMS_RED_TEAM_COLOR;
    }

    public ChatColor redTeamChatColor(ChatColor value) {
        if (value != null) {
            ANNI_TEAMS_RED_TEAM_COLOR = String.valueOf(value.getChar());
            return value;
        } else {
            ANNI_TEAMS_RED_TEAM_COLOR = "f";
            return ChatColor.RESET;
        }
    }

    public String blueTeamID() {
        return ANNI_TEAMS_BLUE_ID;
    }

    public String blueTeamID(String value) {
        ANNI_TEAMS_BLUE_ID = value;
        return ANNI_TEAMS_BLUE_ID;
    }

    public String blueTeamDisplay() {
        return ANNI_TEAMS_BLUE_DISPLAY;
    }

    public String blueTeamDisplay(String value) {
        ANNI_TEAMS_BLUE_DISPLAY = value;
        return ANNI_TEAMS_BLUE_DISPLAY;
    }

    public String blueTeamPrefix() {
        return ANNI_TEAMS_BLUE_PREFIX;
    }

    public String blueTeamPrefix(String value) {
        ANNI_TEAMS_BLUE_PREFIX = value;
        return ANNI_TEAMS_BLUE_PREFIX;
    }

    public String blueTeamColor() {
        return ANNI_TEAMS_BLUE_TEAM_COLOR;
    }

    public ChatColor blueTeamChatColor() {
        return colorMap.getOrDefault(ANNI_TEAMS_BLUE_TEAM_COLOR, ChatColor.RESET);
    }

    public String blueTeamColor(String value) {
        ANNI_TEAMS_BLUE_TEAM_COLOR = value;
        return ANNI_TEAMS_BLUE_TEAM_COLOR;
    }

    public ChatColor blueTeamChatColor(ChatColor value) {
        if (value != null) {
            ANNI_TEAMS_BLUE_TEAM_COLOR = String.valueOf(value.getChar());
            return value;
        } else {
            ANNI_TEAMS_BLUE_TEAM_COLOR = "f";
            return ChatColor.RESET;
        }
    }

    public String yellowTeamID() {
        return ANNI_TEAMS_YELLOW_ID;
    }

    public String yellowTeamID(String value) {
        ANNI_TEAMS_YELLOW_ID = value;
        return ANNI_TEAMS_YELLOW_ID;
    }

    public String yellowTeamDisplay() {
        return ANNI_TEAMS_YELLOW_DISPLAY;
    }

    public String yellowTeamDisplay(String value) {
        ANNI_TEAMS_YELLOW_DISPLAY = value;
        return ANNI_TEAMS_YELLOW_DISPLAY;
    }

    public String yellowTeamPrefix() {
        return ANNI_TEAMS_YELLOW_PREFIX;
    }

    public String yellowTeamPrefix(String value) {
        ANNI_TEAMS_YELLOW_PREFIX = value;
        return ANNI_TEAMS_YELLOW_PREFIX;
    }

    public String yellowTeamColor() {
        return ANNI_TEAMS_YELLOW_TEAM_COLOR;
    }

    public ChatColor yellowTeamChatColor() {
        return colorMap.getOrDefault(ANNI_TEAMS_YELLOW_TEAM_COLOR, ChatColor.RESET);
    }

    public String yellowTeamColor(String value) {
        ANNI_TEAMS_YELLOW_TEAM_COLOR = value;
        return ANNI_TEAMS_YELLOW_TEAM_COLOR;
    }

    public ChatColor yellowTeamChatColor(ChatColor value) {
        if (value != null) {
            ANNI_TEAMS_YELLOW_TEAM_COLOR = String.valueOf(value.getChar());
            return value;
        } else {
            ANNI_TEAMS_YELLOW_TEAM_COLOR = "f";
            return ChatColor.RESET;
        }
    }

    public String greenTeamID() {
        return ANNI_TEAMS_GREEN_ID;
    }

    public String greenTeamID(String value) {
        ANNI_TEAMS_GREEN_ID = value;
        return ANNI_TEAMS_GREEN_ID;
    }

    public String greenTeamDisplay() {
        return ANNI_TEAMS_GREEN_DISPLAY;
    }

    public String greenTeamDisplay(String value) {
        ANNI_TEAMS_GREEN_DISPLAY = value;
        return ANNI_TEAMS_GREEN_DISPLAY;
    }

    public String greenTeamPrefix() {
        return ANNI_TEAMS_GREEN_PREFIX;
    }

    public String greenTeamPrefix(String value) {
        ANNI_TEAMS_GREEN_PREFIX = value;
        return ANNI_TEAMS_GREEN_PREFIX;
    }

    public String greenTeamColor() {
        return ANNI_TEAMS_GREEN_TEAM_COLOR;
    }

    public ChatColor greenTeamChatColor() {
        return colorMap.getOrDefault(ANNI_TEAMS_GREEN_TEAM_COLOR, ChatColor.RESET);
    }

    public String greenTeamColor(String value) {
        ANNI_TEAMS_GREEN_TEAM_COLOR = value;
        return ANNI_TEAMS_GREEN_TEAM_COLOR;
    }

    public ChatColor greenTeamChatColor(ChatColor value) {
        if (value != null) {
            ANNI_TEAMS_GREEN_TEAM_COLOR = String.valueOf(value.getChar());
            return value;
        } else {
            ANNI_TEAMS_GREEN_TEAM_COLOR = "f";
            return ChatColor.RESET;
        }
    }

    public String spectatorTeamID() {
        return ANNI_TEAMS_SPECTATOR_ID;
    }

    public String spectatorTeamID(String value) {
        ANNI_TEAMS_SPECTATOR_ID = value;
        return ANNI_TEAMS_SPECTATOR_ID;
    }

    public String spectatorTeamDisplay() {
        return ANNI_TEAMS_SPECTATOR_DISPLAY;
    }

    public String spectatorTeamDisplay(String value) {
        ANNI_TEAMS_SPECTATOR_DISPLAY = value;
        return ANNI_TEAMS_SPECTATOR_DISPLAY;
    }

    public String spectatorTeamPrefix() {
        return ANNI_TEAMS_SPECTATOR_PREFIX;
    }

    public String spectatorTeamPrefix(String value) {
        ANNI_TEAMS_SPECTATOR_PREFIX = value;
        return ANNI_TEAMS_SPECTATOR_PREFIX;
    }

    public String spectatorTeamColor() {
        return ANNI_TEAMS_SPECTATOR_TEAM_COLOR;
    }

    public ChatColor spectatorTeamChatColor() {
        return colorMap.getOrDefault(ANNI_TEAMS_SPECTATOR_TEAM_COLOR, ChatColor.RESET);
    }

    public String spectatorTeamColor(String value) {
        ANNI_TEAMS_SPECTATOR_TEAM_COLOR = value;
        return ANNI_TEAMS_SPECTATOR_TEAM_COLOR;
    }

    public ChatColor spectatorTeamChatColor(ChatColor value) {
        if (value != null) {
            ANNI_TEAMS_SPECTATOR_TEAM_COLOR = String.valueOf(value.getChar());
            return value;
        } else {
            ANNI_TEAMS_SPECTATOR_TEAM_COLOR = "f";
            return ChatColor.RESET;
        }
    }
}
