package com.itmh.mh.search;

import com.itmh.mh.ItemFinderPlugin;
import com.itmh.mh.listener.EnderChestListener;
import com.itmh.mh.model.ItemSearchResult;
import com.itmh.mh.util.ItemMatcher;
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

    public record InventorySnapshot(ItemStack[] contents,
                                    ItemSearchResult.LocationType type,
                                    String label,
                                    Location location) {}

    /**
     * Collecte tous les inventaires sur le main thread.
     * Les mondes listés dans disabled-worlds sont ignorés.
     */
    public List<InventorySnapshot> collectSnapshots() {
        List<InventorySnapshot> snapshots = new ArrayList<>();
        List<String> disabledWorlds = plugin.getConfig().getStringList("disabled-worlds");

        // 1. Joueurs en ligne
        for (Player player : Bukkit.getOnlinePlayers()) {
            snapshots.add(new InventorySnapshot(
                    player.getInventory().getContents().clone(),
                    ItemSearchResult.LocationType.PLAYER_INVENTORY,
                    player.getName(), player.getLocation()));

            snapshots.add(new InventorySnapshot(
                    player.getEnderChest().getContents().clone(),
                    ItemSearchResult.LocationType.PLAYER_ENDERCHEST,
                    player.getName(), player.getLocation()));
        }

        // 2. Ender chests des joueurs hors-ligne (cache)
        Map<UUID, ItemStack[]> cache = EnderChestListener.getEnderChestCache();
        for (Map.Entry<UUID, ItemStack[]> entry : cache.entrySet()) {
            if (Bukkit.getPlayer(entry.getKey()) != null) continue;
            String name = Optional.ofNullable(Bukkit.getOfflinePlayer(entry.getKey()).getName())
                    .orElse(entry.getKey().toString());
            snapshots.add(new InventorySnapshot(
                    entry.getValue().clone(),
                    ItemSearchResult.LocationType.PLAYER_ENDERCHEST,
                    name + " (hors-ligne)", null));
        }

        // 3. Containers dans les chunks chargés (filtre par monde)
        for (World world : Bukkit.getWorlds()) {
            if (disabledWorlds.contains(world.getName())) continue;

            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState state : chunk.getTileEntities()) {
                    if (!(state instanceof Container container)) continue;

                    Location loc = state.getLocation();
                    String coords = formatCoords(loc);

                    ItemSearchResult.LocationType type = switch (state.getType()) {
                        case CHEST, TRAPPED_CHEST -> ItemSearchResult.LocationType.CHEST;
                        case BARREL               -> ItemSearchResult.LocationType.BARREL;
                        case SHULKER_BOX,
                             WHITE_SHULKER_BOX, ORANGE_SHULKER_BOX, MAGENTA_SHULKER_BOX,
                             LIGHT_BLUE_SHULKER_BOX, YELLOW_SHULKER_BOX, LIME_SHULKER_BOX,
                             PINK_SHULKER_BOX, GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX,
                             CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX, BLUE_SHULKER_BOX,
                             BROWN_SHULKER_BOX, GREEN_SHULKER_BOX, RED_SHULKER_BOX,
                             BLACK_SHULKER_BOX    -> ItemSearchResult.LocationType.SHULKER_BOX;
                        default                   -> ItemSearchResult.LocationType.OTHER_CONTAINER;
                    };

                    snapshots.add(new InventorySnapshot(
                            container.getInventory().getContents().clone(),
                            type, coords, loc));
                }
            }
        }

        return snapshots;
    }

    /** Matching pur sur données déjà collectées — safe en async. */
    public List<ItemSearchResult> search(List<InventorySnapshot> snapshots, String query) {
        List<ItemSearchResult> results = new ArrayList<>();
        for (InventorySnapshot snap : snapshots) {
            scanInventory(snap.contents(), query, snap.type(), snap.label(), snap.location(), results);
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