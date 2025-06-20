package me.Kulmodroid.serverPlugin.serverPlugin.commands;

import me.Kulmodroid.serverPlugin.serverPlugin.GameSelection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to open the game selection GUI.
 */
public class GameSelectionCommand implements CommandExecutor {

    private final GameSelection gameSelection;

    public GameSelectionCommand(GameSelection gameSelection) {
        this.gameSelection = gameSelection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if(!player.isOp()){
                player.sendMessage("You need an Admin persmission to use this command!");
                return false;
            }
            gameSelection.open(player);
            return true;
        }
        return false;
    }
}
