package com.itmh.mh.listener;

import com.itmh.mh.ItemFinderPlugin;
import com.itmh.mh.model.ItemSearchResult;
import com.itmh.mh.search.SearchEngine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AlertManager {

    private final ItemFinderPlugin plugin;

    public AlertManager(ItemFinderPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(int intervalSeconds) {
        long ticks = Math.max(20L, intervalSeconds * 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> alertItems = plugin.getConfig().getStringList("alert-items");
                if (alertItems.isEmpty()) return;

                // Snapshot sur le main thread (on est dans runTaskTimer = main thread)
                List<SearchEngine.InventorySnapshot> snapshots =
                        plugin.getSearchEngine().collectSnapshots();

                // Matching en async
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    for (String itemName : alertItems) {
                        List<ItemSearchResult> results =
                                plugin.getSearchEngine().search(snapshots, itemName.toLowerCase());

                        if (results.isEmpty()) continue;

                        // Notification sur le main thread
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            // Compte total
                            int total = results.stream().mapToInt(ItemSearchResult::getCount).sum();

                            String msg = ChatColor.RED + "⚠ [ItemFinder] "
                                    + ChatColor.YELLOW + total + "x "
                                    + ChatColor.WHITE + itemName.toUpperCase()
                                    + ChatColor.YELLOW + " détecté(s) sur le serveur !";

                            // Notifie tous les joueurs avec la permission
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission("itemfinder.alerts")) {
                                    p.sendMessage(msg);
                                }
                            }

                            // Log console
                            plugin.getLogger().warning("[ALERT] " + total + "x "
                                    + itemName.toUpperCase() + " détecté(s) sur le serveur !");
                        });
                    }
                });
            }
        }.runTaskTimer(plugin, ticks, ticks);
    }
}