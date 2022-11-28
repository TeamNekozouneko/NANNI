package com.nekozouneko.anni;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ANNIUtil {

    private static final Map<Material, Integer> oreRequiredPickaxeLevel = new HashMap<>();
    private static final Map<Material, Integer> pickaxeLevel = new HashMap<>();
    private static final Map<Material, Material> oreResult = new HashMap<>();

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

}
