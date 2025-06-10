package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Material;
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
        // Track locations of all blocks players place so they can be broken later
        placedBlocks.add(event.getBlock().getLocation());
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
            if (event.getBlock().getType() != Material.AIR) {
                event.setCancelled(true);
            }
        } else {
            // Remove from set so the block can be placed again in future
            placedBlocks.remove(loc);
        }
    }
}
