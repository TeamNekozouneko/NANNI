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
    private String shortId;
    private String description;
    private String content;
    private double price;

    public ANNIKit(String display, final String id, String shortId, String content, double price) {
        this.display = display;
        this.id = id;
        this.shortId = shortId;
        this.description="";
        this.content = content;
        this.price = price;
        this.icon = "CHEST";
    }

    public ANNIKit(String display, final String id, String shortId, ItemStack[] content, double price) {
        this.display = display;
        this.id = id;
        this.shortId = shortId;
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

    public void setIcon(Material m) {
        if (m == Material.AIR) return;
        this.icon = m.name();
    }

    public void setShortId(String si) {
        this.shortId = si;
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

    public String getShortId() {
        return shortId;
    }

    @Override
    public String toString() {
        String cont = getContent();
        if (!(cont == null || cont.isEmpty())) cont = "*****";

        return getClass().getName() + "[Id="+id+", Display="+display+"]";    }

}
