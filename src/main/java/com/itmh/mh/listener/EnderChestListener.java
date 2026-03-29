package com.itmh.mh.listener;

import com.itmh.mh.ItemFinderPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderChestListener implements Listener {

    private final ItemFinderPlugin plugin;

    // Cache des ender chests des joueurs hors-ligne
    private static final Map<UUID, ItemStack[]> enderChestCache = new HashMap<>();

    public EnderChestListener(ItemFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        enderChestCache.put(player.getUniqueId(), player.getEnderChest().getContents().clone());
    }

    public static Map<UUID, ItemStack[]> getEnderChestCache() {
        return enderChestCache;
    }
}