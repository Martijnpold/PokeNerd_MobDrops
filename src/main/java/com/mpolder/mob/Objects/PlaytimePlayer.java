package com.mpolder.mob.Objects;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Martijn on 12-6-2017.
 */
public class PlaytimePlayer {
    public Player player = null;
    public HashMap<String, Integer> playTime = new HashMap<>();

    public PlaytimePlayer(Player p) {
        player = p;
    }
}
