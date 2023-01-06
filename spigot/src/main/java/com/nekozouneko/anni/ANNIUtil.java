package com.nekozouneko.anni;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ANNIUtil {

    private static final Map<Material, Integer> oreRequiredPickaxeLevel = new HashMap<>();
    private static final Map<Material, Integer> pickaxeLevel = new HashMap<>();
    private static final Map<Material, Material> oreResult = new HashMap<>();

    public static class copyDirectoryVisitor extends SimpleFileVisitor<Path> {

        private final Path source;
        private final Path target;
        private final List<String> exclude;

        public copyDirectoryVisitor(Path s, Path t, List<String> exclude) {
            this.source = s;
            this.target = t;
            this.exclude = exclude;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes bfa) throws IOException {
            Path copyDir = target.resolve(source.relativize(d));
            if (!Files.isDirectory(copyDir)) {
                Files.createDirectory(copyDir);
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path p, BasicFileAttributes bfa) throws IOException {
            String name = p.getFileName().toString();

            if (exclude != null && exclude.contains(name)) {
                return FileVisitResult.CONTINUE;
            }

            Path targetFil = target.resolve(source.relativize(p));
            Files.copy(p, targetFil, StandardCopyOption.COPY_ATTRIBUTES);

            return FileVisitResult.CONTINUE;
        }

    }

    static {
        oreRequiredPickaxeLevel.put(Material.COAL_ORE, 1);
        oreRequiredPickaxeLevel.put(Material.LAPIS_ORE, 2);
        oreRequiredPickaxeLevel.put(Material.IRON_ORE, 2);
        oreRequiredPickaxeLevel.put(Material.GOLD_ORE, 3);
        oreRequiredPickaxeLevel.put(Material.REDSTONE_ORE, 3);
        oreRequiredPickaxeLevel.put(Material.DIAMOND_ORE, 3);
        oreRequiredPickaxeLevel.put(Material.EMERALD_ORE, 3);

        pickaxeLevel.put(Material.WOODEN_PICKAXE, 1);
        pickaxeLevel.put(Material.GOLDEN_PICKAXE, 1);
        pickaxeLevel.put(Material.STONE_PICKAXE, 2);
        pickaxeLevel.put(Material.IRON_PICKAXE, 3);
        pickaxeLevel.put(Material.DIAMOND_PICKAXE, 3);
        pickaxeLevel.put(Material.NETHERITE_PICKAXE, 3);

        oreResult.put(Material.COAL_ORE, Material.COAL);
        oreResult.put(Material.LAPIS_ORE, Material.LAPIS_LAZULI);
        oreResult.put(Material.IRON_ORE, Material.IRON_ORE);
        oreResult.put(Material.GOLD_ORE, Material.GOLD_ORE);
        oreResult.put(Material.REDSTONE_ORE, Material.REDSTONE);
        oreResult.put(Material.DIAMOND_ORE, Material.DIAMOND);
        oreResult.put(Material.EMERALD_ORE, Material.EMERALD);
    }

    public static int getFortuneOreDropItemAmounts(Player p) {
        Map<Enchantment, Integer> enc = p.getInventory().getItemInMainHand().getEnchantments();

        if (enc.containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
            return enc.get(Enchantment.LOOT_BONUS_BLOCKS) + 1;
        }

        return 1;
    }

    public static boolean isMineableOre(Material ore, Material pickaxe) {
        if (!oreRequiredPickaxeLevel.containsKey(ore) || !pickaxeLevel.containsKey(pickaxe)) return false;

        int lvl = pickaxeLevel.get(pickaxe);
        int orelvl = oreRequiredPickaxeLevel.get(ore);

        return lvl >= orelvl;
    }

    public static Material getOreMinedResult(Material ore) {
        return oreResult.get(ore);
    }

    public static String toTimerFormat(long s) {
        long minutes = s / 60;
        long seconds = s - minutes * 60;
        return (String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    }

    public static String toHoursTimerFormat(long s) {
        long hours = s / 3600;
        long minutes = s / 60 - hours * 60;
        long seconds = s - (hours * 3600 + minutes * 60);
        return (String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    }

    public static boolean equalsAllValues(Map<?, ?> map) {
        if (map.values().size() <= 1) return true;

        Object firstVal = map.values().toArray()[0];

        for (Object v : map.values()) {
            if (!(firstVal == v)) return false;
        }

        return true;
    }

    public static <K, V> Map.Entry<K, V> toEntry(Map<K, V> map, K key) {
        if (key == null || map == null || !map.containsKey(key)) return null;

        return new AbstractMap.SimpleEntry<>(key, map.get(key));
    }

    public static Team balancingJoin(Map<Team, Integer> tm) {
        Map<Team, Integer> ti = new HashMap<>(tm);
        ti.remove(Team.SPECTATOR);
        ti.remove(Team.NOT_JOINED);

        if (!equalsAllValues(ti) && ti.size() >= 1) {
            Map.Entry<Team, Integer> minTeamEntry = null;
            for (Team t : ti.keySet()) {
                Map.Entry<Team, Integer> e = toEntry(ti, t);
                if (minTeamEntry == null) minTeamEntry = e;
                else {
                    if (minTeamEntry.getValue() > e.getValue()) {
                        minTeamEntry = e;
                    }
                }
            }

            return minTeamEntry.getKey();
        } else {
            Team[] teams = ti.keySet().toArray(new Team[0]);
            Random r = new Random();

            return teams[r.nextInt(teams.length)];
        }
    }

    public static double bossBarProgress(long max, long val) {
        final double cl = max /100.0;
        double prg = (((double) val) / cl) / 100;

        if (prg > 1.0) prg = 1.0;
        if (prg < 0.0) prg = 0.0;

        return prg;
    }

    public static String teamPrefixSuffixAppliedName(Player p) {
        Scoreboard mb = ANNIPlugin.getSb();
        org.bukkit.scoreboard.Team pt = mb.getPlayerTeam(p);
        if (pt == null) pt = mb.getEntryTeam(p.getName());

        if (pt != null) return pt.getPrefix()+pt.getColor()+p.getDisplayName()+pt.getSuffix();
        else return p.getDisplayName();
    }

    public static ItemStack[] createColorLeatherArmor(Color col) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta helmetMeta = ((LeatherArmorMeta) helmet.getItemMeta());
        LeatherArmorMeta chestMeta = ((LeatherArmorMeta) chest.getItemMeta());
        LeatherArmorMeta legsMeta = ((LeatherArmorMeta) legs.getItemMeta());
        LeatherArmorMeta bootsMeta = ((LeatherArmorMeta) boots.getItemMeta());

        helmetMeta.setColor(col);
        chestMeta.setColor(col);
        legsMeta.setColor(col);
        bootsMeta.setColor(col);

        helmet.setItemMeta(helmetMeta);
        chest.setItemMeta(chestMeta);
        legs.setItemMeta(legsMeta);
        boots.setItemMeta(bootsMeta);

        return new ItemStack[] {boots,legs,chest,helmet};
    }

    public static String phaseId2BoardDisplay(int phase) {
        if (phase >= 1) {
            return Integer.valueOf(phase).toString();
        } else {
            Map<Integer, String> tr = new HashMap<Integer, String>() {{
                put(-1, "待機中");put(-2, "停止中");
                put(-3, "開始不可");put(-4, "再起動待機中");
            }};

            return tr.get(phase);
        }
    }

    public static void safeDeleteDir(Path path) throws IOException {
        if (path.toFile().isDirectory()) {
            File[] files = path.toFile().listFiles();

            for (File f : files) {
                if (f.isDirectory()) {
                    safeDeleteDir(f.toPath());
                } else {
                    try {
                        Files.deleteIfExists(f.toPath());
                    } catch (IOException ignored) {
                    }
                }
            }

        }
        Files.deleteIfExists(path);
    }

    public static float KDCalc(int k, int d) {
        if (d == 0) return 1F;
        else if (k == 0) return 0F;

        return ((float) k) / ((float) d);
    }

    public static String doubleToString(double d, boolean comma) {
        String a;
        if (comma) a = String.format("%,.1f", d);
        else a = Double.toString(d);

        a = a.replaceAll("^([0-9]+\\.[1-9]*?)(0+)$", "$1");

        if (a.endsWith(".")) a = a.substring(0, a.length()-1);

        return a;
    }

    public static void healPlayer(Player p) {
        AttributeInstance ai =  p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        p.setHealth(ai != null ? ai.getValue() : 20.0);
        p.setFoodLevel(20);
        p.setSaturation(20F);
    }

    public static void setShopPrice(ItemStack is, double price, String ex, String... more) {
        ItemMeta im = is.getItemMeta();
        List<String> l = new ArrayList<>();
        l.add("§7価格: " + price + " " + ex);
        l.add(" ");
        if (more.length != 0) l.addAll(Arrays.asList(more));
        if (more.length != 0) l.add(" ");
        l.add("§8Price:"+price);
        im.setLore(l);
        is.setItemMeta(im);
    }

    public static ItemStack[] getSellItems(ItemStack[] contents) {
        ItemStack[] res = new ItemStack[21];
        res[0] = contents[10];
        res[1] = contents[11];
        res[2] = contents[12];
        res[3] = contents[13];
        res[4] = contents[14];
        res[5] = contents[15];
        res[6] = contents[16];

        res[7] = contents[19];
        res[8] = contents[20];
        res[9] = contents[21];
        res[10] = contents[22];
        res[11] = contents[23];
        res[12] = contents[24];
        res[13] = contents[25];

        res[14] = contents[28];
        res[15] = contents[29];
        res[16] = contents[30];
        res[17] = contents[31];
        res[18] = contents[32];
        res[19] = contents[33];
        res[20] = contents[34];
        return res;
    }

}
