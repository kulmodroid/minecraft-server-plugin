package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.GameRule;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pig;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class ServerPlugin extends JavaPlugin implements Listener {

    private DuelManager duelManager;
    private GameSelection gameSelection;
    private WitchShop witchShop;

    private static final ItemStack LIGHTNING_STAFF;
    private static final ItemStack PIG_BOW;
    private static final String PIG_ARROW_KEY = "pig-bow-arrow";

    static {
        LIGHTNING_STAFF = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = LIGHTNING_STAFF.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Lightning Staff");
        LIGHTNING_STAFF.setItemMeta(meta);

        PIG_BOW = new ItemStack(Material.BOW);
        ItemMeta bowMeta = PIG_BOW.getItemMeta();
        bowMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pig Bow");
        PIG_BOW.setItemMeta(bowMeta);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to our server, " + event.getPlayer().getName() + ", your current ping is: " + event.getPlayer().getPing());
        event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
        event.getPlayer().getInventory().addItem(LIGHTNING_STAFF.clone());
        event.getPlayer().getInventory().addItem(PIG_BOW.clone());
        event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 64));
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
        } else if (isPigBow(item)) {
            // Right click with pig bow should not trigger any special action
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack bow = event.getBow();
        if (bow != null && isPigBow(bow)) {
            event.getProjectile().setMetadata(PIG_ARROW_KEY,
                    new FixedMetadataValue(this, true));
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }
        if (!arrow.hasMetadata(PIG_ARROW_KEY)) {
            return;
        }

        arrow.removeMetadata(PIG_ARROW_KEY, this);
        Location loc = arrow.getLocation();
        World world = arrow.getWorld();
        arrow.remove();
        for (int i = 0; i < 5; i++) {
            Pig pig = (Pig) world.spawnEntity(loc, EntityType.PIG);
            Vector velocity = new Vector(Math.random() - 0.5, 0.5, Math.random() - 0.5);
            pig.setVelocity(velocity);
        }
    }

    private boolean isLightningStaff(ItemStack item) {
        if (item.getType() != Material.BLAZE_ROD) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(LIGHTNING_STAFF.getItemMeta().getDisplayName());
    }

    private boolean isPigBow(ItemStack item) {
        if (item.getType() != Material.BOW) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(PIG_BOW.getItemMeta().getDisplayName());
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
