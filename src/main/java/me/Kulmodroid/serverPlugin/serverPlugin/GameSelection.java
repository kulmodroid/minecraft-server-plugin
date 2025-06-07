package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GameSelection implements Listener
{
    boolean isDuelOpponentHere;
    boolean isDuelOpponentWaiting;

    private Inventory gameSelection;

    public void openNewGui(Player player)
    {
        gameSelection = Bukkit.createInventory(null, 9 * 4, ChatColor.BLUE + "Game selection");

        ItemStack swordItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordMeta = swordItem.getItemMeta();
        swordMeta.setDisplayName("Duel" + ChatColor.AQUA);
        swordItem.setItemMeta(swordMeta);

        ItemStack bedItem = new ItemStack(Material.RED_BED);
        ItemMeta bedMeta = bedItem.getItemMeta();
        bedMeta.setDisplayName("Bedfight" + ChatColor.RED);
        bedItem.setItemMeta(bedMeta);

        gameSelection.setItem(0, swordItem);

        gameSelection.setItem(1, bedItem);

        player.openInventory(gameSelection);
    }

    @EventHandler
    public void guiClickEvent(InventoryClickEvent event)
    {
        if(!event.getInventory().equals(gameSelection))
        {
            return;
        }

        event.setCancelled(true);



        switch(event.getSlot())
        {
            case 0:
                if (!isDuelOpponentWaiting)
                {
                    Player duelPlayer1 = (Player) event.getWhoClicked();
                    duelPlayer1.closeInventory();
                    duelPlayer1.sendMessage(ChatColor.RED + "Waiting for the opponent...");
                    new DuelExecuter().onDuelQueue(duelPlayer1);
                }
                if(isDuelOpponentWaiting)
                {
                    Player duelPlayer2 = (Player) event.getWhoClicked();
                    new DuelExecuter().player2 = duelPlayer2;
                    isDuelOpponentHere = true;
                    duelPlayer2.teleport(new Location(duelPlayer2.getLocation().getWorld(), 2000, 2, -1));
                    isDuelOpponentHere = false;
                }

                break;

            case 1:
                Player bedfightPlayer = (Player) event.getWhoClicked();
                bedfightPlayer.closeInventory();
                bedfightPlayer.sendMessage(ChatColor.RED +"You are queueing for the Bedfight (programming in progress... uhhh...)");
//              // BEDFIGHT
                break;
        }
    }
    @EventHandler
    public void openGuiEvent(GameSelectionEvent event)
    {
        openNewGui(event.getPlayer());
    }
}
