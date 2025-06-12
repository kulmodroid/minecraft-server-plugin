package me.Kulmodroid.serverPlugin.serverPlugin.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles queueing of players and starting Bedwars games.
 */
public class BedwarsQueue {
    private final JavaPlugin plugin;
    private final List<Player> queue = new ArrayList<>();
    private long firstJoin;

    public BedwarsQueue(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized void add(Player player) {
        if (queue.isEmpty()) {
            firstJoin = System.currentTimeMillis();
        }
        if (!queue.contains(player)) {
            queue.add(player);
            player.sendMessage("Players in queue: " + queue.size());
            checkStart();
        }
    }

    private void checkStart() {
        int required = plugin.getConfig().getInt("bedwars.players", 4);
        int minPlayers = plugin.getConfig().getInt("bedwars.timeout-min-players", 2);
        int timeoutMin = plugin.getConfig().getInt("bedwars.timeout-minutes", 3);
        long now = System.currentTimeMillis();
        if (queue.size() >= required || (queue.size() >= minPlayers && now - firstJoin > timeoutMin * 60_000L)) {
            startGame();
        }
    }

    private void startGame() {
        List<Player> players = new ArrayList<>(queue);
        queue.clear();
        String baseName = plugin.getConfig().getString("maps.bedwars", "bedwars");
        String worldName = baseName + "_game_" + System.currentTimeMillis();
        Path worldFolder = plugin.getServer().getWorldContainer().toPath().resolve(worldName);
        try {
            copyWorld(plugin.getServer().getWorldContainer().toPath().resolve(baseName), worldFolder);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to copy world: " + e.getMessage());
            return;
        }
        World world = Bukkit.createWorld(new WorldCreator(worldName));
        List<Location> spawns = new ArrayList<>();
        for (var map : plugin.getConfig().getMapList("bedwars.spawnpoints")) {
            double x = ((Number) map.get("x")).doubleValue();
            double y = ((Number) map.get("y")).doubleValue();
            double z = ((Number) map.get("z")).doubleValue();
            spawns.add(new Location(world, x, y, z));
        }
        BedwarsGame game = new BedwarsGame(plugin, world, worldFolder, spawns);
        for (int i = 0; i < players.size(); i++) {
            game.addPlayer(players.get(i), i);
        }
    }

    private void copyWorld(Path src, Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path target = dst.resolve(src.relativize(dir).toString());
                Files.createDirectories(target);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path target = dst.resolve(src.relativize(file).toString());
                Files.copy(file, target);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
