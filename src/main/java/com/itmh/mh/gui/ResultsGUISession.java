package com.itmh.mh.gui;

import com.itmh.mh.model.ItemSearchResult;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResultsGUISession {

    private record Session(List<ItemSearchResult> results, String query, int page) {}

    private static final Map<UUID, Session> sessions = new HashMap<>();

    public static void store(Player player, List<ItemSearchResult> results, String query, int page) {
        sessions.put(player.getUniqueId(), new Session(results, query, page));
    }

    public static List<ItemSearchResult> getResults(Player player) {
        Session s = sessions.get(player.getUniqueId());
        return s != null ? s.results() : null;
    }

    public static String getQuery(Player player) {
        Session s = sessions.get(player.getUniqueId());
        return s != null ? s.query() : null;
    }

    public static int getPage(Player player) {
        Session s = sessions.get(player.getUniqueId());
        return s != null ? s.page() : 0;
    }

    public static void clear(Player player) {
        sessions.remove(player.getUniqueId());
    }
}