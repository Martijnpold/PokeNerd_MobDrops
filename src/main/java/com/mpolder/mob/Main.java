package com.mpolder.mob;

import com.mpolder.mob.GUI.MainMenu;
import com.mpolder.mob.GUI.MobDrops.*;
import com.mpolder.mob.GUI.Rewards.MainRewardMenu;
import com.mpolder.mob.GUI.Rewards.RewardOptions;
import com.mpolder.mob.GUI.Rewards.RewardTable;
import com.mpolder.mob.GUI.Rewards.SetRewardPercentage;
import com.mpolder.mob.Listener.MobKill;
import com.mpolder.mob.Listener.PlayerLog;
import com.mpolder.mob.Objects.Drop;
import com.mpolder.mob.Objects.PlaytimePlayer;
import com.mpolder.mob.Objects.Reward;
import com.mpolder.mob.Utils.CMan;
import com.mpolder.mob.Utils.SaveUnclaimed;
import com.mpolder.mob.Utils.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.*;

/**
 * Created by Martijn on 11-6-2017.
 */
public class Main extends JavaPlugin {
    public HashMap<String, List<ItemStack>> unclaimed = new HashMap<>();
    CMan cman = null;
    private final Plugin pl = this;

    @Override
    public void onEnable() {
        unclaimed.clear();
        cman = new CMan(this);
        cman.lastUpdate = System.currentTimeMillis();
        getServer().getPluginManager().registerEvents(new LootTable(this, cman), this);
        getServer().getPluginManager().registerEvents(new FriendlyMobs(this, cman), this);
        getServer().getPluginManager().registerEvents(new HostileMobs(this, cman), this);
        getServer().getPluginManager().registerEvents(new MainMenu(this, cman), this);
        getServer().getPluginManager().registerEvents(new MobType(this, cman), this);
        getServer().getPluginManager().registerEvents(new SetDropPercentage(this, cman), this);
        getServer().getPluginManager().registerEvents(new MobKill(this, cman), this);
        getServer().getPluginManager().registerEvents(new MainRewardMenu(this, cman), this);
        getServer().getPluginManager().registerEvents(new RewardOptions(this, cman), this);
        getServer().getPluginManager().registerEvents(new RewardTable(this, cman), this);
        getServer().getPluginManager().registerEvents(new SetRewardPercentage(this, cman), this);
        getServer().getPluginManager().registerEvents(new PlayerLog(this, cman), this);

        for (Player player : getServer().getOnlinePlayers()) {
            cman.playTimers.put(player, cman.getPlayTime(player));
        }

        File file = new File(getDataFolder(), "unclaimed.yml");
        cman.create(file);
        unclaimed = new SaveUnclaimed().readFile(file);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    PlaytimePlayer ptp = cman.playTimers.get(player);
                    Integer diff = (int) (System.currentTimeMillis() - cman.lastUpdate);
                    for (int i = 1; i <= 5; i++) {
                        Reward reward = cman.rewards.get(i + "");
                        if (cman.enabledRewards.contains(i + "")) {
                            ptp.playTime.put(i + "", ptp.playTime.get(i + "") + diff);
                        }
                        if (reward != null) {
                            if (ptp.playTime.get(i + "") >= reward.time * 60 * 60 * 1000) {
                                //getServer().getLogger().info("Gave player: " + player.getName() + " reward #" + i);
                                Boolean dropped = false;
                                for (Drop drop : reward.drops) {
                                    if (drop.willDrop()) {
                                        Integer slot = player.getInventory().firstEmpty();
                                        if (slot != -1) {
                                            player.getInventory().addItem(drop.item);
                                        } else {
                                            if (!unclaimed.containsKey(player.getName()))
                                                unclaimed.put(player.getName(), new ArrayList<ItemStack>());
                                            unclaimed.get(player.getName()).add(drop.item);
                                            dropped = true;
                                        }
                                        if (drop.item.getItemMeta().hasDisplayName()) {
                                            player.sendMessage(Text.colorize("&dYou received: &a" + drop.item.getAmount() + "x " + ChatColor.stripColor(drop.item.getItemMeta().getDisplayName())));
                                        } else {
                                            String type = drop.item.getType().toString().toLowerCase().replace("_", " ");
                                            type = type.substring(0, 1).toUpperCase() + type.substring(1, type.length());
                                            player.sendMessage(Text.colorize("&dYou received: &a" + drop.item.getAmount() + "x " + type));
                                        }
                                    }
                                }
                                if (dropped) {
                                    player.sendMessage(Text.colorize("&cBecause your inventory was full you did not receive an item, type /drop claim with enough inventory space to receive it"));
                                }
                                player.updateInventory();
                                ptp.playTime.put(i + "", 0);
                            }
                        }
                    }
                }
                if ((cman.curDate.getTime() > new MobKill(pl, cman).rewardDrop)) {
                    cman.ul();
                }
                cman.lastUpdate = System.currentTimeMillis();
            }
        }, 0L, 20L * 5);
    }

    @Override
    public void onDisable() {
        cman.savePlayTime();

        for (String p : unclaimed.keySet()) {
            List<ItemStack> uncl = unclaimed.get(p);
            if (uncl.size() == 0) {
                unclaimed.remove(p);
            }
        }

        File file = new File(getDataFolder(), "unclaimed.yml");
        cman.create(file);
        new SaveUnclaimed().saveFile(cman, unclaimed, file);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("drop")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 0) {
                    sender.sendMessage(Text.colorize("&d----------- Drop Help -----------"));
                    sender.sendMessage(Text.colorize("&d/drop time  &a- See when you'll receive rewards"));
                    sender.sendMessage(Text.colorize("&d/drop claim &a- Claim unreceived rewards"));
                    if (sender.hasPermission("drop.admin"))
                        sender.sendMessage(Text.colorize("&d/drop edit &a- Edit drops/rewards"));
                    sender.sendMessage(Text.colorize("&d-------------------------------"));
                    return true;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("edit") && sender.hasPermission("drop.admin")) {
                        Player player = (Player) sender;
                        player.openInventory(new MainMenu(pl, cman).getGUI());
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("claim")) {
                        if (unclaimed.containsKey(p.getName()) && unclaimed.get(p.getName()) != null && unclaimed.get(p.getName()).size() > 0) {
                            List<Integer> givenRewards = new ArrayList<>();
                            Boolean dropped = false;
                            for (int o = 0; o < unclaimed.get(p.getName()).size(); o++) {
                                ItemStack i = unclaimed.get(p.getName()).get(o);
                                Integer slot = p.getInventory().firstEmpty();
                                if (slot != -1) {
                                    p.getInventory().addItem(i);
                                    givenRewards.add(o);
                                } else dropped = true;
                                if (i.getItemMeta().hasDisplayName()) {
                                    p.sendMessage(Text.colorize("&dYou received: &a" + i.getAmount() + "x " + ChatColor.stripColor(i.getItemMeta().getDisplayName())));
                                } else {
                                    String type = i.getType().toString().toLowerCase().replace("_", " ");
                                    type = type.substring(0, 1).toUpperCase() + type.substring(1, type.length());
                                    p.sendMessage(Text.colorize("&dYou received: &a" + i.getAmount() + "x " + type));
                                }
                            }
                            Collections.sort(givenRewards);
                            Collections.reverse(givenRewards);
                            for (int o : givenRewards) {
                                unclaimed.get(p.getName()).remove(o);
                            }
                            if (dropped) {
                                p.sendMessage(Text.colorize("&cBecause your inventory was full you did not receive an item, type /drop claim with enough inventory space to receive it"));
                            } else {
                                p.sendMessage(Text.colorize("&2Succesfully claimed all remaining rewards"));
                            }
                        } else {
                            p.sendMessage(Text.colorize("&cYou do not have any unreceived rewards"));
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("time")) {
                        sender.sendMessage(Text.colorize("&d----------- Rewards -----------"));
                        if (cman.enabledRewards.size() == 0) {
                            sender.sendMessage(Text.colorize("&cThere are no rewards enabled"));
                        } else {
                            for (String reward : Arrays.asList("1", "2", "3", "4", "5")) {
                                if (cman.enabledRewards.contains(reward)) {
                                    Double complete = Double.parseDouble(cman.playTimers.get(p).playTime.get(reward) + "") / 3600000.0;
                                    Integer hours = (int) Math.floor(complete);
                                    Integer minutes = (int) Math.floor((complete - hours) * 60);
                                    String playTime = hours + ":" + minutes;
                                    if (playTime.split(":")[1].length() == 1) {
                                        String[] strings = playTime.split(":");
                                        playTime = strings[0] + ":0" + strings[1];
                                    }

                                    Double rewardComplete = cman.rewards.get(reward).time;
                                    Integer rewardHours = (int) Math.floor(rewardComplete);
                                    Integer rewardMinutes = (int) Math.floor((rewardComplete - rewardHours) * 60);
                                    String rewardTime = rewardHours + ":" + rewardMinutes;
                                    if (rewardTime.split(":")[1].length() == 1) {
                                        String[] strings = rewardTime.split(":");
                                        rewardTime = strings[0] + ":0" + strings[1];
                                    }

                                    sender.sendMessage(Text.colorize("&dReward #" + reward + ": &a" + playTime + "/" + rewardTime + " Hour(s)"));
                                }
                            }
                        }
                        sender.sendMessage(Text.colorize("&d------------------------------"));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
