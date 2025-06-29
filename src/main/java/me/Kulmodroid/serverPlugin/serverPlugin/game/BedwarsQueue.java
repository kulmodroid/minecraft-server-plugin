package me.Kulmodroid.serverPlugin.serverPlugin.game;

import me.Kulmodroid.serverPlugin.serverPlugin.GameManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
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
import java.util.List;

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

    private Location getLocation(World world, String path) {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection(path);
        if (sec == null) {
            throw new IllegalArgumentException("Missing config section " + path);
        }
        return new Location(world,
                ((Number) sec.get("x")).doubleValue(),
                ((Number) sec.get("y")).doubleValue(),
                ((Number) sec.get("z")).doubleValue());
    }

    private List<Location> getMidGenerators(World world, String path) {
        List<Location> result = new ArrayList<>();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection(path);
        if (sec == null) {
            return result;
        }
        for (var map : sec.getMapList("mid")) {
            result.add(new Location(world,
                    ((Number) map.get("x")).doubleValue(),
                    ((Number) map.get("y")).doubleValue(),
                    ((Number) map.get("z")).doubleValue()));
        }
        return result;
    }

    private Vector getVector(String path) {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection(path);
        if (sec == null) {
            throw new IllegalArgumentException("Missing config section " + path);
        }
        return new Vector(
                ((Number) sec.get("x")).doubleValue(),
                ((Number) sec.get("y")).doubleValue(),
                ((Number) sec.get("z")).doubleValue());
    }

    public BedwarsQueue(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized void add(Player player) {
        if (queue.isEmpty()) {
            firstJoin = System.currentTimeMillis();
        }
        if (queue.contains(player)) {
            return;
        }

        if (redPlayer == null) {
            redPlayer = player;
        } else if (bluePlayer == null) {
            bluePlayer = player;
        } else if (yellowPlayer == null) {
            yellowPlayer = player;
        } else if (greenPlayer == null) {
            greenPlayer = player;
        }

        queue.add(player);
        player.sendMessage("Players in queue: " + queue.size());
        checkStart();
    }

    private void checkStart() {
        int required = plugin.getConfig().getInt("bedwars.players", 4);
        boolean testMode = plugin.getConfig().getBoolean("bedwars.test-mode", false);
        int minPlayers = plugin.getConfig().getInt("bedwars.timeout-min-players", 2);
        int timeoutMin = plugin.getConfig().getInt("bedwars.timeout-minutes", 3);
        long now = System.currentTimeMillis();
        if (queue.size() >= required || (testMode && queue.size() >= 1)
                || (queue.size() >= minPlayers && now - firstJoin > timeoutMin * 60_000L)) {
            startGame();
        }
    }

    private void startGame() {
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
//        if (spawns.size() != capacity) {
//            plugin.getLogger().warning("Spawn count " + spawns.size() + " does not match players " + capacity + " for map " + mapName);
//        }

        List<Player> players = new ArrayList<>();
        while (!queue.isEmpty() && players.size() < capacity) {
            players.add(queue.remove(0));
        }

        Player redPlayer = players.size() > 0 ? players.get(0) : null;
        Player bluePlayer = players.size() > 1 ? players.get(1) : null;
        Player yellowPlayer = players.size() > 2 ? players.get(2) : null;
        Player greenPlayer = players.size() > 3 ? players.get(3) : null;

        Vector min = getVector("maps." + mapName + ".zoneLimit.min");
        Vector max = getVector("maps." + mapName + ".zoneLimit.max");

        Location redSpawn = getLocation(world, "maps." + mapName + ".spawnpoints.red");
        Location blueSpawn = getLocation(world, "maps." + mapName + ".spawnpoints.blue");
        Location yellowSpawn = getLocation(world, "maps." + mapName + ".spawnpoints.yellow");
        Location greenSpawn = getLocation(world, "maps." + mapName + ".spawnpoints.green");

        spawns.add(redSpawn);
        spawns.add(blueSpawn);
        spawns.add(yellowSpawn);
        spawns.add(greenSpawn);

        Location blueBed1 = getLocation(world, "maps." + mapName + ".bedPositions.blue.1");
        Location blueBed2 = getLocation(world, "maps." + mapName + ".bedPositions.blue.2");
        Location redBed1 = getLocation(world, "maps." + mapName + ".bedPositions.red.1");
        Location redBed2 = getLocation(world, "maps." + mapName + ".bedPositions.red.2");
        Location yellowBed1 = getLocation(world, "maps." + mapName + ".bedPositions.yellow.1");
        Location yellowBed2 = getLocation(world, "maps." + mapName + ".bedPositions.yellow.2");
        Location greenBed1 = getLocation(world, "maps." + mapName + ".bedPositions.green.1");
        Location greenBed2 = getLocation(world, "maps." + mapName + ".bedPositions.green.2");


        double waitx = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".waitingpoint") .get("x")).doubleValue();
        double waity = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".waitingpoint") .get("y")).doubleValue();
        double waitz = ((Number) plugin.getConfig().getConfigurationSection("maps." + mapName + ".waitingpoint") .get("z")).doubleValue();

        Location waitPos = new Location(world, waitx, waity, waitz);


        List<Location> emeraldGens = getMidGenerators(world, "maps." + mapName + ".generators.emerald");
        List<Location> diamondGens = getMidGenerators(world, "maps." + mapName + ".generators.diamond");
        List<Location> goldGens = getMidGenerators(world, "maps." + mapName + ".generators.gold");


        Location reg = getLocation(world, "maps." + mapName + ".generators.emerald.base.red");
        Location rdg = getLocation(world, "maps." + mapName + ".generators.diamond.base.red");
        Location rgg = getLocation(world, "maps." + mapName + ".generators.gold.base.red");
        Location rig = getLocation(world, "maps." + mapName + ".generators.iron.base.red");


        Location beg = getLocation(world, "maps." + mapName + ".generators.emerald.base.blue");
        Location bdg = getLocation(world, "maps." + mapName + ".generators.diamond.base.blue");
        Location bgg = getLocation(world, "maps." + mapName + ".generators.gold.base.blue");
        Location big = getLocation(world, "maps." + mapName + ".generators.iron.base.blue");


        Location yeg = getLocation(world, "maps." + mapName + ".generators.emerald.base.yellow");
        Location ydg = getLocation(world, "maps." + mapName + ".generators.diamond.base.yellow");
        Location ygg = getLocation(world, "maps." + mapName + ".generators.gold.base.yellow");
        Location yig = getLocation(world, "maps." + mapName + ".generators.iron.base.yellow");


        Location geg = getLocation(world, "maps." + mapName + ".generators.emerald.base.green");
        Location gdg = getLocation(world, "maps." + mapName + ".generators.diamond.base.green");
        Location ggg = getLocation(world, "maps." + mapName + ".generators.gold.base.green");
        Location gig = getLocation(world, "maps." + mapName + ".generators.iron.base.green");


        Location ri = getLocation(world, "maps." + mapName + ".itemShopPositions.red");
        Location ru = getLocation(world, "maps." + mapName + ".upgradeShopPositions.red");
        Location bi = getLocation(world, "maps." + mapName + ".itemShopPositions.blue");
        Location bu = getLocation(world, "maps." + mapName + ".upgradeShopPositions.blue");
        Location yi = getLocation(world, "maps." + mapName + ".itemShopPositions.yellow");
        Location yu = getLocation(world, "maps." + mapName + ".upgradeShopPositions.yellow");
        Location gi = getLocation(world, "maps." + mapName + ".itemShopPositions.green");
        Location gu = getLocation(world, "maps." + mapName + ".upgradeShopPositions.green");


        if (redPlayer != null) {
            redPlayer.setDisplayName(Color.RED + redPlayer.getName());
        }
        if (bluePlayer != null) {
            bluePlayer.setDisplayName(Color.BLUE + bluePlayer.getName());
        }
        if (yellowPlayer != null) {
            yellowPlayer.setDisplayName(Color.YELLOW + yellowPlayer.getName());
        }
        if (greenPlayer != null) {
            greenPlayer.setDisplayName(Color.GREEN + greenPlayer.getName());
        }


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
                redPlayer,
                bluePlayer,
                yellowPlayer,
                greenPlayer,
                spawns,
                emeraldGens,
                diamondGens,
                goldGens,
                reg,
                rdg,
                rgg,
                rig,
                beg,
                bdg,
                bgg,
                big,
                yeg,
                ydg,
                ygg,
                yig,
                geg,
                gdg,
                ggg,
                gig,
                ri,
                ru,
                bi,
                bu,
                yi,
                yu,
                gi,
                gu,
                gameManager,
                new ItemStack(Material.RECOVERY_COMPASS));

        game.canRedRespawn = true;
        game.canBlueRespawn = true;
        game.canYellowRespawn = true;
        game.canGreenRespawn = true;

        game.isRedEliminated = false;
        game.isBlueEliminated = false;
        game.isYellowEliminated = false;
        game.isGreenEliminated = false;

        game.redUnlockedGen1 = false;
        game.redUnlockedGen2 = false;

        game.blueUnlockedGen1 = false;
        game.blueUnlockedGen2 = false;

        game.yellowUnlockedGen1 = false;
        game.yellowUnlockedGen2 = false;

        game.greenUnlockedGen1 = false;
        game.greenUnlockedGen2 = false;

        game.redIronCount = 0;
        game.blueIronCount = 0;
        game.yellowIronCount = 0;
        game.greenIronCount = 0;

        game.redGoldCount = 0;
        game.blueGoldCount = 0;
        game.yellowGoldCount = 0;
        game.greenGoldCount = 0;

        game.redDiamondCount = 0;
        game.blueDiamondCount = 0;
        game.yellowDiamondCount = 0;
        game.greenDiamondCount = 0;

        game.redEmeraldCount = 0;
        game.blueEmeraldCount = 0;
        game.yellowEmeraldCount = 0;
        game.greenEmeraldCount = 0;

        game.goldCooldown = 5;
        game.diamondCooldown = 15;
        game.emeraldCooldown = 30;

        game.redGoldCooldown = 5;
        game.redDiamondCooldown = 7;
        game.redEmeraldCooldown = 15;

        game.blueGoldCooldown = 5;
        game.blueDiamondCooldown = 7;
        game.blueEmeraldCooldown = 15;

        game.yellowGoldCooldown = 5;
        game.yellowDiamondCooldown = 7;
        game.yellowEmeraldCooldown = 15;

        game.greenGoldCooldown = 5;
        game.greenDiamondCooldown = 7;
        game.greenEmeraldCooldown = 15;

        game.goldCount = 0;
        game.diamondCount = 0;
        game.emeraldCount = 0;

        game.alivePlayers = 4;

        for (int i = 0; i < players.size(); i++) {
            game.addPlayer(players.get(i), i);
        }

        game.gameLoop();

        if (plugin.getConfig().getBoolean("bedwars.test-mode", false)) {
            if (bluePlayer == null) {
                world.spawnEntity(blueSpawn, EntityType.WARDEN);
            }
            if (yellowPlayer == null) {
                world.spawnEntity(yellowSpawn, EntityType.WARDEN);
            }
            if (greenPlayer == null) {
                world.spawnEntity(greenSpawn, EntityType.WARDEN);
            }
        }

        this.redPlayer = null;
        this.bluePlayer = null;
        this.yellowPlayer = null;
        this.greenPlayer = null;
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
