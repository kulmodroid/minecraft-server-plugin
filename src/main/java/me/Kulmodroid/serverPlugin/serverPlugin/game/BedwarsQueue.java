package me.Kulmodroid.serverPlugin.serverPlugin.game;

import me.Kulmodroid.serverPlugin.serverPlugin.GameManager;
import me.Kulmodroid.serverPlugin.serverPlugin.GameSelection;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles queueing of players and starting Bedwars games.
 */
public class BedwarsQueue {
    private final JavaPlugin plugin;
    private final List<Player> queue = new ArrayList<>();
    private Player redPlayer;
    private Player bluePlayer;
    private Player yellowPlayer;
    private Player greenPlayer;
    private long firstJoin;
    private GameManager gameManager;

    public BedwarsQueue(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized void add(Player player) {
        if (queue.isEmpty()) {
            firstJoin = System.currentTimeMillis();
        }
        if (!queue.contains(player)) {
            if (redPlayer == null) {
                player = redPlayer;
            }
            if (bluePlayer == null) {
                player = bluePlayer;
            }
            if (yellowPlayer == null) {
                player = yellowPlayer;
            }
            if (greenPlayer == null) {
                player = greenPlayer;
            }
            queue.add(player);
            player.sendMessage("Players in queue: " + queue.size());
            checkStart(redPlayer, bluePlayer, yellowPlayer, greenPlayer);
        }
    }

    private void checkStart(Player redPlayer1, Player bluePlayer1, Player yellowPlayer1, Player greenPlayer1) {
        int required = plugin.getConfig().getInt("bedwars.players", 4);
        int minPlayers = plugin.getConfig().getInt("bedwars.timeout-min-players", 2);
        int timeoutMin = plugin.getConfig().getInt("bedwars.timeout-minutes", 3);
        long now = System.currentTimeMillis();
        if (queue.size() >= required || (queue.size() >= minPlayers && now - firstJoin > timeoutMin * 60_000L)) {
            startGame(redPlayer1, bluePlayer1, yellowPlayer1, greenPlayer1);
        }
    }

    private void startGame(Player redPlayer2, Player bluePlayer2, Player yellowPlayer2, Player greenPlayer2) {
        List<String> mapNames = plugin.getConfig().getStringList("bedwars.maps");
        if (mapNames.isEmpty()) {
            plugin.getLogger().warning("No Bedwars maps configured");
            return;
        }
        String mapName = mapNames.get((int) (Math.random() * mapNames.size()));
        var mapSec = plugin.getConfig().getConfigurationSection("maps." + mapName);
        if (mapSec == null) {
            plugin.getLogger().warning("Map config missing for " + mapName);
            return;
        }
        String baseName = mapSec.getString("path", mapName);
        int capacity = mapSec.getInt("players", plugin.getConfig().getInt("bedwars.players", 4));
        String worldName = mapName + "_game_" + System.currentTimeMillis();
        Path worldFolder = plugin.getServer().getWorldContainer().toPath().resolve(worldName);
        try {
            copyWorld(plugin.getServer().getWorldContainer().toPath().resolve(baseName), worldFolder);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to copy world: " + e.getMessage());
            return;
        }
        World world = Bukkit.createWorld(new WorldCreator(worldName));
        List<Location> spawns = new ArrayList<>();
        if (spawns.size() != capacity) {
            plugin.getLogger().warning("Spawn count " + spawns.size() + " does not match players " + capacity + " for map " + mapName);
        }

        List<Player> players = new ArrayList<>();
        while (!queue.isEmpty() && players.size() < capacity) {
            players.add(queue.remove(0));
        }

        double xmin = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".zoneLimit.min") .get("x")).doubleValue();
        double ymin = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".zoneLimit.min") .get("y")).doubleValue();
        double zmin = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".zoneLimit.min") .get("z")).doubleValue();

        Vector min = new Vector(xmin, ymin, zmin);

        double xmax = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".zoneLimit.max").get("x")).doubleValue();
        double ymax = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".zoneLimit.max").get("y")).doubleValue();
        double zmax = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".zoneLimit.max").get("z")).doubleValue();

        Vector max = new Vector(xmax, ymax, zmax);


        double redx = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.red") .get("x")).doubleValue();
        double redy = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.red") .get("y")).doubleValue();
        double redz = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.red") .get("z")).doubleValue();

        Location redSpawn = new Location(world, redx, redy, redz);


        double bluex = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.blue") .get("x")).doubleValue();
        double bluey = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.blue") .get("y")).doubleValue();
        double bluez = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.blue") .get("z")).doubleValue();

        Location blueSpawn = new Location(world, bluex, bluey, bluez);


        double yellowx = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.yellow") .get("x")).doubleValue();
        double yellowy = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.yellow") .get("y")).doubleValue();
        double yellowz = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.yellow") .get("z")).doubleValue();

        Location yellowSpawn = new Location(world, yellowx, yellowy, yellowz);


        double greenx = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.green") .get("x")).doubleValue();
        double greeny = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.green") .get("y")).doubleValue();
        double greenz = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".spawnpoints.green") .get("z")).doubleValue();

        Location greenSpawn = new Location(world, greenx, greeny, greenz);


        spawns.add(redSpawn);
        spawns.add(blueSpawn);
        spawns.add(yellowSpawn);
        spawns.add(greenSpawn);

        double blueBed1x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.blue.1") .get("x")).doubleValue();
        double blueBed1y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.blue.1") .get("y")).doubleValue();
        double blueBed1z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.blue.1") .get("z")).doubleValue();

        Location blueBed1 = new Location(world, blueBed1x, blueBed1y, blueBed1z);


        double blueBed2x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.blue.2") .get("x")).doubleValue();
        double blueBed2y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.blue.2") .get("y")).doubleValue();
        double blueBed2z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.blue.2") .get("z")).doubleValue();

        Location blueBed2 = new Location(world, blueBed2x, blueBed2y, blueBed2z);


        double redBed1x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.red.1") .get("x")).doubleValue();
        double redBed1y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.red.1") .get("y")).doubleValue();
        double redBed1z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.red.1") .get("z")).doubleValue();

        Location redBed1 = new Location(world, redBed1x, redBed1y, redBed1z);


        double redBed2x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.red.2") .get("x")).doubleValue();
        double redBed2y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.red.2") .get("y")).doubleValue();
        double redBed2z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.red.2") .get("z")).doubleValue();

        Location redBed2 = new Location(world, redBed2x, redBed2y, redBed2z);


        double yellowBed1x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.yellow.1") .get("x")).doubleValue();
        double yellowBed1y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.yellow.1") .get("y")).doubleValue();
        double yellowBed1z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.yellow.1") .get("z")).doubleValue();

        Location yellowBed1 = new Location(world, yellowBed1x, yellowBed1y, yellowBed1z);


        double yellowBed2x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.yellow.2") .get("x")).doubleValue();
        double yellowBed2y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.yellow.2") .get("y")).doubleValue();
        double yellowBed2z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.yellow.2") .get("z")).doubleValue();

        Location yellowBed2 = new Location(world, yellowBed2x, yellowBed2y, yellowBed2z);


        double greenBed1x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.green.1") .get("x")).doubleValue();
        double greenBed1y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.green.1") .get("y")).doubleValue();
        double greenBed1z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.green.1") .get("z")).doubleValue();

        Location greenBed1 = new Location(world, greenBed1x, greenBed1y, greenBed1z);


        double greenBed2x = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.green.2") .get("x")).doubleValue();
        double greenBed2y = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.green.2") .get("y")).doubleValue();
        double greenBed2z = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".bedPositions.green.2") .get("z")).doubleValue();

        Location greenBed2 = new Location(world, greenBed2x, greenBed2y, greenBed2z);


        double waitx = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".waitingpoint") .get("x")).doubleValue();
        double waity = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".waitingpoint") .get("y")).doubleValue();
        double waitz = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".waitingpoint") .get("z")).doubleValue();

        Location waitPos = new Location(world, waitx, waity, waitz);


        redPlayer2.setDisplayName(Color.RED + redPlayer2.getName());
        bluePlayer2.setDisplayName(Color.BLUE + bluePlayer2.getName());
        yellowPlayer2.setDisplayName(Color.YELLOW + yellowPlayer2.getName());
        greenPlayer2.setDisplayName(Color.GREEN + greenPlayer2.getName());

        BedwarsGame game = new BedwarsGame(
                plugin,
                world,
                worldFolder,
                max,
                min,
                ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName).get("fellY")).doubleValue(),
                ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName).get("heightBuildLimit")).doubleValue(),
                ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName).get("depthBuildLimit")).doubleValue(),
                waitPos,
                redSpawn,
                blueSpawn,
                yellowSpawn,
                greenSpawn,
                blueBed1,
                blueBed2,
                redBed1,
                redBed2,
                yellowBed1,
                yellowBed2,
                greenBed1,
                greenBed2,
                redPlayer2,
                bluePlayer2,
                yellowPlayer2,
                greenPlayer2,
                spawns,
                gameManager,
                new ItemStack(Material.RECOVERY_COMPASS));

        game.canRedRespawn = true;
        game.canBlueRespawn = true;
        game.canYellowRespawn = true;
        game.canGreenRespawn = true;

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
