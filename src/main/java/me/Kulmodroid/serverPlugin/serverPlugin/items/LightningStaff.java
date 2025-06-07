package me.Kulmodroid.serverPlugin.serverPlugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * Lightning staff item that strikes lightning in a line when used.
 */
public class LightningStaff implements Listener {

    /** The display item for this staff. */
    private static final ItemStack ITEM;

    static {
        ITEM = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Lightning Staff");
        ITEM.setItemMeta(meta);
    }

    private final JavaPlugin plugin;

    public LightningStaff(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Returns a copy of the lightning staff item. */
    public ItemStack getItem() {
        return ITEM.clone();
    }

    private boolean isStaff(ItemStack stack) {
        if (stack == null || stack.getType() != Material.BLAZE_ROD) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isStaff(event.getItem())) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);
        strikeLightningLine(event.getPlayer());
    }

    private void strikeLightningLine(Player player) {
        Location start = player.getLocation();
        Vector direction = start.getDirection().normalize();
        World world = player.getWorld();
        for (int i = 1; i <= 20; i++) {
            Location loc = start.clone().add(direction.clone().multiply(i));
            world.strikeLightning(loc);
        }
    }
}
