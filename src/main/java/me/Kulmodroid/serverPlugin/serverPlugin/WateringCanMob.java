package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Spawns and maintains the peaceful "walking watering can" mob.
 */
public class WateringCanMob implements Listener {

    private final JavaPlugin plugin;
    private final Set<UUID> canIds = new HashSet<>();

    public WateringCanMob(JavaPlugin plugin) {
        this.plugin = plugin;
        spawnCans();
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkCans, 20L, 20L * 60L);
    }

    private void spawnCans() {
        for (World world : Bukkit.getServer().getWorlds()) {
            spawnCan(world);
        }
    }

    private void spawnCan(World world) {
        Location loc = new Location(world, -48, 11, 25); // near the witch shop

        for (Entity entity : world.getEntities()) {
            if (entity instanceof ArmorStand stand && ChatColor.GRAY + "Walking Watering Can".equals(stand.getCustomName())) {
                canIds.add(stand.getUniqueId());
                return;
            }
        }

        ArmorStand stand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM);
        stand.setCustomName(ChatColor.GRAY + "Walking Watering Can");
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setArms(true);
        stand.setBasePlate(false);
        stand.setSmall(true);
        stand.setHelmet(new ItemStack(Material.WATER_BUCKET));
        stand.setRemoveWhenFarAway(false);
        stand.setInvulnerable(true);
        try {
            stand.setPersistent(true);
        } catch (NoSuchMethodError ignore) {
            // older API
        }
        canIds.add(stand.getUniqueId());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        spawnCan(event.getWorld());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (canIds.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (canIds.contains(event.getEntity().getUniqueId())) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

    /**
     * Verify the mob is present in each world and respawn if missing.
     */
    private void checkCans() {
        for (World world : Bukkit.getServer().getWorlds()) {
            boolean found = false;
            loop: for (UUID id : new HashSet<>(canIds)) {
                for (Entity e : world.getEntities()) {
                    if (e instanceof ArmorStand && e.getUniqueId().equals(id)) {
                        found = true;
                        break loop;
                    }
                }
            }
            if (!found) {
                spawnCan(world);
            }
        }
    }
}
