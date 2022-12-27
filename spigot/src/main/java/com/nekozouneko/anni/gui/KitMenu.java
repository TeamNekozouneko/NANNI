package com.nekozouneko.anni.gui;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.file.ANNIKit;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.manager.KitManager;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KitMenu {

    public static void open(Player p, int page, boolean passEdit) {
        Inventory inv = Bukkit.createInventory(null, 9*6, "キットを選択... (ページ: "+page+")");
        KitManager km = ANNIPlugin.getKM();

        ItemStack back = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bm = back.getItemMeta();
        bm.setDisplayName(" ");
        back.setItemMeta(bm);

        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta pm = prev.getItemMeta();
        pm.setDisplayName("§a前のページ");
        if (passEdit) pm.setLore(Collections.singletonList("§8[EditPage: "+(page-1)+"]"));
        else pm.setLore(Collections.singletonList("§8[Page: "+(page-1)+"]"));
        prev.setItemMeta(pm);

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nm = next.getItemMeta();
        nm.setDisplayName("§a次のページ");
        if (passEdit) nm.setLore(Collections.singletonList("§8[EditPage: "+(page+1)+"]"));
        else nm.setLore(Collections.singletonList("§8[Page: "+(page+1)+"]"));
        next.setItemMeta(nm);

        ItemStack unAv = new ItemStack(Material.STICK);
        ItemMeta um = unAv.getItemMeta();
        um.setDisplayName("§c利用不可");
        unAv.setItemMeta(um);

        ItemStack cls = new ItemStack(Material.BARRIER);
        ItemMeta cm = cls.getItemMeta();
        cm.setDisplayName("§c閉じる");
        cls.setItemMeta(cm);

        ItemStack defKit = new ItemStack(Material.ENDER_CHEST);
        ItemMeta defMeta = defKit.getItemMeta();
        defMeta.setDisplayName("§2デフォルトキット §7(0 NAP)");
        if (passEdit) defMeta.setLore(Arrays.asList("§7普通なキット","","§8[EditKit: <default>]"));
        else defMeta.setLore(Arrays.asList("§7普通なキット","","§8[Kit: <default>]"));
        defKit.setItemMeta(defMeta);

        inv.setItem(0, defKit);
        inv.setItem(1, back);
        inv.setItem(2, back);
        inv.setItem(3, back);
        inv.setItem(4, back);
        inv.setItem(5, back);
        inv.setItem(6, back);
        inv.setItem(7, back);
        inv.setItem(8, back);
        inv.setItem(9, back);
        inv.setItem(17, back);
        inv.setItem(18, back);
        inv.setItem(26, back);
        inv.setItem(27, back);
        inv.setItem(35, back);
        inv.setItem(36, back);
        inv.setItem(37, back);
        inv.setItem(38, back);
        inv.setItem(39, back);
        inv.setItem(40, back);
        inv.setItem(41, back);
        inv.setItem(42, back);
        inv.setItem(43, back);
        inv.setItem(44, back);

        if (km.getLoadedKits().size() > 21) {
            if (page <= 1) inv.setItem(45, unAv);
            else inv.setItem(45, prev);

            if (((double)km.getLoadedKits().size()) / 21D > (double) page) {
                inv.setItem(53, next);
            } else inv.setItem(53, unAv);
        } else {
            inv.setItem(45, unAv);
            inv.setItem(53, unAv);
        }

        inv.setItem(46, back);
        inv.setItem(47, back);
        inv.setItem(48, back);
        inv.setItem(49, cls);
        inv.setItem(50, back);
        inv.setItem(51, back);
        inv.setItem(52, back);

        ItemStack[] kitsIcon = new ItemStack[21];
        List<ANNIKit> kitList = new ArrayList<>(km.getLoadedKits().values());

        kitList.removeIf(k -> k.getID().equals("<default>"));

        for (int i = 0; i < kitsIcon.length; i++) {
            if ((i*page)+1 > kitList.size()) break;
            ANNIKit kit = kitList.get(i*page);

            ItemStack stack = new ItemStack(kit.getIconMaterial());
            ItemMeta meta = stack.getItemMeta();

            String[] d = kit.getDescription().split("\n");
            List<String> lore;
            if (d.length <= 1) lore = new ArrayList<>();
            else lore = new ArrayList<>(Arrays.asList(d));
            lore.add("");
            if (passEdit) lore.add("§8[EditKit: "+kit.getID()+"]");
            else lore.add("§8[Kit: "+kit.getID()+"]");

            meta.setLore(lore);
            meta.setDisplayName("§r"+kit.getDisplayName());

            stack.setItemMeta(meta);

            kitsIcon[i] = stack;
        }

        inv.setItem(10, kitsIcon[0]);
        inv.setItem(11, kitsIcon[1]);
        inv.setItem(12, kitsIcon[2]);
        inv.setItem(13, kitsIcon[3]);
        inv.setItem(14, kitsIcon[4]);
        inv.setItem(15, kitsIcon[5]);
        inv.setItem(16, kitsIcon[6]);

        inv.setItem(19, kitsIcon[7]);
        inv.setItem(20, kitsIcon[8]);
        inv.setItem(21, kitsIcon[9]);
        inv.setItem(22, kitsIcon[10]);
        inv.setItem(23, kitsIcon[11]);
        inv.setItem(24, kitsIcon[12]);
        inv.setItem(25, kitsIcon[13]);

        inv.setItem(28, kitsIcon[14]);
        inv.setItem(29, kitsIcon[15]);
        inv.setItem(30, kitsIcon[16]);
        inv.setItem(31, kitsIcon[17]);
        inv.setItem(32, kitsIcon[18]);
        inv.setItem(33, kitsIcon[19]);
        inv.setItem(34, kitsIcon[20]);

        p.openInventory(inv);
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return e.getView().getTitle().startsWith("キットを選択...") && e.getView().getType() == InventoryType.CHEST
                && e.getView().getTitle().matches("^キットを選択\\.\\.\\. \\(ページ: ([0-9]+)\\)");
    }

    public static void handle(InventoryClickEvent e) {
        if (isHandleable(e)) {
            int slot = e.getRawSlot();
            ItemStack item = e.getCurrentItem();
            e.setCancelled(true);

            if (slot < 54) {
                if ((slot == 0) || (slot >= 10 && 16 >= slot) || (slot >= 19 && 25 >= slot) || (slot >= 28 && 34 >= slot)) {
                    List<String> lore = item.getItemMeta().getLore();
                    String last = lore.get(lore.size()-1);
                    if (last.matches("^§8\\[EditKit: ([0-9A-Fa-f]+|<default>)]")) {
                        Matcher m = Pattern.compile("^§8\\[EditKit: ([0-9A-Fa-f]+|<default>)]").matcher(last);

                        if (m.find()) {
                            KitEditor.open(ANNIPlugin.getKM().getLoadedKits().get(m.group(1)), (Player)e.getWhoClicked());
                        }
                    } else if (last.matches("^§8\\[Kit: ([0-9A-Fa-f]+|<default>)]")) {
                        Matcher m = Pattern.compile("^§8\\[Kit: ([0-9A-Fa-f]+|<default>)]").matcher(last);

                        if (m.find()) {
                            ANNIKit k = ANNIPlugin.getKM().getLoadedKits().get(m.group(1));
                            e.getWhoClicked().sendMessage(k.getDisplayName() + "を選択しました");

                            if (
                                    !ANNIPlugin.getANNIDB().isKitPurchased(m.group(1), e.getWhoClicked().getUniqueId())
                                    && !(k.getPrice() == 0.0)
                            ) {
                                if (ANNIPlugin.getVaultEconomy().getBalance((OfflinePlayer) e.getWhoClicked()) >= k.getPrice()) {
                                    ANNIPlugin.getVaultEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), k.getPrice());
                                    e.getWhoClicked().sendMessage(NChatColor.GREEN + "購入しました！");
                                } else {
                                    e.getWhoClicked().sendMessage(NChatColor.RED + "金額が不足しています。");
                                    return;
                                }
                            }
                            ANNIPlugin.getANNIDB().setUsingKit(m.group(1), e.getWhoClicked().getUniqueId());
                            if (ANNIPlugin.getGM().getGame().getStatus().getPhaseId() >= 1) {
                                e.getWhoClicked().setHealth(0);
                            }

                            e.getView().close();
                        }
                    }
                } else {
                    switch (item.getType()) {
                        case BARRIER:
                            e.getView().close();
                            break;
                        case ARROW:
                            if (item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).matches("^§8\\[Page: [0-9]+\\]")) {
                                String c = item.getItemMeta().getLore().get(0);

                                if (c.matches("^§8\\[Page: ([0-9]+)]")) {
                                    Matcher m = Pattern.compile("^§8\\[Page: ([0-9]+)]").matcher(c);

                                    if (m.find()) {
                                        KitMenu.open((Player) e.getWhoClicked(), Integer.parseInt(m.group(1)), false);
                                    }
                                } else if (c.matches("^§8\\[EditPage: ([0-9]+)]")) {
                                    Matcher m = Pattern.compile("^§8\\[EditPage: ([0-9]+)]").matcher(c);

                                    if (m.find()) {
                                        KitMenu.open((Player) e.getWhoClicked(), Integer.parseInt(m.group(1)), true);
                                    }
                                }
                            }
                            break;
                        default: break;
                    }
                }
            }
        }
    }
}
