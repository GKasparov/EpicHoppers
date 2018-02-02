package com.songoda.epichoppers.Utils;

import com.songoda.arconix.Arconix;
import com.songoda.epichoppers.Lang;
import com.songoda.epichoppers.EpicHoppers;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by songoda on 2/24/2017.
 */
public class Methods {

    public static boolean isSync(Player p) {
        try {
            if (p.getItemInHand().hasItemMeta()) {
                if (p.getItemInHand().getType() != Material.AIR) {
                    if (p.getItemInHand().getItemMeta().hasLore()) {
                        for (String str : p.getItemInHand().getItemMeta().getLore()) {
                            if (str.equals(Arconix.pl().format().formatText("&7Sync Touch")) || str.equals(Arconix.pl().format().formatText("&aSync Touch"))) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

    public static ItemStack getGlass() {
        try {
            EpicHoppers plugin = EpicHoppers.pl();
            return Arconix.pl().getGUI().getGlass(plugin.getConfig().getBoolean("settings.Rainbow-Glass"), plugin.getConfig().getInt("settings.Glass-Type-1"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        try {
            EpicHoppers plugin = EpicHoppers.pl();
            if (type)
                return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-2"));
            else
                return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-3"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static String formatName(int level, boolean full) {
        try {
            String name = Lang.NAME_FORMAT.getConfigValue(level);

            String info = "";
            if (full) {
                info += Arconix.pl().format().convertToInvisibleString(level + ":");
            }

            return info + Arconix.pl().format().formatText(name);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }


    public static void tpPlayer(Player p, Block b) {
        try {
            Methods.doParticles(p, b.getLocation());
            EpicHoppers plugin = EpicHoppers.pl();
            Block next = b;
            int num = 1;
            while (plugin.dataFile.getConfig().contains("data.sync." + Arconix.pl().serialize().serializeLocation(next) + ".block") && num != 15) {
                next = Arconix.pl().serialize().unserializeLocation(plugin.dataFile.getConfig().getString("data.sync." + Arconix.pl().serialize().serializeLocation(next) + ".block")).getBlock();
                Location location = next.getLocation();
                location.setX(location.getX() + 0.5);
                location.setZ(location.getZ() + 0.5);
                location.setY(location.getY() + 1);
                location.setPitch(p.getLocation().getPitch());
                location.setDirection(p.getLocation().getDirection());
                p.teleport(location);
                next = p.getLocation().subtract(0, 0.5, 0).getBlock();
                num++;
            }
            Methods.doParticles(p, next.getLocation());
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public static void doParticles(Player p, Location location) {
        try {
            EpicHoppers plugin = EpicHoppers.pl();
            Location loc = location;
            loc.setX(loc.getX() + .5);
            loc.setY(loc.getY() + .5);
            loc.setZ(loc.getZ() + .5);
            if (!plugin.v1_8 && !plugin.v1_7) {
                p.getWorld().spawnParticle(org.bukkit.Particle.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), loc, 200, .5, .5, .5);
            } else {
                p.getWorld().spigot().playEffect(loc, org.bukkit.Effect.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), 1, 0, (float) 1, (float) 1, (float) 1, 1, 200, 10);
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

}
