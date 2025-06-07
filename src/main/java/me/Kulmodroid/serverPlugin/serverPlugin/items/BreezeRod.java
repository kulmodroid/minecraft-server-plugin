package me.Kulmodroid.serverPlugin.serverPlugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Item that summons a circle of vexes when used.
 */
public class BreezeRod implements Listener {

    /** Display item for the rod. */
    private static final ItemStack ITEM;
    /** Metadata key used to mark vexes spawned by this rod. */
    private static final String VEX_OWNER_KEY = "breeze-rod-owner";

    private final JavaPlugin plugin;

    static {
        ITEM = new ItemStack(Material.BREEZE_ROD);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Breeze Rod");
        ITEM.setItemMeta(meta);
    }

    public BreezeRod(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Returns a copy of the Breeze Rod item. */
    public ItemStack getItem() {
        return ITEM.clone();
    }

    private boolean isBreezeRod(ItemStack stack) {
        if (stack == null || stack.getType() != Material.BREEZE_ROD) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isBreezeRod(event.getItem())) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);
        spawnVexes(event.getPlayer());
    }

    private void spawnVexes(Player player) {
        Location center = player.getLocation();
        World world = player.getWorld();
        double radius = 5.0;
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(world, x, center.getY(), z);
            Mob vex = (Mob) world.spawnEntity(loc, EntityType.VEX);
            vex.setMetadata(VEX_OWNER_KEY,
                    new FixedMetadataValue(plugin, player.getUniqueId().toString()));
            Player target = findNearestTarget(player, vex.getLocation());
            if (target != null) {
                vex.setTarget(target);
            }
        }
    }

    private Player findNearestTarget(Player summoner, Location from) {
        Player closest = null;
        double best = Double.MAX_VALUE;
        for (Player p : from.getWorld().getPlayers()) {
            if (p.equals(summoner)) {
                continue;
            }
            double dist = p.getLocation().distanceSquared(from);
            if (dist < best) {
                best = dist;
                closest = p;
            }
        }
        return closest;
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob mob)) {
            return;
        }
        if (!mob.hasMetadata(VEX_OWNER_KEY)) {
            return;
        }
        String ownerId = mob.getMetadata(VEX_OWNER_KEY).get(0).asString();
        if (event.getTarget() instanceof Player player
                && player.getUniqueId().toString().equals(ownerId)) {
            event.setCancelled(true);
            Player newTarget = findNearestTarget(player, mob.getLocation());
            if (newTarget != null) {
                mob.setTarget(newTarget);
            }
        }
    }
}
