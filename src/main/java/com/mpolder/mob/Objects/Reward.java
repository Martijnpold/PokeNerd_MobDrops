package com.mpolder.mob.Objects;

import com.mpolder.mob.Utils.CMan;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class Reward {
    public List<Drop> drops = new ArrayList<>();
    CMan cman = null;
    public Double time = 0.0;

    public Reward(CMan cman) {
        this.cman = cman;
    }
}
