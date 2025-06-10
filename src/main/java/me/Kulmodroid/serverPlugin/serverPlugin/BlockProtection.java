package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Prevents normal players from breaking existing world blocks.
 */
public class BlockProtection implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("serverPlugin.admin")) {
            return;
        }
        if (event.getBlock().getType() != Material.AIR) {
            event.setCancelled(true);
        }
    }
}
