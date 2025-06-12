package me.Kulmodroid.serverPlugin.serverPlugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Special compass that opens the game selection menu.
 */
public class GameCompass {
    private static final ItemStack ITEM;

    static {
        ITEM = new ItemStack(Material.COMPASS);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Game Selector");
        ITEM.setItemMeta(meta);
    }

    /** Returns a new instance of the compass item. */
    public ItemStack getItem() {
        return ITEM.clone();
    }

    /** Checks if the stack represents this compass. */
    public boolean isCompass(ItemStack stack) {
        if (stack == null || stack.getType() != Material.COMPASS) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }
}
