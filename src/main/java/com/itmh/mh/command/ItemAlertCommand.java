package com.itmh.mh.command;

import com.itmh.mh.ItemFinderPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemAlertCommand implements CommandExecutor, TabCompleter {

    private final ItemFinderPlugin plugin;

    public ItemAlertCommand(ItemFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemfinder.alert")) {
            sender.sendMessage(ChatColor.RED + "❌ Permission refusée.");
            return true;
        }

        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage : /itemalert add <matériau>");
                    return true;
                }
                String item = args[1].toUpperCase();
                try { Material.valueOf(item); }
                catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "❌ Matériau inconnu : " + item);
                    return true;
                }
                List<String> list = new ArrayList<>(plugin.getConfig().getStringList("alert-items"));
                if (list.contains(item)) {
                    sender.sendMessage(ChatColor.YELLOW + "⚠ " + item + " est déjà dans la liste d'alertes.");
                    return true;
                }
                list.add(item);
                plugin.getConfig().set("alert-items", list);
                plugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "✅ " + item + " ajouté aux alertes.");
            }
            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage : /itemalert remove <matériau>");
                    return true;
                }
                String item = args[1].toUpperCase();
                List<String> list = new ArrayList<>(plugin.getConfig().getStringList("alert-items"));
                if (!list.remove(item)) {
                    sender.sendMessage(ChatColor.RED + "❌ " + item + " n'est pas dans la liste d'alertes.");
                    return true;
                }
                plugin.getConfig().set("alert-items", list);
                plugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "✅ " + item + " retiré des alertes.");
            }
            case "list" -> {
                List<String> list = plugin.getConfig().getStringList("alert-items");
                if (list.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "Aucun item en alerte pour l'instant.");
                    return true;
                }
                sender.sendMessage(ChatColor.AQUA + "━━━ 🚨 Items en alerte ━━━");
                for (String item : list) {
                    sender.sendMessage(ChatColor.GRAY + "  • " + ChatColor.WHITE + item);
                }
                sender.sendMessage(ChatColor.GRAY + "Vérification toutes les "
                        + plugin.getConfig().getInt("alert-interval-seconds", 60) + "s");
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "━━━ 🚨 ItemAlert ━━━");
        sender.sendMessage(ChatColor.YELLOW + "/itemalert add <item>"    + ChatColor.GRAY + " — Ajouter une alerte");
        sender.sendMessage(ChatColor.YELLOW + "/itemalert remove <item>" + ChatColor.GRAY + " — Supprimer une alerte");
        sender.sendMessage(ChatColor.YELLOW + "/itemalert list"          + ChatColor.GRAY + " — Voir les alertes actives");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("add", "remove", "list");
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return plugin.getConfig().getStringList("alert-items");
        }
        return List.of();
    }
}