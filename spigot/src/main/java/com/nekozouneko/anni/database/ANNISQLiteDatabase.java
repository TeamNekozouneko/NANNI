package com.nekozouneko.anni.database;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ANNISQLiteDatabase implements ANNIDatabase {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private Statement state;

    public ANNISQLiteDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");

            plugin.getLogger().info("Loading database (SQLite) P: "+plugin.getDataFolder()+"/"+ANNIPlugin.getANNIConf().SQLiteFile());

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/" + ANNIPlugin.getANNIConf().SQLiteFile());
            connection.setAutoCommit(true);

            this.state = connection.createStatement();
            state.execute("CREATE TABLE IF NOT EXISTS anni_purchased (kit string, player string)");
            state.execute("CREATE TABLE IF NOT EXISTS anni_statistic (" +
                    "player string," + // UUID of player
                    "kit string," +
                    "kill integer," +
                    "death integer," +
                    "nexus_mined_count integer," +
                    "win integer, lose integer)");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Cant get sqlite connection");
        }
    }

    @Override
    public @NotNull String getType() {
        return "SQLite";
    }

    @Override
    public void close() {
        try {
            if (!state.isClosed()) {
                state.close();
                state.getConnection().close();
            }
        } catch (SQLException se) {se.printStackTrace();}
    }

    @Override
    public void initPlayerData(@NotNull UUID player) {
        Objects.requireNonNull(player, "Argument 'player' cannot be null [SystemError]");

        try {
            // Remove old data
            state.executeUpdate("DELETE FROM anni_purchased WHERE player='"+player+"'");
            state.executeUpdate("DELETE FROM anni_statistic WHERE player='"+player+"'");

            state.executeUpdate("INSERT INTO anni_statistic VALUES (" +
                    "'"+player+"'," +
                    "'<default>'," +
                    "0,0,0,0,0)"); // kill,death,nexus_mined,win,lose
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void purchaseKit(@NotNull String id, @NotNull UUID player) {
        Objects.requireNonNull(id, "Kit ID cannot be null");
        Objects.requireNonNull(player, "UUID (player) cannot be null");
        try {
            this.state.executeUpdate("INSERT INTO anni_purchased values('"+id+"', '"+ player.toString() +"')");
        } catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public void unpurchaseKit(@Nullable String id, @NotNull UUID player) {
        Objects.requireNonNull(player, "UUID (player) cannot be null");
        try {
            if (id != null) {
                this.state.executeUpdate("DELETE FROM anni_purchased WHERE kit='"+id+"' and player='"+player+"'");
            } else {
                this.state.executeUpdate("DELETE FROM anni_purchased WHERE player='"+player+"'");
            }
        } catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public boolean isKitPurchased(@NotNull String id, @NotNull UUID player) {
        return getPlayerPurchasedKits(player).contains(id);
    }

    @Override
    public List<String> getPlayerPurchasedKits(@NotNull UUID player) {
        List<String> purchased = new ArrayList<>();
        try {
            ResultSet rs = this.state.executeQuery("SELECT * FROM anni_purchased WHERE player='"+player+"'");

            while (rs.next()) {
                String ki = rs.getString("kit");
                purchased.add(ki);
            }
        } catch (SQLException se) {se.printStackTrace();}


        return purchased;
    }

    @Override
    public boolean hasPurchaseData(UUID player) {
        try {
            return state.executeQuery("SELECT * FROM anni_purchased WHERE player='" + player + "' LIMIT 1").next();
        } catch (SQLException se) {return false;}
    }

    @Override
    public long getNexusMinedCount(UUID player) {
        try {
            ResultSet rs = state.executeQuery("SELECT * FROM anni_statistic WHERE player='"+player+"' LIMIT 1");

            if (rs.next()) {
                return rs.getInt("nexus_mined_count");
            }
        } catch (SQLException ignored) {}

        return 0;
    }

    @Override
    public int getKillCount(UUID player) {
        try {
            ResultSet rs = state.executeQuery("SELECT * FROM anni_statistic WHERE player='"+player+"' LIMIT 1");

            if (rs.next()) {
                return rs.getInt("kill");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return 0;
    }

    @Override
    public int getDeathCount(UUID player) {
        try {
            ResultSet rs = state.executeQuery("SELECT * FROM anni_statistic WHERE player='"+player+"' LIMIT 1");

            if (rs.next()) {
                return rs.getInt("death");
            }
        } catch (SQLException ignored) {}

        return 0;
    }

    @Override
    public int getWinCount(UUID player) {
        try {
            ResultSet rs = state.executeQuery("SELECT * FROM anni_statistic WHERE player='"+player+"' LIMIT 1");

            if (rs.next()) {
                return rs.getInt("win");
            }
        } catch (SQLException ignored) {}

        return 0;
    }

    @Override
    public int getLoseCount(UUID player) {
        try {
            ResultSet rs = state.executeQuery("SELECT * FROM anni_statistic WHERE player='"+player+"' LIMIT 1");

            if (rs.next()) {
                return rs.getInt("lose");
            }
        } catch (SQLException ignored) {}

        return 0;
    }

    @Override
    public long addNexusMinedCount(UUID player, int add) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET nexus_mined_count = "+(getNexusMinedCount(player)+add)+" WHERE player='"+player+"'");
            return getNexusMinedCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public long takeNexusMinedCount(UUID player, int take) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET nexus_mined_count = "+(getNexusMinedCount(player)-take)+" WHERE player='"+player+"'");
            return getNexusMinedCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int addKillCount(UUID player, int add) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET kill = kill + "+add+" WHERE player='"+player+"'");
            return getKillCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int takeKillCount(UUID player, int take) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET kill = kill - "+take+" WHERE player='"+player+"'");
            return getKillCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int addDeathCount(UUID player, int add) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET death = death + "+add+" WHERE player='"+player+"'");
            return getDeathCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int takeDeathCount(UUID player, int take) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET death = death - "+take+" WHERE player='"+player+"'");
            return getDeathCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int addWinCount(UUID player, int add) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET win = win + "+add+" WHERE player='"+player+"'");
            return getWinCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int takeWinCount(UUID player, int take) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET win = win - "+take+" WHERE player='"+player+"'");
            return getWinCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int addLoseCount(UUID player, int add) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET lose = lose + "+add+" WHERE player='"+player+"'");
            return getLoseCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int takeLoseCount(UUID player, int take) {
        try {
            state.executeUpdate("UPDATE anni_statistic SET lose = lose - "+take+" WHERE player='"+player+"'");
            return getLoseCount(player);
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean hasStatsData(UUID player) {
        try {
            return state.executeQuery("SELECT * FROM anni_statistic WHERE player='"+player+"' LIMIT 1").next();
        } catch (SQLException se) {return false;}
    }
}
