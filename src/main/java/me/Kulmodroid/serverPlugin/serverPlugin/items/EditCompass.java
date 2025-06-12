package me.Kulmodroid.serverPlugin.serverPlugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Special compass for map editing.
 */
public class EditCompass {
    private static final ItemStack ITEM;

    static {
        ITEM = new ItemStack(Material.COMPASS);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Edit Maps");
        ITEM.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return ITEM.clone();
    }

    public boolean isCompass(ItemStack stack) {
        if (stack == null || stack.getType() != Material.COMPASS) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }
}
