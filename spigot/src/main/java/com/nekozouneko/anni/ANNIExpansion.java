package com.nekozouneko.anni;

import com.nekozouneko.anni.database.ANNIDatabase;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ANNIExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "nanni";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", ANNIPlugin.getInstance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return ANNIPlugin.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        ANNIDatabase db = ANNIPlugin.getANNIDB();

        if (params.equalsIgnoreCase("kill")) {
            return Objects.toString(db.getKillCount(player.getUniqueId()));
        }
        else if (params.equalsIgnoreCase("death")) {
            return Objects.toString(db.getDeathCount(player.getUniqueId()));
        }
        else if (params.equalsIgnoreCase("kd_late")) {
            return Objects.toString(
                    ANNIUtil.KDCalc(
                            db.getKillCount(player.getUniqueId()),
                            db.getDeathCount(player.getUniqueId())
                    )
            );
        }
        else if (params.equalsIgnoreCase("nexus_mined")) {
            return Objects.toString(db.getNexusMinedCount(player.getUniqueId()));
        }
        else if (params.equalsIgnoreCase("win")) {
            return Objects.toString(db.getWinCount(player.getUniqueId()));
        }
        else if (params.equalsIgnoreCase("lose")) {
            return Objects.toString(db.getLoseCount(player.getUniqueId()));
        }

        return null;
    }
}
