package com.nekozouneko.anni;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ANNIConfig {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private final FileConfiguration conf = plugin.getConfig();

    /* Configuration */

    private String TimeZone;

    private Integer ANNI_CORE_health;
    private Material ANNI_CORE_material;

    private String TEAMS_RED_id;
    private String TEAMS_RED_display;
    private String TEAMS_RED_prefix;
    private String TEAMS_RED_teamColor;
    private List<String> TEAMS_RED_bigLogo;

    private String TEAMS_BLUE_id;
    private String TEAMS_BLUE_display;
    private String TEAMS_BLUE_prefix;
    private String TEAMS_BLUE_teamColor;
    private List<String> TEAMS_BLUE_bigLogo;

    private String TEAMS_YELLOW_id;
    private String TEAMS_YELLOW_display;
    private String TEAMS_YELLOW_prefix;
    private String TEAMS_YELLOW_teamColor;
    private List<String> TEAMS_YELLOW_bigLogo;

    private String TEAMS_GREEN_id;
    private String TEAMS_GREEN_display;
    private String TEAMS_GREEN_prefix;
    private String TEAMS_GREEN_teamColor;
    private List<String> TEAMS_GREEN_bigLogo;

    public ANNIConfig() {
        this.TimeZone = conf.getString("timezone", "Asia/Tokyo");
    }


}
