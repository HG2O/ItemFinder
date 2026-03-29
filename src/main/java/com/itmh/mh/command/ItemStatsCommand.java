package com.itmh.mh.command;

import com.itmh.mh.ItemFinderPlugin;
import com.itmh.mh.search.SearchEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemStatsCommand implements CommandExecutor {

    private final ItemFinderPlugin plugin;

    public ItemStatsCommand(ItemFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemfinder.stats")) {
            sender.sendMessage(ChatColor.RED + "❌ Permission refusée.");
            return true;
        }

        sender.sendMessage(ChatColor.AQUA + "📊 Calcul des statistiques en cours...");

        // Snapshot sur le main thread
        List<SearchEngine.InventorySnapshot> snapshots = plugin.getSearchEngine().collectSnapshots();

        // Traitement async
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<String, Integer> counts = new HashMap<>();

            for (SearchEngine.InventorySnapshot snap : snapshots) {
                if (snap.contents() == null) continue;
                for (ItemStack item : snap.contents()) {
                    if (item == null || item.getType().isAir()) continue;
                    counts.merge(item.getType().name(), item.getAmount(), Integer::sum);
                }
            }

            // Top 10 par quantité décroissante
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
            sorted.sort((a, b) -> b.getValue() - a.getValue());
            List<Map.Entry<String, Integer>> top = sorted.subList(0, Math.min(10, sorted.size()));

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (counts.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "Aucun item trouvé sur le serveur.");
                    return;
                }
                sender.sendMessage(ChatColor.AQUA + "━━━ 📊 Top items sur le serveur ━━━");
                int rank = 1;
                for (Map.Entry<String, Integer> entry : top) {
                    String medal = switch (rank) {
                        case 1 -> "🥇";
                        case 2 -> "🥈";
                        case 3 -> "🥉";
                        default -> ChatColor.GRAY + "#" + rank + " ";
                    };
                    sender.sendMessage(medal + " " + ChatColor.WHITE + entry.getKey()
                            + ChatColor.GRAY + " — " + ChatColor.YELLOW + entry.getValue() + " unité(s)");
                    rank++;
                }
                sender.sendMessage(ChatColor.GRAY + "Types distincts scannés : "
                        + ChatColor.WHITE + counts.size());
            });
        });

        return true;
    }
}