package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameSelectionCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Bukkit.getServer().getPluginManager().callEvent(new GameSelectionEvent((Player) sender));
            return true;
        }

        else
        {
            Bukkit.getLogger().info("You must be a player to run this command!");

        }

        return false;
    }

}
