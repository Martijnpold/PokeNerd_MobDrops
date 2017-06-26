package com.mpolder.mob.GUI.Rewards;

import com.mpolder.mob.Objects.Drop;
import com.mpolder.mob.Objects.Reward;
import com.mpolder.mob.Utils.CMan;
import com.mpolder.mob.Utils.Item;
import com.mpolder.mob.Utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
 * Created by Martijn on 12-6-2017.
 */
public class RewardTable implements Listener {
    private String menuName = Text.colorize("&9&lReward Drops");
    private Plugin pl = null;
    private CMan cman = null;

    public RewardTable(Plugin plugin, CMan cman) {
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
                Reward reward = cman.rewards.get(e2);
                if (e.getClick().equals(ClickType.RIGHT) && e.getRawSlot() < 27) {
                    e.setCancelled(true);
                    if (reward != null && reward.drops.size() >= e.getRawSlot() - 1) {
                        cman.saveRewards();
                        if (e.getCurrentItem().getItemMeta().hasLore() && e.getCurrentItem().getItemMeta().getLore().get(e.getCurrentItem().getItemMeta().getLore().size() - 1).contains("Drop Chance: ")) {
                            p.openInventory(new SetRewardPercentage(pl, cman).getGUI(cman, e2, e.getRawSlot()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                        }
                    }
                }
                if (e.getRawSlot() == 35) {
                    cman.saveRewards();
                    p.openInventory(new RewardOptions(pl, cman).getGUI(cman, e2));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() == 31) {
                    for (int i = 0; i < 27; i++) {
                        ItemStack item = inv.getItem(i);
                        if (item != null) {
                            item = item.clone();
                            removeDropChance(item);
                            Double chance = 100.0;
                            if (reward.drops != null && reward.drops.size() >= i + 1 && reward.drops.get(i) != null) {
                                chance = reward.drops.get(i).dropChance;
                            }
                            if (reward.drops.size() >= i + 1 && reward.drops.get(i) != null) {
                                reward.drops.set(i, new Drop(item, chance));
                            } else {
                                reward.drops.add(new Drop(item, chance));
                            }
                        } else {
                            if (reward.drops != null && reward.drops.size() >= i + 1 && reward.drops.get(i) != null) {
                                reward.drops.remove(i);
                            }
                        }
                    }
                    cman.saveRewards();
                    p.openInventory(getGUI(cman, e2));
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

    public Inventory getGUI(CMan cman, String id) {
        Inventory inv = Bukkit.createInventory(null, 36, menuName);
        Reward reward = cman.rewards.get(id);
        Item.fillWithPanes(inv, 27, 36);
        if (reward != null) {
            for (int i = 0; i < reward.drops.size(); i++) {
                Drop drop = reward.drops.get(i);
                ItemStack item = drop.item.clone();
                addDropChance(item, drop.dropChance);
                inv.setItem(i, item);
            }
        }
        inv.setItem(31, Item.getNamedItemStack(Material.EMERALD_BLOCK, 1, (short) 0, Text.colorize("&aSave"), Arrays.asList(id)));
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
