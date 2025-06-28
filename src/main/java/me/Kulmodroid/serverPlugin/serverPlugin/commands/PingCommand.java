package me.Kulmodroid.serverPlugin.serverPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int ping = Bukkit.getPlayer(sender.getName()).getPing();
        if (ping < 30) {
            sender.sendMessage("ping: " + ChatColor.DARK_GREEN + ping);
        } else if (30 < ping && ping < 60) {
            sender.sendMessage("ping: " + ChatColor.GREEN + ping);
        } else if (60 < ping && ping < 100) {
            sender.sendMessage("ping: " + ChatColor.YELLOW + ping);
        } else if (100 < ping && ping < 150) {
            sender.sendMessage("ping: " + ChatColor.GOLD + ping);
        } else if (150 < ping && ping < 250) {
            sender.sendMessage("ping: " + ChatColor.RED + ping);
        } else if (250 < ping && ping < 400) {
            sender.sendMessage("ping: " + ChatColor.DARK_RED + ping);
        } else if (400 < ping && ping < 900) {
            sender.sendMessage("ping: " + ChatColor.LIGHT_PURPLE + ping);
        } else if (900 < ping && ping < 1200) {
            sender.sendMessage("ping: " + ChatColor.DARK_PURPLE + ping);
        } else if (ping > 1200) {
            sender.sendMessage("ping: " + ChatColor.BLACK + ping);
        }

        return true;
    }
}
