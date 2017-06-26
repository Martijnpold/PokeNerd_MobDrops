package com.mpolder.mob.Utils;

import org.bukkit.ChatColor;

/**
 * Created by Martijn on 11-6-2017.
 */
public class Text {
    public static String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
