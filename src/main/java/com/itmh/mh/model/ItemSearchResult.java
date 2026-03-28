package com.itmh.dragon.model;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ItemSearchResult {

    public enum LocationType {
        PLAYER_INVENTORY,
        PLAYER_ENDERCHEST,
        CHEST,
        BARREL,
        SHULKER_BOX,
        OTHER_CONTAINER
    }

    private final ItemStack item;
    private final LocationType locationType;
    private final String ownerOrCoords;
    private final Location location;
    private final int count;

    public ItemSearchResult(ItemStack item, LocationType locationType, String ownerOrCoords, Location location, int count) {
        this.item = item;
        this.locationType = locationType;
        this.ownerOrCoords = ownerOrCoords;
        this.location = location;
        this.count = count;
    }

    public ItemStack getItem() { return item; }
    public LocationType getLocationType() { return locationType; }
    public String getOwnerOrCoords() { return ownerOrCoords; }
    public Location getLocation() { return location; }
    public int getCount() { return count; }
}