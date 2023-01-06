package com.nekozouneko.anni.gui;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GameShopMenu {

    public enum Tab {
        SELL,
        EQUIPMENTS,
        POTIONS
    }

    private static class SellObject {

        private final ItemStack item;
        private final double price;

        public SellObject(ItemStack item, double price) {
            this.item = item;
            this.price = price;
        }

        public ItemStack getItem() {
            return item;
        }

        public double getPrice() {
            return price;
        }

    }

    public static final Map<Material,Double> sellMap = Collections.unmodifiableMap(new HashMap<Material, Double>() {{
        put(Material.EMERALD, 150.);
        put(Material.DIAMOND, 150.);
        put(Material.GOLD_INGOT, 100.);
        put(Material.IRON_INGOT, 75.);
        put(Material.COAL, 50.);
        put(Material.REDSTONE, 25.);
        put(Material.LAPIS_LAZULI, 20.);

        put(Material.EMERALD_ORE, 75.);
        put(Material.DIAMOND_ORE, 75.);
        put(Material.GOLD_ORE, 50.);
        put(Material.IRON_ORE, 37.5);
        put(Material.COAL_ORE, 25.);
        put(Material.REDSTONE_ORE, 12.5);
        put(Material.LAPIS_ORE, 10.);
    }});

    private static void setUpInventory(Inventory inv, Tab tab) {
        ItemStack back = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(" ");
        back.setItemMeta(backMeta);

        inv.setItem(0, back);
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

        ItemStack disabledTab = new ItemStack(Material.STICK);
        ItemMeta disabledMeta = disabledTab.getItemMeta();
        disabledMeta.setDisplayName("§8 --- ");
        disabledTab.setItemMeta(disabledMeta);

        ItemStack SellTab = new ItemStack(Material.GOLD_INGOT);
        ItemMeta SellMeta = SellTab.getItemMeta();
        SellMeta.setDisplayName("§a換金 §7[SELL]");
        SellTab.setItemMeta(SellMeta);

        ItemStack PotionTab = new ItemStack(Material.POTION);
        PotionMeta PotionTabMeta = (PotionMeta) PotionTab.getItemMeta();
        PotionTabMeta.setDisplayName("§aポーション §7[POTIONS]");
        PotionTabMeta.setColor(Color.BLUE);
        PotionTab.setItemMeta(PotionTabMeta);

        ItemStack EquipmentTab = new ItemStack(Material.IRON_SWORD);
        ItemMeta EquipmentMeta = EquipmentTab.getItemMeta();
        EquipmentMeta.setDisplayName("§a装備品 §7[EQUIPMENTS]");
        EquipmentTab.setItemMeta(EquipmentMeta);

        switch (tab) {
            case SELL:
                inv.setItem(45, disabledTab);
                inv.setItem(46, PotionTab);
                inv.setItem(47, EquipmentTab);
                break;
            case POTIONS: {
                inv.setItem(45, SellTab);
                inv.setItem(46, disabledTab);
                inv.setItem(47, EquipmentTab);

                ItemStack gsd = new ItemStack(Material.GLOWSTONE_DUST);
                ItemStack rs = new ItemStack(Material.REDSTONE);
                ItemStack gp = new ItemStack(Material.GUNPOWDER);
                ItemStack nw = new ItemStack(Material.NETHER_WART, 3);
                ItemStack bp = new ItemStack(Material.BLAZE_POWDER, 3);
                ItemStack fse = new ItemStack(Material.FERMENTED_SPIDER_EYE, 3);
                ItemStack mc = new ItemStack(Material.MAGMA_CREAM);
                ItemStack gc = new ItemStack(Material.GOLDEN_CARROT);
                ItemStack gms = new ItemStack(Material.GLISTERING_MELON_SLICE);
                ItemStack se = new ItemStack(Material.SPIDER_EYE);
                ItemStack rf = new ItemStack(Material.RABBIT_FOOT);
                ItemStack s = new ItemStack(Material.SUGAR);
                ItemStack gt = new ItemStack(Material.GHAST_TEAR);
                ItemStack pm = new ItemStack(Material.PHANTOM_MEMBRANE);
                ItemStack th = new ItemStack(Material.TURTLE_HELMET);
                ItemStack gb = new ItemStack(Material.GLASS_BOTTLE, 3);
                ItemStack c = new ItemStack(Material.CAULDRON);
                ItemStack bs = new ItemStack(Material.BREWING_STAND);

                String ext = ANNIPlugin.getVaultEconomy().currencyNamePlural();
                ANNIUtil.setShopPrice(gsd, 1000, ext);
                ANNIUtil.setShopPrice(rs, 1000, ext);
                ANNIUtil.setShopPrice(gp, 1000, ext);
                ANNIUtil.setShopPrice(nw, 1000, ext);
                ANNIUtil.setShopPrice(bp, 1000, ext);
                ANNIUtil.setShopPrice(fse, 1000, ext);
                ANNIUtil.setShopPrice(mc, 1000, ext);
                ANNIUtil.setShopPrice(gc, 1000, ext);
                ANNIUtil.setShopPrice(gms, 1000, ext);
                ANNIUtil.setShopPrice(se, 1000, ext);
                ANNIUtil.setShopPrice(rf, 1000, ext);
                ANNIUtil.setShopPrice(s, 1000, ext);
                ANNIUtil.setShopPrice(gt, 1000, ext);
                ANNIUtil.setShopPrice(pm, 1000, ext);
                ANNIUtil.setShopPrice(th, 1000, ext);
                ANNIUtil.setShopPrice(gb, 1000, ext);
                ANNIUtil.setShopPrice(c, 1000, ext);
                ANNIUtil.setShopPrice(bs, 1000, ext);

                inv.setItem(10, gsd);
                inv.setItem(11, rs);
                inv.setItem(12, gp);
                inv.setItem(13, back);
                inv.setItem(14, mc);
                inv.setItem(15, gc);
                inv.setItem(16, gms);

                inv.setItem(19, nw);
                inv.setItem(20, bp);
                inv.setItem(21, fse);
                inv.setItem(22, back);
                inv.setItem(23, se);
                inv.setItem(24, rf);
                inv.setItem(25, s);

                inv.setItem(28, gb);
                inv.setItem(29, c);
                inv.setItem(30, bs);
                inv.setItem(31, back);
                inv.setItem(32, gt);
                inv.setItem(33, pm);
                inv.setItem(34, th);

                break;
            }
            case EQUIPMENTS: {
                inv.setItem(45, SellTab);
                inv.setItem(46, PotionTab);
                inv.setItem(47, disabledTab);

                ItemStack irh = new ItemStack(Material.IRON_HELMET);
                ItemStack irc = new ItemStack(Material.IRON_CHESTPLATE);
                ItemStack irl = new ItemStack(Material.IRON_LEGGINGS);
                ItemStack irb = new ItemStack(Material.IRON_BOOTS);
                ItemStack irs = new ItemStack(Material.IRON_SWORD);
                ItemStack exp = new ItemStack(Material.EXPERIENCE_BOTTLE, 16);
                ItemStack bok = new ItemStack(Material.BOOK);
                ItemStack epr = new ItemStack(Material.ENDER_PEARL, 2);
                ItemStack irp = new ItemStack(Material.IRON_PICKAXE);
                ItemStack brd = new ItemStack(Material.BREAD, 10);
                ItemStack stk = new ItemStack(Material.COOKED_BEEF, 10);
                ItemStack cke = new ItemStack(Material.CAKE);
                ItemStack mlk = new ItemStack(Material.MILK_BUCKET);
                ItemStack arr = new ItemStack(Material.ARROW, 8);

                String ext = ANNIPlugin.getVaultEconomy().currencyNamePlural();

                ANNIUtil.setShopPrice(irh, 1000., ext);
                ANNIUtil.setShopPrice(irc, 1000., ext);
                ANNIUtil.setShopPrice(irl, 1000., ext);
                ANNIUtil.setShopPrice(irb, 1000., ext);
                ANNIUtil.setShopPrice(irs, 1000., ext);
                ANNIUtil.setShopPrice(exp, 1000., ext);
                ANNIUtil.setShopPrice(bok, 1000., ext);
                ANNIUtil.setShopPrice(epr, 1000., ext);
                ANNIUtil.setShopPrice(irp, 1000., ext);
                ANNIUtil.setShopPrice(brd, 1000., ext);
                ANNIUtil.setShopPrice(stk, 1000., ext);
                ANNIUtil.setShopPrice(cke, 1000., ext);
                ANNIUtil.setShopPrice(mlk, 1000., ext);
                ANNIUtil.setShopPrice(arr, 1000., ext);

                inv.setItem(10, irh);
                inv.setItem(11, irc);
                inv.setItem(12, irl);
                inv.setItem(13, irb);
                inv.setItem(14, back);
                inv.setItem(15, back);
                inv.setItem(16, irs);

                inv.setItem(19, exp);
                inv.setItem(20, bok);
                inv.setItem(21, back);
                inv.setItem(22, epr);
                inv.setItem(23, back);
                inv.setItem(24, back);
                inv.setItem(25, irp);

                inv.setItem(28, brd);
                inv.setItem(29, stk);
                inv.setItem(30, cke);
                inv.setItem(31, mlk);
                inv.setItem(32, back);
                inv.setItem(33, back);
                inv.setItem(34, arr);
                break;
            }
            default:
                inv.setItem(45, SellTab);
                inv.setItem(46, PotionTab);
                inv.setItem(47, EquipmentTab);
                break;
        }

        inv.setItem(48, back);

        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName("§c閉じる");
        closeButton.setItemMeta(closeMeta);

        inv.setItem(49, closeButton);
        inv.setItem(50, back);
        inv.setItem(51, back);
        inv.setItem(52, back);

        if (tab == Tab.SELL) {
            ItemStack sellButton = new ItemStack(Material.EMERALD);
            ItemMeta sellMeta = sellButton.getItemMeta();
            sellMeta.setDisplayName("§a売る");
            sellButton.setItemMeta(sellMeta);

            inv.setItem(53, sellButton);
        }
        else inv.setItem(53, back);
    }

    public static void open(Player player, Tab page) {
        Inventory inv;
        switch (page) {
            case SELL:
                inv = Bukkit.createInventory(null, 54, "ショップ - 換金 [SELL]");
                break;
            case POTIONS:
                inv = Bukkit.createInventory(null, 54, "ショップ - ポーション [POTIONS]");
                break;
            case EQUIPMENTS:
                inv = Bukkit.createInventory(null, 54, "ショップ - 装備品 [EQUIPMENTS]");
                break;
            default: return;
        }

        setUpInventory(inv, page);
        player.openInventory(inv);
    }

    public static boolean isOpenAllowed(Player p) {
        return (
                ANNIPlugin.getGM().getGame() != null
                && ANNIPlugin.getGM().getGame().getStatus().getPhaseId() >= 1
                && !ANNIPlugin.getGM().getGame().getPlayerJoinedTeam(p).isSpectator()
        );
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return (
                e.getView().getTitle().matches("^ショップ - .+? \\[([A-Z]+)]$")
                && e.getInventory().getSize() == 54
                && e.getInventory().getType() == InventoryType.CHEST
                && ANNIPlugin.getGM().getGame() != null
                && ANNIPlugin.getGM().getGame().getStatus().getPhaseId() >= 1
                && !ANNIPlugin.getGM().getGame().getPlayerJoinedTeam(((Player) e.getWhoClicked())).isSpectator()
        );
    }

    public static void handle(InventoryClickEvent e) {
        if (isHandleable(e)) {
            ItemStack clicked = e.getCurrentItem();
            Player p = ((Player) e.getWhoClicked());
            int slot = e.getRawSlot();

            if (clicked != null) {
                // background click
                if (
                        clicked.hasItemMeta()
                        && clicked.getItemMeta().hasDisplayName()
                        && (clicked.getItemMeta().getDisplayName().equals(" ")
                        || clicked.getItemMeta().getDisplayName().equals("§8 --- "))
                        && clicked.getType() == Material.GRAY_STAINED_GLASS_PANE
                ) {
                    e.setCancelled(true);
                    return;
                }

                // close
                if (
                        clicked.hasItemMeta()
                        && clicked.getType() == Material.BARRIER
                        && slot == 49
                ) {
                    e.setCancelled(true);
                    e.getView().close();
                    return;
                }

                // buy
                if ((slot == 0)  || (slot >= 10 && 16 >= slot) || (slot >= 19 && 25 >= slot) || (slot >= 28 && 34 >= slot)) {
                    if (
                            clicked.hasItemMeta()
                            && clicked.getItemMeta().hasLore()
                            && clicked.getItemMeta().getLore().size() > 0
                    ) {
                        List<String> l = clicked.getItemMeta().getLore();
                        Matcher m = Pattern.compile("^§8Price:([0-9]+\\.?[0-9]*?)$").matcher(l.get(l.size()-1));

                        if (m.find()) {
                            Double price = Double.valueOf(m.group(1));

                            if (ANNIPlugin.getVaultEconomy().getBalance(p) >= price) {
                                ANNIPlugin.getVaultEconomy().withdrawPlayer(p, price);
                                ItemStack buyObj = clicked.clone();
                                ItemMeta buyMeta = buyObj.getItemMeta();
                                buyMeta.setDisplayName(null);
                                buyMeta.setLore(null);
                                buyObj.setItemMeta(buyMeta);

                                for (Map.Entry<Integer, ItemStack> i : p.getInventory().addItem(buyObj).entrySet()) {
                                    p.getLocation().getWorld().dropItemNaturally(p.getLocation(), i.getValue());
                                    p.sendMessage("§7[§c警告§7] §c" + i.getValue().getItemMeta().getLocalizedName() + " (" + i.getValue().getAmount() + "個) がインベントリに収まらないためドロップしました。");
                                }

                                p.sendMessage("§a購入しました! (- "+price+" "+ANNIPlugin.getVaultEconomy().currencyNamePlural()+"");
                            } else {
                                p.sendMessage("§c残高が不足しています ("+(ANNIPlugin.getVaultEconomy().getBalance(p)-price)+" "+ANNIPlugin.getVaultEconomy().currencyNamePlural()+"不足)");
                            }
                        }
                    }
                    e.setCancelled(true);
                    return;
                }

                // change tab
                if (slot >= 45 && slot <= 47) {
                    switch (clicked.getType()) {
                        case GOLD_INGOT:
                            open(p,Tab.SELL);
                            break;
                        case POTION:
                            open(p,Tab.POTIONS);
                            break;
                        case IRON_SWORD:
                            open(p,Tab.EQUIPMENTS);
                        default:
                            break;
                    }
                    e.setCancelled(true);
                    return;
                }

                if (slot == 53 && clicked.getType() == Material.EMERALD) {
                    ItemStack[] sellItems = ANNIUtil.getSellItems(e.getInventory().getContents());
                    double add = 0D;

                    if (sellItems.length != 0) {
                        for (ItemStack sellItem : sellItems) {
                            if (sellItem == null) continue;
                            if (sellMap.containsKey(sellItem.getType())) {
                                add += sellMap.get(sellItem.getType()) * (sellItem.getAmount());
                            } else {
                                for (Map.Entry<Integer, ItemStack> en : p.getInventory().addItem(sellItem).entrySet()) {
                                    p.getLocation().getWorld().dropItemNaturally(p.getLocation(), en.getValue());
                                }
                            }
                        }

                        open(p, Tab.SELL);
                        ANNIPlugin.getVaultEconomy().depositPlayer(p, add);

                        p.sendMessage("§a合計 " + add + " " + ANNIPlugin.getVaultEconomy().currencyNamePlural() + "を取得しました！");
                    }

                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

}
