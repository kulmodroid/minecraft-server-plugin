package me.Kulmodroid.serverPlugin.serverPlugin.game;

import me.Kulmodroid.serverPlugin.serverPlugin.GameManager;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static org.bukkit.ChatColor.*;

/**
 * Represents a running Bedwars game world.
 */
public class BedwarsGame implements Listener {
    private final JavaPlugin plugin;
    private final World world;
    private final Set<Player> players = new HashSet<>();
    private final List<Location> spawns;
    private final List<Location> midEmeraldGenerators;
    private final List<Location> midDiamondGenerators;
    private final List<Location> midGoldGenerators;
    private final Location redEmeraldGenerator;
    private final Location redDiamondGenerator;
    private final Location redGoldGenerator;
    private final Location redIronGenerator;
    private final Location blueEmeraldGenerator;
    private final Location blueDiamondGenerator;
    private final Location blueGoldGenerator;
    private final Location blueIronGenerator;
    private final Location yellowEmeraldGenerator;
    private final Location yellowDiamondGenerator;
    private final Location yellowGoldGenerator;
    private final Location yellowIronGenerator;
    private final Location greenEmeraldGenerator;
    private final Location greenDiamondGenerator;
    private final Location greenGoldGenerator;
    private final Location greenIronGenerator;
    private final Set<Location> placedBlocks = new HashSet<>();
    private final Map<Player, Location> lastValid = new HashMap<>();
    private final Path worldFolder;
    private final Vector max;
    private final Vector min;
    private final double fellY;
    private final double heightBuildLimit;
    private final double depthBuildLimit;
    private final Location waitPos;
    private final Location redSpawn;
    private final Location blueSpawn;
    private final Location yellowSpawn;
    private final Location greenSpawn;
    private final Location blueBed1;
    private final Location blueBed2;
    private final Location redBed1;
    private final Location redBed2;
    private final Location yellowBed1;
    private final Location yellowBed2;
    private final Location greenBed1;
    private final Location greenBed2;
    private final Player redPlayer;
    private final Player bluePlayer;
    private final Player yellowPlayer;
    private final Player greenPlayer;
    private final GameManager gameManager;
    private static ItemStack ITEM;

    public BedwarsGame(JavaPlugin plugin,
                       World world,
                       Path worldFolder,
                       Vector max, Vector min,
                       double fellY,
                       double heightBuildLimit,
                       double depthBuildLimit,
                       Location waitPos,
                       Location redSpawn,
                       Location blueSpawn,
                       Location yellowSpawn,
                       Location greenSpawn,
                       Location blueBed1,
                       Location blueBed2,
                       Location redBed1,
                       Location redBed2,
                       Location yellowBed1,
                       Location yellowBed2,
                       Location greenBed1,
                       Location greenBed2,
                       Player redPlayer,
                       Player bluePlayer,
                       Player yellowPlayer,
                       Player greenPlayer,
                       List<Location> spawns,
                       List<Location> midEmeraldGenerators,
                       List<Location> midDiamondGenerators,
                       List<Location> midGoldGenerators,
                       Location redEmeraldGenerator,
                       Location redDiamondGenerator,
                       Location redGoldGenerator,
                       Location redIronGenerator,
                       Location blueEmeraldGenerator,
                       Location blueDiamondGenerator,
                       Location blueGoldGenerator,
                       Location blueIronGenerator,
                       Location yellowEmeraldGenerator,
                       Location yellowDiamondGenerator,
                       Location yellowGoldGenerator,
                       Location yellowIronGenerator,
                       Location greenEmeraldGenerator,
                       Location greenDiamondGenerator,
                       Location greenGoldGenerator,
                       Location greenIronGenerator,
                       GameManager gameManger,
                       ItemStack ITEM
                       ) {
        this.plugin = plugin;
        this.world = world;
        this.worldFolder = worldFolder;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.max = max;
        this.min = min;
        this.fellY = fellY;
        this.heightBuildLimit = heightBuildLimit;
        this.depthBuildLimit = depthBuildLimit;
        this.waitPos = waitPos;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.yellowSpawn = yellowSpawn;
        this.greenSpawn = greenSpawn;
        this.blueBed1 = blueBed1;
        this.blueBed2 = blueBed2;
        this.redBed1 = redBed1;
        this.redBed2 = redBed2;
        this.yellowBed1 = yellowBed1;
        this.yellowBed2 = yellowBed2;
        this.greenBed1 = greenBed1;
        this.greenBed2 = greenBed2;
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.yellowPlayer = yellowPlayer;
        this.greenPlayer = greenPlayer;
        this.spawns = spawns;
        this.midEmeraldGenerators = midEmeraldGenerators;
        this.midDiamondGenerators = midDiamondGenerators;
        this.midGoldGenerators = midGoldGenerators;
        this.redEmeraldGenerator = redEmeraldGenerator;
        this.redDiamondGenerator = redDiamondGenerator;
        this.redGoldGenerator = redGoldGenerator;
        this.redIronGenerator = redIronGenerator;
        this.blueEmeraldGenerator = blueEmeraldGenerator;
        this.blueDiamondGenerator = blueDiamondGenerator;
        this.blueGoldGenerator = blueGoldGenerator;
        this.blueIronGenerator = blueIronGenerator;
        this.yellowEmeraldGenerator = yellowEmeraldGenerator;
        this.yellowDiamondGenerator = yellowDiamondGenerator;
        this.yellowGoldGenerator = yellowGoldGenerator;
        this.yellowIronGenerator = yellowIronGenerator;
        this.greenEmeraldGenerator = greenEmeraldGenerator;
        this.greenDiamondGenerator = greenDiamondGenerator;
        this.greenGoldGenerator = greenGoldGenerator;
        this.greenIronGenerator = greenIronGenerator;
        this.gameManager = gameManger;
        this.ITEM = ITEM;
    }

    public boolean canRedRespawn;
    public boolean canBlueRespawn;
    public boolean canYellowRespawn;
    public boolean canGreenRespawn;

    public boolean isRedEliminated;
    public boolean isBlueEliminated;
    public boolean isYellowEliminated;
    public boolean isGreenEliminated;


    public void addPlayer(Player player, int i) {
        players.add(player);

    }

    static {
        ITEM = new ItemStack(Material.RECOVERY_COMPASS);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(DARK_PURPLE + "Return to lobby");
        ITEM.setItemMeta(meta);
    }

    private void summonMaterial(ItemStack material, Location loc) {
        world.dropItem(loc, material);
    }

    public ItemStack getItem() {
        return ITEM.clone();
    }

    private boolean isCompass(ItemStack stack) {
        if (stack == null || stack.getType() != Material.RECOVERY_COMPASS) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isCompass(event.getItem())) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);
        // teleport player to lobby
        player.setGameMode(GameMode.ADVENTURE);
        if (player.isOp()) {
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    public void eliminatePlayer(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().addItem(ITEM);
    }

    public void onWin(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        new BukkitRunnable() {
            int secondsLeft = 10;
            boolean toggle = true;
            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    // send player to lobby
                    cancel();
                    return;
                }
                player.setDisplayName("Good boy");
                if (toggle) {
                    player.sendTitle("", ChatColor.RED + "You won!");
                    toggle = false;
                } else {
                    player.sendTitle("", ChatColor.BLUE + "You won!");
                    toggle = true;
                }
                player.getWorld().spawnEntity(new Location(world,
                        player.getLocation().getX(),
                        player.getLocation().getY() + 1, player.getLocation().getZ()),
                        EntityType.FIREWORK_ROCKET);
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        return;
    }

    private boolean nearSpawn(Location loc) {
        for (Location spawn : spawns) {
            if (spawn.getWorld().equals(loc.getWorld()) && spawn.distanceSquared(loc) <= 2.0) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        if (!event.getBlock().getWorld().equals(world)) {
            return;
        }
        if (nearSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
        if (event.getBlock().getY() <= depthBuildLimit) {
            event.setCancelled(true);
        }
        if (event.getBlock().getY() >= heightBuildLimit) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (event.getBlock().getLocation() == redBed1 | event.getBlock().getLocation() == redBed2) {
            canRedRespawn = false;
            return;
        }else if (event.getBlock().getLocation() == blueBed1 | event.getBlock().getLocation() == blueBed2) {
            canBlueRespawn = false;
            return;
        }else if (event.getBlock().getLocation() == yellowBed1 | event.getBlock().getLocation() == yellowBed2) {
            canYellowRespawn = false;
            return;
        } else if (event.getBlock().getLocation() == greenBed1 | event.getBlock().getLocation() == greenBed2) {
            canGreenRespawn = false;
            return;
        }

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

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player == redPlayer) {
            if (canRedRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            redPlayer.teleport(redSpawn);
                            redPlayer.sendTitle("", "§e You respawned! §f");
                            redPlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(redPlayer);
                isRedEliminated = true;
                if (isBlueEliminated && isYellowEliminated) {
                    onWin(greenPlayer);
                } else if (isBlueEliminated && isGreenEliminated) {
                    onWin(yellowPlayer);
                } else if (isGreenEliminated && isYellowEliminated) {
                    onWin(bluePlayer);
                }
                return;
            }
        } else if (player == bluePlayer) {
            if (canBlueRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            bluePlayer.teleport(blueSpawn);
                            bluePlayer.sendTitle("", "§e You respawned! §f");
                            bluePlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(bluePlayer);
                isBlueEliminated = true;
                if (isRedEliminated && isYellowEliminated) {
                    onWin(greenPlayer);
                } else if (isRedEliminated && isGreenEliminated) {
                    onWin(yellowPlayer);
                } else if (isGreenEliminated && isYellowEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        } else if (player == yellowPlayer) {
            if (canYellowRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            yellowPlayer.teleport(yellowSpawn);
                            yellowPlayer.sendTitle("", "§e You respawned! §f");
                            yellowPlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(yellowPlayer);
                isYellowEliminated = true;
                if (isRedEliminated && isBlueEliminated) {
                    onWin(greenPlayer);
                } else if (isRedEliminated && isGreenEliminated) {
                    onWin(bluePlayer);
                } else if (isGreenEliminated && isBlueEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        } else if (player == greenPlayer) {
            if (canGreenRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            greenPlayer.teleport(greenSpawn);
                            greenPlayer.sendTitle("", "§e You respawned! §f");
                            greenPlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(greenPlayer);
                isGreenEliminated = true;
                if (isRedEliminated && isBlueEliminated) {
                    onWin(yellowPlayer);
                } else if (isRedEliminated && isYellowEliminated) {
                    onWin(bluePlayer);
                } else if (isYellowEliminated && isBlueEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        }
    }

    public void onPlayerFell(Player player) {
        if (player == redPlayer) {
            if (canRedRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            redPlayer.teleport(redSpawn);
                            redPlayer.sendTitle("", "§e You respawned! §f");
                            redPlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(redPlayer);
                return;
            }
        } else if (player == bluePlayer) {
            if (canBlueRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            bluePlayer.teleport(blueSpawn);
                            bluePlayer.sendTitle("", "§e You respawned! §f");
                            bluePlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(bluePlayer);
                return;
            }
        } else if (player == yellowPlayer) {
            if (canYellowRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            yellowPlayer.teleport(yellowSpawn);
                            yellowPlayer.sendTitle("", "§e You respawned! §f");
                            yellowPlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(yellowPlayer);
                return;
            }
        } else if (player == greenPlayer) {
            if (canGreenRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            greenPlayer.teleport(greenSpawn);
                            greenPlayer.sendTitle("", "§e You respawned! §f");
                            greenPlayer.setGameMode(GameMode.SURVIVAL);
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(greenPlayer);
                return;
            }
        }
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
        if (to.getY() < fellY) {
            event.setCancelled(true);
            if (player != redPlayer && player != bluePlayer && player != yellowPlayer && player != greenPlayer) {
                player.kickPlayer(RED + "You're not supposed to join this game!");
            }
            onPlayerFell(player);
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
