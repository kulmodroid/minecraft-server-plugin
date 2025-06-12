package me.Kulmodroid.serverPlugin.serverPlugin.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a running Bedwars game world.
 */
public class BedwarsGame implements Listener {
    private final JavaPlugin plugin;
    private final World world;
    private final List<Location> spawns;
    private final Set<Player> players = new HashSet<>();
    private final Path worldFolder;

    public BedwarsGame(JavaPlugin plugin, World world, Path worldFolder, List<Location> spawns) {
        this.plugin = plugin;
        this.world = world;
        this.spawns = spawns;
        this.worldFolder = worldFolder;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addPlayer(Player player, int index) {
        players.add(player);
        Location spawn = spawns.get(index % spawns.size());
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.teleport(spawn);
            player.setRespawnLocation(spawn);
        });
    }

    private boolean nearSpawn(Location loc) {
        for (Location spawn : spawns) {
            if (spawn.getWorld().equals(loc.getWorld()) && spawn.distanceSquared(loc) <= 4.0) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getWorld().equals(world)) {
            return;
        }
        if (nearSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!players.contains(player)) {
            return;
        }
        Location spawn = spawns.get(0);
        player.setRespawnLocation(spawn);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!players.contains(player)) {
            return;
        }
        event.setRespawnLocation(player.getRespawnLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (players.remove(player)) {
            checkEnd();
        }
    }

    private void checkEnd() {
        if (players.isEmpty()) {
            end();
        }
    }

    private void end() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.unloadWorld(world, false);
            try {
                deleteDirectory(worldFolder);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to delete world folder: " + e.getMessage());
            }
        });
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
