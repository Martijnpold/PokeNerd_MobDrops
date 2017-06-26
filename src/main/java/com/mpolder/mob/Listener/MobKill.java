package com.mpolder.mob.Listener;

import com.mpolder.mob.Objects.Drop;
import com.mpolder.mob.Objects.Mob;
import com.mpolder.mob.Utils.CMan;
import com.mpolder.mob.Utils.Text;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class MobKill implements Listener {
    public long rewardDrop = 1501399335451L;
    private List<ItemStack> mobItems = new ArrayList<>();
    private String menuName = Text.colorize("&9&lFriendly Mobs");
    private Plugin pl = null;
    private CMan cman = null;

    public MobKill(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;
    }

    @EventHandler
    public void killEntity(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (e.getEntity().getKiller() instanceof Player) {
            Player p = (Player) e.getEntity().getKiller();
            if (cman.drops.get(entity.getType()) != null) {
                Mob mob = cman.drops.get(entity.getType());
                if (mob != null && mob.drops != null && mob.drops.size() > 0) {
                    for (Drop drop : mob.drops) {
                        if (drop.willDrop()) {
                            e.getDrops().add(drop.item);
                        }
                    }
                }
            }
        }
    }
}
