package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BedwarsManager extends GameManager {

    private final JavaPlugin plugin;
    private final Location waitingLoc;
    private final Location respawnLoc;
    private final int respawnDelaySec;

    public BedwarsManager(
            JavaPlugin plugin,
            Location waitingLoc,
            Location respawnLoc,
            int respawnDelaySec) {
        this.plugin = plugin;
        this.waitingLoc = waitingLoc;
        this.respawnLoc = respawnLoc;
        this.respawnDelaySec = respawnDelaySec;
    }

    public void onPlayerFell(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(waitingLoc);
        });

        new BukkitRunnable() {
            int secondsLeft = respawnDelaySec;

            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    player.teleport(respawnLoc);
                    player.sendTitle("", "§e You respawned! §f");
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                }
                player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                        0, 20, 0);
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}