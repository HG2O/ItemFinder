package com.itmh.mh.command;

import com.itmh.mh.ItemFinderPlugin;
import com.itmh.mh.gui.ResultsGUI;
import com.itmh.mh.model.ItemSearchResult;
import com.itmh.mh.search.SearchEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FindItemCommand implements CommandExecutor, TabCompleter {

    private final ItemFinderPlugin plugin;

    public FindItemCommand(ItemFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est réservée aux joueurs.");
            return true;
        }

        if (!player.hasPermission("itemfinder.use")) {
            player.sendMessage(ChatColor.RED + "❌ Tu n'as pas la permission d'utiliser cette commande.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage : /finditem <nom ou matériau>");
            return true;
        }

        // ── Cooldown ──
        if (plugin.getCooldownManager().isOnCooldown(player)) {
            int remaining = plugin.getCooldownManager().getRemainingSeconds(player);
            player.sendMessage(ChatColor.RED + "⏳ Attends encore " + ChatColor.YELLOW + remaining + "s"
                    + ChatColor.RED + " avant de relancer une recherche.");
            return true;
        }
        plugin.getCooldownManager().setCooldown(player);

        String query = String.join(" ", args);
        player.sendMessage(ChatColor.AQUA + "🔍 Recherche de " + ChatColor.WHITE + "\"" + query + "\""
                + ChatColor.AQUA + " en cours...");

        runSearch(plugin, player, query);
        return true;
    }

    /**
     * Lance une recherche complète.
     * Doit être appelé depuis le main thread (collecte des snapshots).
     * Réutilisé par le bouton Refresh de la GUI.
     */
    public static void runSearch(ItemFinderPlugin plugin, Player player, String query) {
        List<SearchEngine.InventorySnapshot> snapshots = plugin.getSearchEngine().collectSnapshots();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<ItemSearchResult> results = plugin.getSearchEngine().search(snapshots, query);

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (results.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "❌ Aucun item trouvé pour "
                            + ChatColor.WHITE + "\"" + query + "\"");
                    return;
                }
                player.sendMessage(ChatColor.GREEN + "✅ " + ChatColor.WHITE
                        + results.size() + " résultat(s) trouvé(s) !");
                ResultsGUI.open(player, results, query, 0);
            });
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList(
                    "diamond_sword", "netherite_sword", "diamond_pickaxe",
                    "netherite_pickaxe", "enchanted", "sharpness", "protection"
            );
        }
        return List.of();
    }
}