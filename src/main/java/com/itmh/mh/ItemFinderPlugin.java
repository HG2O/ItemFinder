package com.itmh.mh;

import com.itmh.mh.command.FindItemCommand;
import com.itmh.mh.command.ItemAlertCommand;
import com.itmh.mh.command.ItemStatsCommand;
import com.itmh.mh.listener.AlertManager;
import com.itmh.mh.listener.EnderChestListener;
import com.itmh.mh.listener.GUIListener;
import com.itmh.mh.search.SearchEngine;
import com.itmh.mh.util.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFinderPlugin extends JavaPlugin {

    private SearchEngine searchEngine;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        searchEngine    = new SearchEngine(this);
        cooldownManager = new CooldownManager(getConfig().getInt("cooldown-seconds", 5));

        // /finditem
        FindItemCommand findItemCommand = new FindItemCommand(this);
        getCommand("finditem").setExecutor(findItemCommand);
        getCommand("finditem").setTabCompleter(findItemCommand);

        // /itemstats
        getCommand("itemstats").setExecutor(new ItemStatsCommand(this));

        // /itemalert
        ItemAlertCommand alertCommand = new ItemAlertCommand(this);
        getCommand("itemalert").setExecutor(alertCommand);
        getCommand("itemalert").setTabCompleter(alertCommand);

        // Listeners
        getServer().getPluginManager().registerEvents(new EnderChestListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        // Tâche périodique des alertes
        int interval = getConfig().getInt("alert-interval-seconds", 60);
        new AlertManager(this).start(interval);

        getLogger().info("ItemFinder activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemFinder désactivé !");
    }

    public SearchEngine getSearchEngine()       { return searchEngine; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
}