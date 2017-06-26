package com.mpolder.mob.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class Item {
    public static ItemStack getNamedItemStack(Material material, Integer amount, Short damage, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount, damage);
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(Text.colorize(name));
        if (lore != null) {
            ArrayList<String> colorLore = new ArrayList<>();
            for (String s : lore) {
                colorLore.add(Text.colorize(s));
            }
            iMeta.setLore(colorLore);
        }
        item.setItemMeta(iMeta);
        return item;
    }

    public static ItemStack getNamedItemStack(ItemStack item, String name, List<String> lore) {
        item = item.clone();
        ItemMeta iMeta = item.getItemMeta();
        if (name != null) {
            iMeta.setDisplayName(Text.colorize(name));
        }
        if (lore != null) {
            ArrayList<String> colorLore = new ArrayList<>();
            for (String s : lore) {
                colorLore.add(Text.colorize(s));
            }
            iMeta.setLore(colorLore);
        }
        item.setItemMeta(iMeta);
        return item;
    }

    public static void fillWithPanes(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15));
            }
        }
    }

    public static void fillWithPanes(Inventory inv, int min, int max) {
        for (int i = min; i < max; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15));
            }
        }
    }

    public static ItemStack getSkull(String owner) {
        ItemStack skull = new ItemStack(397, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(owner);
        skull.setItemMeta(meta);
        return skull;
    }
}
