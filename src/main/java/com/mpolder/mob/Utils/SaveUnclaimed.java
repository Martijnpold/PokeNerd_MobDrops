package com.mpolder.mob.Utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Martijn on 16-6-2017.
 */
public class SaveUnclaimed {

    public void saveFile(CMan cman, HashMap<String, List<ItemStack>> users, File PATH) {
        System.out.println(users);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(PATH);
        yaml.set("Unclaimed", null);
        for (String key : users.keySet()) {
            for (int i = 0; i < users.get(key).size(); i++) {
                yaml.set("Unclaimed." + key + "." + i, users.get(key).get(i));
            }
        }
        cman.save(PATH, yaml);
    }

    public HashMap<String, List<ItemStack>> readFile(File PATH) {
        HashMap<String, List<ItemStack>> i = new HashMap<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(PATH);
        if (yaml.getConfigurationSection("Unclaimed") != null) {
            for (String p : yaml.getConfigurationSection("Unclaimed").getKeys(false)) {
                ArrayList<ItemStack> i2 = new ArrayList<>();
                for (String p2 : yaml.getConfigurationSection("Unclaimed." + p).getKeys(false)) {
                    i2.add(yaml.getItemStack("Unclaimed." + p + "." + p2));
                }
                i.put(p, i2);
            }
        }
        return i;
    }
}
