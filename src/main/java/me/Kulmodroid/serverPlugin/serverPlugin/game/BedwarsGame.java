package me.Kulmodroid.serverPlugin.serverPlugin.game;

import me.Kulmodroid.serverPlugin.serverPlugin.GameManager;
import me.Kulmodroid.serverPlugin.serverPlugin.WitchShop;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Represents a running Bedwars game world.
 */
public class BedwarsGame implements Listener {
    private final JavaPlugin plugin;
    private final World world;
    private final Set<Player> players = new HashSet<>();
    private final List<Location> spawns;
    private final List<Location> midEmeraldGenerators;
    private final List<Location> midDiamondGenerators;
    private final List<Location> midGoldGenerators;
    private final Location redEmeraldGenerator;
    private final Location redDiamondGenerator;
    private final Location redGoldGenerator;
    private final Location redIronGenerator;
    private final Location blueEmeraldGenerator;
    private final Location blueDiamondGenerator;
    private final Location blueGoldGenerator;
    private final Location blueIronGenerator;
    private final Location yellowEmeraldGenerator;
    private final Location yellowDiamondGenerator;
    private final Location yellowGoldGenerator;
    private final Location yellowIronGenerator;
    private final Location greenEmeraldGenerator;
    private final Location greenDiamondGenerator;
    private final Location greenGoldGenerator;
    private final Location greenIronGenerator;
    private final Set<Location> placedBlocks = new HashSet<>();
    private final Map<Player, Location> lastValid = new HashMap<>();
    private final Path worldFolder;
    private final Vector max;
    private final Vector min;
    private final double fellY;
    private final double heightBuildLimit;
    private final double depthBuildLimit;
    private final Location waitPos;
    private final Location redSpawn;
    private final Location blueSpawn;
    private final Location yellowSpawn;
    private final Location greenSpawn;
    private final Location blueBed1;
    private final Location blueBed2;
    private final Location redBed1;
    private final Location redBed2;
    private final Location yellowBed1;
    private final Location yellowBed2;
    private final Location greenBed1;
    private final Location greenBed2;
    private final Location redItemShopPos;
    private final Location redUpgradeShopPos;
    private final Location blueItemShopPos;
    private final Location blueUpgradeShopPos;
    private final Location yellowItemShopPos;
    private final Location yellowUpgradeShopPos;
    private final Location greenItemShopPos;
    private final Location greenUpgradeShopPos;
    private final Player redPlayer;
    private final Player bluePlayer;
    private final Player yellowPlayer;
    private final Player greenPlayer;
    private final GameManager gameManager;
    private static ItemStack ITEM;
    private static final InventoryHolder HOLDER = new BedwarsGame.ShopHolder();
    private static final Set<Material> NON_DROPPABLE_ITEMS = EnumSet.of(
            Material.SHEARS,
            Material.WOODEN_SWORD,
            Material.WOODEN_PICKAXE,
            Material.WOODEN_AXE,
            Material.STONE_SWORD,
            Material.STONE_PICKAXE,
            Material.STONE_AXE,
            Material.IRON_SWORD,
            Material.IRON_PICKAXE,
            Material.IRON_AXE,
            Material.DIAMOND_SWORD,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_AXE,
            Material.LEATHER_BOOTS,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_HELMET,
            Material.CHAINMAIL_BOOTS,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_HELMET,
            Material.IRON_BOOTS,
            Material.IRON_LEGGINGS,
            Material.IRON_CHESTPLATE,
            Material.IRON_HELMET,
            Material.DIAMOND_BOOTS,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_HELMET
    );
    private final Set<UUID> shopIds = new HashSet<>();

    public BedwarsGame(JavaPlugin plugin,
                       World world,
                       Path worldFolder,
                       Vector max, Vector min,
                       double fellY,
                       double heightBuildLimit,
                       double depthBuildLimit,
                       Location waitPos,
                       Location redSpawn,
                       Location blueSpawn,
                       Location yellowSpawn,
                       Location greenSpawn,
                       Location blueBed1,
                       Location blueBed2,
                       Location redBed1,
                       Location redBed2,
                       Location yellowBed1,
                       Location yellowBed2,
                       Location greenBed1,
                       Location greenBed2,
                       Player redPlayer,
                       Player bluePlayer,
                       Player yellowPlayer,
                       Player greenPlayer,
                       List<Location> spawns,
                       List<Location> midEmeraldGenerators,
                       List<Location> midDiamondGenerators,
                       List<Location> midGoldGenerators,
                       Location redEmeraldGenerator,
                       Location redDiamondGenerator,
                       Location redGoldGenerator,
                       Location redIronGenerator,
                       Location blueEmeraldGenerator,
                       Location blueDiamondGenerator,
                       Location blueGoldGenerator,
                       Location blueIronGenerator,
                       Location yellowEmeraldGenerator,
                       Location yellowDiamondGenerator,
                       Location yellowGoldGenerator,
                       Location yellowIronGenerator,
                       Location greenEmeraldGenerator,
                       Location greenDiamondGenerator,
                       Location greenGoldGenerator,
                       Location greenIronGenerator,
                       Location redItemShopPos,
                       Location redUpgradeShopPos,
                       Location blueItemShopPos,
                       Location blueUpgradeShopPos,
                       Location yellowItemShopPos,
                       Location yellowUpgradeShopPos,
                       Location greenItemShopPos,
                       Location greenUpgradeShopPos,
                       GameManager gameManger,
                       ItemStack ITEM
                       ) {
        this.plugin = plugin;
        this.world = world;
        this.worldFolder = worldFolder;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.max = max;
        this.min = min;
        this.fellY = fellY;
        this.heightBuildLimit = heightBuildLimit;
        this.depthBuildLimit = depthBuildLimit;
        this.waitPos = waitPos;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.yellowSpawn = yellowSpawn;
        this.greenSpawn = greenSpawn;
        this.blueBed1 = blueBed1;
        this.blueBed2 = blueBed2;
        this.redBed1 = redBed1;
        this.redBed2 = redBed2;
        this.yellowBed1 = yellowBed1;
        this.yellowBed2 = yellowBed2;
        this.greenBed1 = greenBed1;
        this.greenBed2 = greenBed2;
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.yellowPlayer = yellowPlayer;
        this.greenPlayer = greenPlayer;
        this.spawns = spawns;
        this.midEmeraldGenerators = midEmeraldGenerators;
        this.midDiamondGenerators = midDiamondGenerators;
        this.midGoldGenerators = midGoldGenerators;
        this.redEmeraldGenerator = redEmeraldGenerator;
        this.redDiamondGenerator = redDiamondGenerator;
        this.redGoldGenerator = redGoldGenerator;
        this.redIronGenerator = redIronGenerator;
        this.blueEmeraldGenerator = blueEmeraldGenerator;
        this.blueDiamondGenerator = blueDiamondGenerator;
        this.blueGoldGenerator = blueGoldGenerator;
        this.blueIronGenerator = blueIronGenerator;
        this.yellowEmeraldGenerator = yellowEmeraldGenerator;
        this.yellowDiamondGenerator = yellowDiamondGenerator;
        this.yellowGoldGenerator = yellowGoldGenerator;
        this.yellowIronGenerator = yellowIronGenerator;
        this.greenEmeraldGenerator = greenEmeraldGenerator;
        this.greenDiamondGenerator = greenDiamondGenerator;
        this.greenGoldGenerator = greenGoldGenerator;
        this.greenIronGenerator = greenIronGenerator;
        this.redItemShopPos = redItemShopPos;
        this.redUpgradeShopPos = redUpgradeShopPos;
        this.blueItemShopPos = blueItemShopPos;
        this.blueUpgradeShopPos = blueUpgradeShopPos;
        this.yellowItemShopPos = yellowItemShopPos;
        this.yellowUpgradeShopPos = yellowUpgradeShopPos;
        this.greenItemShopPos = greenItemShopPos;
        this.greenUpgradeShopPos = greenUpgradeShopPos;
        this.gameManager = gameManger;
        this.ITEM = ITEM;
        cleanupOldShops();
    }
    EntityType redIV = EntityType.VILLAGER;
    EntityType blueIV = EntityType.VILLAGER;
    EntityType yellowIV = EntityType.VILLAGER;
    EntityType greenIV = EntityType.VILLAGER;

    public boolean canRedRespawn;
    public boolean canBlueRespawn;
    public boolean canYellowRespawn;
    public boolean canGreenRespawn;

    public boolean isRedEliminated;
    public boolean isBlueEliminated;
    public boolean isYellowEliminated;
    public boolean isGreenEliminated;

    public boolean redUnlockedGen1;
    public boolean blueUnlockedGen1;
    public boolean yellowUnlockedGen1;
    public boolean greenUnlockedGen1;

    public boolean redUnlockedGen2;
    public boolean blueUnlockedGen2;
    public boolean yellowUnlockedGen2;
    public boolean greenUnlockedGen2;

    public boolean isRedInBlockSec;
    public boolean isRedInCombatSec;
    public boolean isRedInToolsSec;
    public boolean isRedInUtilitiesSec;
    public boolean isRedInPotionsSec;

    int goldCooldown;
    int diamondCooldown;
    int emeraldCooldown;

    int redGoldCooldown;
    int redDiamondCooldown;
    int redEmeraldCooldown;

    int blueGoldCooldown;
    int blueDiamondCooldown;
    int blueEmeraldCooldown;

    int yellowGoldCooldown;
    int yellowDiamondCooldown;
    int yellowEmeraldCooldown;

    int greenGoldCooldown;
    int greenDiamondCooldown;
    int greenEmeraldCooldown;

    int ironMax = 20;
    int goldMax = 10;
    int diamonMax = 6;
    int emeraldMax = 4;

    public int redIronCount;
    public int redGoldCount;
    public int redDiamondCount;
    public int redEmeraldCount;
    public int blueIronCount;
    public int blueGoldCount;
    public int blueDiamondCount;
    public int blueEmeraldCount;
    public int yellowIronCount;
    public int yellowGoldCount;
    public int yellowDiamondCount;
    public int yellowEmeraldCount;
    public int greenIronCount;
    public int greenGoldCount;
    public int greenDiamondCount;
    public int greenEmeraldCount;

    public int goldCount;
    public int diamondCount;
    public int emeraldCount;

    public short redArmor;
    public short redSword;
    public short redAxe;
    public short redPickaxe;

    public short blueArmor;
    public short blueSword;
    public short blueAxe;
    public short bluePickaxe;

    public short yellowArmor;
    public short yellowSword;
    public short yellowAxe;
    public short yellowPickaxe;

    public short greenArmor;
    public short greenSword;
    public short greenAxe;
    public short greenPickaxe;

    private List<Entity> redGolems;
    private List<Entity> blueGolems;
    private List<Entity> yellowGolems;
    private List<Entity> greenGolems;

    public int alivePlayers;

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Material type = e.getItemDrop().getItemStack().getType();
        if (NON_DROPPABLE_ITEMS.contains(type)) {
            e.setCancelled(true);
        }
    }

    private static class ShopHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    Inventory redI;
    Inventory blueI;
    Inventory yellowI;
    Inventory greenI;

    private Inventory createShopInventory(Material buttonMaterial, Material costMaterial, ChatColor color) {
        Inventory inv = Bukkit.createInventory(HOLDER, 11 * 4, ChatColor.DARK_GRAY + "Item shop");

        addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        addItem(1, new ItemStack(buttonMaterial), false, "Blocks", inv, color);
        addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", inv, ChatColor.GRAY);
        addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", inv, ChatColor.GRAY);
        addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", inv, ChatColor.GRAY);
        addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        addItem(9, new ItemStack(Material.POTION), false, "Potions", inv, ChatColor.GRAY);
        addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        addItem(12, new ItemStack(costMaterial, 16), true, " costs 8 iron", inv, ChatColor.GRAY);
        addItem(14, new ItemStack(Material.OAK_PLANKS, 8), true, " costs 20 iron ingots", inv, ChatColor.GRAY);
        addItem(16, new ItemStack(Material.END_STONE, 4), true, " costs 2 gold ingots", inv, ChatColor.GRAY);
        addItem(18, new ItemStack(Material.OBSIDIAN, 2), true, " costs 1 emerald", inv, ChatColor.GRAY);

        return inv;
    }

    private void setupBlueInventory() {
        blueI = createShopInventory(Material.BLUE_WOOL, Material.BLUE_WOOL, ChatColor.BLUE);
    }

    private void setupYellowInventory() {
        yellowI = createShopInventory(Material.GREEN_WOOL, Material.YELLOW_WOOL, ChatColor.YELLOW);
    }

    private void setupGreenInventory() {
        greenI = createShopInventory(Material.YELLOW_WOOL, Material.GREEN_WOOL, ChatColor.GREEN);
    }

    private void setupRedInventory() {
        redI = createShopInventory(Material.RED_WOOL, Material.RED_WOOL, ChatColor.RED);
    }

    @EventHandler
    public void onShopOpen(InventoryOpenEvent event) {
        if (event.getInventory() != redI||event.getInventory() != blueI||event.getInventory() != yellowI||event.getInventory() != greenI) {
            return;
        }
        if (event.getPlayer().equals(redPlayer)) {
            isRedInBlockSec = true;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = plugin.getServer().getPlayer(event.getWhoClicked().getName());
        Inventory playerInventory = player.getInventory();
        if (event.getInventory() != redI||event.getInventory() != blueI||event.getInventory() != yellowI||event.getInventory() != greenI) {
            return;
        }
        isRedInBlockSec = true;
        short swordType = -1;
        short armorType = -1;
        short pickaxeType = -1;
        short axeType = -1;
        int ironAmount = 0;
        int goldAmount = 0;
        int emeraldAmount = 0;
        for (ItemStack i : playerInventory) {
            if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                ironAmount ++;
            } else if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                goldAmount ++;
            } else if (i.equals(new ItemStack(Material.EMERALD))) {
                emeraldAmount ++;
            }
            if (i.equals(new ItemStack(Material.WOODEN_SWORD))) {
                swordType = 0;
            } else if (i.equals(new ItemStack(Material.STONE_SWORD))) {
                swordType = 1;
            } else if (i.equals(new ItemStack(Material.IRON_SWORD))) {
                swordType = 2;
            } else if (i.equals(new ItemStack(Material.DIAMOND_SWORD))) {
                swordType = 3;
            }
            if (i.equals(new ItemStack(Material.LEATHER_BOOTS))) {
                armorType = 0;
            } else if (i.equals(new ItemStack(Material.CHAINMAIL_BOOTS))) {
                armorType = 1;
            } else if (i.equals(new ItemStack(Material.IRON_BOOTS))) {
                armorType = 2;
            } else if (i.equals(new ItemStack(Material.DIAMOND_BOOTS))) {
                armorType = 3;
            }
            if (i.equals(new ItemStack(Material.WOODEN_PICKAXE))) {
                pickaxeType = 0;
            } else if (i.equals(new ItemStack(Material.STONE_PICKAXE))) {
                pickaxeType = 1;
            } else if (i.equals(new ItemStack(Material.IRON_PICKAXE))) {
                pickaxeType = 2;
            } else if (i.equals(new ItemStack(Material.DIAMOND_PICKAXE))) {
                pickaxeType = 3;
            }
            if (i.equals(new ItemStack(Material.WOODEN_AXE))) {
                axeType = 0;
            } else if (i.equals(new ItemStack(Material.STONE_AXE))) {
                axeType = 1;
            } else if (i.equals(new ItemStack(Material.IRON_AXE))) {
                axeType = 2;
            } else if (i.equals(new ItemStack(Material.DIAMOND_AXE))) {
                axeType = 3;
            }
        }
        if (event.getSlot() == 1 && event.getInventory().equals(redI)) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.RED_WOOL), false, "Blocks", blueI, ChatColor.RED);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.RED_WOOL, 16), true, " costs 8 iron", blueI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.OAK_PLANKS, 8), true, " costs 20 iron", blueI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.END_STONE, 4), true, " costs 2 gold", blueI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.OBSIDIAN, 2), true, " costs 1 emerald", blueI, ChatColor.GRAY);
            isRedInBlockSec = true;
            isRedInCombatSec = false;
            isRedInToolsSec = false;
            isRedInUtilitiesSec = false;
            isRedInPotionsSec = false;
            return;
        } else if (event.getSlot() == 3 && event.getInventory().equals(redI)) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", redI, ChatColor.RED);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", redI, ChatColor.GRAY);
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", redI, ChatColor.GRAY);
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", redI, ChatColor.GRAY);
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade sword anymore!", redI, ChatColor.GRAY);
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", redI, ChatColor.GRAY);
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", redI, ChatColor.GRAY);
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", redI, ChatColor.GRAY);
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", redI, ChatColor.GRAY);
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", redI, ChatColor.GRAY);
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", redI, ChatColor.GRAY);
            isRedInBlockSec = false;
            isRedInCombatSec = true;
            isRedInToolsSec = false;
            isRedInUtilitiesSec = false;
            isRedInPotionsSec = false;
            return;
        } else if (event.getSlot() == 5 && event.getInventory().equals(redI)) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.RED);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (pickaxeType == 0) {
                addItem(24, new ItemStack(Material.STONE_PICKAXE), true, " costs 10 iron", redI, ChatColor.GRAY);
            } else if (pickaxeType == 1) {
                addItem(24, new ItemStack(Material.IRON_PICKAXE), true, " costs 5 gold", redI, ChatColor.GRAY);
            } else if (pickaxeType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_PICKAXE), true, " costs 10 gold", redI, ChatColor.GRAY);
            } else if (pickaxeType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " you can't upgrade your pickaxe anymore!", redI, ChatColor.GRAY);
            }
            short dm = 1;
            if (axeType == 0) {
                addItem(26, new ItemStack(Material.STONE_PICKAXE, 1, dm), true, " costs 10 iron", redI, ChatColor.GRAY);
            } else if (axeType == 1) {
                addItem(26, new ItemStack(Material.IRON_PICKAXE, 1, dm), true, " costs 5 gold", redI, ChatColor.GRAY);
            } else if (axeType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_AXE, 1, dm), true, " costs 10 gold", redI, ChatColor.GRAY);
            } else if (axeType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE, 1, dm), false, " you can't upgrade your axe anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SHEARS), true, " costs 17 iron", redI, ChatColor.GRAY);
            isRedInBlockSec = false;
            isRedInCombatSec = false;
            isRedInToolsSec = true;
            isRedInUtilitiesSec = false;
            isRedInPotionsSec = false;
            return;
        } else if (event.getSlot() == 7 && event.getInventory().equals(redI)) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(25, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            addItem(27, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            addItem(29, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            addItem(31, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            isRedInBlockSec = false;
            isRedInCombatSec = false;
            isRedInToolsSec = false;
            isRedInUtilitiesSec = true;
            isRedInPotionsSec = false;
            return;
        } else if (event.getSlot() == 9 && event.getInventory().equals(redI)) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LEAPING);
            potion.setItemMeta(potionMeta);
            addItem(9, new ItemStack(potion), false, "Potions", redI, ChatColor.RED);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);

            ItemStack strenghtPotion = new ItemStack(Material.POTION);
            PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
            strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
            strenghtPotion.setItemMeta(strenghtPotionMeta);
            addItem(24, new ItemStack(strenghtPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack invisibilityPotion = new ItemStack(Material.POTION);
            PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
            invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
            invisibilityPotion.setItemMeta(invisibilityPotionMeta);
            addItem(26, new ItemStack(invisibilityPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack jumpPotion = new ItemStack(Material.POTION);
            PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
            jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
            jumpPotion.setItemMeta(jumpPotionMeta);
            addItem(28, new ItemStack(jumpPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack levitationPotion = new ItemStack(Material.POTION);
            PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
            levitationPotionMeta.setBasePotionType(PotionType.WATER);
            levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
            levitationPotion.setItemMeta(levitationPotionMeta);
            addItem(30, new ItemStack(levitationPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            isRedInBlockSec = false;
            isRedInCombatSec = false;
            isRedInToolsSec = false;
            isRedInUtilitiesSec = false;
            isRedInPotionsSec = true;
            return;
        } else if (event.getSlot() == 24 && event.getInventory().equals(redI) && isRedInBlockSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.RED_WOOL), false, "Blocks", blueI, ChatColor.RED);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.RED_WOOL, 16), true, " costs 8 iron", blueI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.OAK_PLANKS, 8), true, " costs 20 iron", blueI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.END_STONE, 4), true, " costs 2 gold", blueI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.OBSIDIAN, 2), true, " costs 1 emerald", blueI, ChatColor.GRAY);
            if (ironAmount >= 8) {
                int neededAmount = 8;
                playerInventory.addItem(new ItemStack(Material.RED_WOOL, 16));
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
        } else if (event.getSlot() == 26 && event.getInventory().equals(redI) && isRedInBlockSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.RED_WOOL), false, "Blocks", blueI, ChatColor.RED);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.RED_WOOL, 16), true, " costs 8 iron", blueI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.OAK_PLANKS, 8), true, " costs 20 iron", blueI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.END_STONE, 4), true, " costs 2 gold", blueI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.OBSIDIAN, 2), true, " costs 1 emerald", blueI, ChatColor.GRAY);
            if (ironAmount >= 20) {
                int neededAmount = 20;
                playerInventory.addItem(new ItemStack(Material.OAK_PLANKS, 8));
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
        } else if (event.getSlot() == 28 && event.getInventory().equals(redI) && isRedInBlockSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.RED_WOOL), false, "Blocks", blueI, ChatColor.RED);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.RED_WOOL, 16), true, " costs 8 iron", blueI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.OAK_PLANKS, 8), true, " costs 20 iron", blueI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.END_STONE, 4), true, " costs 2 gold", blueI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.OBSIDIAN, 2), true, " costs 1 emerald", blueI, ChatColor.GRAY);
            if (goldAmount >= 2) {
                int neededAmount = 2;
                playerInventory.addItem(new ItemStack(Material.END_STONE, 4));
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have gold enough");
                return;
            }
        } else if (event.getSlot() == 30 && event.getInventory().equals(redI) && isRedInBlockSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.RED_WOOL), false, "Blocks", blueI, ChatColor.RED);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.RED_WOOL, 16), true, " costs 8 iron", blueI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.OAK_PLANKS, 8), true, " costs 20 iron", blueI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.END_STONE, 4), true, " costs 2 gold", blueI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.OBSIDIAN, 2), true, " costs 1 emerald", blueI, ChatColor.GRAY);
            if (emeraldAmount >= 1) {
                int neededAmount = 1;
                playerInventory.addItem(new ItemStack(Material.OBSIDIAN, 2));
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.EMERALD))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }
        } else if (event.getSlot() == 24 && event.getInventory().equals(redI) && isRedInCombatSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", redI, ChatColor.RED);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", redI, ChatColor.GRAY);
                if (ironAmount >= 14) {
                    int neededAmount = 14;
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.WOODEN_SWORD)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.STONE_SWORD));
                        }
                    }
                    redSword ++;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have iron enough");
                    return;
                }
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", redI, ChatColor.GRAY);
                if (goldAmount >= 8) {
                    int neededAmount = 8;
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.WOODEN_SWORD)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.IRON_SWORD));
                        }
                    }
                    redSword ++;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have gold enough");
                    return;
                }
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", redI, ChatColor.GRAY);
                if (emeraldAmount >= 4) {
                    int neededAmount = 8;
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.WOODEN_SWORD)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.DIAMOND_SWORD));
                        }
                    }
                    redSword ++;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.EMERALD))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have emerald enough");
                    return;
                }
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " can't upgrade sword anymore!", redI, ChatColor.GRAY);
                player.sendMessage(ChatColor.RED + "can't upgrade sword anymore!");
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", redI, ChatColor.GRAY);
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", redI, ChatColor.GRAY);
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", redI, ChatColor.GRAY);
                return;
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", redI, ChatColor.GRAY);
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", redI, ChatColor.GRAY);
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 26 && event.getInventory().equals(redI) && isRedInCombatSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", redI, ChatColor.RED);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", redI, ChatColor.GRAY);
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", redI, ChatColor.GRAY);
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", redI, ChatColor.GRAY);
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " can't upgrade sword anymore!", redI, ChatColor.GRAY);
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", redI, ChatColor.GRAY);
                if (ironAmount >= 8) {
                    int neededAmount = 8;
                    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
                    LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
                    helmetMeta.setColor(Color.RED);
                    helmet.setItemMeta(helmetMeta);
                    player.getInventory().setHelmet(helmet);

                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                    LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    chestplateMeta.setColor(Color.RED);
                    chestplate.setItemMeta(chestplateMeta);
                    player.getInventory().setChestplate(chestplate);

                    ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
                    ArmorMeta leggingsMeta = (ArmorMeta) leggings.getItemMeta();
                    leggingsMeta.setTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.WILD));
                    leggings.setItemMeta(leggingsMeta);
                    player.getInventory().setLeggings(leggings);

                    ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
                    ArmorMeta bootsMeta = (ArmorMeta) boots.getItemMeta();
                    bootsMeta.setTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.WILD));
                    boots.setItemMeta(bootsMeta);
                    player.getInventory().setBoots(boots);
                    redArmor ++;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have iron enough");
                    return;
                }
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", redI, ChatColor.GRAY);
                if (ironAmount >= 32) {
                    int neededAmount = 32;
                    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
                    LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
                    helmetMeta.setColor(Color.RED);
                    helmet.setItemMeta(helmetMeta);
                    player.getInventory().setHelmet(helmet);

                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                    LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    chestplateMeta.setColor(Color.RED);
                    chestplate.setItemMeta(chestplateMeta);
                    player.getInventory().setChestplate(chestplate);

                    ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS, 1);
                    ArmorMeta leggingsMeta = (ArmorMeta) leggings.getItemMeta();
                    leggingsMeta.setTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.WILD));
                    leggings.setItemMeta(leggingsMeta);
                    player.getInventory().setLeggings(leggings);

                    ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
                    ArmorMeta bootsMeta = (ArmorMeta) boots.getItemMeta();
                    bootsMeta.setTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.WILD));
                    boots.setItemMeta(bootsMeta);
                    player.getInventory().setBoots(boots);
                    redArmor ++;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have iron enough");
                    return;
                }
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", redI, ChatColor.GRAY);
                if (emeraldAmount >= 6) {
                    int neededAmount = 6;
                    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
                    LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
                    helmetMeta.setColor(Color.RED);
                    helmet.setItemMeta(helmetMeta);
                    player.getInventory().setHelmet(helmet);

                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                    LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    chestplateMeta.setColor(Color.RED);
                    chestplate.setItemMeta(chestplateMeta);
                    player.getInventory().setChestplate(chestplate);

                    ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
                    ArmorMeta leggingsMeta = (ArmorMeta) leggings.getItemMeta();
                    leggingsMeta.setTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.WILD));
                    leggings.setItemMeta(leggingsMeta);
                    player.getInventory().setLeggings(leggings);

                    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
                    ArmorMeta bootsMeta = (ArmorMeta) boots.getItemMeta();
                    bootsMeta.setTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.WILD));
                    boots.setItemMeta(bootsMeta);
                    player.getInventory().setBoots(boots);
                    redArmor ++;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.EMERALD))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have emerald enough");
                    return;
                }
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", redI, ChatColor.GRAY);
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", redI, ChatColor.GRAY);
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 28 && event.getInventory().equals(redI) && isRedInCombatSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", redI, ChatColor.RED);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", redI, ChatColor.GRAY);
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", redI, ChatColor.GRAY);
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", redI, ChatColor.GRAY);
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " can't upgrade sword anymore!", redI, ChatColor.GRAY);
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", redI, ChatColor.GRAY);
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", redI, ChatColor.GRAY);
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", redI, ChatColor.GRAY);
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", redI, ChatColor.GRAY);
            if (goldAmount >= 2) {
                playerInventory.addItem(new ItemStack(Material.SNOWBALL));
                int neededAmount = 2;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have gold enough");
                return;
            }
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", redI, ChatColor.GRAY);
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", redI, ChatColor.GRAY);
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 30 && event.getInventory().equals(redI) && isRedInCombatSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", redI, ChatColor.RED);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", redI, ChatColor.GRAY);
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", redI, ChatColor.GRAY);
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", redI, ChatColor.GRAY);
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " can't upgrade sword anymore!", redI, ChatColor.GRAY);
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", redI, ChatColor.GRAY);
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", redI, ChatColor.GRAY);
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", redI, ChatColor.GRAY);
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", redI, ChatColor.GRAY);
            if (goldAmount >= 12) {
                playerInventory.addItem(new ItemStack(Material.BOW));
                int neededAmount = 12;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have gold enough");
                return;
            }
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", redI, ChatColor.GRAY);
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 32 && event.getInventory().equals(redI) && isRedInCombatSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", redI, ChatColor.RED);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", redI, ChatColor.GRAY);
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", redI, ChatColor.GRAY);
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", redI, ChatColor.GRAY);
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " can't upgrade sword anymore!", redI, ChatColor.GRAY);
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", redI, ChatColor.GRAY);
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", redI, ChatColor.GRAY);
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", redI, ChatColor.GRAY);
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", redI, ChatColor.GRAY);
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", redI, ChatColor.GRAY);
            if (ironAmount >= 4) {
                playerInventory.addItem(new ItemStack(Material.ARROW));
                int neededAmount = 4;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 32 && event.getInventory().equals(redI) && isRedInCombatSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", redI, ChatColor.RED);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", redI, ChatColor.GRAY);
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", redI, ChatColor.GRAY);
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", redI, ChatColor.GRAY);
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " can't upgrade sword anymore!", redI, ChatColor.GRAY);
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", redI, ChatColor.GRAY);
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", redI, ChatColor.GRAY);
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", redI, ChatColor.GRAY);
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", redI, ChatColor.GRAY);
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", redI, ChatColor.GRAY);
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", redI, ChatColor.GRAY);
            if (ironAmount >= 64) {
                playerInventory.addItem(new ItemStack(Material.ARROW, 64));
                int neededAmount = 64;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if(i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount --;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
            return;
        } else if (event.getSlot() == 24 && event.getInventory().equals(redI) && isRedInToolsSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.RED);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (pickaxeType == 0) {
                addItem(24, new ItemStack(Material.STONE_PICKAXE), true, " costs 10 iron", redI, ChatColor.GRAY);
                if (ironAmount >= 10) {
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.WOODEN_PICKAXE)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.STONE_PICKAXE));
                        }
                    }
                    int neededAmount = 10;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have iron enough");
                    return;
                }
            } else if (pickaxeType == 1) {
                addItem(24, new ItemStack(Material.IRON_PICKAXE), true, " costs 5 gold", redI, ChatColor.GRAY);
                if (goldAmount >= 5) {
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.STONE_PICKAXE)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.IRON_PICKAXE));
                        }
                    }
                    int neededAmount = 5;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have gold enough");
                    return;
                }
            } else if (pickaxeType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_PICKAXE), true, " costs 10 gold", redI, ChatColor.GRAY);
                if (goldAmount >= 10) {
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.IRON_PICKAXE)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.DIAMOND_PICKAXE));
                        }
                    }
                    int neededAmount = 10;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have gold enough");
                    return;
                }
            } else if (pickaxeType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " you can't upgrade your pickaxe anymore!", redI, ChatColor.GRAY);
            }
            short dm = 1;
            if (axeType == 0) {
                addItem(26, new ItemStack(Material.STONE_PICKAXE, 1, dm), true, " costs 10 iron", redI, ChatColor.GRAY);
            } else if (axeType == 1) {
                addItem(26, new ItemStack(Material.IRON_PICKAXE, 1, dm), true, " costs 5 gold", redI, ChatColor.GRAY);
            } else if (axeType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_AXE, 1, dm), true, " costs 10 gold", redI, ChatColor.GRAY);
            } else if (axeType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE, 1, dm), false, " you can't upgrade your axe anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SHEARS), true, " costs 17 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 26 && event.getInventory().equals(redI) && isRedInToolsSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.RED);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (pickaxeType == 0) {
                addItem(24, new ItemStack(Material.STONE_PICKAXE), true, " costs 10 iron", redI, ChatColor.GRAY);
            } else if (pickaxeType == 1) {
                addItem(24, new ItemStack(Material.IRON_PICKAXE), true, " costs 5 gold", redI, ChatColor.GRAY);
            } else if (pickaxeType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_PICKAXE), true, " costs 10 gold", redI, ChatColor.GRAY);
            } else if (pickaxeType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " you can't upgrade your pickaxe anymore!", redI, ChatColor.GRAY);
            }
            short dm = 1;
            if (axeType == 0) {
                addItem(26, new ItemStack(Material.STONE_AXE, 1, dm), true, " costs 10 iron", redI, ChatColor.GRAY);
                if (ironAmount >= 10) {
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.WOODEN_AXE)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.STONE_AXE));
                        }
                    }
                    int neededAmount = 10;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have iron enough");
                    return;
                }
            } else if (axeType == 1) {
                addItem(26, new ItemStack(Material.IRON_AXE, 1, dm), true, " costs 5 gold", redI, ChatColor.GRAY);
                if (goldAmount >= 5) {
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.STONE_AXE)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.IRON_AXE));
                        }
                    }
                    int neededAmount = 5;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                            if(i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount --;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have gold enough");
                    return;
                }
            } else if (axeType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_AXE, 1, dm), true, " costs 10 gold", redI, ChatColor.GRAY);
                if (goldAmount >= 10) {
                    for (ItemStack i : playerInventory) {
                        if (i == new ItemStack(Material.IRON_AXE)) {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(Material.DIAMOND_AXE));
                        }
                    }
                    int neededAmount = 10;
                    for (ItemStack i : playerInventory) {
                        if (neededAmount <= 0) {
                            return;
                        }
                        if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                            if (i.getAmount() <= neededAmount) {
                                playerInventory.remove(i);
                                neededAmount--;
                            } else {
                                playerInventory.remove(i);
                                playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                                return;
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have gold enough");
                    return;
                }
            } else if (axeType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE, 1, dm), false, " you can't upgrade your axe anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SHEARS), true, " costs 17 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 28 && event.getInventory().equals(redI) && isRedInToolsSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.RED);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            if (pickaxeType == 0) {
                addItem(24, new ItemStack(Material.STONE_PICKAXE), true, " costs 10 iron", redI, ChatColor.GRAY);
            } else if (pickaxeType == 1) {
                addItem(24, new ItemStack(Material.IRON_PICKAXE), true, " costs 5 gold", redI, ChatColor.GRAY);
            } else if (pickaxeType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_PICKAXE), true, " costs 10 gold", redI, ChatColor.GRAY);
            } else if (pickaxeType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " you can't upgrade your pickaxe anymore!", redI, ChatColor.GRAY);
            }
            short dm = 1;
            if (axeType == 0) {
                addItem(26, new ItemStack(Material.STONE_AXE, 1, dm), true, " costs 10 iron", redI, ChatColor.GRAY);
            } else if (axeType == 1) {
                addItem(26, new ItemStack(Material.IRON_AXE, 1, dm), true, " costs 5 gold", redI, ChatColor.GRAY);
            } else if (axeType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_AXE, 1, dm), true, " costs 10 gold", redI, ChatColor.GRAY);
            } else if (axeType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE, 1, dm), false, " you can't upgrade your axe anymore!", redI, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SHEARS), true, " costs 17 iron", redI, ChatColor.GRAY);
            if (ironAmount >= 17) {
                playerInventory.addItem(new ItemStack(Material.SHEARS));
                int neededAmount = 17;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
            return;
        } else if (event.getSlot() == 24 && event.getInventory().equals(redI) && isRedInUtilitiesSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            if (goldAmount >= 3) {
                playerInventory.addItem(new ItemStack(Material.GOLDEN_APPLE));
                int neededAmount = 3;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have gold enough");
                return;
            }
            addItem(25, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            addItem(27, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            addItem(29, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 25 && event.getInventory().equals(redI) && isRedInUtilitiesSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            addItem(25, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            if (ironAmount >= 30) {
                playerInventory.addItem(new ItemStack(Material.FIRE_CHARGE));
                int neededAmount = 30;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
            addItem(26, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            addItem(27, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            addItem(29, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 26 && event.getInventory().equals(redI) && isRedInUtilitiesSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            addItem(25, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            if (ironAmount >= 25) {
                playerInventory.addItem(new ItemStack(Material.WIND_CHARGE));
                int neededAmount = 25;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
            addItem(27, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            addItem(29, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 27 && event.getInventory().equals(redI) && isRedInUtilitiesSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            addItem(25, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            addItem(27, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            if (emeraldAmount >= 2) {
                playerInventory.addItem(new ItemStack(Material.ENDER_PEARL));
                int neededAmount = 2;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.EMERALD))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }
            addItem(28, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            addItem(29, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 28 && event.getInventory().equals(redI) && isRedInUtilitiesSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            addItem(25, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            addItem(27, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            if (emeraldAmount >= 3) {
                playerInventory.addItem(new ItemStack(Material.FISHING_ROD));
                int neededAmount = 3;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.EMERALD))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }
            addItem(29, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 29 && event.getInventory().equals(redI) && isRedInUtilitiesSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            addItem(25, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            addItem(27, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            addItem(29, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            if (goldAmount >= 6) {
                playerInventory.addItem(new ItemStack(Material.IRON_GOLEM_SPAWN_EGG));
                int neededAmount = 6;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have gold enough");
                return;
            }
            addItem(30, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 29 && event.getInventory().equals(redI) && isRedInUtilitiesSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", redI, ChatColor.RED);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", redI, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(24, new ItemStack(Material.GOLDEN_APPLE), true, " costs 3 gold", redI, ChatColor.GRAY);
            addItem(25, new ItemStack(Material.FIRE_CHARGE), true, " costs 30 iron", redI, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.WIND_CHARGE), true, " costs 25 iron", redI, ChatColor.GRAY);
            addItem(27, new ItemStack(Material.ENDER_PEARL), true, " costs 2 emerald", redI, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.FISHING_ROD), true, " costs 3 emerald", redI, ChatColor.GRAY);
            addItem(29, new ItemStack(Material.CHORUS_FRUIT), true, " costs 6 gold", redI, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), true, " costs 120 iron", redI, ChatColor.GRAY);
            if (ironAmount >= 120) {
                playerInventory.addItem(new ItemStack(Material.IRON_GOLEM_SPAWN_EGG));
                int neededAmount = 120;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
            return;
        } else if (event.getSlot() == 24 && event.getInventory().equals(redI) && isRedInPotionsSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LEAPING);
            potion.setItemMeta(potionMeta);
            addItem(9, new ItemStack(potion), false, "Potions", redI, ChatColor.RED);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);

            ItemStack strenghtPotion = new ItemStack(Material.POTION);
            PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
            strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
            strenghtPotion.setItemMeta(strenghtPotionMeta);
            addItem(24, new ItemStack(strenghtPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);
            if (emeraldAmount >= 2) {
                playerInventory.addItem(strenghtPotion);
                int neededAmount = 2;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.EMERALD))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }
            ItemStack invisibilityPotion = new ItemStack(Material.POTION);
            PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
            invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
            invisibilityPotion.setItemMeta(invisibilityPotionMeta);
            addItem(26, new ItemStack(invisibilityPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack jumpPotion = new ItemStack(Material.POTION);
            PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
            jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
            jumpPotion.setItemMeta(jumpPotionMeta);
            addItem(28, new ItemStack(jumpPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack levitationPotion = new ItemStack(Material.POTION);
            PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
            levitationPotionMeta.setBasePotionType(PotionType.WATER);
            levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
            levitationPotion.setItemMeta(levitationPotionMeta);
            addItem(30, new ItemStack(levitationPotion), false, "potion of levitation costs 2 emerald", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 26 && event.getInventory().equals(redI) && isRedInPotionsSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LEAPING);
            potion.setItemMeta(potionMeta);
            addItem(9, new ItemStack(potion), false, "Potions", redI, ChatColor.RED);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);

            ItemStack strenghtPotion = new ItemStack(Material.POTION);
            PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
            strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
            strenghtPotion.setItemMeta(strenghtPotionMeta);
            addItem(24, new ItemStack(strenghtPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack invisibilityPotion = new ItemStack(Material.POTION);
            PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
            invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
            invisibilityPotion.setItemMeta(invisibilityPotionMeta);
            addItem(26, new ItemStack(invisibilityPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);
            if (emeraldAmount >= 2) {
                playerInventory.addItem(invisibilityPotion);
                int neededAmount = 2;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.EMERALD))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }

            ItemStack jumpPotion = new ItemStack(Material.POTION);
            PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
            jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
            jumpPotion.setItemMeta(jumpPotionMeta);
            addItem(28, new ItemStack(jumpPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack levitationPotion = new ItemStack(Material.POTION);
            PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
            levitationPotionMeta.setBasePotionType(PotionType.WATER);
            levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
            levitationPotion.setItemMeta(levitationPotionMeta);
            addItem(30, new ItemStack(levitationPotion), false, "potion of levitation costs 2 emerald", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 28 && event.getInventory().equals(redI) && isRedInPotionsSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LEAPING);
            potion.setItemMeta(potionMeta);
            addItem(9, new ItemStack(potion), false, "Potions", redI, ChatColor.RED);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);

            ItemStack strenghtPotion = new ItemStack(Material.POTION);
            PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
            strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
            strenghtPotion.setItemMeta(strenghtPotionMeta);
            addItem(24, new ItemStack(strenghtPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack invisibilityPotion = new ItemStack(Material.POTION);
            PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
            invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
            invisibilityPotion.setItemMeta(invisibilityPotionMeta);
            addItem(26, new ItemStack(invisibilityPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack jumpPotion = new ItemStack(Material.POTION);
            PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
            jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
            jumpPotion.setItemMeta(jumpPotionMeta);
            addItem(28, new ItemStack(jumpPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);
            if (emeraldAmount >= 2) {
                playerInventory.addItem(jumpPotion);
                int neededAmount = 2;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.EMERALD))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }

            ItemStack levitationPotion = new ItemStack(Material.POTION);
            PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
            levitationPotionMeta.setBasePotionType(PotionType.WATER);
            levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
            levitationPotion.setItemMeta(levitationPotionMeta);
            addItem(30, new ItemStack(levitationPotion), false, "potion of levitation costs 2 emerald", redI, ChatColor.GRAY);
            return;
        } else if (event.getSlot() == 30 && event.getInventory().equals(redI) && isRedInPotionsSec) {
            event.getInventory().clear();
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", blueI, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", redI, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", redI, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", redI, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LEAPING);
            potion.setItemMeta(potionMeta);
            addItem(9, new ItemStack(potion), false, "Potions", redI, ChatColor.RED);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", redI, ChatColor.BLACK);

            ItemStack strenghtPotion = new ItemStack(Material.POTION);
            PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
            strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
            strenghtPotion.setItemMeta(strenghtPotionMeta);
            addItem(24, new ItemStack(strenghtPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack invisibilityPotion = new ItemStack(Material.POTION);
            PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
            invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
            invisibilityPotion.setItemMeta(invisibilityPotionMeta);
            addItem(26, new ItemStack(invisibilityPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack jumpPotion = new ItemStack(Material.POTION);
            PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
            jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
            jumpPotion.setItemMeta(jumpPotionMeta);
            addItem(28, new ItemStack(jumpPotion), true, " costs 2 emerald", redI, ChatColor.GRAY);

            ItemStack levitationPotion = new ItemStack(Material.POTION);
            PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
            levitationPotionMeta.setBasePotionType(PotionType.WATER);
            levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
            levitationPotion.setItemMeta(levitationPotionMeta);
            addItem(30, new ItemStack(levitationPotion), false, "potion of levitation costs 2 emerald", redI, ChatColor.GRAY);
            if (emeraldAmount >= 2) {
                playerInventory.addItem(levitationPotion);
                int neededAmount = 2;
                for (ItemStack i : playerInventory) {
                    if (neededAmount <= 0) {
                        return;
                    }
                    if (i.equals(new ItemStack(Material.EMERALD))) {
                        if (i.getAmount() <= neededAmount) {
                            playerInventory.remove(i);
                            neededAmount--;
                        } else {
                            playerInventory.remove(i);
                            playerInventory.addItem(new ItemStack(i.getType(), i.getAmount() - neededAmount));
                            return;
                        }
                    }
                }
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }
            return;
        }
    }

    @EventHandler
    public void onEnderChestAirClick(InventoryOpenEvent event) {
        if (event.getInventory().equals(event.getPlayer().getEnderChest())) {
            if (!event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.WOODEN_SWORD))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.WOODEN_PICKAXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.WOODEN_AXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.IRON_SWORD))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.IRON_PICKAXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.IRON_AXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.STONE_SWORD))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.STONE_PICKAXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.STONE_AXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.DIAMOND_SWORD))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.DIAMOND_PICKAXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.DIAMOND_AXE))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.SHEARS))||                    !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.AIR))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEnderChestRightClick(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        if (!event.getClickedBlock().equals(BlockType.ENDER_CHEST)) {
            return;
        }
        if (!event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.WOODEN_SWORD))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.WOODEN_PICKAXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.WOODEN_AXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.IRON_SWORD))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.IRON_PICKAXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.IRON_AXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.STONE_SWORD))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.STONE_PICKAXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.STONE_AXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.DIAMOND_SWORD))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.DIAMOND_PICKAXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.DIAMOND_AXE))||                !event.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.SHEARS))) {
            event.getPlayer().getEnderChest().addItem(event.getPlayer().getInventory().getItemInMainHand());
        }
    }

    private void cleanupOldShops() {
        for (World world : Bukkit.getServer().getWorlds()) {
            int removed = 0;
            for (Entity e : world.getEntities()) {
                if (e instanceof Villager w && (ChatColor.DARK_PURPLE + "Shopkeeper").equals(w.getCustomName())) {
                    w.remove();
                    removed++;
                }
            }
            if (removed > 0) {
                plugin.getLogger().info("Removed " + removed + " old witches in world '" + world.getName() + "'");
            }
        }
        shopIds.clear();
    }

    private void addItem(int slot, ItemStack item, boolean haveCosting, String str, Inventory inv, ChatColor color) {
        ItemMeta meta = item.getItemMeta();
        if (haveCosting) {
            meta.setDisplayName(item.getType().name().toLowerCase() + str);
        } else {
            meta.setDisplayName(color + str);
        }
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private ItemStack coloredArmor(Material type, Color color) {
        ItemStack item = new ItemStack(type, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack trimmedArmor(Material type, TrimMaterial material) {
        ItemStack item = new ItemStack(type, 1);
        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
        meta.setTrim(new ArmorTrim(material, TrimPattern.WILD));
        item.setItemMeta(meta);
        return item;
    }

    private void equipTeamArmor(Player player, Color color, int armorType, TrimMaterial trimMaterial) {
        if (armorType == 0) {
            player.getInventory().setHelmet(coloredArmor(Material.LEATHER_HELMET, color));
            player.getInventory().setChestplate(coloredArmor(Material.LEATHER_CHESTPLATE, color));
            player.getInventory().setLeggings(coloredArmor(Material.LEATHER_LEGGINGS, color));
            player.getInventory().setBoots(coloredArmor(Material.LEATHER_BOOTS, color));
        } else if (armorType == 1) {
            player.getInventory().setHelmet(coloredArmor(Material.LEATHER_HELMET, color));
            player.getInventory().setChestplate(coloredArmor(Material.LEATHER_CHESTPLATE, color));
            player.getInventory().setLeggings(trimmedArmor(Material.CHAINMAIL_LEGGINGS, trimMaterial));
            player.getInventory().setBoots(trimmedArmor(Material.CHAINMAIL_BOOTS, trimMaterial));
        } else if (armorType == 2) {
            player.getInventory().setHelmet(coloredArmor(Material.LEATHER_HELMET, color));
            player.getInventory().setChestplate(coloredArmor(Material.LEATHER_CHESTPLATE, color));
            player.getInventory().setLeggings(trimmedArmor(Material.IRON_LEGGINGS, trimMaterial));
            player.getInventory().setBoots(trimmedArmor(Material.IRON_BOOTS, trimMaterial));
        } else if (armorType == 3) {
            player.getInventory().setHelmet(coloredArmor(Material.LEATHER_HELMET, color));
            player.getInventory().setChestplate(coloredArmor(Material.LEATHER_CHESTPLATE, color));
            player.getInventory().setLeggings(trimmedArmor(Material.DIAMOND_LEGGINGS, trimMaterial));
            player.getInventory().setBoots(trimmedArmor(Material.DIAMOND_BOOTS, trimMaterial));
        }
    }


    public synchronized void gameLoop() {
        redGolems = null;
        blueGolems = null;
        yellowGolems = null;
        greenGolems = null;

        redArmor = 0;
        redSword = 0;
        redAxe = 0;
        redPickaxe = 0;

        blueArmor = 0;
        blueSword = 0;
        blueAxe = 0;
        bluePickaxe = 0;

        yellowArmor = 0;
        yellowSword = 0;
        yellowAxe = 0;
        yellowPickaxe = 0;

        greenArmor = 0;
        greenSword = 0;
        greenAxe = 0;
        greenPickaxe = 0;

        for (Player player : world.getPlayers()) {
            if (player.equals(redPlayer)) {
                equipTeamArmor(player, Color.RED, 0, TrimMaterial.REDSTONE);
            } else if (player.equals(bluePlayer)) {
                equipTeamArmor(player, Color.BLUE, 0, TrimMaterial.LAPIS);
            } else if (player.equals(yellowPlayer)) {
                equipTeamArmor(player, Color.YELLOW, 0, TrimMaterial.GOLD);
            } else if (player.equals(greenPlayer)) {
                equipTeamArmor(player, Color.GREEN, 0, TrimMaterial.EMERALD);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (alivePlayers <= 1) {
                    cancel();
                    return;
                }
                if (redUnlockedGen1) {
                    if (redIronCount - 10 < ironMax) {
                        world.dropItem(redIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        redIronCount += 2;
                    }
                    if (redGoldCount - 5 < goldMax && redGoldCooldown <= 0) {
                        world.dropItem(redGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        redGoldCount ++;
                    }
                    redGoldCooldown --;
                } else if (redUnlockedGen2) {
                    if (redIronCount - 20 < ironMax) {
                        world.dropItem(redIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        redIronCount += 2;
                    }
                    if (redGoldCount - 5 < goldMax && redGoldCooldown <= 0) {
                        world.dropItem(redGoldGenerator, new ItemStack(Material.GOLD_INGOT, 2));
                        redGoldCount += 2;
                    }
                    if (redDiamondCount < diamonMax && redDiamondCooldown <= 0) {
                        world.dropItem(redDiamondGenerator, new ItemStack(Material.DIAMOND, 1));
                        redDiamondCount ++;
                    }
                    if (redEmeraldCount < emeraldMax && redEmeraldCooldown <= 0) {
                        world.dropItem(redEmeraldGenerator, new ItemStack(Material.EMERALD, 1));
                        redEmeraldCount ++;
                    }
                    redGoldCooldown -= 2;
                    redDiamondCooldown -= 1;
                    redEmeraldCooldown -= 1;
                } else {
                    if (redIronCount < ironMax) {
                        world.dropItem(redIronGenerator, new ItemStack(Material.IRON_INGOT, 1));
                        redIronCount ++;
                    }
                    if (redGoldCount < goldMax) {
                        world.dropItem(redGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        redGoldCount ++;
                    }
                    redGoldCooldown --;
                }
                if (blueUnlockedGen1) {
                    if (blueIronCount - 10 < ironMax) {
                        world.dropItem(blueIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        blueIronCount += 2;
                    }
                    if (blueGoldCount - 5 < goldMax && blueGoldCooldown <= 0) {
                        world.dropItem(blueGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        blueGoldCount ++;
                    }
                    blueGoldCooldown --;
                } else if (blueUnlockedGen2) {
                    if (blueIronCount + 20 < ironMax) {
                        world.dropItem(blueIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        blueIronCount += 2;
                    }
                    if (blueGoldCount - 5 < goldMax && blueGoldCooldown <= 0) {
                        world.dropItem(blueGoldGenerator, new ItemStack(Material.GOLD_INGOT, 2));
                        blueGoldCount += 2;
                    }
                    if (blueDiamondCount < diamonMax && blueDiamondCooldown <= 0) {
                        world.dropItem(blueDiamondGenerator, new ItemStack(Material.DIAMOND, 1));
                        blueDiamondCount ++;
                    }
                    if (blueEmeraldCount < emeraldMax && blueEmeraldCooldown <= 0) {
                        world.dropItem(blueEmeraldGenerator, new ItemStack(Material.EMERALD, 1));
                        blueEmeraldCount ++;
                    }
                    blueGoldCooldown -= 2;
                    blueDiamondCooldown -= 1;
                    blueEmeraldCooldown -= 1;
                } else {
                    if (blueIronCount < ironMax) {
                        world.dropItem(blueIronGenerator, new ItemStack(Material.IRON_INGOT, 1));
                        blueIronCount ++;
                    }
                    if (blueGoldCount < goldMax) {
                        world.dropItem(blueGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        blueGoldCount ++;
                    }
                    blueGoldCooldown --;
                }
                if (greenUnlockedGen1) {
                    if (greenIronCount - 10 < ironMax) {
                        world.dropItem(greenIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        greenIronCount += 2;
                    }
                    if (greenGoldCount - 5 < goldMax && greenGoldCooldown <= 0) {
                        world.dropItem(greenGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        greenGoldCount ++;
                    }
                    greenGoldCooldown --;
                } else if (greenUnlockedGen2) {
                    if (greenIronCount + 20 < ironMax) {
                        world.dropItem(greenIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        greenIronCount += 2;
                    }
                    if (greenGoldCount - 5 < goldMax && greenGoldCooldown <= 0) {
                        world.dropItem(greenGoldGenerator, new ItemStack(Material.GOLD_INGOT, 2));
                        greenGoldCount += 2;
                    }
                    if (greenDiamondCount < diamonMax && greenDiamondCooldown <= 0) {
                        world.dropItem(greenDiamondGenerator, new ItemStack(Material.DIAMOND, 1));
                        greenDiamondCount ++;
                    }
                    if (greenEmeraldCount < emeraldMax && greenEmeraldCooldown <= 0) {
                        world.dropItem(greenEmeraldGenerator, new ItemStack(Material.EMERALD, 1));
                        greenEmeraldCount ++;
                    }
                    greenGoldCooldown -= 2;
                    greenDiamondCooldown -= 1;
                    greenEmeraldCooldown -= 1;
                } else {
                    if (greenIronCount < ironMax) {
                        world.dropItem(greenIronGenerator, new ItemStack(Material.IRON_INGOT, 1));
                        greenIronCount ++;
                    }
                    if (greenGoldCount < goldMax) {
                        world.dropItem(greenGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        greenGoldCount ++;
                    }
                    greenGoldCooldown --;
                }
                if (yellowUnlockedGen1) {
                    if (yellowIronCount - 10 < ironMax) {
                        world.dropItem(yellowIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        yellowIronCount += 2;
                    }
                    if (yellowGoldCount - 5 < goldMax && yellowGoldCooldown <= 0) {
                        world.dropItem(yellowGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        yellowGoldCount ++;
                    }
                    yellowGoldCooldown --;
                } else if (yellowUnlockedGen2) {
                    if (yellowIronCount + 20 < ironMax) {
                        world.dropItem(yellowIronGenerator, new ItemStack(Material.IRON_INGOT, 2));
                        yellowIronCount += 2;
                    }
                    if (yellowGoldCount - 5 < goldMax && blueGoldCooldown <= 0) {
                        world.dropItem(yellowGoldGenerator, new ItemStack(Material.GOLD_INGOT, 2));
                        yellowGoldCount += 2;
                    }
                    if (yellowDiamondCount < diamonMax && blueDiamondCooldown <= 0) {
                        world.dropItem(yellowDiamondGenerator, new ItemStack(Material.DIAMOND, 1));
                        yellowDiamondCount ++;
                    }
                    if (yellowEmeraldCount < emeraldMax && yellowEmeraldCooldown <= 0) {
                        world.dropItem(yellowEmeraldGenerator, new ItemStack(Material.EMERALD, 1));
                        yellowEmeraldCount ++;
                    }
                    yellowGoldCooldown -= 2;
                    yellowDiamondCooldown -= 1;
                    yellowEmeraldCooldown -= 1;
                } else {
                    if (yellowIronCount < ironMax) {
                        world.dropItem(yellowIronGenerator, new ItemStack(Material.IRON_INGOT, 1));
                        yellowIronCount ++;
                    }
                    if (yellowGoldCount < goldMax) {
                        world.dropItem(yellowGoldGenerator, new ItemStack(Material.GOLD_INGOT, 1));
                        yellowGoldCount ++;
                    }
                    yellowGoldCooldown --;
                }
                if (goldCooldown <= 0) {
                    for (Location loc : midGoldGenerators) {
                        world.dropItem(loc, new ItemStack(Material.GOLD_INGOT));
                        Entity entity = world.spawnEntity(loc, EntityType.ARMOR_STAND);
                        entity.setVisibleByDefault(false);
                        entity.setCustomName(ChatColor.GOLD + "" + goldCooldown  + "seconds remaining");
                        goldCooldown = 5;
                    }
                }
                if (diamondCooldown <= 0) {
                    for (Location loc : midDiamondGenerators) {
                        world.dropItem(loc, new ItemStack(Material.DIAMOND));
                        diamondCooldown = 15;
                    }
                }
                if (emeraldCooldown <= 0) {
                    for (Location loc : midDiamondGenerators) {
                        world.dropItem(loc, new ItemStack(Material.EMERALD));
                        diamondCooldown = 30;
                    }
                }
                for (Location loc : midGoldGenerators) {
                    Entity entity = world.spawnEntity(loc, EntityType.ARMOR_STAND);
                    entity.setVisibleByDefault(false);
                    entity.setCustomName(ChatColor.GOLD + "" + goldCooldown  + "seconds remaining");
                }
                for (Location loc : midDiamondGenerators) {
                    Entity entity = world.spawnEntity(loc, EntityType.ARMOR_STAND);
                    entity.setVisibleByDefault(false);
                    entity.setCustomName(ChatColor.AQUA + "" + diamondCooldown  + "seconds remaining");
                }
                for (Location loc : midEmeraldGenerators) {
                    Entity entity = world.spawnEntity(loc, EntityType.ARMOR_STAND);
                    entity.setVisibleByDefault(false);
                    entity.setCustomName(ChatColor.GREEN + "" + emeraldCooldown  + "seconds remaining");
                }
                goldCooldown --;
                diamondCooldown --;
                emeraldCooldown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        return;
    }

    @EventHandler
    public void onShopInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == redIV) {
            event.getPlayer().openInventory(redI);
        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.VILLAGER||e.getEntity().getType() == EntityType.ARMOR_STAND) {
            e.setCancelled(true);
        }
    }

    public void addPlayer(Player player, int i) {
        players.add(player);

    }

    static {
        ITEM = new ItemStack(Material.RECOVERY_COMPASS);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Return to lobby");
        ITEM.setItemMeta(meta);
    }

    private boolean isCompass(ItemStack stack) {
        if (stack == null || stack.getType() != Material.RECOVERY_COMPASS) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }

    private boolean isGolemSpawnEgg(ItemStack stack) {
        if (stack == null || stack.getType() != Material.IRON_GOLEM_SPAWN_EGG) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasDisplayName()
                && meta.getDisplayName().equals(ITEM.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void noForgiving(EntityTargetEvent e) {
        for (Entity golem : redGolems) {
            if(e.getEntity().getType() == golem.getType()){
                for (Player p : world.getPlayers()) {
                    if (p != redPlayer) {
                        e.setTarget(p);
                    }
                }
            }
        }
        for (Entity golem : blueGolems) {
            if(e.getEntity().getType() == golem.getType()){
                for (Player p : world.getPlayers()) {
                    if (p != bluePlayer) {
                        e.setTarget(p);
                    }
                }
            }
        }
        for (Entity golem : yellowGolems) {
            if(e.getEntity().getType() == golem.getType()){
                for (Player p : world.getPlayers()) {
                    if (p != yellowPlayer) {
                        e.setTarget(p);
                    }
                }
            }
        }
        for (Entity golem : greenGolems) {
            if(e.getEntity().getType() == golem.getType()){
                for (Player p : world.getPlayers()) {
                    if (p != greenPlayer) {
                        e.setTarget(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGolemSpawnEggInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!isGolemSpawnEgg(e.getItem())) {
            return;
        }
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        e.setCancelled(true);
        p.getInventory().removeItem(e.getItem());
        int am = e.getItem().getAmount() - 1;
        p.getInventory().addItem(new ItemStack(e.getItem().getType(), e.getItem().getAmount() - 1));
        if (p == redPlayer) {
            redGolems.add(world.spawnEntity(p.getLocation(), EntityType.IRON_GOLEM));
        } else if (p == bluePlayer) {
            blueGolems.add(world.spawnEntity(p.getLocation(), EntityType.IRON_GOLEM));
        } else if (p == yellowPlayer) {
            yellowGolems.add(world.spawnEntity(p.getLocation(), EntityType.IRON_GOLEM));
        } else if (p == greenPlayer) {
            greenGolems.add(world.spawnEntity(p.getLocation(), EntityType.IRON_GOLEM));
        }

    }

    @EventHandler
    public void onGolemAttack(EntityDamageByEntityEvent event) {
        for (Entity golem : redGolems) {
            if (event.getDamager() == golem && event.getEntity() == redPlayer) {
                event.setCancelled(true);
            }
        }
        for (Entity golem : blueGolems) {
            if (event.getDamager() == golem && event.getEntity() == bluePlayer) {
                event.setCancelled(true);
            }
        }
        for (Entity golem : yellowGolems) {
            if (event.getDamager() == golem && event.getEntity() == yellowPlayer) {
                event.setCancelled(true);
            }
        }
        for (Entity golem : greenGolems) {
            if (event.getDamager() == golem && event.getEntity() == greenPlayer) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isCompass(event.getItem())) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);
        String lobbyName = plugin.getConfig().getString("lobby", "");
        String lobbyPath = plugin.getConfig().getString("maps." + lobbyName + ".path", lobbyName);
        World lobby = Bukkit.getWorld(lobbyPath);
        if (lobby == null) {
            lobby = Bukkit.getWorld(lobbyName);
        }
        if (lobby == null && !Bukkit.getWorlds().isEmpty()) {
            lobby = Bukkit.getWorlds().get(0);
        }
        if (lobby != null) {
            player.teleport(lobby.getSpawnLocation());
        }
        player.setGameMode(GameMode.ADVENTURE);
        if (player.isOp()) {
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    public void eliminatePlayer(Player player) {
        player.setDisplayName(ChatColor.GRAY + player.getName());
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.getInventory().addItem(ITEM);
        alivePlayers -= 1;
    }

    public void onWin(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        new BukkitRunnable() {
            int secondsLeft = 10;
            boolean toggle = true;
            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    // send player to lobby
                    cancel();
                    return;
                }
                if (!player.isOp()) {
                    player.setDisplayName(ChatColor.GRAY + player.getName() + "is good dog");
                }
                if (toggle) {
                    player.sendTitle("", ChatColor.RED + "You won!");
                    toggle = false;
                } else {
                    player.sendTitle("", ChatColor.BLUE + "You won!");
                    toggle = true;
                }
                player.getWorld().spawnEntity(new Location(world,
                        player.getLocation().getX(),
                        player.getLocation().getY() + 1, player.getLocation().getZ()),
                        EntityType.FIREWORK_ROCKET);
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        return;
    }

    private boolean nearSpawn(Location loc) {
        for (Location spawn : spawns) {
            if (spawn.getWorld().equals(loc.getWorld()) && spawn.distanceSquared(loc) <= 2.0) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        if (!event.getBlock().getWorld().equals(world)) {
            return;
        }
        if (nearSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
        if (event.getBlock().getY() <= depthBuildLimit) {
            event.setCancelled(true);
        }
        if (event.getBlock().getY() >= heightBuildLimit) {
            event.setCancelled(true);
        }
        if (event.getBlock().getType().equals(BlockType.TNT)) {
            event.setCancelled(true);
            world.getBlockAt(event.getBlockPlaced().getLocation()).breakNaturally();
            world.spawnEntity(event.getBlock().getLocation(), EntityType.TNT);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (event.getBlock().getLocation() == redBed1||event.getBlock().getLocation() == redBed2) {
            canRedRespawn = false;
            return;
        }else if (event.getBlock().getLocation() == blueBed1||event.getBlock().getLocation() == blueBed2) {
            canBlueRespawn = false;
            return;
        }else if (event.getBlock().getLocation() == yellowBed1||event.getBlock().getLocation() == yellowBed2) {
            canYellowRespawn = false;
            return;
        } else if (event.getBlock().getLocation() == greenBed1||event.getBlock().getLocation() == greenBed2) {
            canGreenRespawn = false;
            return;
        }

        if (player.isOp() || player.hasPermission("serverPlugin.admin")) {
            return;
        }

        Location loc = event.getBlock().getLocation();

        // Allow breaking only for blocks placed after the plugin was enabled
        if (!placedBlocks.contains(loc)) {
            event.setCancelled(true);
        } else {
            // Remove from set so the block can be placed again in future
            placedBlocks.remove(loc);
        }
    }

    public void removeItems(ItemStack i, Player player, int amount) {
        int v = amount;
        for (ItemStack item : player.getInventory()) {
            if (item == i) {
                if (v == 0) {
                    return;
                }
                if (item.getAmount() <= v) {
                    player.getInventory().removeItem(item);
                    v -= item.getAmount();
                } else {
                    player.getInventory().removeItem(item);
                    player.getInventory().addItem(new ItemStack(item.getType(), item.getAmount() - amount));
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player == redPlayer) {
            if (canRedRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            redPlayer.teleport(redSpawn);
                            redPlayer.sendTitle("", "§e You respawned! §f");
                            redPlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                if (player.equals(redPlayer)) {
                                    if (redArmor == 0) {
                                        equipTeamArmor(player, Color.RED, 0, TrimMaterial.REDSTONE);
                                    } else if (redArmor == 1) {
                                        equipTeamArmor(player, Color.RED, 1, TrimMaterial.REDSTONE);
                                    } else if (redArmor == 2) {
                                        equipTeamArmor(player, Color.RED, 2, TrimMaterial.REDSTONE);
                                    } else if (redArmor == 3) {
                                        equipTeamArmor(player, Color.RED, 3, TrimMaterial.REDSTONE);
                                    }
                                    if (redSword == 0) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                    } else if (redSword == 1) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        redSword--;
                                    } else if (redSword == 2) {
                                        player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                        redSword--;
                                    } else if (redSword == 3) {
                                        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                        redSword--;
                                    }
                                    if (redPickaxe == 0) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                    } else if (redPickaxe == 1) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        redPickaxe--;
                                    } else if (redPickaxe == 2) {
                                        player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                        redPickaxe--;
                                    } else if (redPickaxe == 3) {
                                        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                        redPickaxe--;
                                    }
                                    if (redAxe == 0) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                    } else if (redAxe == 1) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        redAxe--;
                                    } else if (redAxe == 2) {
                                        player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                        redAxe--;
                                    } else if (redAxe == 3) {
                                        player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                        redAxe--;
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(redPlayer);
                isRedEliminated = true;
                if (isBlueEliminated && isYellowEliminated) {
                    onWin(greenPlayer);
                } else if (isBlueEliminated && isGreenEliminated) {
                    onWin(yellowPlayer);
                } else if (isGreenEliminated && isYellowEliminated) {
                    onWin(bluePlayer);
                }
                return;
            }
        } else if (player == bluePlayer) {
            if (canBlueRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            bluePlayer.teleport(blueSpawn);
                            bluePlayer.sendTitle("", "§e You respawned! §f");
                            bluePlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                 if (player == bluePlayer) {
                                    if (blueArmor == 0) {
                                        equipTeamArmor(player, Color.BLUE, 0, TrimMaterial.LAPIS);
                                    } else if (blueArmor == 1) {
                                        equipTeamArmor(player, Color.BLUE, 1, TrimMaterial.LAPIS);
                                    } else if (blueArmor == 2) {
                                        equipTeamArmor(player, Color.BLUE, 2, TrimMaterial.LAPIS);
                                    } else if (blueArmor == 3) {
                                        equipTeamArmor(player, Color.BLUE, 3, TrimMaterial.LAPIS);
                                    }
                                    if (blueSword == 0) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                    } else if (blueSword == 1) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        blueSword--;
                                    } else if (blueSword == 2) {
                                        player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                        blueSword--;
                                    } else if (blueSword == 3) {
                                        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                        blueSword--;
                                    }
                                    if (bluePickaxe == 0) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                    } else if (bluePickaxe == 1) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        bluePickaxe--;
                                    } else if (bluePickaxe == 2) {
                                        player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                        bluePickaxe--;
                                    } else if (bluePickaxe == 3) {
                                        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                        bluePickaxe--;
                                    }
                                    if (blueAxe == 0) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                    } else if (blueAxe == 1) {
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        blueAxe--;
                                    } else if (blueAxe == 2) {
                                        player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                        blueAxe--;
                                    } else if (blueAxe == 3) {
                                        player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                        blueAxe--;
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(bluePlayer);
                isBlueEliminated = true;
                if (isRedEliminated && isYellowEliminated) {
                    onWin(greenPlayer);
                } else if (isRedEliminated && isGreenEliminated) {
                    onWin(yellowPlayer);
                } else if (isGreenEliminated && isYellowEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        } else if (player == yellowPlayer) {
            if (canYellowRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            yellowPlayer.teleport(yellowSpawn);
                            yellowPlayer.sendTitle("", "§e You respawned! §f");
                            yellowPlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                if (player.equals(yellowPlayer)) {
                                    if (player == yellowPlayer) {
                                        if (yellowArmor == 0) {
                                            equipTeamArmor(player, Color.YELLOW, 0, TrimMaterial.GOLD);
                                        } else if (yellowArmor == 1) {
                                            equipTeamArmor(player, Color.YELLOW, 1, TrimMaterial.GOLD);
                                        } else if (yellowArmor == 2) {
                                            equipTeamArmor(player, Color.YELLOW, 2, TrimMaterial.GOLD);
                                        } else if (yellowArmor == 3) {
                                            equipTeamArmor(player, Color.YELLOW, 3, TrimMaterial.GOLD);
                                        }
                                        if (yellowSword == 0) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        } else if (yellowSword == 1) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                            yellowSword--;
                                        } else if (yellowSword == 2) {
                                            player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                            yellowSword--;
                                        } else if (yellowSword == 3) {
                                            player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                            yellowSword--;
                                        }
                                        if (yellowPickaxe == 0) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        } else if (yellowPickaxe == 1) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                            yellowPickaxe--;
                                        } else if (yellowPickaxe == 2) {
                                            player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                            yellowPickaxe--;
                                        } else if (yellowPickaxe == 3) {
                                            player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                            yellowPickaxe--;
                                        }
                                        if (yellowAxe == 0) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        } else if (yellowAxe == 1) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                            yellowAxe--;
                                        } else if (yellowAxe == 2) {
                                            player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                            yellowAxe--;
                                        } else if (yellowAxe == 3) {
                                            player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                            yellowAxe--;
                                        }
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(yellowPlayer);
                isYellowEliminated = true;
                if (isRedEliminated && isBlueEliminated) {
                    onWin(greenPlayer);
                } else if (isRedEliminated && isGreenEliminated) {
                    onWin(bluePlayer);
                } else if (isGreenEliminated && isBlueEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        } else if (player == greenPlayer) {
            if (canGreenRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            greenPlayer.teleport(greenSpawn);
                            greenPlayer.sendTitle("", "§e You respawned! §f");
                            greenPlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                if (player.equals(greenPlayer)) {
                                    if (player == yellowPlayer) {
                                        if (greenArmor == 0) {
                                            equipTeamArmor(player, Color.GREEN, 0, TrimMaterial.EMERALD);
                                        } else if (greenArmor == 1) {
                                            equipTeamArmor(player, Color.GREEN, 1, TrimMaterial.EMERALD);
                                        } else if (greenArmor == 2) {
                                            equipTeamArmor(player, Color.GREEN, 2, TrimMaterial.EMERALD);
                                        } else if (greenArmor == 3) {
                                            equipTeamArmor(player, Color.GREEN, 3, TrimMaterial.EMERALD);
                                        }
                                        if (greenSword == 0) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        } else if (greenSword == 1) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                            greenSword--;
                                        } else if (greenSword == 2) {
                                            player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                            greenSword--;
                                        } else if (greenSword == 3) {
                                            player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                            greenSword--;
                                        }
                                        if (greenPickaxe == 0) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        } else if (greenPickaxe == 1) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                            greenPickaxe--;
                                        } else if (greenPickaxe == 2) {
                                            player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                            greenPickaxe--;
                                        } else if (greenPickaxe == 3) {
                                            player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                            greenPickaxe--;
                                        }
                                        if (greenAxe == 0) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        } else if (greenAxe == 1) {
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                            greenAxe--;
                                        } else if (greenAxe == 2) {
                                            player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                            greenAxe--;
                                        } else if (greenAxe == 3) {
                                            player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                            greenAxe--;
                                        }
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(greenPlayer);
                isGreenEliminated = true;
                if (isRedEliminated && isBlueEliminated) {
                    onWin(yellowPlayer);
                } else if (isRedEliminated && isYellowEliminated) {
                    onWin(bluePlayer);
                } else if (isYellowEliminated && isBlueEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        }
    }

    public void onPlayerFell(Player player) {
        if (player == redPlayer) {
            if (canRedRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            redPlayer.teleport(redSpawn);
                            redPlayer.sendTitle("", "§e You respawned! §f");
                            redPlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                if (player.equals(redPlayer)) {
                                    if (redArmor == 0) {
                                        equipTeamArmor(player, Color.RED, 0, TrimMaterial.REDSTONE);
                                    } else if (redArmor == 1) {
                                        equipTeamArmor(player, Color.RED, 1, TrimMaterial.REDSTONE);
                                    } else if (redArmor == 2) {
                                        equipTeamArmor(player, Color.RED, 2, TrimMaterial.REDSTONE);
                                    } else if (redArmor == 3) {
                                        equipTeamArmor(player, Color.RED, 3, TrimMaterial.REDSTONE);
                                    }
                                    if (redSword == 0) {
                                        removeItems(new ItemStack(Material.WOODEN_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                    } else if (redSword == 1) {
                                        removeItems(new ItemStack(Material.STONE_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        redSword--;
                                    } else if (redSword == 2) {
                                        removeItems(new ItemStack(Material.IRON_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                        redSword--;
                                    } else if (redSword == 3) {
                                        removeItems(new ItemStack(Material.DIAMOND_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                        redSword--;
                                    }
                                    if (redPickaxe == 0) {
                                        removeItems(new ItemStack(Material.WOODEN_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                    } else if (redPickaxe == 1) {
                                        removeItems(new ItemStack(Material.STONE_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        redPickaxe--;
                                    } else if (redPickaxe == 2) {
                                        removeItems(new ItemStack(Material.IRON_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                        redPickaxe--;
                                    } else if (redPickaxe == 3) {
                                        removeItems(new ItemStack(Material.DIAMOND_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                        redPickaxe--;
                                    }
                                    if (redAxe == 0) {
                                        removeItems(new ItemStack(Material.WOODEN_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                    } else if (redAxe == 1) {
                                        removeItems(new ItemStack(Material.STONE_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        redAxe--;
                                    } else if (redAxe == 2) {
                                        removeItems(new ItemStack(Material.IRON_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                        redAxe--;
                                    } else if (redAxe == 3) {
                                        removeItems(new ItemStack(Material.DIAMOND_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                        redAxe--;
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(redPlayer);
                isRedEliminated = true;
                if (isBlueEliminated && isYellowEliminated) {
                    onWin(greenPlayer);
                } else if (isBlueEliminated && isGreenEliminated) {
                    onWin(yellowPlayer);
                } else if (isGreenEliminated && isYellowEliminated) {
                    onWin(bluePlayer);
                }
                return;
            }
        } else if (player == bluePlayer) {
            if (canBlueRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            bluePlayer.teleport(blueSpawn);
                            bluePlayer.sendTitle("", "§e You respawned! §f");
                            bluePlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                if (player == bluePlayer) {
                                    if (blueArmor == 0) {
                                        equipTeamArmor(player, Color.BLUE, 0, TrimMaterial.LAPIS);
                                    } else if (blueArmor == 1) {
                                        equipTeamArmor(player, Color.BLUE, 1, TrimMaterial.LAPIS);
                                    } else if (blueArmor == 2) {
                                        equipTeamArmor(player, Color.BLUE, 2, TrimMaterial.LAPIS);
                                    } else if (blueArmor == 3) {
                                        equipTeamArmor(player, Color.BLUE, 3, TrimMaterial.LAPIS);
                                    }
                                    if (blueSword == 0) {
                                        removeItems(new ItemStack(Material.WOODEN_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                    } else if (blueSword == 1) {
                                        removeItems(new ItemStack(Material.STONE_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        blueSword--;
                                    } else if (blueSword == 2) {
                                        removeItems(new ItemStack(Material.IRON_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                        blueSword--;
                                    } else if (blueSword == 3) {
                                        removeItems(new ItemStack(Material.DIAMOND_SWORD), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                        blueSword--;
                                    }
                                    if (bluePickaxe == 0) {
                                        removeItems(new ItemStack(Material.WOODEN_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                    } else if (bluePickaxe == 1) {
                                        removeItems(new ItemStack(Material.STONE_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        bluePickaxe--;
                                    } else if (bluePickaxe == 2) {
                                        removeItems(new ItemStack(Material.IRON_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                        bluePickaxe--;
                                    } else if (bluePickaxe == 3) {
                                        removeItems(new ItemStack(Material.DIAMOND_PICKAXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                        bluePickaxe--;
                                    }
                                    if (blueAxe == 0) {
                                        removeItems(new ItemStack(Material.WOODEN_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                    } else if (blueAxe == 1) {
                                        removeItems(new ItemStack(Material.STONE_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        blueAxe--;
                                    } else if (blueAxe == 2) {
                                        removeItems(new ItemStack(Material.IRON_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                        blueAxe--;
                                    } else if (blueAxe == 3) {
                                        removeItems(new ItemStack(Material.DIAMOND_AXE), player, 1);
                                        player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                        blueAxe--;
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(bluePlayer);
                isBlueEliminated = true;
                if (isRedEliminated && isYellowEliminated) {
                    onWin(greenPlayer);
                } else if (isRedEliminated && isGreenEliminated) {
                    onWin(yellowPlayer);
                } else if (isGreenEliminated && isYellowEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        } else if (player == yellowPlayer) {
            if (canYellowRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            yellowPlayer.teleport(yellowSpawn);
                            yellowPlayer.sendTitle("", "§e You respawned! §f");
                            yellowPlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                if (player.equals(yellowPlayer)) {
                                    if (player == yellowPlayer) {
                                        if (yellowArmor == 0) {
                                            equipTeamArmor(player, Color.YELLOW, 0, TrimMaterial.GOLD);
                                        } else if (yellowArmor == 1) {
                                            equipTeamArmor(player, Color.YELLOW, 1, TrimMaterial.GOLD);
                                        } else if (yellowArmor == 2) {
                                            equipTeamArmor(player, Color.YELLOW, 2, TrimMaterial.GOLD);
                                        } else if (yellowArmor == 3) {
                                            equipTeamArmor(player, Color.YELLOW, 3, TrimMaterial.GOLD);
                                        }
                                        if (yellowSword == 0) {
                                            removeItems(new ItemStack(Material.WOODEN_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        } else if (yellowSword == 1) {
                                            removeItems(new ItemStack(Material.STONE_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                            yellowSword--;
                                        } else if (yellowSword == 2) {
                                            removeItems(new ItemStack(Material.IRON_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                            yellowSword--;
                                        } else if (yellowSword == 3) {
                                            removeItems(new ItemStack(Material.DIAMOND_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                            yellowSword--;
                                        }
                                        if (yellowPickaxe == 0) {
                                            removeItems(new ItemStack(Material.WOODEN_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        } else if (yellowPickaxe == 1) {
                                            removeItems(new ItemStack(Material.STONE_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                            yellowPickaxe--;
                                        } else if (yellowPickaxe == 2) {
                                            removeItems(new ItemStack(Material.IRON_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                            yellowPickaxe--;
                                        } else if (yellowPickaxe == 3) {
                                            removeItems(new ItemStack(Material.DIAMOND_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                            yellowPickaxe--;
                                        }
                                        if (yellowAxe == 0) {
                                            removeItems(new ItemStack(Material.WOODEN_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        } else if (yellowAxe == 1) {
                                            removeItems(new ItemStack(Material.STONE_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                            yellowAxe--;
                                        } else if (yellowAxe == 2) {
                                            removeItems(new ItemStack(Material.IRON_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                            yellowAxe--;
                                        } else if (yellowAxe == 3) {
                                            removeItems(new ItemStack(Material.DIAMOND_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                            yellowAxe--;
                                        }
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(yellowPlayer);
                isYellowEliminated = true;
                if (isRedEliminated && isBlueEliminated) {
                    onWin(greenPlayer);
                } else if (isRedEliminated && isGreenEliminated) {
                    onWin(bluePlayer);
                } else if (isGreenEliminated && isBlueEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        } else if (player == greenPlayer) {
            if (canGreenRespawn) {
                player.setRespawnLocation(waitPos);
                player.setGameMode(GameMode.SPECTATOR);
                new BukkitRunnable() {
                    int secondsLeft = 3;
                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            greenPlayer.teleport(greenSpawn);
                            greenPlayer.sendTitle("", "§e You respawned! §f");
                            greenPlayer.setGameMode(GameMode.SURVIVAL);
                            for (Player player : world.getPlayers()) {
                                if (player.equals(greenPlayer)) {
                                    if (player == yellowPlayer) {
                                        if (greenArmor == 0) {
                                            equipTeamArmor(player, Color.GREEN, 0, TrimMaterial.EMERALD);
                                        } else if (greenArmor == 1) {
                                            equipTeamArmor(player, Color.GREEN, 1, TrimMaterial.EMERALD);
                                        } else if (greenArmor == 2) {
                                            equipTeamArmor(player, Color.GREEN, 2, TrimMaterial.EMERALD);
                                        } else if (greenArmor == 3) {
                                            equipTeamArmor(player, Color.GREEN, 3, TrimMaterial.EMERALD);
                                        }
                                        if (greenSword == 0) {
                                            removeItems(new ItemStack(Material.WOODEN_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                        } else if (greenSword == 1) {
                                            removeItems(new ItemStack(Material.STONE_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                                            greenSword--;
                                        } else if (greenSword == 2) {
                                            removeItems(new ItemStack(Material.IRON_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                                            greenSword--;
                                        } else if (greenSword == 3) {
                                            removeItems(new ItemStack(Material.DIAMOND_SWORD), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                                            greenSword--;
                                        }
                                        if (greenPickaxe == 0) {
                                            removeItems(new ItemStack(Material.WOODEN_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                        } else if (greenPickaxe == 1) {
                                            removeItems(new ItemStack(Material.STONE_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                                            greenPickaxe--;
                                        } else if (greenPickaxe == 2) {
                                            removeItems(new ItemStack(Material.IRON_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                                            greenPickaxe--;
                                        } else if (greenPickaxe == 3) {
                                            removeItems(new ItemStack(Material.DIAMOND_PICKAXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                                            greenPickaxe--;
                                        }
                                        if (greenAxe == 0) {
                                            removeItems(new ItemStack(Material.WOODEN_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                        } else if (greenAxe == 1) {
                                            removeItems(new ItemStack(Material.STONE_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                                            greenAxe--;
                                        } else if (greenAxe == 2) {
                                            removeItems(new ItemStack(Material.IRON_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                                            greenAxe--;
                                        } else if (greenAxe == 3) {
                                            removeItems(new ItemStack(Material.DIAMOND_AXE), player, 1);
                                            player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                                            greenAxe--;
                                        }
                                    }
                                }
                            }
                            cancel();
                            return;
                        }
                        player.sendTitle("", "You'll respawn in §e" + secondsLeft + "§f ",
                                0, 20, 0);
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                return;
            } else {
                eliminatePlayer(greenPlayer);
                isGreenEliminated = true;
                if (isRedEliminated && isBlueEliminated) {
                    onWin(yellowPlayer);
                } else if (isRedEliminated && isYellowEliminated) {
                    onWin(bluePlayer);
                } else if (isYellowEliminated && isBlueEliminated) {
                    onWin(redPlayer);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!players.contains(player)) {
            return;
        }
        event.setRespawnLocation(player.getRespawnLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (players.remove(player)) {
            checkEnd();
        }
    }

    private void checkEnd() {
        if (players.isEmpty()) {
            end();
        }
    }

    private void end() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.unloadWorld(world, false);
            try {
                deleteDirectory(worldFolder);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to delete world folder: " + e.getMessage());
            }
        });
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private boolean isInside(Location loc) {
        return loc.getX() >= min.getX() && loc.getX() <= max.getX()
                && loc.getY() >= min.getY() && loc.getY() <= max.getY()
                && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        if (!player.getWorld().equals(world)) {
            return;
        }
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        if (to.getY() < fellY) {
            event.setCancelled(true);
            if (player != redPlayer && player != bluePlayer && player != yellowPlayer && player != greenPlayer) {
                player.kickPlayer(ChatColor.RED + "You're not supposed to join this game!");
            }
            onPlayerFell(player);
            return;
        }
        if (isInside(to)) {
            lastValid.put(player, to);
            return;
        }
        Location back = lastValid.getOrDefault(player, event.getFrom());
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, () -> player.teleport(back));
    }
}
