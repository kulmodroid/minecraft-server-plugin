package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.Kulmodroid.serverPlugin.serverPlugin.items.BreezeRod;
import me.Kulmodroid.serverPlugin.serverPlugin.items.LightningStaff;
import me.Kulmodroid.serverPlugin.serverPlugin.items.PigBow;
import me.Kulmodroid.serverPlugin.serverPlugin.items.JumpBow;

public final class ServerPlugin extends JavaPlugin implements Listener {

    private DuelManager duelManager;
    private GameSelection gameSelection;
    private WitchShop witchShop;
    private WateringCanMob wateringCanMob;

    private LightningStaff lightningStaff;
    private PigBow pigBow;
    private BreezeRod breezeRod;
    private JumpBow jumpBow;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Welcome to our server, " + player.getName() + ", your current ping is: " + player.getPing());
        player.getInventory().addItem(new ItemStack(Material.COMPASS));
        player.getInventory().addItem(lightningStaff.getItem());
        player.getInventory().addItem(pigBow.getItem());
        player.getInventory().addItem(breezeRod.getItem());
        player.getInventory().addItem(jumpBow.getItem());
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.COMPASS) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);
        gameSelection.open(event.getPlayer());
    }


    @Override
    public void onEnable() {
        duelManager = new DuelManager(this);
        gameSelection = new GameSelection(duelManager);
        witchShop = new WitchShop(this);
        wateringCanMob = new WateringCanMob(this);
        lightningStaff = new LightningStaff(this);
        pigBow = new PigBow(this);
        breezeRod = new BreezeRod(this);
        jumpBow = new JumpBow(this);

        for (World world : getServer().getWorlds()) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(gameSelection, this);
        getServer().getPluginManager().registerEvents(duelManager, this);
        getServer().getPluginManager().registerEvents(witchShop, this);
        getServer().getPluginManager().registerEvents(wateringCanMob, this);
        getServer().getPluginManager().registerEvents(lightningStaff, this);
        getServer().getPluginManager().registerEvents(pigBow, this);
        getServer().getPluginManager().registerEvents(breezeRod, this);
        getServer().getPluginManager().registerEvents(jumpBow, this);

        getCommand("gameselection").setExecutor(new GameSelectionCommand(gameSelection));
        getCommand("ping").setExecutor(new PingCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
