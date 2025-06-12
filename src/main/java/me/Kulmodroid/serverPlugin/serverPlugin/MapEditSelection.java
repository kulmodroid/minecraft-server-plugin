package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * GUI allowing admins to choose a map to edit.
 */
public class MapEditSelection implements Listener {
    private static class Holder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private static final InventoryHolder HOLDER = new Holder();

    private final JavaPlugin plugin;

    public MapEditSelection(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        plugin.getLogger().warning(plugin.getConfig().saveToString());
        List<String> maps = plugin.getConfig().getStringList("maps.edit-maps");
        Inventory gui = Bukkit.createInventory(HOLDER, 9, ChatColor.GOLD + "Edit Map");
        for (int i = 0; i < maps.size() && i < 9; i++) {
            ItemStack item = new ItemStack(Material.MAP);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(maps.get(i));
            item.setItemMeta(meta);
            gui.setItem(i, item);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getCurrentItem();
        if (stack == null || stack.getType() != Material.MAP || !stack.hasItemMeta()) {
            return;
        }
        String mapName = stack.getItemMeta().getDisplayName();
        String path = null;
        var maps = plugin.getConfig().getConfigurationSection("maps");
        if (maps != null) {
            var sec = maps.getConfigurationSection(mapName);
            if (sec != null) {
                path = sec.getString("path");
            }
        }
        if (path == null) {
            player.sendMessage(ChatColor.RED + "Map configuration not found for " + mapName);
            return;
        }
        player.closeInventory();
        String finalPath = path;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (Bukkit.getWorld(finalPath) == null) {
                Bukkit.createWorld(new org.bukkit.WorldCreator(finalPath));
            }
            player.teleport(Bukkit.getWorld(finalPath).getSpawnLocation());
            player.sendMessage(ChatColor.GREEN + "Editing map " + mapName);
        });
    }
}
