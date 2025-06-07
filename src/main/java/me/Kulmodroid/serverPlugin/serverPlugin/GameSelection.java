package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Displays the game selection GUI.
 */
public class GameSelection implements Listener {

    private final DuelManager duelManager;

    public GameSelection(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    /** Opens the GUI for the given player. */
    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9 * 4, ChatColor.BLUE + "Game selection");

        ItemStack swordItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordMeta = swordItem.getItemMeta();
        swordMeta.setDisplayName("Duel" + ChatColor.AQUA);
        swordItem.setItemMeta(swordMeta);

        gui.setItem(0, swordItem);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() != null) {
            return;
        }
        if (!ChatColor.stripColor(inv.getTitle()).equals("Game selection")) {
            return;
        }

        event.setCancelled(true);
        if (event.getSlot() == 0 && event.getWhoClicked() instanceof Player player) {
            player.closeInventory();
            duelManager.queuePlayer(player);
        }
    }
}
