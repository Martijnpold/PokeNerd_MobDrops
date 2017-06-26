package com.mpolder.mob.Listener;

import com.mpolder.mob.Utils.CMan;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 * Created by Martijn on 12-6-2017.
 */
public class PlayerLog implements Listener {
    private Plugin pl = null;
    private CMan cman = null;

    public PlayerLog(Plugin plugin, CMan cman) {
        this.pl = plugin;
        this.cman = cman;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        cman.playTimers.put(e.getPlayer(), cman.getPlayTime(e.getPlayer()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        cman.savePlayTime(e.getPlayer());
        cman.playTimers.remove(e.getPlayer());
    }
}
