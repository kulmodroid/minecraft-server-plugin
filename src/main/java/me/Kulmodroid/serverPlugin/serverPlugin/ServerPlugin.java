package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Material;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerPlugin extends JavaPlugin implements Listener {

    private DuelManager duelManager;
    private GameSelection gameSelection;
    private WitchShop witchShop;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to our server, " + event.getPlayer().getName() + ", your current ping is: " + event.getPlayer().getPing());
        event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
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

        if (event.getPlayer() != null) {
            event.setCancelled(true);
            gameSelection.open(event.getPlayer());
        }
    }

    @Override
    public void onEnable() {
        duelManager = new DuelManager(this);
        gameSelection = new GameSelection(duelManager);
        witchShop = new WitchShop(this);

        for (World world : getServer().getWorlds()) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(gameSelection, this);
        getServer().getPluginManager().registerEvents(duelManager, this);
        getServer().getPluginManager().registerEvents(witchShop, this);

        getCommand("gameselection").setExecutor(new GameSelectionCommand(gameSelection));
        getCommand("ping").setExecutor(new PingCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
