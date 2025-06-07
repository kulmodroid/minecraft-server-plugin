package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getWorld;

public class DuelExecuter
{
    public Player player2;

    public void onDuelQueue(Player player1)
    {
        new GameSelection().isDuelOpponentWaiting = true;
        while (true) {
            if (new GameSelection().isDuelOpponentHere){

                player1.teleport(new Location(player1.getLocation().getWorld(), 2000, 2, 15));
                new GameSelection().isDuelOpponentWaiting = false;
                onDuel(player1, player2);
                break;
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onDuel(Player player1, Player player2)
    {
        player1.setRespawnLocation(new Location(player1.getLocation().getWorld(), 2000, 2, 7));
        player2.setRespawnLocation(new Location(player1.getLocation().getWorld(), 2000, 2, 7));

        while (true)
        {
            ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
            ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 2);

            ItemStack diamondHelmet = new ItemStack(Material.DIAMOND_HELMET);
            ItemStack diamondChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
            ItemStack diamondLeggings = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemStack diamondBoots = new ItemStack(Material.DIAMOND_BOOTS);

            player1.getInventory().setItem(0, diamondSword);
            player2.getInventory().setItem(0, diamondSword);
            player1.getInventory().setItem(1, goldenApple);
            player2.getInventory().setItem(1, goldenApple);
            player1.getInventory().setItem(103, diamondHelmet);
            player2.getInventory().setItem(103, diamondHelmet);
            player1.getInventory().setItem(102, diamondChestplate);
            player2.getInventory().setItem(102, diamondChestplate);
            player1.getInventory().setItem(101, diamondLeggings);
            player2.getInventory().setItem(101, diamondLeggings);
            player1.getInventory().setItem(100, diamondBoots);
            player2.getInventory().setItem(100, diamondBoots);

            if (player1.isDead())
            {
                onPlayerDeath(player1, player2);

                break;
            }
            if (player2.isDead())
            {
                onPlayerDeath(player2, player1);

                break;
            }
        }
    }

    public void onPlayerDeath(Player deadPlayer, Player alivePlayer)
    {
        deadPlayer.getInventory().clear();
        alivePlayer.getInventory().clear();

        deadPlayer.setInvisible(true);

        deadPlayer.sendMessage("You lost!");
        alivePlayer.sendMessage("You won!");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deadPlayer.teleport(new Location(deadPlayer.getLocation().getWorld(), 19, 12, 0));

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        alivePlayer.teleport(new Location(deadPlayer.getLocation().getWorld(), 19, 12, 0));

        Bukkit.getServer().broadcastMessage(deadPlayer.getName() + " was slain by " + alivePlayer.getName());
    }

}
