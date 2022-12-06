package com.nekozouneko.anni;

public enum Team {
    RED(false),
    BLUE(false),
    YELLOW(false),
    GREEN(false),
    SPECTATOR(true),
    NOT_JOINED(true);

    private final boolean isSpec;

    private Team(boolean isSpectator) {
        this.isSpec = isSpectator;
    }

    public boolean isSpectator() {
        return isSpec;
    }
}
