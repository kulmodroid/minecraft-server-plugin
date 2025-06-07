package me.Kulmodroid.serverPlugin.serverPlugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * Bow that spawns pigs where arrows land.
 */
public class PigBow implements Listener {

    /** Metadata key to tag arrows shot from this bow. */
    private static final String ARROW_KEY = "pig-bow-arrow";

    /** Display item for the bow. */
    private static final ItemStack ITEM;

    static {
        ITEM = new ItemStack(Material.BOW);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pig Bow");
        ITEM.setItemMeta(meta);
    }

    private final JavaPlugin plugin;

    public PigBow(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Returns a copy of the bow item. */
    public ItemStack getItem() {
        return ITEM.clone();
    }

    private boolean isPigBow(ItemStack stack) {
        if (stack == null || stack.getType() != Material.BOW) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (isPigBow(event.getBow())) {
            event.getProjectile().setMetadata(ARROW_KEY, new FixedMetadataValue(plugin, true));
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }
        if (!arrow.hasMetadata(ARROW_KEY)) {
            return;
        }

        arrow.removeMetadata(ARROW_KEY, plugin);
        Location loc = arrow.getLocation();
        World world = arrow.getWorld();
        arrow.remove();
        for (int i = 0; i < 5; i++) {
            Pig pig = (Pig) world.spawnEntity(loc, EntityType.PIG);
            Vector velocity = new Vector(Math.random() - 0.5, 0.5, Math.random() - 0.5);
            pig.setVelocity(velocity);
        }
    }
}
