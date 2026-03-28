package com.itmh.dragon;

import com.itmh.dragon.command.FindItemCommand;
import com.itmh.dragon.listener.EnderChestListener;
import com.itmh.dragon.listener.GUIListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFinderPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        FindItemCommand findItemCommand = new FindItemCommand(this);
        getCommand("finditem").setExecutor(findItemCommand);
        getCommand("finditem").setTabCompleter(findItemCommand);

        getServer().getPluginManager().registerEvents(new EnderChestListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getLogger().info("ItemFinder activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemFinder désactivé !");
    }
}