package com.itmh.dragon.search;

import com.itmh.dragon.ItemFinderPlugin;
import com.itmh.dragon.listener.EnderChestListener;
import com.itmh.dragon.model.ItemSearchResult;
import com.itmh.dragon.util.ItemMatcher;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.*;

public class SearchEngine {

    private final ItemFinderPlugin plugin;

    public SearchEngine(ItemFinderPlugin plugin) {
        this.plugin = plugin;
    }

    public List<ItemSearchResult> search(String query) {
        List<ItemSearchResult> results = new ArrayList<>();

        // 1. Inventaires et ender chests des joueurs en ligne
        for (Player player : Bukkit.getOnlinePlayers()) {
            scanInventory(player.getInventory().getContents(), query,
                    ItemSearchResult.LocationType.PLAYER_INVENTORY,
                    player.getName(), player.getLocation(), results);

            scanInventory(player.getEnderChest().getContents(), query,
                    ItemSearchResult.LocationType.PLAYER_ENDERCHEST,
                    player.getName(), player.getLocation(), results);
        }

        // 2. Ender chests des joueurs hors-ligne (cache)
        Map<UUID, ItemStack[]> cache = EnderChestListener.getEnderChestCache();
        for (Map.Entry<UUID, ItemStack[]> entry : cache.entrySet()) {
            if (Bukkit.getPlayer(entry.getKey()) != null) continue;

            String name = Optional.ofNullable(Bukkit.getOfflinePlayer(entry.getKey()).getName())
                    .orElse(entry.getKey().toString());

            scanInventory(entry.getValue(), query,
                    ItemSearchResult.LocationType.PLAYER_ENDERCHEST,
                    name + " (hors-ligne)", null, results);
        }

        // 3. Containers dans les chunks chargés
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState state : chunk.getTileEntities()) {
                    if (!(state instanceof Container container)) continue;

                    Location loc = state.getLocation();
                    String coords = formatCoords(loc);

                    ItemSearchResult.LocationType type = switch (state.getType()) {
                        case CHEST, TRAPPED_CHEST -> ItemSearchResult.LocationType.CHEST;
                        case BARREL -> ItemSearchResult.LocationType.BARREL;
                        case SHULKER_BOX,
                             WHITE_SHULKER_BOX, ORANGE_SHULKER_BOX, MAGENTA_SHULKER_BOX,
                             LIGHT_BLUE_SHULKER_BOX, YELLOW_SHULKER_BOX, LIME_SHULKER_BOX,
                             PINK_SHULKER_BOX, GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX,
                             CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX, BLUE_SHULKER_BOX,
                             BROWN_SHULKER_BOX, GREEN_SHULKER_BOX, RED_SHULKER_BOX,
                             BLACK_SHULKER_BOX -> ItemSearchResult.LocationType.SHULKER_BOX;
                        default -> ItemSearchResult.LocationType.OTHER_CONTAINER;
                    };

                    scanInventory(container.getInventory().getContents(), query, type, coords, loc, results);
                }
            }
        }

        return results;
    }

    private void scanInventory(ItemStack[] contents, String query,
                               ItemSearchResult.LocationType type,
                               String label, Location loc,
                               List<ItemSearchResult> results) {
        if (contents == null) return;

        for (ItemStack item : contents) {
            if (item == null || item.getType().isAir()) continue;

            // Scan récursif dans les shulker boxes
            if (item.getItemMeta() instanceof BlockStateMeta bsm
                    && bsm.getBlockState() instanceof Container innerContainer) {
                scanInventory(innerContainer.getInventory().getContents(), query,
                        ItemSearchResult.LocationType.SHULKER_BOX,
                        label + " [Shulker]", loc, results);
            }

            if (ItemMatcher.matches(item, query)) {
                results.add(new ItemSearchResult(item.clone(), type, label, loc, item.getAmount()));
            }
        }
    }

    private String formatCoords(Location loc) {
        return loc.getWorld().getName() + " [" + loc.getBlockX() + ", "
                + loc.getBlockY() + ", " + loc.getBlockZ() + "]";
    }
}