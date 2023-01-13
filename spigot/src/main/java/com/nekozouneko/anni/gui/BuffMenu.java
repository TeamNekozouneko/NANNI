package com.nekozouneko.anni.gui;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.util.BooleanDataType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuffMenu {

    public final static ItemStack CHAIN_HELMET;
    public final static ItemStack CHAIN_CHESTPLATE;
    public final static ItemStack CHAIN_LEGGINGS;
    public final static ItemStack CHAIN_BOOTS;

    public final static Map<Enchantment, Integer> CHAIN_ENCHANTMENTS = new HashMap<>();

    public final static ItemStack INVISIBLE_POTION;
    public final static ItemStack REGENERATION_POTION;

    static {
        INVISIBLE_POTION = new ItemStack(Material.SPLASH_POTION);
        REGENERATION_POTION = new ItemStack(Material.SPLASH_POTION);

        CHAIN_HELMET = new ItemStack(Material.CHAINMAIL_HELMET);
        CHAIN_CHESTPLATE = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        CHAIN_LEGGINGS = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        CHAIN_BOOTS = new ItemStack(Material.CHAINMAIL_BOOTS);

        // potion

        PotionMeta inv_meta = ((PotionMeta) INVISIBLE_POTION.getItemMeta());
        inv_meta.setBasePotionData(new PotionData(PotionType.INVISIBILITY, true, false));
        inv_meta.setLore(Collections.singletonList("§8Soulbound"));
        INVISIBLE_POTION.setItemMeta(inv_meta);

        PotionMeta regen_meta = ((PotionMeta) REGENERATION_POTION.getItemMeta());
        regen_meta.setBasePotionData(new PotionData(PotionType.REGEN, false, true));
        regen_meta.setLore(Collections.singletonList("§8Soulbound"));
        REGENERATION_POTION.setItemMeta(regen_meta);

        // armor

        NamespacedKey specialArmor = new NamespacedKey(ANNIPlugin.getInstance(), "specialbuffarmor");

        CHAIN_ENCHANTMENTS.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        CHAIN_ENCHANTMENTS.put(Enchantment.DURABILITY, 10);
        CHAIN_ENCHANTMENTS.put(Enchantment.MENDING, 5);

        ItemMeta chm = CHAIN_HELMET.getItemMeta();
        ItemMeta ccm = CHAIN_CHESTPLATE.getItemMeta();
        ItemMeta clm = CHAIN_LEGGINGS.getItemMeta();
        ItemMeta cbm = CHAIN_BOOTS.getItemMeta();

        for (Map.Entry<Enchantment,Integer> entr : CHAIN_ENCHANTMENTS.entrySet()) {
            chm.addEnchant(entr.getKey(), entr.getValue(), true);
            ccm.addEnchant(entr.getKey(), entr.getValue(), true);
            clm.addEnchant(entr.getKey(), entr.getValue(), true);
            cbm.addEnchant(entr.getKey(), entr.getValue(), true);
        }

        chm.getPersistentDataContainer().set(specialArmor, new BooleanDataType(), true);
        chm.setLore(Collections.singletonList("§8Soulbound"));
        chm.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(
                        UUID.randomUUID(),
                        "chain_buff_armor",
                        3,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlot.HEAD
                )
        );
        CHAIN_HELMET.setItemMeta(chm);

        ccm.getPersistentDataContainer().set(specialArmor, new BooleanDataType(), true);
        ccm.setLore(Collections.singletonList("§8Soulbound"));
        ccm.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(
                        UUID.randomUUID(),
                        "chain_buff_armor",
                        8,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlot.CHEST
                )
        );
        CHAIN_CHESTPLATE.setItemMeta(ccm);

        clm.getPersistentDataContainer().set(specialArmor, new BooleanDataType(), true);
        clm.setLore(Collections.singletonList("§8Soulbound"));
        clm.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(
                        UUID.randomUUID(),
                        "chain_buff_armor",
                        6,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlot.LEGS
                )
        );
        CHAIN_LEGGINGS.setItemMeta(clm);

        cbm.getPersistentDataContainer().set(specialArmor , new BooleanDataType(), true);
        cbm.setLore(Collections.singletonList("§8Soulbound"));
        cbm.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(
                        UUID.randomUUID(),
                        "chain_buff_armor",
                        3,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlot.FEET
                )
        );
        CHAIN_BOOTS.setItemMeta(cbm);
    }

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "バフメニュー [BUFF]");

        ItemStack back = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = back.getItemMeta();
        m.setDisplayName(" ");
        back.setItemMeta(m);

        inv.setItem(15, INVISIBLE_POTION);
        inv.setItem(16, REGENERATION_POTION);

        inv.setItem(10, CHAIN_HELMET);
        inv.setItem(19, CHAIN_CHESTPLATE);
        inv.setItem(28, CHAIN_LEGGINGS);
        inv.setItem(37, CHAIN_BOOTS);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getContents()[i] == null) inv.setItem(i, back);
        }

        p.openInventory(inv);
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return (
                e.getView().getTitle().matches("^.+ \\[BUFF]")
                && e.getInventory().getSize() == 54
        );
    }

    public static void handle(InventoryClickEvent e) {
        if (isHandleable(e)) {
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
                e.setCancelled(true);
            }
            else if (e.getRawSlot() < 54) {
                e.setCancelled(true);

                for (Map.Entry<Integer, ItemStack> en : e.getWhoClicked().getInventory().addItem(e.getCurrentItem()).entrySet()) {
                    e.getWhoClicked().getLocation().getWorld().dropItemNaturally(
                            e.getWhoClicked().getLocation(), en.getValue()
                    );
                }

                e.getWhoClicked().closeInventory();
                for (int i = 0; i < e.getWhoClicked().getInventory().getContents().length; i++) {
                    ItemStack is = e.getWhoClicked().getInventory().getContents()[i];
                    if (is == null) continue;
                    if (is.getItemMeta() != null) {
                        ItemMeta im = is.getItemMeta();
                        PersistentDataContainer container = im.getPersistentDataContainer();
                        if (container.getOrDefault(
                                new NamespacedKey(ANNIPlugin.getInstance(), "buffmenu"),
                                new BooleanDataType(), false
                        )) {
                            if (is.getAmount() > 1) {
                                is.setAmount(is.getAmount()-1);
                                e.getWhoClicked().getInventory().setItem(i, is);
                            } else e.getWhoClicked().getInventory().setItem(i, null);
                        }
                    }
                }
            }
            else {
                e.setCancelled(true);
            }
        }
    }

}
