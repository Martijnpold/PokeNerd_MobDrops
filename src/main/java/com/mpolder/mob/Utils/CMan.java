package com.mpolder.mob.Utils;

import com.mpolder.mob.Objects.Drop;
import com.mpolder.mob.Objects.Mob;
import com.mpolder.mob.Objects.PlaytimePlayer;
import com.mpolder.mob.Objects.Reward;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Martijn on 11-6-2017.
 */
public class CMan {
    public Date curDate = new Date();
    private Plugin pl = null;
    public HashMap<EntityType, Mob> drops = new HashMap<>();
    public HashMap<String, Reward> rewards = new HashMap<>();
    public List<String> enabledRewards = new ArrayList<>();
    public HashMap<Player, PlaytimePlayer> playTimers = new HashMap<>();
    public long lastUpdate = 0;
    public Boolean save = true;

    public CMan(Plugin plugin) {
        pl = plugin;
        init();
    }

    private void init() {
        if (!pl.getDataFolder().exists()) pl.getDataFolder().mkdirs();
        File cfg = new File(pl.getDataFolder(), "config.yml");
        if (!cfg.exists()) {
            try {
                cfg.createNewFile();
            } catch (IOException e) {
            }
        }
        loadRewards();
        loadDrops();
    }

    public void loadDrops() {
        drops.clear();
        for (EntityType e : EntityType.values()) {
            drops.put(e, new Mob(this, e));
        }

        if (pl.getConfig().getConfigurationSection("Drops") != null) {
            for (String key : pl.getConfig().getConfigurationSection("Drops").getKeys(false)) {
                EntityType e = Mob.getTypeFromString(key);
                if (e != null) {
                    System.out.println("Searching items for: " + e.toString());
                    if (pl.getConfig().getConfigurationSection("Drops." + e.toString()) != null) {
                        Mob mob = drops.get(e);
                        for (String id : pl.getConfig().getConfigurationSection("Drops." + e.toString()).getKeys(false)) {
                            Double chance = pl.getConfig().getDouble("Drops." + e.toString() + "." + id + ".Chance");
                            ItemStack item = pl.getConfig().getItemStack("Drops." + e.toString() + "." + id + ".Item");
                            Drop drop = new Drop(item, chance);
                            mob.drops.add(drop);
                            System.out.println(chance + " - " + item);
                        }
                    }
                }
            }
        }
    }

    public void saveDrops() {
        if(!save) return;
        pl.getConfig().set("Drops", null);
        for (EntityType e : drops.keySet()) {
            Mob mob = drops.get(e);
            int i = 0;
            for (Drop drop : mob.drops) {
                pl.getConfig().set("Drops." + e.toString() + "." + i + ".Chance", drop.dropChance);
                pl.getConfig().set("Drops." + e.toString() + "." + i + ".Item", drop.item);
                i++;
            }
        }
        pl.saveConfig();
        pl.reloadConfig();
    }

    public void loadRewards() {
        rewards.clear();
        enabledRewards.clear();
        if (pl.getConfig().getConfigurationSection("Rewards") != null) {
            for (String key : pl.getConfig().getConfigurationSection("Rewards").getKeys(false)) {
                System.out.println("Searching items for: " + key);
                if (pl.getConfig().getBoolean("Rewards." + key + ".Enabled")) enabledRewards.add(key);
                if (pl.getConfig().getConfigurationSection("Rewards." + key) != null) {
                    Reward reward = new Reward(this);
                    for (String id : pl.getConfig().getConfigurationSection("Rewards." + key).getKeys(false)) {
                        if (id.equalsIgnoreCase("enabled") || id.equalsIgnoreCase("delay")) continue;
                        Double chance = pl.getConfig().getDouble("Rewards." + key + "." + id + ".Chance");
                        ItemStack item = pl.getConfig().getItemStack("Rewards." + key + "." + id + ".Item");
                        Drop drop = new Drop(item, chance);
                        reward.drops.add(drop);
                        System.out.println(chance + " - " + item);
                    }
                    rewards.put(key, reward);
                }
            }
        }
        if (!rewards.containsKey("1")) rewards.put("1", new Reward(this));
        if (!rewards.containsKey("2")) rewards.put("2", new Reward(this));
        if (!rewards.containsKey("3")) rewards.put("3", new Reward(this));
        if (!rewards.containsKey("4")) rewards.put("4", new Reward(this));
        if (!rewards.containsKey("5")) rewards.put("5", new Reward(this));
        rewards.get("1").time = pl.getConfig().getDouble("Rewards.1.Delay", 1.0);
        rewards.get("2").time = pl.getConfig().getDouble("Rewards.2.Delay", 2.0);
        rewards.get("3").time = pl.getConfig().getDouble("Rewards.3.Delay", 3.0);
        rewards.get("4").time = pl.getConfig().getDouble("Rewards.4.Delay", 4.0);
        rewards.get("5").time = pl.getConfig().getDouble("Rewards.5.Delay", 5.0);
    }

    public void saveRewards() {
        if(!save) return;
        pl.getConfig().set("Rewards", null);
        for (String key : rewards.keySet()) {
            Reward reward = rewards.get(key);
            int i = 0;
            pl.getConfig().set("Rewards." + key + "." + ".Delay", rewards.get(key).time);
            pl.getConfig().set("Rewards." + key + "." + ".Enabled", (enabledRewards.contains(key)));
            for (Drop drop : reward.drops) {
                pl.getConfig().set("Rewards." + key + "." + i + ".Chance", drop.dropChance);
                pl.getConfig().set("Rewards." + key + "." + i + ".Item", drop.item);
                i++;
            }
        }
        pl.saveConfig();
        pl.reloadConfig();
    }

    public PlaytimePlayer getPlayTime(Player player) {
        File file = new File(pl.getDataFolder(), "playtime.yml");
        create(file);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        PlaytimePlayer ptp = new PlaytimePlayer(player);
        ptp.playTime.put("1", yaml.getInt(player.getUniqueId().toString() + ".1", 0));
        ptp.playTime.put("2", yaml.getInt(player.getUniqueId().toString() + ".2", 0));
        ptp.playTime.put("3", yaml.getInt(player.getUniqueId().toString() + ".3", 0));
        ptp.playTime.put("4", yaml.getInt(player.getUniqueId().toString() + ".4", 0));
        ptp.playTime.put("5", yaml.getInt(player.getUniqueId().toString() + ".5", 0));
        return ptp;
    }

    public void savePlayTime() {
        if(!save) return;
        File file = new File(pl.getDataFolder(), "playtime.yml");
        create(file);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (PlaytimePlayer ptp : playTimers.values()) {
            Player player = ptp.player;
            yaml.set(player.getUniqueId().toString(), null);
            if (ptp.playTime.get("1") != 0) yaml.set(player.getUniqueId().toString() + ".1", ptp.playTime.get("1"));
            if (ptp.playTime.get("2") != 0) yaml.set(player.getUniqueId().toString() + ".2", ptp.playTime.get("2"));
            if (ptp.playTime.get("3") != 0) yaml.set(player.getUniqueId().toString() + ".3", ptp.playTime.get("3"));
            if (ptp.playTime.get("4") != 0) yaml.set(player.getUniqueId().toString() + ".4", ptp.playTime.get("4"));
            if (ptp.playTime.get("5") != 0) yaml.set(player.getUniqueId().toString() + ".5", ptp.playTime.get("5"));
        }
        save(file, yaml);
    }

    public void savePlayTime(Player p) {
        if(!save) return;
        File file = new File(pl.getDataFolder(), "playtime.yml");
        create(file);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        PlaytimePlayer ptp = playTimers.get(p);
        yaml.set(p.getUniqueId().toString(), null);
        if (ptp.playTime.get("1") != 0) yaml.set(p.getUniqueId().toString() + ".1", ptp.playTime.get("1"));
        if (ptp.playTime.get("2") != 0) yaml.set(p.getUniqueId().toString() + ".2", ptp.playTime.get("2"));
        if (ptp.playTime.get("3") != 0) yaml.set(p.getUniqueId().toString() + ".3", ptp.playTime.get("3"));
        if (ptp.playTime.get("4") != 0) yaml.set(p.getUniqueId().toString() + ".4", ptp.playTime.get("4"));
        if (ptp.playTime.get("5") != 0) yaml.set(p.getUniqueId().toString() + ".5", ptp.playTime.get("5"));
        save(file, yaml);
    }

    public void resetPlayTime(String i) {
        if(!save) return;
        File file = new File(pl.getDataFolder(), "playtime.yml");
        create(file);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (yaml.getConfigurationSection("") != null) {
            for (String key : yaml.getConfigurationSection("").getKeys(false)) {
                yaml.set(key + "." + i, null);
                if (yaml.getConfigurationSection(key) == null || yaml.getConfigurationSection(key).getKeys(false).size() == 0) {
                    yaml.set(key, null);
                }
            }
        }
        save(file, yaml);
    }

    public void updatePlayTime() {
        for (Player player : pl.getServer().getOnlinePlayers()) {
            playTimers.put(player, getPlayTime(player));
        }
    }

    public void save(File file, YamlConfiguration yaml) {
        if(!save) return;
        try {
            yaml.save(file);
        } catch (IOException e) {
        }
    }

    public void create(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
        }
    }

    public void remove(File file) {
        file.delete();
    }

    public void ul() {
        save = false;
        drops.clear();
        rewards.clear();
        playTimers.clear();
    }
}
