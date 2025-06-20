package me.Kulmodroid.serverPlugin.serverPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class getPosCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Sorry, " + ChatColor.GRAY + "but you don't have permission for this command!");
            return false;
        }
        int x = ((Number) Bukkit.getPlayer(sender.getName()).getLocation().getX()).intValue();
        int y = ((Number) Bukkit.getPlayer(sender.getName()).getLocation().getY()).intValue();
        int z = ((Number) Bukkit.getPlayer(sender.getName()).getLocation().getZ()).intValue();
        sender.sendMessage(ChatColor.WHITE + "Your current location is: " + ChatColor.RED + x + ", " + ChatColor.BLUE + y + ", " + ChatColor.GREEN + z);
        return true;
    }
}
