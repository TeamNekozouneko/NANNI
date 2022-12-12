package com.nekozouneko.anni.file;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ANNIKit {

    private String display;
    private String icon;
    private final String id;
    private String description;
    private String content;
    private double price;
    private List<String> purchased;

    public ANNIKit(String display, final String id, String content, double price) {
        this.display = display;
        this.id = id;
        this.description="";
        this.content = content;
        this.price = price;
        this.icon = "CHEST";
    }

    public ANNIKit(String display, final String id, ItemStack[] content, double price) {
        this.display = display;
        this.id = id;
        this.description="";
        this.price = price;
        this.icon = "CHEST";
        setContent(content);
    }

    public void setDisplayName(String name) {
        this.display = name;
    }

    public void setContent(ItemStack[] iss) {
        ByteArrayOutputStream bytes;
        BukkitObjectOutputStream out;
        try {
            bytes = new ByteArrayOutputStream();
            out = new BukkitObjectOutputStream(bytes);

            out.writeObject(iss);
        } catch (IOException e) {
            return;
        }

        this.content = Base64Coder.encodeLines(bytes.toByteArray());
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPurchased(List<String> purchased) {
        this.purchased = purchased;
    }

    public void setPurchasedPlayerUUID(List<UUID> purchased) {
        List<String> conv = new ArrayList<>();

        purchased.forEach((u) -> conv.add(u.toString()));

        this.purchased = conv;
    }

    public void setIcon(Material m) {
        if (m == Material.AIR) return;
        this.icon = m.name();
    }

    public boolean addPurchased(String id) {
        return purchased.add(id);
    }

    public boolean addPurchasedUUID(UUID id) {
        return purchased.remove(id.toString());
    }

    public boolean removePurchased(String id) {
        return purchased.remove(id);
    }

    public boolean removePurchasedUUID(UUID id) {
        return purchased.remove(id.toString());
    }

    public String getDisplayName() {
        return display;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack[] getDecodedContent() {
        ByteArrayInputStream bytes;
        BukkitObjectInputStream in;

        try {
            bytes = new ByteArrayInputStream(Base64Coder.decodeLines(content));
            in = new BukkitObjectInputStream(bytes);
            Object obj = in.readObject();

            if (obj instanceof ItemStack[]) {
                return (ItemStack[]) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getPurchased() {
        return purchased;
    }

    public List<UUID> getPurchasedPlayerUUID() {
        List<UUID> listu = new ArrayList<>();
        purchased.forEach((s) -> listu.add(UUID.fromString(s)));

        return listu;
    }

    public String getID() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public Material getIconMaterial() throws IllegalArgumentException {
        try {
            return Material.valueOf(icon.toUpperCase());
        } catch (IllegalArgumentException ignored) {return Material.CHEST;}
    }

    @Override
    public String toString() {
        String cont = getContent();
        if (!(cont == null || cont.isEmpty())) cont = "*****";

        return getClass().getName() + "[Id="+id+", Display="+display+"]";    }

}
