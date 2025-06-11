package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks placed blocks and only allows breaking those blocks.
 * Original world blocks remain protected from normal players.
 */
public class BlockProtection implements Listener {

    private final Set<Location> placedBlocks = new HashSet<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (player.isOp() || player.hasPermission("serverPlugin.admin")) {
            return;
        }

        if (loc.getY() > 50) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place blocks above level 50!");
            return;
        }
        if (loc.getY() < 20) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place blocks there!");
            return;
        }

        placedBlocks.add(loc);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("serverPlugin.admin")) {
            return;
        }

        Location loc = event.getBlock().getLocation();

        // Allow breaking only for blocks placed after the plugin was enabled
        if (!placedBlocks.contains(loc)) {
            event.setCancelled(true);
        } else {
            // Remove from set so the block can be placed again in future
            placedBlocks.remove(loc);
        }
    }
}
