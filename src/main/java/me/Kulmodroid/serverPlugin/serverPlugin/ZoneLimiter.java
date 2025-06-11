package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Restricts players to a rectangular zone and teleports them back if they exit.
 */
public class ZoneLimiter implements Listener {

    private final JavaPlugin plugin;
    private final World world;
    private final Vector min;
    private final Vector max;
    private final double fellY;
    private final Map<Player, Location> lastValid = new HashMap<>();
    private final GameManager gameManager;

    public ZoneLimiter(JavaPlugin plugin, World world,
                       double x1, double y1, double z1,
                       double x2, double y2, double z2,
                       double fellY,
                       GameManager gameManager) {
        this.plugin = plugin;
        this.world = world;
        this.gameManager = gameManager;
        this.min = new Vector(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        this.max = new Vector(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
        this.fellY = fellY;
    }

    private boolean isInside(Location loc) {
        return loc.getX() >= min.getX() && loc.getX() <= max.getX()
                && loc.getY() >= min.getY() && loc.getY() <= max.getY()
                && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        if (!player.getWorld().equals(world)) {
            return;
        }
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        if (to.getY() < this.fellY) {
            event.setCancelled(true);
            this.gameManager.onPlayerFell(player);
            return;
        }
        if (isInside(to)) {
            lastValid.put(player, to);
            return;
        }
        Location back = lastValid.getOrDefault(player, event.getFrom());
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, () -> player.teleport(back));
    }
}
