package com.mpolder.mob.GUI.Rewards;

import com.mpolder.mob.GUI.MainMenu;
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
public class MainRewardMenu implements Listener {
    private String menuName = Text.colorize("&9&lSelect Reward");
    private Plugin pl = null;
    private CMan cman = null;

    public MainRewardMenu(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;
    }

    @EventHandler
    public void iClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (inv.getName().equalsIgnoreCase(menuName)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getRawSlot() == 22) {
                    p.openInventory(new MainMenu(pl, cman).getGUI());
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() >= 11 && e.getRawSlot() <= 15) {
                    p.openInventory(new RewardOptions(pl, cman).getGUI(cman, e.getRawSlot() - 10 + ""));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
            }
        }
    }

    public Inventory getGUI(CMan cman) {
        Inventory inv = Bukkit.createInventory(null, 27, menuName);
        inv.setItem(11, Item.getNamedItemStack(Material.PAPER, 1, (short) 0, "&2Every " + getHourThingy(cman.rewards.get("1").time), Arrays.asList("&9Reward Count: &3" + cman.rewards.get("1").drops.size(), getOnThingy(cman, "1"))));
        inv.setItem(12, Item.getNamedItemStack(Material.PAPER, 1, (short) 0, "&3Every " + getHourThingy(cman.rewards.get("2").time), Arrays.asList("&9Reward Count: &3" + cman.rewards.get("2").drops.size(), getOnThingy(cman, "2"))));
        inv.setItem(13, Item.getNamedItemStack(Material.PAPER, 1, (short) 0, "&4Every " + getHourThingy(cman.rewards.get("3").time), Arrays.asList("&9Reward Count: &3" + cman.rewards.get("3").drops.size(), getOnThingy(cman, "3"))));
        inv.setItem(14, Item.getNamedItemStack(Material.PAPER, 1, (short) 0, "&cEvery " + getHourThingy(cman.rewards.get("4").time), Arrays.asList("&9Reward Count: &3" + cman.rewards.get("4").drops.size(), getOnThingy(cman, "4"))));
        inv.setItem(15, Item.getNamedItemStack(Material.PAPER, 1, (short) 0, "&9Every " + getHourThingy(cman.rewards.get("5").time), Arrays.asList("&9Reward Count: &3" + cman.rewards.get("5").drops.size(), getOnThingy(cman, "5"))));
        inv.setItem(22, Item.getNamedItemStack(Material.ARROW, 1, (short) 0, Text.colorize("&cReturn"), null));
        Item.fillWithPanes(inv);
        return inv;
    }

    private String getHourThingy(Double length) {
        if (length < 2) {
            return length + " Hour";
        } else {
            return length + " Hours";
        }
    }

    private String getOnThingy(CMan cman, String id) {
        if (cman.enabledRewards.contains(id)) {
            return Text.colorize("&9Current State: &2On");
        } else {
            return Text.colorize("&9Current State: &cOff");
        }
    }
}
