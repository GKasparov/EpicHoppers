package com.songoda.epichoppers.Hooks;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.songoda.epichoppers.EpicHoppers;
import com.songoda.epichoppers.Utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class FactionsHook implements Hooks {

    private EpicHoppers plugin = EpicHoppers.pl();
    private String pluginName = "Factions";

    FactionsHook() {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
            plugin.hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
            if (!plugin.hooks.hooksFile.getConfig().contains("hooks." + pluginName) || plugin.hooks.hooksFile.getConfig().getBoolean("hooks." + pluginName)) {
                if (plugin.getServer().getPluginManager().getPlugin("FactionsFramework") != null) {
                    try {
                        Class.forName("com.massivecraft.factions.FPlayer");
                        plugin.hooks.FactionsHook = this;
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (p.hasPermission(plugin.getDescription().getName() + ".bypass")) {
                return true;
            } else {
                MPlayer mp = MPlayer.get(p);

                Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));

                if (mp.getFaction().equals(faction)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

    @Override
    public boolean isInClaim(String id, Location location) {
        Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));

        if (faction.getId().equals(id)) {
            return true;
        }
        return false;
    }

    @Override
    public String getClaimId(String name) {
        try {
            Faction faction = FactionColl.get().getByName(name);

            return faction.getId();
        } catch (Exception e) {
        }
        return null;
    }

}
