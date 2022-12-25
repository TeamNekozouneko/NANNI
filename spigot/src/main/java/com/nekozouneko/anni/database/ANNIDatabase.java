package com.nekozouneko.anni.database;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * <p>ANNI Database interface
 */
public interface ANNIDatabase {

    @NotNull String getType();

    void initPlayerData(UUID player);

    void close();

    /**
     *
     * @param id Kit ID
     * @param player UUID of purchased player
     */
    void purchaseKit(String id, UUID player);

    /**
     *
     * @param id Kit ID
     * @param player UUID of purchased player
     */
    void unpurchaseKit(String id, UUID player);

    boolean isKitPurchased(String id, UUID player);

    List<String> getPlayerPurchasedKits(UUID player);

    boolean hasPurchaseData(UUID player);

    long getNexusMinedCount(UUID player);

    int getKillCount(UUID player);

    int getDeathCount(UUID player);

    int getWinCount(UUID player);

    int getLoseCount(UUID player);

    int addKillCount(UUID player, int add);

    long addNexusMinedCount(UUID player, int add);

    default long addNexusMinedCount(UUID player) {
        return addDeathCount(player, 1);
    }

    long takeNexusMinedCount(UUID player, int take);

    default long takeNexusMinedCount(UUID player) {
        return takeDeathCount(player, 1);
    }

    default int addKillCount(UUID player) {
        return addKillCount(player, 1);
    }

    int takeKillCount(UUID player, int take);

    default int takeKillCount(UUID player) {
        return takeKillCount(player, 1);
    }

    int addDeathCount(UUID player, int add);

    default int addDeathCount(UUID player) {
        return addDeathCount(player, 1);
    }

    int takeDeathCount(UUID player, int take);

    default int takeDeathCount(UUID player) {
        return takeDeathCount(player, 1);
    }

    int addWinCount(UUID player, int add);

    default int addWinCount(UUID player) {
        return addDeathCount(player, 1);
    }

    int takeWinCount(UUID player, int take);

    default int takeWinCount(UUID player) {
        return takeDeathCount(player, 1);
    }

    int addLoseCount(UUID player, int add);

    default int addLoseCount(UUID player) {
        return addDeathCount(player, 1);
    }

    int takeLoseCount(UUID player, int take);

    default int takeLoseCount(UUID player) {
        return takeDeathCount(player, 1);
    }

    boolean hasStatsData(UUID player);

}
