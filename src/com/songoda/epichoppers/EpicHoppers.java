package com.songoda.epichoppers;

import java.io.IOException;


import com.songoda.arconix.Arconix;
import com.songoda.epichoppers.API.MCUpdate;
import com.songoda.epichoppers.API.SyncCraftAPI;
import com.songoda.epichoppers.Events.*;
import com.songoda.epichoppers.Handlers.CommandHandler;
import com.songoda.epichoppers.Handlers.EnchantmentHandler;
import com.songoda.epichoppers.Handlers.HopHandler;
import com.songoda.epichoppers.Hooks.HookHandler;
import com.songoda.epichoppers.Utils.ConfigWrapper;
import com.songoda.epichoppers.Utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public final class EpicHoppers extends JavaPlugin implements Listener {
    public static CommandSender console = Bukkit.getConsoleSender();

    public boolean v1_12 = Bukkit.getServer().getClass().getPackage().getName().contains("1_12");
    public boolean v1_7 = Bukkit.getServer().getClass().getPackage().getName().contains("1_7");
    public boolean v1_8 = Bukkit.getServer().getClass().getPackage().getName().contains("1_8");

    public Map<Player, String> inShow = new HashMap<>();
    public Map<Player, String> inFilter = new HashMap<>();

    public HookHandler hooks;
    public SettingsManager sm;

    public References references = null;
    private ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");
    public ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");

    public Map<Player, Block> sync = new HashMap<>();
    public Map<Player, Block> bsync = new HashMap<>();
    public Map<Player, Block> lastBlock = new HashMap<>();

    public Map<Player, Date> lastTp = new HashMap<>();

    public EnchantmentHandler enchant;

    public SyncCraftAPI api;

    public void onEnable() {
        console.sendMessage(Arconix.pl().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().format().formatText("&7EpicHoppers " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(Arconix.pl().format().formatText("&7Action: &aEnabling&7..."));
        Bukkit.getPluginManager().registerEvents(this, this);

        api = new SyncCraftAPI();

        sm = new SettingsManager();
        setupConfig();
        loadDataFile();
        enchant = new EnchantmentHandler();

        langFile.createNewFile("Loading language file", "EpicHoppers language file");
        loadLanguageFile();
        references = new References();

        hooks = new HookHandler();
        hooks.hook();

        new HopHandler();

        this.getCommand("EpicHoppers").setExecutor(new CommandHandler(this));

        getServer().getPluginManager().registerEvents(new HopperListeners(), this);
        getServer().getPluginManager().registerEvents(new BlockListeners(), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        getServer().getPluginManager().registerEvents(new LoginListeners(), this);
        getServer().getPluginManager().registerEvents(new MovementListeners(), this);

        try {
            new MCUpdate(this, true);
        } catch (IOException e) {
            Bukkit.getLogger().info(references.getPrefix() + "Failed initialize MCUpdate");
        }
        console.sendMessage(Arconix.pl().format().formatText("&a============================="));
    }

    public void onDisable() {
        console.sendMessage(Arconix.pl().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().format().formatText("&7EpicHoppers " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(Arconix.pl().format().formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Arconix.pl().format().formatText("&a============================="));
        dataFile.saveConfig();
    }

    private void setupConfig() {
        sm.updateSettings();

        if (!getConfig().contains("settings.levels.Level-1")) {
            getConfig().addDefault("settings.levels.Level-1.Range", 10);
            getConfig().addDefault("settings.levels.Level-1.Amount", 1);
            getConfig().addDefault("settings.levels.Level-1.Cost-xp", 20);
            getConfig().addDefault("settings.levels.Level-1.Cost-eco", 5000);

            getConfig().addDefault("settings.levels.Level-2.Range", 20);
            getConfig().addDefault("settings.levels.Level-2.Amount", 2);
            getConfig().addDefault("settings.levels.Level-2.Cost-xp", 25);
            getConfig().addDefault("settings.levels.Level-2.Cost-eco", 7500);

            getConfig().addDefault("settings.levels.Level-3.Range", 30);
            getConfig().addDefault("settings.levels.Level-3.Amount", 3);
            getConfig().addDefault("settings.levels.Level-3.Suction", 1);
            getConfig().addDefault("settings.levels.Level-3.Cost-xp", 30);
            getConfig().addDefault("settings.levels.Level-3.Cost-eco", 10000);

            getConfig().addDefault("settings.levels.Level-4.Range", 40);
            getConfig().addDefault("settings.levels.Level-4.Amount", 4);
            getConfig().addDefault("settings.levels.Level-4.Suction", 2);
            getConfig().addDefault("settings.levels.Level-4.Cost-xp", 35);
            getConfig().addDefault("settings.levels.Level-4.Cost-eco", 12000);

            getConfig().addDefault("settings.levels.Level-5.Range", 50);
            getConfig().addDefault("settings.levels.Level-5.Amount", 5);
            getConfig().addDefault("settings.levels.Level-5.Suction", 3);
            getConfig().addDefault("settings.levels.Level-5.Cost-xp", 40);
            getConfig().addDefault("settings.levels.Level-5.Cost-eco", 15000);
        }

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void loadDataFile() {
        dataFile.getConfig().options().copyDefaults(true);
        dataFile.saveConfig();
    }

    public void reload() {
        langFile.createNewFile("Loading language file", "EpicHoppers language file");
        hooks.hooksFile.createNewFile("Loading Hooks File", "EpicHoppers Spawners File");
        loadLanguageFile();
        references = new References();
        reloadConfig();
        saveConfig();
    }

    private void loadLanguageFile() {
        Lang.setFile(langFile.getConfig());

        for (final Lang value : Lang.values()) {
            langFile.getConfig().addDefault(value.getPath(), value.getDefault());
        }

        langFile.getConfig().options().copyDefaults(true);
        langFile.saveConfig();
    }

    public static EpicHoppers pl() {
        return (EpicHoppers) Bukkit.getServer().getPluginManager().getPlugin("EpicHoppers");
    }

    public SyncCraftAPI getApi() {
        return api;
    }
}
