package com.mpolder.mob.GUI.Rewards;

import com.mpolder.mob.Utils.CMan;
import com.mpolder.mob.Utils.Item;
import com.mpolder.mob.Utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

/**
 * Created by Martijn on 11-6-2017.
 */
public class RewardOptions implements Listener {
    private String menuName = Text.colorize("&9&lReward Menu ");
    private Plugin pl = null;
    private CMan cman = null;

    public RewardOptions(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;
    }

    @EventHandler
    public void iClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (inv.getName().startsWith(menuName)) {
            String id = inv.getName().replace(Text.colorize("&9&lReward Menu "), "");
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getRawSlot() == 11) {
                    p.openInventory(new MainRewardMenu(pl, cman).getGUI(cman));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() == 13) {
                    p.openInventory(new RewardTable(pl, cman).getGUI(cman, id));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() == 15) {
                    if (cman.enabledRewards.contains(id)) {
                        cman.enabledRewards.remove(id);
                    } else {
                        cman.enabledRewards.add(id);
                    }
                    cman.resetPlayTime(id);
                    cman.updatePlayTime();
                    cman.saveRewards();
                    p.openInventory(getGUI(cman, id));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
            }
        }
    }

    public Inventory getGUI(CMan cman, String id) {
        Inventory inv = Bukkit.createInventory(null, 27, menuName + id);
        inv.setItem(11, Item.getNamedItemStack(Material.ARROW, 1, (short) 0, Text.colorize("&cReturn"), null));
        inv.setItem(13, Item.getNamedItemStack(Material.CHEST, 1, (short) 0, Text.colorize("&2Rewards"), Arrays.asList("&9Reward Count: &3" + cman.rewards.get(id).drops.size())));
        if (cman.enabledRewards.contains(id)) {
            inv.setItem(15, Item.getNamedItemStack(Material.STAINED_CLAY, 1, (short) 5, Text.colorize("&2Reward Enabled"), null));
        } else {
            inv.setItem(15, Item.getNamedItemStack(Material.STAINED_CLAY, 1, (short) 14, Text.colorize("&cReward Disabled"), null));
        }
        Item.fillWithPanes(inv);
        return inv;
    }
}
