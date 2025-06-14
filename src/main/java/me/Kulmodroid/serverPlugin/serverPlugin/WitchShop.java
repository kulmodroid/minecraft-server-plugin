package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Handles the witch shop NPC and its GUI.
 */
public class WitchShop implements Listener {

    /* Marker holder used to identify the shop inventory. */
    private static class ShopHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private static final InventoryHolder HOLDER = new ShopHolder();

    private final JavaPlugin plugin;
    private final Set<UUID> witchIds = new HashSet<>();

    private void logSpawn(World world, Location loc) {
        int count = world.getEntitiesByClass(Witch.class).size();
        plugin.getLogger().info(
                "Spawning witch in world '" + world.getName() + "' at " +
                loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() +
                " (total witches: " + count + ")");
    }

    private void cleanupOldWitches() {
        for (World world : Bukkit.getServer().getWorlds()) {
            int removed = 0;
            for (Entity e : world.getEntities()) {
                if (e instanceof Witch w && (ChatColor.DARK_PURPLE + "Shopkeeper").equals(w.getCustomName())) {
                    w.remove();
                    removed++;
                }
            }
            if (removed > 0) {
                plugin.getLogger().info("Removed " + removed + " old witches in world '" + world.getName() + "'");
            }
        }
        witchIds.clear();
    }
    private Inventory shopInv;

    public WitchShop(JavaPlugin plugin) {
        this.plugin = plugin;
        cleanupOldWitches();
        spawnWitches();
        setupInventory();

        // Periodically ensure the shopkeeper exists in case the entity was
        // removed by the server (for example in peaceful mode).
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkWitches, 20L, 20L * 60L);
    }

    private void spawnWitches() {
        for (World world : Bukkit.getServer().getWorlds()) {
            spawnWitch(world);
        }
    }

    private void spawnWitch(World world) {
        Location loc = new Location(world, -50, 11, 25);

        // If a witch with our custom name already exists, reuse it.
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Witch existing
                    && (ChatColor.DARK_PURPLE + "Shopkeeper").equals(existing.getCustomName())) {
                witchIds.add(existing.getUniqueId());
                logSpawn(world, existing.getLocation());
                return;
            }
        }

        Witch witch = (Witch) world.spawnEntity(loc, EntityType.WITCH);
        witch.setAI(false);
        witch.setCustomName(ChatColor.DARK_PURPLE + "Shopkeeper");
        witch.setCustomNameVisible(true);
        witch.setRemoveWhenFarAway(false);
        witch.setInvulnerable(true);
        try {
            witch.setPersistent(true);
        } catch (NoSuchMethodError ignore) {
            // method not available on older API versions
        }
        witchIds.add(witch.getUniqueId());
        logSpawn(world, loc);
    }

    private void setupInventory() {
        shopInv = Bukkit.createInventory(HOLDER, 9, ChatColor.DARK_PURPLE + "Shop");

        addArmorPiece(0, Material.DIAMOND_HELMET);
        addArmorPiece(1, Material.DIAMOND_CHESTPLATE);
        addArmorPiece(2, Material.DIAMOND_LEGGINGS);
        addArmorPiece(3, Material.DIAMOND_BOOTS);
    }

    private void addArmorPiece(int slot, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(material.name().toLowerCase() + " - 1 GOLD BLOCK");
        item.setItemMeta(meta);
        shopInv.setItem(slot, item);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!witchIds.contains(entity.getUniqueId())) {
            return;
        }
        Player player = event.getPlayer();
        player.openInventory(shopInv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopHolder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        if (!player.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 1)) {
            player.sendMessage(ChatColor.RED + "Not enough gold ingots!");
            return;
        }

        player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 1));
        player.getInventory().addItem(new ItemStack(clicked.getType()));
        player.sendMessage(ChatColor.GREEN + "Purchased " + clicked.getType().name().toLowerCase());
    }

    // Prevent natural despawn or damage to the witch
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (witchIds.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (witchIds.contains(event.getEntity().getUniqueId())) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        spawnWitch(event.getWorld());
    }
    
    /**
     * Verify the shopkeeper is present in each world and respawn if missing.
     */
    private void checkWitches() {
        for (World world : Bukkit.getServer().getWorlds()) {
            java.util.List<Witch> shops = new java.util.ArrayList<>();
            for (Entity e : world.getEntities()) {
                if (e instanceof Witch w &&
                        (ChatColor.DARK_PURPLE + "Shopkeeper").equals(w.getCustomName())) {
                    shops.add(w);
                }
            }

            if (shops.isEmpty()) {
                spawnWitch(world);
                continue;
            }

            Witch keeper = shops.get(0);
            witchIds.add(keeper.getUniqueId());
            int removed = 0;
            for (int i = 1; i < shops.size(); i++) {
                shops.get(i).remove();
                removed++;
            }
            if (removed > 0) {
                plugin.getLogger().info(
                        "Removed " + removed + " extra witches in world '" + world.getName() + "'");
            }
        }
    }
}
