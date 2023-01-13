package com.nekozouneko.anni;

import org.bukkit.Color;

public enum ANNITeam {
    RED(false, Color.RED),
    BLUE(false, Color.BLUE),
    YELLOW(false, Color.YELLOW),
    GREEN(false, Color.GREEN),
    SPECTATOR(true, Color.GRAY),
    NOT_JOINED(true, null);

    private final boolean isSpec;
    private final Color color;

    private ANNITeam(boolean isSpectator, Color color) {
        this.isSpec = isSpectator;
        this.color = color;
    }

    public boolean isSpectator() {
        return isSpec;
    }

    public Color getColor() {
        return color;
    }
}
