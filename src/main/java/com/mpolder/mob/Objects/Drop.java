package com.mpolder.mob.Objects;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Martijn on 11-6-2017.
 */
public class Drop {
    public ItemStack item = null;
    public Double dropChance = 0.0;

    public Drop(ItemStack item, Double dropChance) {
        this.item = item;
        this.dropChance = dropChance;
    }

    public Boolean willDrop() {
        Double rng = RNG(0.0, 100.0);
        if (rng <= dropChance) return true;
        return false;
    }

    private Double RNG(Double min, Double max) {
        Double randomNum = min + (int) (Math.random() * max);
        return randomNum;
    }
}
