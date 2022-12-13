package com.nekozouneko.anni.gui;

import com.google.gson.Gson;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNIKit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KitEditor {

    public static void open(ANNIKit k, Player p) {
        Inventory inv = Bukkit.createInventory(null, 9*6, "キットエディタ: "+k.getDisplayName()+" ("+k.getID()+")");

        ItemStack[] kc = k.getDecodedContent();
        for (int i = 0; i <= 35; i++) {
            inv.setItem(i, kc[i]);
        }
        ItemStack gsgp = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = gsgp.getItemMeta();
        m.setDisplayName(" ");
        gsgp.setItemMeta(m);

        inv.setItem(36, gsgp);
        inv.setItem(37, gsgp);
        inv.setItem(38, gsgp);
        inv.setItem(39, new ItemStack(Material.SHIELD));
        inv.setItem(40, gsgp);
        inv.setItem(41, new ItemStack(Material.LEATHER_HELMET));
        inv.setItem(42, new ItemStack(Material.LEATHER_CHESTPLATE));
        inv.setItem(43, new ItemStack(Material.LEATHER_LEGGINGS));
        inv.setItem(44, new ItemStack(Material.LEATHER_BOOTS));
        inv.setItem(45, gsgp);
        inv.setItem(46, gsgp);
        inv.setItem(47, gsgp);

        inv.setItem(48, kc[40]);

        inv.setItem(49, gsgp);

        inv.setItem(50, kc[39]);
        inv.setItem(51, kc[38]);
        inv.setItem(52, kc[37]);
        inv.setItem(53, kc[36]);

        p.openInventory(inv);
    }

    public static boolean isHandleable(InventoryEvent e) {
        return e.getView().getTitle().matches("^キットエディタ: .+ \\(([0-9A-Fa-f]+|<default>)\\)");
    }

    public static void handle(InventoryClickEvent e) {
        if (isHandleable(e)) {
            int slot = e.getRawSlot();

            if (!(slot < 36 || slot == 48 || slot > 49)) {
                e.setCancelled(true);
            }
        }
    }

    public static void closeHandle(InventoryCloseEvent e) {
        if (isHandleable(e)) {
            Matcher m = Pattern.compile("^キットエディタ: .+ \\(([0-9A-Fa-f]+|<default>)\\)").matcher(e.getView().getTitle());

            if (m.find()) {
                String id = m.group(1);

                ANNIKit kit = ANNIPlugin.getKM().getLoadedKits().get(id);
                Inventory inv = e.getInventory();

                ItemStack[] conv = new ItemStack[41];
                for (int i = 0; i <= 35 ; i++) {
                    conv[i] = inv.getItem(i);
                }

                conv[36] = inv.getItem(53);
                conv[37] = inv.getItem(52);
                conv[38] = inv.getItem(51);
                conv[39] = inv.getItem(50);
                conv[40] = inv.getItem(48);

                kit.setContent(conv);

                Gson gson = new Gson();
                String f = kit.getID();
                if (f.equals("<default>")) f = "default";
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(new File(ANNIPlugin.getKitDir(), f+".json")),
                                StandardCharsets.UTF_8
                        )
                )) {
                    gson.toJson(kit, ANNIKit.class, writer);
                    writer.flush();

                    ANNIPlugin.getKM().unload(kit.getID());
                    ANNIPlugin.getKM().load(new File(ANNIPlugin.getKitDir(), f+".json"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                e.getPlayer().sendMessage("操作は正常に終了しました。");
            }
        }
    }

}
