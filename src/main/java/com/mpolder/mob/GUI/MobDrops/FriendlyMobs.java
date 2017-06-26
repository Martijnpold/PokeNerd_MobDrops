package com.mpolder.mob.GUI.MobDrops;

import com.mpolder.mob.Objects.Mob;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class FriendlyMobs implements Listener {
    private Inventory inv = null;
    private List<ItemStack> mobItems = new ArrayList<>();
    private String menuName = Text.colorize("&9&lFriendly Mobs");
    private Plugin pl = null;
    private CMan cman = null;

    public FriendlyMobs(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;

        mobItems.clear();
        addMobItem("&cCow", Material.LEATHER, (short) 0);
        addMobItem("&fSheep", Material.WOOL, (short) 0);
        addMobItem("&fChicken", Material.FEATHER, (short) 0);
        addMobItem("&dPig", Material.PORK, (short) 0);
        addMobItem("&8Bat", Material.COAL_BLOCK, (short) 0);
        addMobItem("&8Squid", Material.INK_SACK, (short) 0);
        addMobItem("&cHorse", Material.SADDLE, (short) 0);
        addMobItem("&2Skeleton_Horse", Material.SADDLE, (short) 0);
        addMobItem("&2Zombie_Horse", Material.SADDLE, (short) 0);
        addMobItem("&cDonkey", Material.SADDLE, (short) 0);
        addMobItem("&cMule", Material.SADDLE, (short) 0);
        addMobItem("&6Llama", Material.SADDLE, (short) 0);
        addMobItem("&4Mushroom_Cow", Material.MUSHROOM_SOUP, (short) 0);
        addMobItem("&8Iron_Golem", Material.IRON_INGOT, (short) 0);
        addMobItem("&cRabbit", Material.RABBIT_FOOT, (short) 0);
        addMobItem("&6Ocelot", Material.RAW_FISH, (short) 0);
        addMobItem("&6Wolf", Material.BONE, (short) 0);
        addMobItem("&cParrot", Material.FEATHER, (short) 0);
        addMobItem("&fSnowman", Material.SNOW_BALL, (short) 0);
        addMobItem("&fVillager", Material.EMERALD, (short) 0);
        addMobItem("&fPolar_Bear", Material.RAW_FISH, (short) 1);

        inv = Bukkit.createInventory(null, 27, menuName);
        for (int i = 0; i < mobItems.size(); i++) {
            ItemStack item = mobItems.get(i);
            ItemMeta iMeta = item.getItemMeta();
            iMeta.setLore(Arrays.asList(Text.colorize("&9Drop Count: &3" + cman.drops.get(Mob.getTypeFromString(iMeta.getDisplayName())).drops.size())));
            item.setItemMeta(iMeta);
            inv.setItem(i, mobItems.get(i));
        }
        inv.setItem(26, Item.getNamedItemStack(Material.ARROW, 1, (short) 0, Text.colorize("&cReturn"), null));
        Item.fillWithPanes(inv);
    }

    @EventHandler
    public void iClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (inv.getName().equalsIgnoreCase(menuName)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (!e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE) && e.getRawSlot() < 27) {
                    if (e.getRawSlot() == 26) {
                        p.openInventory(new MobType(pl, cman).getGUI());
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                    } else {
                        p.openInventory(new LootTable(pl, cman).getGUI(cman, Mob.getTypeFromString(e.getCurrentItem().getItemMeta().getDisplayName())));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.10F, 1);
                    }
                }
            }
        }
    }

    public Inventory getGUI() {
        return inv;
    }

    private void addMobItem(String mob, Material item, Short data) {
        if (Mob.isMob(mob)) {
            ItemStack item2 = Item.getNamedItemStack(item, 1, data, Text.colorize("&d" + mob), null);
            mobItems.add(item2);
        }
    }

    private void addMobItem(String mob, ItemStack item) {
        if (Mob.isMob(mob)) {
            mobItems.add(Item.getNamedItemStack(item, Text.colorize("&d" + mob), null));
        }
    }
}
