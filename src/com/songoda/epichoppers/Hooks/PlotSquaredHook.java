package com.songoda.epichoppers.Hooks;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.songoda.epichoppers.EpicHoppers;
import com.songoda.epichoppers.Utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by songoda on 3/17/2017.
 */
public class PlotSquaredHook implements Hooks {

    private EpicHoppers plugin = EpicHoppers.pl();
    private String pluginName = "PlotSquared";

    PlotSquaredHook() {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
            plugin.hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
            if (!plugin.hooks.hooksFile.getConfig().contains("hooks." + pluginName) || plugin.hooks.hooksFile.getConfig().getBoolean("hooks." + pluginName)) {
                plugin.hooks.PlotSquaredHook = this;
            }
        }
    }

    final Plugin plotsquared = Bukkit.getServer().getPluginManager().getPlugin("PlotSquared");

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (p.hasPermission(plugin.getDescription().getName() + ".bypass")) {
                return true;
            } else {
                PlotAPI api = new PlotAPI();
                if (api.getPlot(location) != null) {
                    if (api.isInPlot(p)) {
                        if (api.getPlot(p) == api.getPlot(location)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return true;
    }

    @Override
    public boolean isInClaim(String id, Location location) {
        return false;
    }

    @Override
    public String getClaimId(String name) {
        return null;
    }
}
