package com.nekozouneko.anni.game;

public enum ANNIStatus {

    WAITING(-1),

    PHASE_ONE(1),
    PHASE_TWO(2),
    PHASE_THREE(3),
    PHASE_FOUR(4),
    PHASE_FIVE(5),
    PHASE_SIX(6),
    PHASE_SEVEN(7),

    WAITING_RESTART(-4),

    STOPPING(-2),
    CANT_START(-3);

    private final int phaseId;

    private ANNIStatus(int phaseId) {
        this.phaseId = phaseId;
    }

    public int getPhaseId() {
        return phaseId;
    }

}
