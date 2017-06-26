package com.mpolder.mob.GUI;

import com.mpolder.mob.GUI.MobDrops.MobType;
import com.mpolder.mob.GUI.Rewards.MainRewardMenu;
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

/**
 * Created by Martijn on 11-6-2017.
 */
public class MainMenu implements Listener {
    private String menuName = Text.colorize("&9&lMob Drops Main Menu");
    private Plugin pl = null;
    private CMan cman = null;

    public MainMenu(Plugin plugin, CMan cman) {
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
                if (e.getRawSlot() == 11) {
                    //OPEN HOUR DROPS
                    p.openInventory(new MainRewardMenu(pl, cman).getGUI(cman));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() == 15) {
                    //Open Mob Type Menu
                    p.openInventory(new MobType(pl, cman).getGUI());
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
            }
        }
    }

    public Inventory getGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, menuName);
        inv.setItem(11, Item.getNamedItemStack(Material.WATCH, 1, (short) 0, "&6Hour Drops", null));
        inv.setItem(15, Item.getNamedItemStack(Material.SKULL_ITEM, 1, (short) 5, "&4Mob Drops", null));
        Item.fillWithPanes(inv);
        return inv;
    }
}
