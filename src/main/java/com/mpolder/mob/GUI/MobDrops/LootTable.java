package com.mpolder.mob.GUI.MobDrops;

import com.mpolder.mob.Objects.Drop;
import com.mpolder.mob.Objects.Mob;
import com.mpolder.mob.Utils.CMan;
import com.mpolder.mob.Utils.Item;
import com.mpolder.mob.Utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class LootTable implements Listener {
    private String menuName = Text.colorize("&9&lMob Drops");
    private Plugin pl = null;
    private CMan cman = null;

    public LootTable(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;
    }

    @EventHandler
    public void iClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (inv.getName().equalsIgnoreCase(menuName)) {
            if (e.getCurrentItem() != null) {
                if (e.getRawSlot() >= 27 && e.getRawSlot() < 36) e.setCancelled(true);
                String e2 = null;
                if (e.getRawSlot() == 31) {
                    e2 = e.getCurrentItem().getItemMeta().getLore().get(0);
                } else {
                    e2 = inv.getItem(31).getItemMeta().getLore().get(0);
                }
                Mob mob = cman.drops.get(Mob.getTypeFromString(e2));
                if (e.getClick().equals(ClickType.RIGHT) && e.getRawSlot() < 27) {
                    e.setCancelled(true);
                    if (mob != null && mob.drops.size() >= e.getRawSlot() - 1) {
                        cman.saveDrops();
                        if (e.getCurrentItem().getItemMeta().hasLore() && e.getCurrentItem().getItemMeta().getLore().get(e.getCurrentItem().getItemMeta().getLore().size() - 1).contains("Drop Chance: ")) {
                            p.openInventory(new SetDropPercentage(pl, cman).getGUI(mob, e.getRawSlot()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                        }
                    }
                }
                if (e.getRawSlot() == 35) {
                    if (new HostileMobs(pl, cman).isHostile(mob.entityType.toString())) {
                        cman.saveDrops();
                        p.openInventory(new HostileMobs(pl, cman).getGUI());
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                    } else {
                        cman.saveDrops();
                        p.openInventory(new FriendlyMobs(pl, cman).getGUI());
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                    }
                }
                if (e.getRawSlot() == 31) {
                    for (int i = 0; i < 27; i++) {
                        ItemStack item = inv.getItem(i);
                        if (item != null) {
                            item = item.clone();
                            removeDropChance(item);
                            Double chance = 100.0;
                            if (mob.drops != null && mob.drops.size() >= i + 1 && mob.drops.get(i) != null) {
                                chance = mob.drops.get(i).dropChance;
                            }
                            if (mob.drops.size() >= i + 1 && mob.drops.get(i) != null) {
                                mob.drops.set(i, new Drop(item, chance));
                            } else {
                                mob.drops.add(new Drop(item, chance));
                            }
                        } else {
                            if (mob.drops != null && mob.drops.size() >= i + 1 && mob.drops.get(i) != null) {
                                mob.drops.remove(i);
                            }
                        }
                    }
                    cman.saveDrops();
                    p.openInventory(getGUI(cman, Mob.getTypeFromString(e2)));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
            }
            if (e.getRawSlot() < 27 && e.isLeftClick()) {
                ItemStack item = e.getCurrentItem();
                ItemStack item2 = e.getCursor();
                if (item != null && !item.getType().equals(Material.AIR)) {
                    removeDropChance(item);
                    p.updateInventory();
                }
                if (item2 != null && !item2.getType().equals(Material.AIR)) {
                    ItemMeta iMeta = item2.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    if (iMeta != null && iMeta.hasLore()) {
                        lore.addAll(iMeta.getLore());
                    }
                    lore.add(Text.colorize("&cApply the items first"));
                    iMeta.setLore(lore);
                    item2.setItemMeta(iMeta);
                    e.getInventory().setItem(e.getRawSlot(), e.getCursor());
                    e.setCancelled(true);
                    e.setCursor(null);
                    if (item != null) e.setCursor(item);
                    p.updateInventory();
                }
            }
        }
    }

    public Inventory getGUI(CMan cman, EntityType e) {
        Inventory inv = Bukkit.createInventory(null, 36, menuName);
        Mob mob = cman.drops.get(e);
        Item.fillWithPanes(inv, 27, 36);
        if (mob != null) {
            for (int i = 0; i < mob.drops.size(); i++) {
                Drop drop = mob.drops.get(i);
                ItemStack item = drop.item.clone();
                addDropChance(item, drop.dropChance);
                inv.setItem(i, item);
            }
        }
        inv.setItem(31, Item.getNamedItemStack(Material.EMERALD_BLOCK, 1, (short) 0, Text.colorize("&aSave"), Arrays.asList(e.toString())));
        inv.setItem(35, Item.getNamedItemStack(Material.ARROW, 1, (short) 0, Text.colorize("&cReturn"), null));
        return inv;
    }

    private void addDropChance(ItemStack item, Double dropChance) {
        ItemMeta iMeta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (iMeta.hasLore()) {
            lore.addAll(iMeta.getLore());
        }
        lore.add(Text.colorize("&9Drop Chance: &a" + dropChance + "%"));
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
    }

    private void removeDropChance(ItemStack item) {
        ItemMeta iMeta = item.getItemMeta();
        if (iMeta.hasLore()) {
            List<String> lore = iMeta.getLore();
            for (int i = lore.size() - 1; i >= 0; i--) {
                String line = lore.get(i);
                if (line.contains("Drop Chance: ") || line.contains("Apply the items first")) {
                    lore.remove(line);
                }
            }
            iMeta.setLore(lore);
        }
        item.setItemMeta(iMeta);
    }
}
