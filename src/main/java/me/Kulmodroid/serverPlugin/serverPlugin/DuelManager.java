package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles player queueing and running of duels.
 */
public class DuelManager implements Listener {

    private final JavaPlugin plugin;
    private final Set<Player> activePlayers = new HashSet<>();
    private Player waitingPlayer;

    public DuelManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds the player to the duel queue or starts a duel if someone is waiting.
     */
    public synchronized void queuePlayer(Player player) {
        if (waitingPlayer == null) {
            waitingPlayer = player;
            player.sendMessage("Waiting for the opponent...");
        } else {
            Player opponent = waitingPlayer;
            waitingPlayer = null;
            startDuel(player, opponent);
        }
    }

    private void startDuel(Player p1, Player p2) {
        activePlayers.add(p1);
        activePlayers.add(p2);
        Bukkit.getScheduler().runTask(plugin, () -> {
            equip(p1);
            equip(p2);
            p1.teleport(new Location(p1.getWorld(), 2000, 2, 15));
            p2.teleport(new Location(p2.getWorld(), 2000, 2, -1));
            Location respawn = new Location(p1.getWorld(), 2000, 2, 7);
            p1.setRespawnLocation(respawn);
            p2.setRespawnLocation(respawn);
        });
    }

    private void equip(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(1, new ItemStack(Material.DIAMOND_SWORD));
        player.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE, 2));
        player.getInventory().setItem(39, new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setItem(38, new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setItem(37, new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setItem(36, new ItemStack(Material.DIAMOND_BOOTS));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        if (!activePlayers.contains(dead)) {
            return;
        }
        activePlayers.remove(dead);
        Player winner = dead.getKiller();
        if (winner != null) {
            activePlayers.remove(winner);
            endDuel(dead, winner);
        }
    }

    private void endDuel(Player loser, Player winner) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            loser.getInventory().clear();
            winner.getInventory().clear();
            loser.teleport(new Location(loser.getWorld(), 19, 12, 0));
            winner.teleport(new Location(winner.getWorld(), 19, 12, 0));
            loser.sendMessage("You lost!");
            winner.sendMessage("You won!");
            Bukkit.broadcastMessage(loser.getName() + " was slain by " + winner.getName());
        });
    }
}
