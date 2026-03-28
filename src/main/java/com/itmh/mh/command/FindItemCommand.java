package com.itmh.dragon.command;

import com.itmh.dragon.ItemFinderPlugin;
import com.itmh.dragon.gui.ResultsGUI;
import com.itmh.dragon.model.ItemSearchResult;
import com.itmh.dragon.search.SearchEngine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FindItemCommand implements CommandExecutor, TabCompleter {

    private final ItemFinderPlugin plugin;
    private final SearchEngine searchEngine;

    public FindItemCommand(ItemFinderPlugin plugin) {
        this.plugin = plugin;
        this.searchEngine = new SearchEngine(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est réservée aux joueurs.");
            return true;
        }

        if (!player.hasPermission("itemfinder.use")) {
            player.sendMessage(Component.text("❌ Tu n'as pas la permission d'utiliser cette commande.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage : /finditem <nom ou matériau>", NamedTextColor.YELLOW));
            return true;
        }

        String query = String.join(" ", args);

        player.sendMessage(Component.text("🔍 Recherche de ", NamedTextColor.AQUA)
                .append(Component.text("\"" + query + "\"", NamedTextColor.WHITE))
                .append(Component.text(" en cours...", NamedTextColor.AQUA)));

        // Recherche asynchrone pour ne pas freezer le serveur
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<ItemSearchResult> results = searchEngine.search(query);

            // Retour sur le thread principal pour ouvrir l'inventaire
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (results.isEmpty()) {
                    player.sendMessage(Component.text("❌ Aucun item trouvé pour ", NamedTextColor.RED)
                            .append(Component.text("\"" + query + "\"", NamedTextColor.WHITE)));
                    return;
                }

                player.sendMessage(Component.text("✅ ", NamedTextColor.GREEN)
                        .append(Component.text(results.size() + " résultat(s) trouvé(s) !", NamedTextColor.WHITE)));

                ResultsGUI.open(player, results, query, 0);
            });
        });

        return true;
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