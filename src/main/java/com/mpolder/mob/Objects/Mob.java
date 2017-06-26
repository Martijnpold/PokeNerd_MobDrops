package com.mpolder.mob.Objects;

import com.mpolder.mob.Utils.CMan;
import com.mpolder.mob.Utils.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class Mob {
    public EntityType entityType = null;
    public List<Drop> drops = new ArrayList<>();
    CMan cman = null;

    public Mob(CMan cman, EntityType e) {
        this.cman = cman;
        this.entityType = e;
    }

    public static Boolean isMob(String string) {
        for (EntityType e : EntityType.values()) {
            if (e.toString().equalsIgnoreCase(ChatColor.stripColor(Text.colorize(string)))) {
                System.out.println(e.toString() + " = " + ChatColor.stripColor(Text.colorize(string)));
                return true;
            }
        }
        System.out.println(ChatColor.stripColor(Text.colorize(string))+" is not a mob");
        return false;
    }

    public static EntityType getTypeFromString(String string) {
        for (EntityType e : EntityType.values()) {
            if (e.toString().equalsIgnoreCase(ChatColor.stripColor(Text.colorize(string)))) {
                return e;
            }
        }
        return null;
    }
}
