package com.songoda.epichoppers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {

    PREFIX("prefix", "&7[&6EpicHoppers&7]"),

    UPGRADE_MESSAGE("Upgrade-message", "&7You successfully upgraded this hopper to &6level {LEVEL}&7!"),

    NO_ROOM("No-room", "&7You do not have space in your inventory for this."),

    NAME_FORMAT("Name-format", "&eLevel {LEVEL} &fHopper"),

    SYNC_NEXT("Sync-next", "&7Click another hopper or container to sync."),
    SYNC_SELF("Sync-self", "&cYou can't sync a hopper to itself."),
    SYNC_TIMEOUT("Sync-timeout", "&cSyncing timed out."),
    SYNC_OUT_OF_RANGE("Sync-out-of-range", "&cThis block is out of your hoppers range."),
    SYNC_DID_NOT_PLACE("Sync-did-not-place", "&cSorry! You need to have placed this hopper to sync things to it."),
    SYNC_SUCCESS("Sync-success", "&aSynchronization Successful."),
    UNSYNC("Unsync", "&7You have desynchronized this hopper."),

    XPTITLE("Xp-upgrade-title", "&aUpgrade with XP"),
    XPLORE("Xp-upgrade-lore", "&7Cost: &a{COST} Levels"),

    INFOTITLE("Info-title", "&aFilter Guide"),
    INFOLORE("Info-lore", "&7Items placed in the top left|&7space will be whitelisted.||&7Items placed in the right|&7will be void.||&7Items placed in the bottom left|&7will be blacklisted.||&cUsing the whitelist will disable|&cboth the blacklist and the void."),

    ECOTITLE("Eco-upgrade-title", "&aUpgrade with ECO"),
    ECOLORE("Eco-upgrade-lore", "&7Cost: &a${COST}"),

    SYNCLORE("Sync-lore", "|&7Left-Click then click a another|&7hopper or chest to sync!||&7Right-Click to desync."),

    TOO_MANY("Too-many", "&cYou can only place {AMOUNT} hoppers per chunk..."),

    PEARL_TITLE("Pearl-title", "&6Click to Teleport"),
    PEARLLORE("Pearl-lore", "|&7Left-Click to teleport to|&7the end of the chain.||&7Right-Click to toggle walk|&7on teleport."),


    FILTER_TITLE("Filter-title", "&cClick to Filter"),
    FILTERLORE("Filter-lore", "|&7This allows you to choose|&7which items go where."),

    WALKTELE_ENABLE("Walktele-enable", "Walk on teleporting has been enabled for this hopper."),
    WALKTELE_DISABLE("Walktele-disable", "Walk on teleporting has been disabled for this hopper."),

    SYNC_HOPPER("Sync-Hopper", "&6Click to Sync This Hopper"),
    BSYNC_HOPPER("BSync-Hopper", "&6Click to Sync Rejected Items"),

    LEVEL("Level", "&6Hopper Level &7{LEVEL}"),
    NEXT_LEVEL("Next-Level", "&6Next Level &7{LEVEL}"),
    NEXT_SUCTION("Next-Suction", "&7Suction: &6{SUCTION}"),
    NEXT_RANGE("Next-Range", "&7Range: &6{RANGE}"),
    NEXT_AMOUNT("Next-Amount", "&7Amount: &6{AMOUNT}"),

    MAXED("Maxed", "&6This hopper is maxed out."),
    CANT_AFFORD("Cant-afford", "&cYou cannot afford this upgrade."),

    NEXT("Next", "&9Next"),
    BACK("Back", "&9Back"),

    NO_PERMS("No-perms", "&cYou do not have permission to do this."),
    SYNC_CHEST("Sync-chest", "&7You have synchronized your &9{NAME} &7with this chest."),
    UNSYNC_CHEST("Unsync-chest", "&7You have desynchronized your &9{NAME} &7with this chest."),

    WHITELIST("Whitelist", "&f&lWhite List"),
    BLACKLIST("Blacklist", "&8&lBlack List"),
    VOID("Void", "&5&lVoid"),

    ONLY_ONE("Only-One", "&cYou may only place a single item at a time.");

    private String path;
    private String def;
    private static FileConfiguration LANG;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(final FileConfiguration config) {
        LANG = config;
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path;
    }

    public String getConfigValue() {
        return getConfigValue(null);
    }

    public String getConfigValue(int arg) {
        return getConfigValue(Integer.toString(arg));
    }
        public String getConfigValue(String arg) {
        String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

        if (arg != null) {
            value = value.replace("{NAME}", arg);
            value = value.replace("{LEVEL}", arg);
            value = value.replace("{COST}", arg);
            value = value.replace("{RANGE}", arg);
            value = value.replace("{AMOUNT}", arg);
            value = value.replace("{SUCTION}", arg);
        }
        return value;
    }
}
