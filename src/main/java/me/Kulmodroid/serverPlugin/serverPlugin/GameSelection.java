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

/**
 * Displays the game selection GUI.
 */
public class GameSelection implements Listener {

    /* Marker holder used to identify the custom inventory. */
    private static class GameSelectionHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private static final InventoryHolder HOLDER = new GameSelectionHolder();

    private final DuelManager duelManager;

    public GameSelection(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    /** Opens the GUI for the given player. */
    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(HOLDER, 9 * 4, ChatColor.BLUE + "Game selection");

        ItemStack swordItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordMeta = swordItem.getItemMeta();
        swordMeta.setDisplayName("Duel" + ChatColor.AQUA);
        swordItem.setItemMeta(swordMeta);
        gui.setItem(0, swordItem);

        ItemStack bedwarsItem4 = new ItemStack(Material.RED_BED);
        ItemMeta bedwarsMeta4 = bedwarsItem4.getItemMeta();
        bedwarsMeta4.setDisplayName("Bedwars squads" + ChatColor.AQUA);
        bedwarsItem4.setItemMeta(bedwarsMeta4);
        gui.setItem(0, bedwarsItem4);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof GameSelectionHolder)) {
            return;
        }

        event.setCancelled(true);
        if (event.getSlot() == 0 && event.getWhoClicked() instanceof Player player) {
            player.closeInventory();
            duelManager.queuePlayer(player);
        }
        if (event.getSlot() == 1 && event.getWhoClicked() instanceof Player player) {
            player.closeInventory();
            // BedwarsManager.queuePlayer(player);
        }
    }
}
