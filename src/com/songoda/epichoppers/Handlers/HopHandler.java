package com.songoda.epichoppers.Handlers;

import com.songoda.arconix.Arconix;
import com.songoda.epichoppers.EpicHoppers;
import com.songoda.epichoppers.Utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

import org.apache.commons.lang.StringUtils;

/**
 * Created by songoda on 3/14/2017.
 */
public class HopHandler {

    EpicHoppers plugin = EpicHoppers.pl();

    public HopHandler() {
        try {
            hopperCleaner();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> hopperRunner(), plugin.getConfig().getLong("settings.Hop-Tick"), plugin.getConfig().getLong("settings.Hop-Tick"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    private void hopperCleaner() {
        try {
            if (plugin.dataFile.getConfig().contains("data.sync")) {
                ConfigurationSection cs = plugin.dataFile.getConfig().getConfigurationSection("data.sync");
                for (String key : cs.getKeys(false)) {
                    if (Arconix.pl().serialize().unserializeLocation(key).getWorld() != null) {
                        Block b = Arconix.pl().serialize().unserializeLocation(key).getBlock();
                        if (b == null || !(b.getState() instanceof Hopper)) {
                            plugin.dataFile.getConfig().getConfigurationSection("data.sync").set(key, null);
                            plugin.getLogger().info("EpicHoppers Removing non-hopper entry: " + key.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    private void hopperRunner() {
        try {
            if (plugin.dataFile.getConfig().contains("data.sync")) {

                ConfigurationSection cs = plugin.dataFile.getConfig().getConfigurationSection("data.sync");
                Set<Entity> metaitems = new HashSet<>();

                for (String key : cs.getKeys(false)) {

                    if (plugin.dataFile.getConfig().contains("data.sync." + key + ".block")) {
                        Location hopperloc = Arconix.pl().serialize().unserializeLocation(key);
                        if (hopperloc == null)
                            plugin.dataFile.getConfig().set("data.sync." + key, null);
                        else {
                            int x = hopperloc.getBlockX() >> 4;
                            int z = hopperloc.getBlockZ() >> 4;

                            try {
                                if (!hopperloc.getWorld().isChunkLoaded(x, z)) {
                                    continue;
                                }
                            } catch (Exception e) {
                                continue;
                            }
                            Block b = hopperloc.getBlock();
                            if (b != null && b.getState() instanceof Hopper) {
                                Location dest = Arconix.pl().serialize().unserializeLocation(plugin.dataFile.getConfig().getString("data.sync." + key + ".block"));
                                int destx = hopperloc.getBlockX() >> 4;
                                int destz = hopperloc.getBlockZ() >> 4;
                                if (!dest.getWorld().isChunkLoaded(destx, destz)) {
                                    continue;
                                }
                                Block b2 = dest.getBlock();

                                int amt = plugin.getConfig().getInt("settings.levels.Level-" + (plugin.dataFile.getConfig().getInt("data.sync." + key + ".level")) + ".Amount");

                                if (!(b2.getState() instanceof InventoryHolder) && !(b2.getType().equals(Material.ENDER_CHEST))) {
                                    plugin.dataFile.getConfig().set("data.sync." + Arconix.pl().serialize().serializeLocation(b) + ".block", null);
                                }
                                org.bukkit.block.Hopper hopper = (org.bukkit.block.Hopper) b.getState();

                                if (plugin.getConfig().contains("settings.levels.Level-" + (plugin.dataFile.getConfig().getInt("data.sync." + key + ".level")) + ".Suction")) {
                                    int suck = plugin.getConfig().getInt("settings.levels.Level-" + (plugin.dataFile.getConfig().getInt("data.sync." + key + ".level")) + ".Suction");
                                    double radius = suck + .5;
                                    Collection<Entity> nearbyEntite = b.getLocation().getWorld().getNearbyEntities(b.getLocation().add(0.5, 0.5, 0.5), radius, radius, radius);
                                    for (Entity e : nearbyEntite) {
                                        if (e instanceof Item && e.getTicksLived() > 10) { //e.getTicksLived() > 10 accomplishes the same thing as the commented out map check, and fixes stacks not getting picked up when lot of new items are continually being added (grinders)
                                            if (e.getLocation().getBlock().getType() != Material.HOPPER) {
                                                ItemStack hopItem = ((Item) e).getItemStack().clone();
                                                if (!hopItem.getType().name().contains("SHULKER_BOX")) {
                                                    if (hopItem.hasItemMeta() && hopItem.getItemMeta().hasDisplayName()) {
                                                        if (StringUtils.substring(hopItem.getItemMeta().getDisplayName(), 0, 3).equals("***")) {
                                                            continue; //Compatibility with Shop plugin: https://www.spigotmc.org/resources/shop-a-simple-intuitive-shop-plugin.9628/
                                                        }
                                                    }
                                                    if (!e.hasMetadata("grabbed")) {
                                                        ItemStack item = ((Item) e).getItemStack();
                                                        if (canHop(hopper.getInventory(), item, 1)) {
                                                            ((Item) e).setPickupDelay(999);
                                                            e.setMetadata("grabbed", new FixedMetadataValue(plugin, ""));
                                                            metaitems.add(e);
                                                            float xx = (float) (0 + (Math.random() * .3));
                                                            float yy = (float) (0 + (Math.random() * .3));
                                                            float zz = (float) (0 + (Math.random() * .3));
                                                            Arconix.pl().packetLibrary.getParticleManager().broadcastParticle(e.getLocation(), xx, yy, zz, 0, "FLAME", 5);
                                                            e.remove();
                                                            hopper.getInventory().addItem(hopItem);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }

                                ItemStack[] is = hopper.getInventory().getContents();

                                List<ItemStack> owhite = new ArrayList<>();
                                if (plugin.dataFile.getConfig().contains("data.sync." + key + ".whitelist")) {
                                    owhite = (List<ItemStack>) plugin.dataFile.getConfig().getList("data.sync." + key + ".whitelist");
                                }
                                List<ItemStack> oblack = new ArrayList<>();
                                if (plugin.dataFile.getConfig().contains("data.sync." + key + ".blacklist")) {
                                    oblack = (List<ItemStack>) plugin.dataFile.getConfig().getList("data.sync." + key + ".blacklist");
                                }

                                int num = 0;
                                while (num != 5) {
                                    ItemStack it = null;
                                    if (is[num] != null) {
                                        it = is[num].clone();
                                        it.setAmount(1);
                                    }
                                    if (is[num] != null && !owhite.isEmpty() && !owhite.contains(it)) {
                                        doBlacklist(hopper, is[num].clone(), is, amt, num);
                                    } else if (is[num] != null && !oblack.contains(it)) {
                                        int numm = addItem(hopper, b2, is[num], is, amt, num);
                                        if (numm != 10)
                                            num = numm;
                                    } else if (is[num] != null && oblack.contains(it)) {
                                        doBlacklist(hopper, is[num].clone(), is, amt, num);
                                    }
                                    num++;
                                }
                            } else {
                                plugin.dataFile.getConfig().getConfigurationSection("data.sync").set(key, null);
                                plugin.getLogger().info("EpicHoppers Removing non-hopper entry: " + key.toString());
                            }
                        }
                    }
                }
                for (Entity e : metaitems) {
                    e.removeMetadata("grabbed", plugin);
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    private void doBlacklist(org.bukkit.block.Hopper hopper, ItemStack item, ItemStack[] isS, int amt, int place) {
        try {
            Location loc = hopper.getLocation();
            Block b = loc.getBlock();
            if (plugin.dataFile.getConfig().contains("data.sync." + Arconix.pl().serialize().serializeLocation(b) + ".black")) {
                if (b != null && b.getState() instanceof Hopper) {
                    Location dest = Arconix.pl().serialize().unserializeLocation(plugin.dataFile.getConfig().getString("data.sync." + Arconix.pl().serialize().serializeLocation(b) + ".black"));
                    int destx = loc.getBlockX() >> 4;
                    int destz = loc.getBlockZ() >> 4;
                    if (!dest.getWorld().isChunkLoaded(destx, destz)) {
                        return;
                    }
                    Block b2 = dest.getBlock();

                    addItem(hopper, b2, item, isS, amt, place);
                }
            }
        } catch(Exception e){
            Debugger.runReport(e);
        }
    }

    private int addItem(org.bukkit.block.Hopper hopper, Block b2, ItemStack is, ItemStack[] isS, int amt, int place) {
        try {
            ItemStack it = null;
            if (is != null) {
                it = is.clone();
                it.setAmount(1);
            }

            String key = Arconix.pl().serialize().serializeLocation(hopper.getLocation());
            List<Material> ovoid = new ArrayList<>();

            if (plugin.dataFile.getConfig().contains("data.sync." + key + ".void")) {
                for (ItemStack iss : (List<ItemStack>) plugin.dataFile.getConfig().getList("data.sync." + key + ".void")) {
                    ovoid.add(iss.getType());
                }
            }

            if (is.getType() != Material.AIR) {
                ItemStack item = is;
                ItemStack newItem = is.clone();

                if ((item.getAmount() - amt) <= 0) {
                    amt = item.getAmount();
                }
                if ((item.getAmount() - amt) >= 1) {
                    newItem.setAmount(newItem.getAmount() - amt);
                    is = newItem.clone();
                } else {
                    is = null;
                }

                newItem.setAmount(amt);
                InventoryHolder ih = null;
                if (!b2.getType().equals(Material.ENDER_CHEST)) {
                    ih = (InventoryHolder) b2.getState();
                }

                if (b2.getType().equals(Material.ENDER_CHEST)) {
                    try {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(plugin.dataFile.getConfig().getString("data.enderTracker." + Arconix.pl().serialize().serializeLocation(b2))));
                        if (op.isOnline()) {
                            if (canHop(op.getPlayer().getEnderChest(), newItem, amt)) {
                                if (!ovoid.contains(it.getType())) {
                                    op.getPlayer().getEnderChest().addItem(newItem);
                                }
                                isS[place] = is;
                                hopper.getInventory().setContents(isS);
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    if (canHop(ih.getInventory(), newItem, amt)) {
                        if (b2.getType() != Material.BREWING_STAND) {
                            if (b2.getType() == Material.FURNACE || b2.getType() == Material.BURNING_FURNACE) {
                                FurnaceInventory fi = (FurnaceInventory) ih.getInventory();
                                int amtt = 0;
                                boolean dont = false;
                                if (fi.getSmelting() != null) {
                                    amtt = fi.getSmelting().getAmount();
                                    if (fi.getSmelting().getType() != newItem.getType()) {
                                        dont = true;
                                    } else {
                                        if (fi.getSmelting().getAmount() == fi.getSmelting().getMaxStackSize()) {
                                            dont = true;
                                        }
                                    }
                                }
                                if (!dont) {
                                    if (amtt + newItem.getAmount() <= 64) {
                                        if (!ovoid.contains(it.getType())) {
                                            ih.getInventory().addItem(newItem);
                                        }
                                        isS[place] = is;
                                        hopper.getInventory().setContents(isS);
                                    }
                                }
                            } else {
                                if (!ovoid.contains(it.getType())) {
                                    ih.getInventory().addItem(newItem);
                                }
                                isS[place] = is;
                                hopper.getInventory().setContents(isS);
                            }
                        }
                    }
                }
                return 4;
            }
            return 10;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return 0;
    }

    public boolean canHop(Inventory i, ItemStack item, int hop) {
        try {
            if (i.firstEmpty() != -1) {
                return true;
            }
            boolean can = false;
            for (ItemStack it : i.getContents()) {
                if (it == null) {
                    can = true;
                    break;
                } else {
                    if (it.isSimilar(item)) {
                        if ((it.getAmount() + hop) <= it.getMaxStackSize()) {
                            can = true;
                            break;
                        }
                    }
                }
            }
            return can;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

}
