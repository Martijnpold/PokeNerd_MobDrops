package com.mpolder.mob.GUI.MobDrops;

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

/**
 * Created by Martijn on 11-6-2017.
 */
public class MobType implements Listener {
    private String menuName = Text.colorize("&9&lSelect Mob Type");
    private Plugin pl = null;
    private CMan cman = null;

    public MobType(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;
    }

    @EventHandler
    public void iClick(InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (inv.getName().equalsIgnoreCase(menuName)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getRawSlot() == 11) {
                    //OPEN HOSTILE MOBS
                    p.openInventory(new HostileMobs(pl, cman).getGUI());
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() == 15) {
                    //OPEN FRIENDLY MOBS
                    p.openInventory(new FriendlyMobs(pl, cman).getGUI());
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
                if (e.getRawSlot() == 22) {
                    p.openInventory(new MainMenu(pl, cman).getGUI());
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                }
            }
        }
    }

    public Inventory getGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, menuName);
        inv.setItem(11, Item.getNamedItemStack(Material.SKULL_ITEM, 1, (short) 1, "&4Evil Mobs", null));
        inv.setItem(15, Item.getNamedItemStack(Material.RED_ROSE, 1, (short) 1, "&dFriendly Mobs", null));
        inv.setItem(22, Item.getNamedItemStack(Material.ARROW, 1, (short) 0, Text.colorize("&cReturn"), null));
        Item.fillWithPanes(inv);
        return inv;
    }
}
