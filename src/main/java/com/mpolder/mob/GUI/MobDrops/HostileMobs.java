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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class HostileMobs implements Listener {
    private Inventory inv = null;
    private List<ItemStack> mobItems = new ArrayList<>();
    private String menuName = Text.colorize("&9&lFriendly Mobs");
    private Plugin pl = null;
    private CMan cman = null;

    public HostileMobs(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;

        mobItems.clear();
        addMobItem("&2Zombie", Material.ROTTEN_FLESH, (short) 0);
        addMobItem("&2Zombie_Villager", Material.ROTTEN_FLESH, (short) 0);
        addMobItem("&3Husk", Material.ROTTEN_FLESH, (short) 0);
        addMobItem("&fSkeleton", Material.BONE, (short) 0);
        addMobItem("&aCreeper", Material.SULPHUR, (short) 0);
        addMobItem("&7Spider", Material.STRING, (short) 0);
        addMobItem("&7Cave_Spider", Material.STRING, (short) 0);
        addMobItem("&1Enderman", Material.ENDER_PEARL, (short) 0);
        addMobItem("&4Blaze", Material.BLAZE_ROD, (short) 0);
        addMobItem("&7Guardian", Material.PRISMARINE_SHARD, (short) 0);
        addMobItem("&7Elder_Guardian", Material.SPONGE, (short) 1);
        addMobItem("&4Ghast", Material.GHAST_TEAR, (short) 0);
        addMobItem("&4Pig_Zombie", Material.GOLD_NUGGET, (short) 0);
        addMobItem("&4Magma_Cube", Material.MAGMA_CREAM, (short) 0);
        addMobItem("&2Slime", Material.SLIME_BALL, (short) 0);
        addMobItem("&3Evoker", Material.TOTEM, (short) 0);
        addMobItem("&3Illusioner", Material.TOTEM, (short) 0);
        addMobItem("&3Vex", Material.TOTEM, (short) 0);
        addMobItem("&5Witch", Material.GLOWSTONE_DUST, (short) 0);
        addMobItem("&8Wither", Material.NETHER_STAR, (short) 0);
        addMobItem("&8Wither_Skeleton", Material.SKULL_ITEM, (short) 1);
        addMobItem("&8Shulker", Material.SHULKER_SHELL, (short) 0);
        addMobItem("&8Ender_Dragon", Material.SKULL_ITEM, (short) 5);

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

    public Boolean isHostile(String e) {
        for (ItemStack item : mobItems) {
            if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase(e)) {
                return true;
            }
        }
        return false;
    }
}
