package com.itmh.mh.listener;

import com.itmh.mh.ItemFinderPlugin;
import com.itmh.mh.command.FindItemCommand;
import com.itmh.mh.gui.ResultsGUI;
import com.itmh.mh.gui.ResultsGUISession;
import com.itmh.mh.model.ItemSearchResult;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GUIListener implements Listener {

    private final ItemFinderPlugin plugin;

    public GUIListener(ItemFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!title.contains(ResultsGUI.GUI_TITLE_MARKER)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        List<ItemSearchResult> results = ResultsGUISession.getResults(player);
        String query   = ResultsGUISession.getQuery(player);
        int page       = ResultsGUISession.getPage(player);

        if (results == null || query == null) return;

        int slot       = event.getSlot();
        int totalPages = (int) Math.ceil(results.size() / 45.0);

        // ── Navigation ──
        if (slot == 45 && page > 0) {
            ResultsGUI.open(player, results, query, page - 1);
            return;
        }
        if (slot == 53 && page < totalPages - 1) {
            ResultsGUI.open(player, results, query, page + 1);
            return;
        }

        // ── Bouton Refresh (slot 47) ──
        if (slot == 47) {
            player.sendMessage(ChatColor.AQUA + "🔄 Actualisation de la recherche "
                    + ChatColor.WHITE + "\"" + query + "\"" + ChatColor.AQUA + "...");
            player.closeInventory();
            FindItemCommand.runSearch(plugin, player, query);
            return;
        }

        // ── Clic sur un résultat (slots 0–44) : coordonnées cliquables ──
        if (slot < 45) {
            int resultIndex = page * 45 + slot;
            if (resultIndex < 0 || resultIndex >= results.size()) return;

            ItemSearchResult result = results.get(resultIndex);
            Location loc = result.getLocation();

            if (loc == null) return; // inventaire joueur sans coordonnées fixes

            // Types containers uniquement (pas les inventaires joueur)
            ItemSearchResult.LocationType type = result.getLocationType();
            if (type == ItemSearchResult.LocationType.PLAYER_INVENTORY
                    || type == ItemSearchResult.LocationType.PLAYER_ENDERCHEST) return;

            sendClickableCoords(player, result, loc);
        }
    }

    private void sendClickableCoords(Player player, ItemSearchResult result, Location loc) {
        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        String world = loc.getWorld().getName();
        String teleportCmd = "/tp " + x + " " + y + " " + z;

        // Ligne 1 : label
        TextComponent label = new TextComponent(
                ChatColor.DARK_AQUA + "📍 " + ChatColor.GRAY + result.getOwnerOrCoords() + " ");

        // Lien cliquable
        TextComponent link = new TextComponent(
                ChatColor.GREEN + "" + ChatColor.BOLD + "[Téléporter]");
        link.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, teleportCmd));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.GRAY + "Cliquer pour se téléporter\n"
                        + ChatColor.WHITE + teleportCmd).create()));

        TextComponent copy = new TextComponent(" " + ChatColor.YELLOW + "[Copier]");
        copy.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, teleportCmd));
        copy.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.GRAY + "Copier la commande dans le chat").create()));

        player.spigot().sendMessage(label, link, copy);
    }
}