package me.Kulmodroid.serverPlugin.serverPlugin;

import me.Kulmodroid.serverPlugin.serverPlugin.commands.GameSelectionCommand;
import me.Kulmodroid.serverPlugin.serverPlugin.commands.PingCommand;
import me.Kulmodroid.serverPlugin.serverPlugin.commands.getPosCommand;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.Kulmodroid.serverPlugin.serverPlugin.items.BreezeRod;
import me.Kulmodroid.serverPlugin.serverPlugin.items.LightningStaff;
import me.Kulmodroid.serverPlugin.serverPlugin.items.PigBow;
import me.Kulmodroid.serverPlugin.serverPlugin.items.GameCompass;
import me.Kulmodroid.serverPlugin.serverPlugin.items.EditCompass;
import me.Kulmodroid.serverPlugin.serverPlugin.game.BedwarsQueue;
import me.Kulmodroid.serverPlugin.serverPlugin.items.JumpBow;
import me.Kulmodroid.serverPlugin.serverPlugin.CraftingDisabler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public final class ServerPlugin extends JavaPlugin implements Listener {

    private DuelManager duelManager;
    private GameSelection gameSelection;
    private WitchShop bedwarsShop;

    private LightningStaff lightningStaff;
    private PigBow pigBow;
    private BreezeRod breezeRod;
    private JumpBow jumpBow;
    private ZoneLimiter zoneLimiter;
    private BlockProtection blockProtection;
    private CraftingDisabler craftingDisabler;
    private BackupManager backupManager;
    private GameCompass gameCompass;
    private EditCompass editCompass;
    private BedwarsQueue bedwarsQueue;
    private MapEditSelection mapEditSelection;
    private World lobbyWorld;
    private boolean fireballOnCooldown;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        Bukkit.getServer().broadcastMessage(player.getName() + " joined to the server");
        player.teleport(lobbyWorld.getSpawnLocation());
        player.getInventory().addItem(gameCompass.getItem());
        player.setGameMode(GameMode.ADVENTURE);
        if (player.isOp() || player.hasPermission("serverPlugin.admin")) {
            player.setGameMode(GameMode.CREATIVE);
            player.getInventory().addItem(editCompass.getItem());
        }
        if (!player.isOp()) {
            return;
        }
        player.getInventory().addItem(lightningStaff.getItem());
        player.getInventory().addItem(pigBow.getItem());
        player.getInventory().addItem(breezeRod.getItem());
        player.getInventory().addItem(jumpBow.getItem());
        player.getInventory().addItem(new ItemStack(Material.ARROW, 3));
        player.getInventory().addItem(new ItemStack(Material.FIRE_CHARGE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 255));
        fireballOnCooldown = false;
    }

    @EventHandler
    public void onFireballInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || !event.getItem().equals(new ItemStack(Material.FIRE_CHARGE))) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            player.launchProjectile(Fireball.class).setVelocity(player.getLocation().getDirection().multiply(2.5));
            fireballOnCooldown = true;
            new BukkitRunnable() {
                int secondsLeft = 5;
                @Override
                public void run() {
                    if (secondsLeft <= 0) {
                        fireballOnCooldown = false;
                        cancel();
                    }
                    secondsLeft --;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("serverPlugin"), 0L, 20L);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.FIREBALL) {
            event.blockList().removeIf(block -> !block.getType().equals(Material.OAK_PLANKS));
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        if (stack == null) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (gameCompass.isCompass(stack)) {
            event.setCancelled(true);
            gameSelection.open(event.getPlayer());
        } else if (editCompass.isCompass(stack)) {
            if (event.getPlayer().isOp() || event.getPlayer().hasPermission("serverPlugin.admin")) {
                event.setCancelled(true);
                mapEditSelection.open(event.getPlayer());
            }
        }
    }


    @Override
    public void onEnable() {
        saveResource("config.yml", true);
        getLogger().warning(getConfig().saveToString());
        String lobbyMap = getConfig().getString("lobby", null);
        if (lobbyMap == null) {
            throw new IllegalArgumentException("lobbyMap must not be empty");
        }
        getLogger().warning("lobbyMap: " + lobbyMap);
        var section = getConfig().getConfigurationSection("maps");
        String lobbyPath = null;
        if (section != null) {
            var sec = section.getConfigurationSection(lobbyMap);
            if (sec != null) {
                lobbyPath = sec.getString("path", lobbyMap);
            }
        }
        if (lobbyPath == null) {
            throw new IllegalArgumentException("lobbyPath must not be empty");
        }
        getLogger().warning("lobbyPath: " + lobbyPath);
        lobbyWorld = Bukkit.createWorld(new WorldCreator(lobbyPath));
        World defaultWorld = lobbyWorld;

        duelManager = new DuelManager(this);
        bedwarsQueue = new BedwarsQueue(this);
        gameSelection = new GameSelection(duelManager, bedwarsQueue);
        mapEditSelection = new MapEditSelection(this);
        gameCompass = new GameCompass();
        editCompass = new EditCompass();
        bedwarsShop = new WitchShop(this);
        lightningStaff = new LightningStaff(this);
        pigBow = new PigBow(this);
        breezeRod = new BreezeRod(this);
        jumpBow = new JumpBow(this);
        blockProtection = new BlockProtection();
        craftingDisabler = new CraftingDisabler();
        backupManager = new BackupManager(this);
        BedwarsManager bedwarsManager = new BedwarsManager(
                this,
                new Location(defaultWorld, 0, 46, 0),
                new Location(defaultWorld, 0, 34, -50),
                5);

        for (World world : getServer().getWorlds()) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);

            WorldBorder border = world.getWorldBorder();
            border.setWarningDistance(0);
            border.setWarningTime(0);
        }

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(gameSelection, this);
        getServer().getPluginManager().registerEvents(duelManager, this);
        getServer().getPluginManager().registerEvents(bedwarsShop, this);
        getServer().getPluginManager().registerEvents(lightningStaff, this);
        getServer().getPluginManager().registerEvents(pigBow, this);
        getServer().getPluginManager().registerEvents(breezeRod, this);
        getServer().getPluginManager().registerEvents(jumpBow, this);
        getServer().getPluginManager().registerEvents(blockProtection, this);
        getServer().getPluginManager().registerEvents(craftingDisabler, this);
        getServer().getPluginManager().registerEvents(backupManager, this);
        getServer().getPluginManager().registerEvents(mapEditSelection, this);

        getCommand("gameselection").setExecutor(new GameSelectionCommand(gameSelection));
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("getPos").setExecutor(new getPosCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void setFireballOnCooldown(boolean fireballOnCooldown) {
        this.fireballOnCooldown = fireballOnCooldown;
    }
}
