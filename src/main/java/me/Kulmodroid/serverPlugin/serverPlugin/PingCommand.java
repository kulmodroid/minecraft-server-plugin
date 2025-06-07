package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PingCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        String msg = "Welcome to server " + sender.getName() + "!";

        sender.sendMessage(msg);

        return true;
    }


}