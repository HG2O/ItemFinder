package com.itmh.mh.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final int cooldownSeconds;

    public CooldownManager(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public boolean isOnCooldown(Player player) {
        if (cooldownSeconds <= 0) return false;
        Long last = lastUsed.get(player.getUniqueId());
        if (last == null) return false;
        return (System.currentTimeMillis() - last) < cooldownSeconds * 1000L;
    }

    public int getRemainingSeconds(Player player) {
        Long last = lastUsed.get(player.getUniqueId());
        if (last == null) return 0;
        long elapsed = System.currentTimeMillis() - last;
        return (int) Math.ceil((cooldownSeconds * 1000L - elapsed) / 1000.0);
    }

    public void setCooldown(Player player) {
        lastUsed.put(player.getUniqueId(), System.currentTimeMillis());
    }
}