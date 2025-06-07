package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class ServerPlugin extends JavaPlugin implements Listener {

    private DuelManager duelManager;
    private GameSelection gameSelection;
    private WitchShop witchShop;

    private static final ItemStack LIGHTNING_STAFF;

    static {
        LIGHTNING_STAFF = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = LIGHTNING_STAFF.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Lightning Staff");
        LIGHTNING_STAFF.setItemMeta(meta);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to our server, " + event.getPlayer().getName() + ", your current ping is: " + event.getPlayer().getPing());
        event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
        event.getPlayer().getInventory().addItem(LIGHTNING_STAFF.clone());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (item.getType() == Material.COMPASS) {
            event.setCancelled(true);
            gameSelection.open(player);
        } else if (isLightningStaff(item)) {
            event.setCancelled(true);
            strikeLightningLine(player);
        }
    }

    private boolean isLightningStaff(ItemStack item) {
        if (item.getType() != Material.BLAZE_ROD) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(LIGHTNING_STAFF.getItemMeta().getDisplayName());
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
