package me.Kulmodroid.serverPlugin.serverPlugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Bow that teleports the shooter to the arrow location or a spawned block.
 */
public class JumpBow implements Listener {

    private static final String ARROW_KEY = "jump-bow-arrow";

    private static final ItemStack ITEM;

    static {
        ITEM = new ItemStack(Material.BOW);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Jump Bow");
        ITEM.setItemMeta(meta);
    }

    private final JavaPlugin plugin;

    public JumpBow(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Returns a copy of the jump bow item. */
    public ItemStack getItem() {
        return ITEM.clone();
    }

    private boolean isJumpBow(ItemStack stack) {
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
        if (isJumpBow(event.getBow())) {
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
        Player shooter = (Player) arrow.getShooter();
        if (shooter == null) {
            return;
        }

        Block hitBlock = event.getHitBlock();
        BlockFace face = event.getHitBlockFace();
        Location teleportLoc = null;

        if (hitBlock != null && face != null) {
            if (face == BlockFace.UP || face == BlockFace.DOWN) {
                teleportLoc = arrow.getLocation();
            } else {
                Block target = hitBlock.getRelative(face);
                if (target.getType() == Material.AIR || target.isPassable()) {
                    target.setType(Material.STONE);
                    Location loc = target.getLocation().add(0.5, 1, 0.5);
                    teleportLoc = loc.clone();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            target.setType(Material.AIR);
                        }
                    }.runTaskLater(plugin, 20L * 5);
                }
            }
        }

        arrow.remove();
        if (teleportLoc != null) {
            shooter.teleport(teleportLoc);
        }
    }
}
