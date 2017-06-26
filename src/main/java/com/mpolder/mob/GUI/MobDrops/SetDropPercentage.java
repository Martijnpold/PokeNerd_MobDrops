package com.mpolder.mob.GUI.MobDrops;

import com.mpolder.mob.Objects.Mob;
import com.mpolder.mob.Utils.CMan;
import com.mpolder.mob.Utils.Item;
import com.mpolder.mob.Utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Martijn on 11-6-2017.
 */
public class SetDropPercentage implements Listener {
    private HashMap<Integer, Double> values = new HashMap<>();
    private String menuName = Text.colorize("&9&lDrop Chance: ");
    private Plugin pl = null;
    private CMan cman = null;

    public SetDropPercentage(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;
        values.put(9, -10.0);
        values.put(10, -5.0);
        values.put(11, -1.0);
        values.put(12, -0.1);
        values.put(14, 0.1);
        values.put(15, 1.0);
        values.put(16, 5.0);
        values.put(17, 10.0);
    }

    @EventHandler
    public void iClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (inv.getName().contains(menuName)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (values.containsKey(e.getRawSlot())) {
                    Mob mob = cman.drops.get(Mob.getTypeFromString(e.getInventory().getItem(13).getItemMeta().getLore().get(0)));
                    String i = ChatColor.stripColor(e.getInventory().getItem(13).getItemMeta().getDisplayName());
                    mob.drops.get(Integer.parseInt(i)).dropChance += values.get(e.getRawSlot());
                    if (mob.drops.get(Integer.parseInt(i)).dropChance < 0.0)
                        mob.drops.get(Integer.parseInt(i)).dropChance = 0.0;
                    if (mob.drops.get(Integer.parseInt(i)).dropChance > 100.0)
                        mob.drops.get(Integer.parseInt(i)).dropChance = 100.0;
                    mob.drops.get(Integer.parseInt(i)).dropChance = round(mob.drops.get(Integer.parseInt(i)).dropChance, 1);
                    cman.saveDrops();
                    p.openInventory(getGUI(mob, Integer.parseInt(i)));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() == 22) {
                    Mob mob = cman.drops.get(Mob.getTypeFromString(e.getInventory().getItem(13).getItemMeta().getLore().get(0)));
                    p.openInventory(new LootTable(pl, cman).getGUI(cman, mob.entityType));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
            }
        }
    }

    public Inventory getGUI(Mob mob, Integer index) {
        ItemStack preview = mob.drops.get(index).item.clone();
        Inventory inv = Bukkit.createInventory(null, 27, menuName + mob.drops.get(index).dropChance + "%");
        Item.fillWithPanes(inv);

        preview = Item.getNamedItemStack(preview, index + "", Arrays.asList(mob.entityType.toString()));
        inv.setItem(13, preview);
        inv.setItem(22, Item.getNamedItemStack(Material.ARROW, 1, (short) 0, Text.colorize("&c&lReturn"), null));

        for (Integer slot : values.keySet()) {
            Double value = values.get(slot);
            ItemStack valuable = new ItemStack(Material.STAINED_CLAY, 1, (byte) 13);
            if (value < 0) valuable.setDurability((byte) 14);
            ItemMeta valuableMeta = valuable.getItemMeta();
            valuableMeta.setDisplayName(Text.colorize("&r&a+" + value + "%"));
            if (value < 0) valuableMeta.setDisplayName(Text.colorize("&r&c" + value + "%"));
            valuable.setItemMeta(valuableMeta);
            inv.setItem(slot, valuable);
        }

        return inv;
    }

    private double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
