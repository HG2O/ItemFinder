package com.itmh.dragon.gui;

import com.itmh.dragon.model.ItemSearchResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ResultsGUI {

    private static final int PAGE_SIZE = 45;

    public static void open(Player player, List<ItemSearchResult> results, String query, int page) {
        int totalPages = Math.max(1, (int) Math.ceil(results.size() / (double) PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        Component title = Component.text("🔍 Recherche : ", NamedTextColor.DARK_AQUA)
                .append(Component.text("\"" + query + "\"", NamedTextColor.WHITE))
                .append(Component.text(" — " + results.size() + " résultat(s)", NamedTextColor.GRAY));

        Inventory gui = Bukkit.createInventory(null, 54, title);

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, results.size());

        for (int i = start; i < end; i++) {
            gui.setItem(i - start, buildResultItem(results.get(i), i + 1));
        }

        fillNavigationBar(gui, page, totalPages, query, results.size());

        player.openInventory(gui);
        ResultsGUISession.store(player, results, query, page);
    }

    private static ItemStack buildResultItem(ItemSearchResult result, int index) {
        ItemStack display = result.getItem().clone();
        ItemMeta meta = display.getItemMeta();

        String locationIcon = switch (result.getLocationType()) {
            case PLAYER_INVENTORY  -> "🎒";
            case PLAYER_ENDERCHEST -> "🟣";
            case CHEST             -> "📦";
            case BARREL            -> "🛢";
            case SHULKER_BOX       -> "🟪";
            case OTHER_CONTAINER   -> "🗃";
        };

        String locationLabel = switch (result.getLocationType()) {
            case PLAYER_INVENTORY  -> "Inventaire";
            case PLAYER_ENDERCHEST -> "Ender Chest";
            case CHEST             -> "Coffre";
            case BARREL            -> "Tonneau";
            case SHULKER_BOX       -> "Shulker Box";
            case OTHER_CONTAINER   -> "Conteneur";
        };

        meta.displayName(
                Component.text("#" + index + " ", NamedTextColor.YELLOW)
                        .append(Component.text(locationIcon + " " + locationLabel, NamedTextColor.AQUA))
                        .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>(meta.hasLore() && meta.lore() != null ? meta.lore() : List.of());
        lore.add(Component.empty());
        lore.add(Component.text("━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("📍 ", NamedTextColor.GRAY)
                .append(Component.text(result.getOwnerOrCoords(), NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("🔢 Quantité : ", NamedTextColor.GRAY)
                .append(Component.text(result.getCount(), NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false));

        if (result.getLocation() != null) {
            lore.add(Component.text("🌍 Monde : ", NamedTextColor.GRAY)
                    .append(Component.text(result.getLocation().getWorld().getName(), NamedTextColor.WHITE))
                    .decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        display.setItemMeta(meta);
        return display;
    }

    private static void fillNavigationBar(Inventory gui, int page, int totalPages, String query, int total) {
        ItemStack separator = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta sepMeta = separator.getItemMeta();
        sepMeta.displayName(Component.text(" ").decoration(TextDecoration.ITALIC, false));
        separator.setItemMeta(sepMeta);
        for (int i = 45; i < 54; i++) gui.setItem(i, separator);

        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.displayName(Component.text("◀ Page précédente", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            prevMeta.lore(List.of(Component.text("Page " + page + "/" + totalPages, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
            prev.setItemMeta(prevMeta);
            gui.setItem(45, prev);
        }

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(Component.text("Page " + (page + 1) + " / " + totalPages, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        infoMeta.lore(List.of(
                Component.text(total + " résultat(s)", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("Recherche : \"" + query + "\"", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
        ));
        info.setItemMeta(infoMeta);
        gui.setItem(49, info);

        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.displayName(Component.text("Page suivante ▶", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            nextMeta.lore(List.of(Component.text("Page " + (page + 2) + "/" + totalPages, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
            next.setItemMeta(nextMeta);
            gui.setItem(53, next);
        }
    }
}