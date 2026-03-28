package com.itmh.dragon.util;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class ItemMatcher {

    /**
     * Retourne true si l'item correspond à la requête.
     * Vérifie (insensible à la casse, correspondance partielle) :
     * - Nom du matériau
     * - Nom personnalisé (display name)
     * - Lignes de lore
     * - Noms des enchantements
     */
    public static boolean matches(ItemStack item, String query) {
        if (item == null || item.getType().isAir()) return false;

        String q = query.toLowerCase().trim();

        // 1. Nom du matériau
        if (item.getType().name().toLowerCase().contains(q)) return true;

        // 2. Display name, lore et enchantements
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            // Display name
            if (meta.hasDisplayName()) {
                String displayName = PlainTextComponentSerializer.plainText()
                        .serialize(meta.displayName());
                if (displayName.toLowerCase().contains(q)) return true;
            }

            // Lore
            if (meta.hasLore()) {
                List<net.kyori.adventure.text.Component> lore = meta.lore();
                if (lore != null) {
                    for (net.kyori.adventure.text.Component line : lore) {
                        String plainLine = PlainTextComponentSerializer.plainText().serialize(line);
                        if (plainLine.toLowerCase().contains(q)) return true;
                    }
                }
            }

            // Enchantements
            Map<Enchantment, Integer> enchants = meta.getEnchants();
            for (Enchantment enchant : enchants.keySet()) {
                if (enchant.getKey().getKey().toLowerCase().contains(q)) return true;
            }

            // Livres enchantés
            if (meta instanceof EnchantmentStorageMeta esm) {
                for (Enchantment enchant : esm.getStoredEnchants().keySet()) {
                    if (enchant.getKey().getKey().toLowerCase().contains(q)) return true;
                }
            }
        }

        return false;
    }
}