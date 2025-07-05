package me.Kulmodroid.serverPlugin.serverPlugin.game;

import com.google.common.collect.Lists;
import me.Kulmodroid.serverPlugin.serverPlugin.GameManager;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
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
    // public final short redVfacing;
    // public final short blueVfacing;
    // public final short yellowVfacing;
    // public final short greenVfacing;
    private final Player redPlayer;
    private final Player bluePlayer;
    private final Player yellowPlayer;
    private final Player greenPlayer;
    private final GameManager gameManager;
    private static ItemStack ITEM;
    private static final InventoryHolder HOLDER = new BedwarsGame.ShopHolder();
    private static final Set<Material> BEDS = EnumSet.of(
            Material.RED_BED,
            Material.BLUE_BED,
            Material.YELLOW_BED,
            Material.GREEN_BED
    );
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
            Material.DIAMOND_HELMET,
            Material.AIR
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
                       // short redVfacing,
                       // short blueVfacing,
                       // short yellowVfacing,
                       // short greenVfacing,
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
        // this.redVfacing = redVfacing;
        // this.blueVfacing = blueVfacing;
        // this.yellowVfacing = yellowVfacing;
        // this.greenVfacing = greenVfacing;
        this.gameManager = gameManger;
        this.ITEM = ITEM;
        cleanupOldShops();
    }
    EntityType redIV = EntityType.VILLAGER;
    EntityType blueIV = EntityType.VILLAGER;
    EntityType yellowIV = EntityType.VILLAGER;
    EntityType greenIV = EntityType.VILLAGER;

    public boolean fireballOnCooldown;

    public boolean isChestOpened;

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

    public boolean isBlueInBlockSec;
    public boolean isBlueInCombatSec;
    public boolean isBlueInToolsSec;
    public boolean isBlueInUtilitiesSec;
    public boolean isBlueInPotionsSec;

    public boolean isYellowInBlockSec;
    public boolean isYellowInCombatSec;
    public boolean isYellowInToolsSec;
    public boolean isYellowInUtilitiesSec;
    public boolean isYellowInPotionsSec;

    public boolean isGreenInBlockSec;
    public boolean isGreenInCombatSec;
    public boolean isGreenInToolsSec;
    public boolean isGreenInUtilitiesSec;
    public boolean isGreenInPotionsSec;

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

    private final List<Entity> redGolems = Lists.newArrayList();
    private final List<Entity> blueGolems = Lists.newArrayList();
    private final List<Entity> yellowGolems = Lists.newArrayList();
    private final List<Entity> greenGolems = Lists.newArrayList();

    private List<Integer> midEmeraldCounts = new ArrayList<>();
    private List<Integer> midDiamondCounts = new ArrayList<>();
    private List<Integer> midGoldCounts = new ArrayList<>();

    private final List<ItemStack> ironRed = Lists.newArrayList();
    private final List<ItemStack> goldRed = Lists.newArrayList();
    private final List<ItemStack> diamondRed = Lists.newArrayList();
    private final List<ItemStack> emeraldRed = Lists.newArrayList();

    private final List<ItemStack> ironBlue = Lists.newArrayList();
    private final List<ItemStack> goldBlue = Lists.newArrayList();
    private final List<ItemStack> diamondBlue = Lists.newArrayList();
    private final List<ItemStack> emeraldBlue = Lists.newArrayList();

    private final List<ItemStack> ironGreen = Lists.newArrayList();
    private final List<ItemStack> goldGreen = Lists.newArrayList();
    private final List<ItemStack> diamondGreen = Lists.newArrayList();
    private final List<ItemStack> emeraldGreen = Lists.newArrayList();

    private final List<ItemStack> ironYellow = Lists.newArrayList();
    private final List<ItemStack> goldYellow = Lists.newArrayList();
    private final List<ItemStack> diamondYellow = Lists.newArrayList();
    private final List<ItemStack> emeraldYellow = Lists.newArrayList();


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

    Inventory redUI;
    Inventory blueUI;
    Inventory yellowUI;
    Inventory greenUI;

    private Inventory setupSectionItemShopInventory(Material woolMat, ChatColor color, int sectionIndex) {
        Inventory inv = Bukkit.createInventory(HOLDER, 11 * 4, ChatColor.DARK_GRAY + "Item shop");

        if (sectionIndex == 1) {
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(1, new ItemStack(woolMat), false, "Blocks", inv, color);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", inv, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", inv, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", inv, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", inv, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        } else if (sectionIndex == 3) {
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", inv, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.DIAMOND_SWORD), false, "Weapons", inv, color);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", inv, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", inv, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", inv, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        } else if (sectionIndex == 5) {
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", inv, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", inv, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", inv, color);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", inv, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", inv, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        } else if(sectionIndex == 7) {
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", inv, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", inv, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.WOODEN_PICKAXE), false, "Tools", inv, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.ENDER_PEARL), false, "Utilities", inv, color);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(9, new ItemStack(Material.POTION), false, "Potions", inv, ChatColor.GRAY);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        } else if(sectionIndex == 9) {
            addItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(1, new ItemStack(Material.WHITE_WOOL), false, "Blocks", inv, ChatColor.GRAY);
            addItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(3, new ItemStack(Material.WOODEN_SWORD), false, "Weapons", inv, ChatColor.GRAY);
            addItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(5, new ItemStack(Material.DIAMOND_PICKAXE), false, "Tools", inv, ChatColor.GRAY);
            addItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            addItem(7, new ItemStack(Material.FIRE_CHARGE), false, "Utilities", inv, ChatColor.GRAY);
            addItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LEAPING);
            potion.setItemMeta(potionMeta);
            addItem(9, new ItemStack(potion), false, "Potions", inv, color);
            addItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), false, "", inv, ChatColor.BLACK);
        }

        return inv;
    }

    public Inventory setupTeamItemShop(Material woolmat, ChatColor color, int sectionIndex, Player player) {
        short swordType = -1;
        short armorType = -1;
        short pickaxeType = -2;
        short axeType = -2;
        for (ItemStack i : player.getInventory()) {
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
            if (!player.getInventory().contains(Material.WOODEN_PICKAXE) ||
                    !player.getInventory().contains(Material.IRON_PICKAXE) ||
                    !player.getInventory().contains(Material.STONE_PICKAXE) ||
                    !player.getInventory().contains(Material.DIAMOND_PICKAXE)) {
                pickaxeType = -1;
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
            if (!player.getInventory().contains(Material.WOODEN_AXE) ||
                    !player.getInventory().contains(Material.IRON_AXE) ||
                    !player.getInventory().contains(Material.STONE_AXE) ||
                    !player.getInventory().contains(Material.DIAMOND_AXE)) {
                axeType = -1;
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
        if (sectionIndex == 1) {
            Inventory inv = setupSectionItemShopInventory(woolmat, color, 1);
            addItem(24, new ItemStack(woolmat, 16), true, " costs 8 iron", inv, ChatColor.GRAY);
            addItem(26, new ItemStack(Material.OAK_PLANKS, 8), true, " costs 20 iron", inv, ChatColor.GRAY);
            addItem(28, new ItemStack(Material.END_STONE, 4), true, " costs 2 gold", inv, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.OBSIDIAN, 2), true, " costs 1 emerald", inv, ChatColor.GRAY);
            return inv;
        } else if (sectionIndex == 3) {
            Inventory inv = setupSectionItemShopInventory(woolmat, color, 3);
            if (swordType == 0) {
                addItem(24, new ItemStack(Material.STONE_SWORD), true, " costs 14 iron", inv, ChatColor.GRAY);
            } else if (swordType == 1) {
                addItem(24, new ItemStack(Material.IRON_SWORD), true, " costs 8 gold", inv, ChatColor.GRAY);
            } else if (swordType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_SWORD), true, " costs 4 emerald", inv, ChatColor.GRAY);
            } else if (swordType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade sword anymore!", inv, ChatColor.GRAY);
            }
            if (armorType == 0) {
                addItem(26, new ItemStack(Material.CHAINMAIL_CHESTPLATE), true, " costs 16 iron", inv, ChatColor.GRAY);
            } else if (armorType == 1) {
                addItem(26, new ItemStack(Material.IRON_CHESTPLATE), true, " costs 32 iron", inv, ChatColor.GRAY);
            } else if (armorType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_CHESTPLATE), true, " costs 6 emeralds", inv, ChatColor.GRAY);
            } else if (armorType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE), true, " can't upgrade armor anymore!", inv, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SNOWBALL), true, " costs 2 gold", inv, ChatColor.GRAY);
            addItem(30, new ItemStack(Material.BOW), true, " costs 12 gold", inv, ChatColor.GRAY);
            addItem(32, new ItemStack(Material.ARROW), true, " costs 4 iron", inv, ChatColor.GRAY);
            addItem(34, new ItemStack(Material.ARROW, 16), true, " costs 64 iron", inv, ChatColor.GRAY);
            return inv;
        } else if(sectionIndex == 5) {
            Inventory inv = setupSectionItemShopInventory(woolmat, color, 5);
            if (pickaxeType == 0) {
                addItem(24, new ItemStack(Material.STONE_PICKAXE), true, " costs 10 iron", inv, ChatColor.GRAY);
            } else if (pickaxeType == 1) {
                addItem(24, new ItemStack(Material.IRON_PICKAXE), true, " costs 5 gold", inv, ChatColor.GRAY);
            } else if (pickaxeType == 2) {
                addItem(24, new ItemStack(Material.DIAMOND_PICKAXE), true, " costs 10 gold", inv, ChatColor.GRAY);
            } else if (pickaxeType == 3) {
                addItem(24, new ItemStack(Material.BLACK_CONCRETE), false, " you can't upgrade your pickaxe anymore!", inv, ChatColor.GRAY);
            }
            short dm = 1;
            if (axeType == 0) {
                addItem(26, new ItemStack(Material.STONE_PICKAXE, 1, dm), true, " costs 10 iron", inv, ChatColor.GRAY);
            } else if (axeType == 1) {
                addItem(26, new ItemStack(Material.IRON_PICKAXE, 1, dm), true, " costs 5 gold", inv, ChatColor.GRAY);
            } else if (axeType == 2) {
                addItem(26, new ItemStack(Material.DIAMOND_AXE, 1, dm), true, " costs 10 gold", inv, ChatColor.GRAY);
            } else if (axeType == 3) {
                addItem(26, new ItemStack(Material.BLACK_CONCRETE, 1, dm), false, " you can't upgrade your axe anymore!", inv, ChatColor.GRAY);
            }
            addItem(28, new ItemStack(Material.SHEARS), true, " costs 17 iron", inv, ChatColor.GRAY);
            return inv;
        } else if(sectionIndex == 7) {
            Inventory inv = setupSectionItemShopInventory(woolmat, color, 5);
            return inv;
        } else if (sectionIndex == 9) {
            Inventory inv = setupSectionItemShopInventory(woolmat, color, 9);

            ItemStack strenghtPotion = new ItemStack(Material.POTION);
            PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
            strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
            strenghtPotion.setItemMeta(strenghtPotionMeta);
            addItem(24, new ItemStack(strenghtPotion), true, " costs 2 emerald", inv, ChatColor.GRAY);

            ItemStack invisibilityPotion = new ItemStack(Material.POTION);
            PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
            invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
            invisibilityPotion.setItemMeta(invisibilityPotionMeta);
            addItem(26, new ItemStack(invisibilityPotion), true, " costs 2 emerald", inv, ChatColor.GRAY);

            ItemStack jumpPotion = new ItemStack(Material.POTION);
            PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
            jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
            jumpPotion.setItemMeta(jumpPotionMeta);
            addItem(28, new ItemStack(jumpPotion), true, " costs 2 emerald", inv, ChatColor.GRAY);

            ItemStack levitationPotion = new ItemStack(Material.POTION);
            PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
            levitationPotionMeta.setBasePotionType(PotionType.WATER);
            levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
            levitationPotion.setItemMeta(levitationPotionMeta);
            addItem(30, new ItemStack(levitationPotion), false, "potion of costs 2 emerald", inv, ChatColor.GRAY);

            return inv;
        }
        return null;
    }

    public boolean buyAndReplaceItem(ItemStack removeItem,ItemStack getItem, int costing, Material matType, Player player) {
        assert (getItem != null);
        int ironAmount = 0;
        int goldAmount = 0;
        int emeraldAmount = 0;
        for (ItemStack i : player.getInventory()) {
            if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                ironAmount ++;
            } else if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                goldAmount ++;
            } else if (i.equals(new ItemStack(Material.EMERALD))) {
                goldAmount ++;
            }
        }
        if (matType.equals(Material.IRON_INGOT)) {
            if (ironAmount >= costing) {
                player.getInventory().removeItem(removeItem);
                player.getInventory().addItem(getItem);
                removeItems(new ItemStack(Material.IRON_INGOT), player, costing);
                return true;
            } else {
                player.sendMessage("You don't have iron enough");
                return false;
            }
        } else if (matType.equals(Material.GOLD_INGOT)) {
            if (goldAmount >= costing) {
                player.getInventory().removeItem(removeItem);
                player.getInventory().addItem(getItem);
                removeItems(new ItemStack(Material.GOLD_INGOT), player, costing);
                return true;
            } else {
                player.sendMessage("You don't have gold enough");
                return false;
            }
        } else if (matType.equals(Material.EMERALD)) {
            if (emeraldAmount >= costing) {
                player.getInventory().removeItem(removeItem);
                player.getInventory().addItem(getItem);
                removeItems(new ItemStack(Material.EMERALD), player, costing);
                return true;
            } else {
                player.sendMessage("You don't have emerald enough");
                return false;
            }
        }
        return false;
    }

    public boolean buyAndReplaceArmor(short armorType, int costing, Material matType, Player player, Color color, TrimMaterial trimMaterial) {
        int ironAmount = 0;
        int goldAmount = 0;
        int emeraldAmount = 0;
        for (ItemStack i : player.getInventory()) {
            if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                ironAmount ++;
            } else if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                goldAmount ++;
            } else if (i.equals(new ItemStack(Material.EMERALD))) {
                goldAmount ++;
            }
        }
        if (matType.equals(Material.IRON_INGOT)) {
            if (ironAmount >= costing) {
                equipTeamArmor(player, color, armorType, trimMaterial);
                removeItems(new ItemStack(Material.IRON_INGOT), player, costing);
                return true;
            } else {
                player.sendMessage("You don't have iron enough");
                return false;
            }
        } else if (matType.equals(Material.GOLD_INGOT)) {
            if (goldAmount >= costing) {
                equipTeamArmor(player, color, armorType, trimMaterial);
                removeItems(new ItemStack(Material.GOLD_INGOT), player, costing);
                return true;
            } else {
                player.sendMessage("You don't have gold enough");
                return false;
            }
        } else if (matType.equals(Material.EMERALD)) {
            if (emeraldAmount >= costing) {
                equipTeamArmor(player, color, armorType, trimMaterial);
                removeItems(new ItemStack(Material.EMERALD), player, costing);
                return true;
            } else {
                player.sendMessage("You don't have emerald enough");
                return false;
            }
        }
        return false;
    }

    public void buyItem(ItemStack item, int costing, Material matType, Player player) {
        assert (item != null);
        int ironAmount = 0;
        int goldAmount = 0;
        int emeraldAmount = 0;
        for (ItemStack i : player.getInventory()) {
            if (i.equals(new ItemStack(Material.IRON_INGOT))) {
                ironAmount ++;
            } else if (i.equals(new ItemStack(Material.GOLD_INGOT))) {
                goldAmount ++;
            } else if (i.equals(new ItemStack(Material.EMERALD))) {
                goldAmount ++;
            }
        }
        if (matType.equals(Material.IRON_INGOT)) {
            if (ironAmount >= costing) {
                player.getInventory().addItem(item);
                removeItems(new ItemStack(Material.IRON_INGOT), player, costing);
            } else {
                player.sendMessage("You don't have iron enough");
                return;
            }
        } else if (matType.equals(Material.GOLD_INGOT)) {
            if (goldAmount >= costing) {
                player.getInventory().addItem(item);
                removeItems(new ItemStack(Material.GOLD_INGOT), player, costing);
            } else {
                player.sendMessage("You don't have gold enough");
                return;
            }
        } else if (matType.equals(Material.EMERALD)) {
            if (emeraldAmount >= costing) {
                player.getInventory().addItem(item);
                removeItems(new ItemStack(Material.EMERALD), player, costing);
            } else {
                player.sendMessage("You don't have emerald enough");
                return;
            }
        }
    }

    @EventHandler
    public void onShopOpen(InventoryOpenEvent event) {
        if (event.getInventory() != redI && event.getInventory() != blueI &&
                event.getInventory() != yellowI && event.getInventory() != greenI) {
            return;
        }
        if (event.getPlayer().equals(redPlayer)) {
            isRedInBlockSec = true;
        } else if (event.getPlayer().equals(bluePlayer)) {
            isBlueInBlockSec = true;
        } else if (event.getPlayer().equals(yellowPlayer)) {
            isYellowInBlockSec = true;
        } else if (event.getPlayer().equals(greenPlayer)) {
            isGreenInBlockSec = true;
        }
    }

    @EventHandler
    public void onShopClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.VILLAGER) {
            return;
        }
        if (event.getPlayer().equals(redPlayer)) {
            redPlayer.openInventory(redI);
        } else if (event.getPlayer().equals(bluePlayer)) {
            bluePlayer.openInventory(blueI);
        } else if (event.getPlayer().equals(yellowPlayer)) {
            yellowPlayer.openInventory(yellowI);
        } else if (event.getPlayer().equals(greenPlayer)) {
            greenPlayer.openInventory(greenI);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = plugin.getServer().getPlayer(event.getWhoClicked().getName());
        Inventory playerInventory = player.getInventory();

        short swordType = -1;
        short armorType = -1;
        short pickaxeType = -1;
        short axeType = -1;

        if (event.getInventory() != redI && event.getInventory() != blueI &&
                event.getInventory() != yellowI && event.getInventory() != greenI) {
            return;
        }
        for (ItemStack i : player.getInventory()) {
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
        if (event.getInventory().equals(redI)) {
            int index = event.getSlot();
            Material woolmat = Material.RED_WOOL;
            ChatColor color = ChatColor.RED;
            TrimMaterial trimMaterial = TrimMaterial.REDSTONE;
            Color armorColor = Color.RED;
            boolean block = isRedInBlockSec;
            boolean combat = isRedInCombatSec;
            boolean tools = isRedInToolsSec;
            boolean utilities = isRedInUtilitiesSec;
            boolean potions = isRedInPotionsSec;

            if (index == 1) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, index, player);
                isRedInBlockSec = true;
                isRedInCombatSec = false;
                isRedInToolsSec = false;
                isRedInUtilitiesSec = false;
                isRedInPotionsSec = false;
            } else if (index == 3) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, index, player);
                isRedInBlockSec = false;
                isRedInCombatSec = true;
                isRedInToolsSec = false;
                isRedInUtilitiesSec = false;
                isRedInPotionsSec = false;
            } else if (index == 5) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, index, player);
                isRedInBlockSec = false;
                isRedInCombatSec = false;
                isRedInToolsSec = true;
                isRedInUtilitiesSec = false;
                isRedInPotionsSec = false;
            } else if (index == 7) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, index, player);
                isRedInBlockSec = false;
                isRedInCombatSec = false;
                isRedInToolsSec = false;
                isRedInUtilitiesSec = true;
                isRedInPotionsSec = false;
            } else if (index == 9) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, index, player);
                isRedInBlockSec = false;
                isRedInCombatSec = false;
                isRedInToolsSec = false;
                isRedInUtilitiesSec = false;
                isRedInPotionsSec = true;
            } else if (index == 24 && block) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(woolmat), 8, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && block) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OAK_PLANKS), 20, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 28 && block) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.END_STONE), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30  && block) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OBSIDIAN), 1, Material.EMERALD, player);
            } else if (event.getSlot() == 24 && combat) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 3, player);
                if (swordType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_SWORD),
                            new ItemStack(Material.STONE_SWORD),
                            14,
                            Material.IRON_INGOT,
                            player);
                } else if (swordType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_SWORD),
                            new ItemStack(Material.IRON_SWORD),
                            8,
                            Material.GOLD_INGOT,
                            player);
                } else if (swordType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_SWORD),
                            new ItemStack(Material.DIAMOND_SWORD),
                            4,
                            Material.EMERALD,
                            player);
                }
            } else if (event.getSlot() == 26 && combat) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 3, player);
                if (armorType == 0) {
                    buyAndReplaceArmor(armorType, 16, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 1) {
                    buyAndReplaceArmor(armorType, 32, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 2) {
                    buyAndReplaceArmor(armorType, 6, Material.EMERALD, player, armorColor, trimMaterial);
                }
            } else if (event.getSlot() == 28 && combat) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.SNOWBALL), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && combat) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.BOW), 12, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 32 && combat) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW), 4, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 34 && combat) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW, 16), 64, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && tools) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 5, player);
                if (pickaxeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_PICKAXE),
                            new ItemStack(Material.STONE_PICKAXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (pickaxeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_PICKAXE),
                            new ItemStack(Material.IRON_PICKAXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (pickaxeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_PICKAXE),
                            new ItemStack(Material.DIAMOND_PICKAXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 26 && tools) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 5, player);
                if (axeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (axeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_AXE),
                            new ItemStack(Material.IRON_AXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (axeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 28 && tools) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 5, player);
                buyItem(new ItemStack(Material.SHEARS), 17, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && utilities) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.GOLDEN_APPLE), 3, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 25 && utilities) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FIRE_CHARGE), 30, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && utilities) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.WIND_CHARGE), 25, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 27 && utilities) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.ENDER_PEARL), 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && utilities) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FISHING_ROD), 3, Material.EMERALD, player);
            } else if (event.getSlot() == 29 && utilities) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.CHORUS_FRUIT), 6, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && utilities) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), 120, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && potions) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack strenghtPotion = new ItemStack(Material.POTION);
                PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
                strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
                strenghtPotion.setItemMeta(strenghtPotionMeta);
                buyItem(strenghtPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 26 && potions) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack invisibilityPotion = new ItemStack(Material.POTION);
                PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
                invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
                invisibilityPotion.setItemMeta(invisibilityPotionMeta);
                buyItem(invisibilityPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && potions) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack jumpPotion = new ItemStack(Material.POTION);
                PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
                jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
                jumpPotion.setItemMeta(jumpPotionMeta);;
                buyItem(jumpPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 30 && potions) {
                event.getInventory().clear();
                redI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack levitationPotion = new ItemStack(Material.POTION);
                PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
                levitationPotionMeta.setBasePotionType(PotionType.WATER);
                levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
                levitationPotion.setItemMeta(levitationPotionMeta);
                buyItem(levitationPotion, 2, Material.EMERALD, player);
            }
        } else if (event.getInventory().equals(blueI)) {
            int index = event.getSlot();
            Material woolmat = Material.BLUE_WOOL;
            ChatColor color = ChatColor.BLUE;
            TrimMaterial trimMaterial = TrimMaterial.LAPIS;
            Color armorColor = Color.BLUE;
            boolean block = isBlueInBlockSec;
            boolean combat = isBlueInCombatSec;
            boolean tools = isBlueInToolsSec;
            boolean utilities = isBlueInUtilitiesSec;
            boolean potions = isBlueInPotionsSec;

            if (index == 1) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, index, player);
                isBlueInBlockSec = true;
                isBlueInCombatSec = false;
                isBlueInToolsSec = false;
                isBlueInUtilitiesSec = false;
                isBlueInPotionsSec = false;
            } else if (index == 3) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, index, player);
                isBlueInBlockSec = false;
                isBlueInCombatSec = true;
                isBlueInToolsSec = false;
                isBlueInUtilitiesSec = false;
                isBlueInPotionsSec = false;
            } else if (index == 5) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, index, player);
                isBlueInBlockSec = false;
                isBlueInCombatSec = false;
                isBlueInToolsSec = true;
                isBlueInUtilitiesSec = false;
                isBlueInPotionsSec = false;
            } else if (index == 7) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, index, player);
                isBlueInBlockSec = false;
                isBlueInCombatSec = false;
                isBlueInToolsSec = false;
                isBlueInUtilitiesSec = true;
                isBlueInPotionsSec = false;
            } else if (index == 9) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, index, player);
                isBlueInBlockSec = false;
                isBlueInCombatSec = false;
                isBlueInToolsSec = false;
                isBlueInUtilitiesSec = false;
                isBlueInPotionsSec = true;
            } else if (index == 24 && block) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(woolmat), 8, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && block) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OAK_PLANKS), 20, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 28 && block) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.END_STONE), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30  && block) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OBSIDIAN), 1, Material.EMERALD, player);
            } else if (event.getSlot() == 24 && combat) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 3, player);
                if (swordType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_SWORD),
                            new ItemStack(Material.STONE_SWORD),
                            14,
                            Material.IRON_INGOT,
                            player);
                } else if (swordType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_SWORD),
                            new ItemStack(Material.IRON_SWORD),
                            8,
                            Material.GOLD_INGOT,
                            player);
                } else if (swordType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_SWORD),
                            new ItemStack(Material.DIAMOND_SWORD),
                            4,
                            Material.EMERALD,
                            player);
                }
            } else if (event.getSlot() == 26 && combat) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 3, player);
                if (armorType == 0) {
                    buyAndReplaceArmor(armorType, 16, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 1) {
                    buyAndReplaceArmor(armorType, 32, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 2) {
                    buyAndReplaceArmor(armorType, 6, Material.EMERALD, player, armorColor, trimMaterial);
                }
            } else if (event.getSlot() == 28 && combat) {
                event.getInventory().clear();
                blueI =  setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.SNOWBALL), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && combat) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.BOW), 12, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 32 && combat) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW), 4, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 34 && combat) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW, 16), 64, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && tools) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 5, player);
                if (pickaxeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_PICKAXE),
                            new ItemStack(Material.STONE_PICKAXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (pickaxeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_PICKAXE),
                            new ItemStack(Material.IRON_PICKAXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (pickaxeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_PICKAXE),
                            new ItemStack(Material.DIAMOND_PICKAXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 26 && tools) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 5, player);
                if (axeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (axeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_AXE),
                            new ItemStack(Material.IRON_AXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (axeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 28 && tools) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 5, player);
                buyItem(new ItemStack(Material.SHEARS), 17, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && utilities) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.GOLDEN_APPLE), 3, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 25 && utilities) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FIRE_CHARGE), 30, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && utilities) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.WIND_CHARGE), 25, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 27 && utilities) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.ENDER_PEARL), 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && utilities) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FISHING_ROD), 3, Material.EMERALD, player);
            } else if (event.getSlot() == 29 && utilities) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.CHORUS_FRUIT), 6, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && utilities) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), 120, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && potions) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack strenghtPotion = new ItemStack(Material.POTION);
                PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
                strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
                strenghtPotion.setItemMeta(strenghtPotionMeta);
                buyItem(strenghtPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 26 && potions) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack invisibilityPotion = new ItemStack(Material.POTION);
                PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
                invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
                invisibilityPotion.setItemMeta(invisibilityPotionMeta);
                buyItem(invisibilityPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && potions) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack jumpPotion = new ItemStack(Material.POTION);
                PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
                jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
                jumpPotion.setItemMeta(jumpPotionMeta);;
                buyItem(jumpPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 30 && potions) {
                event.getInventory().clear();
                blueI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack levitationPotion = new ItemStack(Material.POTION);
                PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
                levitationPotionMeta.setBasePotionType(PotionType.WATER);
                levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
                levitationPotion.setItemMeta(levitationPotionMeta);
                buyItem(levitationPotion, 2, Material.EMERALD, player);
            }
        } else if (event.getInventory().equals(yellowI)) {
            int index = event.getSlot();
            Material woolmat = Material.YELLOW_WOOL;
            ChatColor color = ChatColor.YELLOW;
            TrimMaterial trimMaterial = TrimMaterial.GOLD;
            Color armorColor = Color.YELLOW;
            boolean block = isYellowInBlockSec;
            boolean combat = isYellowInCombatSec;
            boolean tools = isYellowInToolsSec;
            boolean utilities = isYellowInUtilitiesSec;
            boolean potions = isYellowInPotionsSec;

            if (index == 1) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, index, player);
                isYellowInBlockSec = true;
                isYellowInCombatSec = false;
                isYellowInToolsSec = false;
                isYellowInUtilitiesSec = false;
                isYellowInPotionsSec = false;
            } else if (index == 3) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, index, player);
                isYellowInBlockSec = false;
                isYellowInCombatSec = true;
                isYellowInToolsSec = false;
                isYellowInUtilitiesSec = false;
                isYellowInPotionsSec = false;
            } else if (index == 5) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, index, player);
                isYellowInBlockSec = false;
                isYellowInCombatSec = false;
                isYellowInToolsSec = true;
                isYellowInUtilitiesSec = false;
                isYellowInPotionsSec = false;
            } else if (index == 7) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, index, player);
                isYellowInBlockSec = false;
                isYellowInCombatSec = false;
                isYellowInToolsSec = false;
                isYellowInUtilitiesSec = true;
                isYellowInPotionsSec = false;
            } else if (index == 9) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, index, player);
                isYellowInBlockSec = false;
                isYellowInCombatSec = false;
                isYellowInToolsSec = false;
                isYellowInUtilitiesSec = false;
                isYellowInPotionsSec = true;
            } else if (index == 24 && block) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(woolmat), 8, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && block) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OAK_PLANKS), 20, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 28 && block) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.END_STONE), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30  && block) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OBSIDIAN), 1, Material.EMERALD, player);
            } else if (event.getSlot() == 24 && combat) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 3, player);
                if (swordType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_SWORD),
                            new ItemStack(Material.STONE_SWORD),
                            14,
                            Material.IRON_INGOT,
                            player);
                } else if (swordType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_SWORD),
                            new ItemStack(Material.IRON_SWORD),
                            8,
                            Material.GOLD_INGOT,
                            player);
                } else if (swordType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_SWORD),
                            new ItemStack(Material.DIAMOND_SWORD),
                            4,
                            Material.EMERALD,
                            player);
                }
            } else if (event.getSlot() == 26 && combat) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 3, player);
                if (armorType == 0) {
                    buyAndReplaceArmor(armorType, 16, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 1) {
                    buyAndReplaceArmor(armorType, 32, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 2) {
                    buyAndReplaceArmor(armorType, 6, Material.EMERALD, player, armorColor, trimMaterial);
                }
            } else if (event.getSlot() == 28 && combat) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.SNOWBALL), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && combat) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.BOW), 12, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 32 && combat) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW), 4, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 34 && combat) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW, 16), 64, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && tools) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 5, player);
                if (pickaxeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_PICKAXE),
                            new ItemStack(Material.STONE_PICKAXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (pickaxeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_PICKAXE),
                            new ItemStack(Material.IRON_PICKAXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (pickaxeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_PICKAXE),
                            new ItemStack(Material.DIAMOND_PICKAXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 26 && tools) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 5, player);
                if (axeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (axeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_AXE),
                            new ItemStack(Material.IRON_AXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (axeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 28 && tools) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 5, player);
                buyItem(new ItemStack(Material.SHEARS), 17, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && utilities) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.GOLDEN_APPLE), 3, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 25 && utilities) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FIRE_CHARGE), 30, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && utilities) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.WIND_CHARGE), 25, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 27 && utilities) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.ENDER_PEARL), 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && utilities) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FISHING_ROD), 3, Material.EMERALD, player);
            } else if (event.getSlot() == 29 && utilities) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.CHORUS_FRUIT), 6, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && utilities) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), 120, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && potions) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack strenghtPotion = new ItemStack(Material.POTION);
                PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
                strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
                strenghtPotion.setItemMeta(strenghtPotionMeta);
                buyItem(strenghtPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 26 && potions) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack invisibilityPotion = new ItemStack(Material.POTION);
                PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
                invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
                invisibilityPotion.setItemMeta(invisibilityPotionMeta);
                buyItem(invisibilityPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && potions) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack jumpPotion = new ItemStack(Material.POTION);
                PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
                jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
                jumpPotion.setItemMeta(jumpPotionMeta);;
                buyItem(jumpPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 30 && potions) {
                event.getInventory().clear();
                yellowI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack levitationPotion = new ItemStack(Material.POTION);
                PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
                levitationPotionMeta.setBasePotionType(PotionType.WATER);
                levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
                levitationPotion.setItemMeta(levitationPotionMeta);
                buyItem(levitationPotion, 2, Material.EMERALD, player);
            }
        } else if (event.getInventory().equals(greenI)) {
            int index = event.getSlot();
            Material woolmat = Material.GREEN_WOOL;
            ChatColor color = ChatColor.GREEN;
            TrimMaterial trimMaterial = TrimMaterial.EMERALD;
            Color armorColor = Color.GREEN;
            boolean block = isGreenInBlockSec;
            boolean combat = isGreenInCombatSec;
            boolean tools = isGreenInToolsSec;
            boolean utilities = isGreenInUtilitiesSec;
            boolean potions = isGreenInPotionsSec;

            if (index == 1) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, index, player);
                isGreenInBlockSec = true;
                isGreenInCombatSec = false;
                isGreenInToolsSec = false;
                isGreenInUtilitiesSec = false;
                isGreenInPotionsSec = false;
            } else if (index == 3) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, index, player);
                isGreenInBlockSec = false;
                isGreenInCombatSec = true;
                isGreenInToolsSec = false;
                isGreenInUtilitiesSec = false;
                isGreenInPotionsSec = false;
            } else if (index == 5) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, index, player);
                isGreenInBlockSec = false;
                isGreenInCombatSec = false;
                isGreenInToolsSec = true;
                isGreenInUtilitiesSec = false;
                isGreenInPotionsSec = false;
            } else if (index == 7) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, index, player);
                isGreenInBlockSec = false;
                isGreenInCombatSec = false;
                isGreenInToolsSec = false;
                isGreenInUtilitiesSec = true;
                isGreenInPotionsSec = false;
            } else if (index == 9) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, index, player);
                isGreenInBlockSec = false;
                isGreenInCombatSec = false;
                isGreenInToolsSec = false;
                isGreenInUtilitiesSec = false;
                isGreenInPotionsSec = true;
            } else if (index == 24 && block) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(woolmat), 8, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && block) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OAK_PLANKS), 20, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 28 && block) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.END_STONE), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30  && block) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 1, player);
                buyItem(new ItemStack(Material.OBSIDIAN), 1, Material.EMERALD, player);
            } else if (event.getSlot() == 24 && combat) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 3, player);
                if (swordType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_SWORD),
                            new ItemStack(Material.STONE_SWORD),
                            14,
                            Material.IRON_INGOT,
                            player);
                } else if (swordType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_SWORD),
                            new ItemStack(Material.IRON_SWORD),
                            8,
                            Material.GOLD_INGOT,
                            player);
                } else if (swordType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_SWORD),
                            new ItemStack(Material.DIAMOND_SWORD),
                            4,
                            Material.EMERALD,
                            player);
                }
            } else if (event.getSlot() == 26 && combat) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 3, player);
                if (armorType == 0) {
                    buyAndReplaceArmor(armorType, 16, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 1) {
                    buyAndReplaceArmor(armorType, 32, Material.IRON_INGOT, player, armorColor, trimMaterial);
                } else if (armorType == 2) {
                    buyAndReplaceArmor(armorType, 6, Material.EMERALD, player, armorColor, trimMaterial);
                }
            } else if (event.getSlot() == 28 && combat) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.SNOWBALL), 2, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && combat) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.BOW), 12, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 32 && combat) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW), 4, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 34 && combat) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 3, player);
                buyItem(new ItemStack(Material.ARROW, 16), 64, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && tools) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 5, player);
                if (pickaxeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_PICKAXE),
                            new ItemStack(Material.STONE_PICKAXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (pickaxeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_PICKAXE),
                            new ItemStack(Material.IRON_PICKAXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (pickaxeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.IRON_PICKAXE),
                            new ItemStack(Material.DIAMOND_PICKAXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 26 && tools) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 5, player);
                if (axeType == 0) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.IRON_INGOT,
                            player);
                } else if (axeType == 1) {
                    buyAndReplaceItem(new ItemStack(Material.STONE_AXE),
                            new ItemStack(Material.IRON_AXE),
                            5,
                            Material.GOLD_INGOT,
                            player);
                } else if (axeType == 2) {
                    buyAndReplaceItem(new ItemStack(Material.WOODEN_AXE),
                            new ItemStack(Material.STONE_AXE),
                            10,
                            Material.GOLD_INGOT,
                            player);
                }
            } else if (event.getSlot() == 28 && tools) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 5, player);
                buyItem(new ItemStack(Material.SHEARS), 17, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && utilities) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.GOLDEN_APPLE), 3, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 25 && utilities) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FIRE_CHARGE), 30, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 26 && utilities) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.WIND_CHARGE), 25, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 27 && utilities) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.ENDER_PEARL), 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && utilities) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.FISHING_ROD), 3, Material.EMERALD, player);
            } else if (event.getSlot() == 29 && utilities) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.CHORUS_FRUIT), 6, Material.GOLD_INGOT, player);
            } else if (event.getSlot() == 30 && utilities) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);
                buyItem(new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), 120, Material.IRON_INGOT, player);
            } else if (event.getSlot() == 24 && potions) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack strenghtPotion = new ItemStack(Material.POTION);
                PotionMeta strenghtPotionMeta = (PotionMeta) strenghtPotion.getItemMeta();
                strenghtPotionMeta.setBasePotionType(PotionType.STRENGTH);
                strenghtPotion.setItemMeta(strenghtPotionMeta);
                buyItem(strenghtPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 26 && potions) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack invisibilityPotion = new ItemStack(Material.POTION);
                PotionMeta invisibilityPotionMeta = (PotionMeta) invisibilityPotion.getItemMeta();
                invisibilityPotionMeta.setBasePotionType(PotionType.INVISIBILITY);
                invisibilityPotion.setItemMeta(invisibilityPotionMeta);
                buyItem(invisibilityPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 28 && potions) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack jumpPotion = new ItemStack(Material.POTION);
                PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
                jumpPotionMeta.setBasePotionType(PotionType.STRONG_LEAPING);
                jumpPotion.setItemMeta(jumpPotionMeta);;
                buyItem(jumpPotion, 2, Material.EMERALD, player);
            } else if (event.getSlot() == 30 && potions) {
                event.getInventory().clear();
                greenI = setupTeamItemShop(woolmat, color, 7, player);

                ItemStack levitationPotion = new ItemStack(Material.POTION);
                PotionMeta levitationPotionMeta = (PotionMeta) levitationPotion.getItemMeta();
                levitationPotionMeta.setBasePotionType(PotionType.WATER);
                levitationPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 7, 2), true);
                levitationPotion.setItemMeta(levitationPotionMeta);
                buyItem(levitationPotion, 2, Material.EMERALD, player);
            }
        }
    }

    @EventHandler
    public void onEnderChestAirClick(InventoryOpenEvent event) {
        if (event.getInventory().equals(event.getPlayer().getEnderChest())) {
            if (!NON_DROPPABLE_ITEMS.contains(event.getPlayer().getInventory().getItemInMainHand().getType())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onChestOpened(PlayerInteractEvent e) {
        if (e.hasBlock()) {
            if (e.getClickedBlock().getType().equals(Material.CHEST)) {
                isChestOpened = true;
            }
        }
    }

    @EventHandler
    public void onChestCloseEvent(InventoryCloseEvent e) {
        if (e.getInventory().equals(redI) ||
                e.getInventory().equals(blueI) ||
                e.getInventory().equals(yellowI) ||
                e.getInventory().equals(greenI) ||
                e.getInventory().equals(e.getPlayer().getEnderChest()) ||
                e.getPlayer().getInventory().equals(e.getInventory())) {
            return;
        }
        isChestOpened = false;
    }

    @EventHandler
    public void onChestInvClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null ||
                !e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
            return;
        }

        InventoryType topType = e.getView().getTopInventory().getType();
        if (topType != InventoryType.CHEST && topType != InventoryType.ENDER_CHEST) {
            return;
        }

        ItemStack item = e.getCurrentItem();
        if (item != null && NON_DROPPABLE_ITEMS.contains(item.getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderChestRightClick(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        if (!event.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
            return;
        }
        if (!NON_DROPPABLE_ITEMS.contains(event.getPlayer().getInventory().getItemInMainHand().getType())) {
            event.getPlayer().getEnderChest().addItem(event.getPlayer().getInventory().getItemInMainHand());
            event.getPlayer().getInventory().removeItem(event.getPlayer().getInventory().getItemInMainHand());
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

    @EventHandler
    public void onFireballInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || !event.getItem().equals(new ItemStack(Material.FIRE_CHARGE))) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (!fireballOnCooldown) {
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
                }.runTaskTimer(plugin, 0L, 20L);
            } else {
                player.sendMessage(ChatColor.RED + "Fireball is still on cooldown");
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.FIREBALL) {
            event.blockList().removeIf(block -> placedBlocks.contains(block.getLocation()));
        }
    }

    public synchronized void gameLoop() {
        isChestOpened = false;

        redI = setupTeamItemShop(Material.RED_WOOL, ChatColor.RED, 1, redPlayer);
        blueI = setupTeamItemShop(Material.BLUE_WOOL, ChatColor.BLUE, 1, bluePlayer);
        yellowI = setupTeamItemShop(Material.YELLOW_WOOL, ChatColor.YELLOW, 1, yellowPlayer);
        greenI = setupTeamItemShop(Material.GREEN_WOOL, ChatColor.GREEN, 1, greenPlayer);

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

        isRedInBlockSec = true;

        initMidGeneratorCounts();
        for (Player player : world.getPlayers()) {
            player.getInventory().clear();
            if (player.equals(redPlayer)) {
                equipTeamArmor(player, Color.RED, 0, TrimMaterial.REDSTONE);
                player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
            } else if (player.equals(bluePlayer)) {
                equipTeamArmor(player, Color.BLUE, 0, TrimMaterial.LAPIS);
                player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
            } else if (player.equals(yellowPlayer)) {
                equipTeamArmor(player, Color.YELLOW, 0, TrimMaterial.GOLD);
                player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
            } else if (player.equals(greenPlayer)) {
                equipTeamArmor(player, Color.GREEN, 0, TrimMaterial.EMERALD);
                player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
            }
        }

        LivingEntity redShop = (LivingEntity) world.spawnEntity(getMovedLoc(redItemShopPos, 0.5), redIV);
        LivingEntity blueShop = (LivingEntity) world.spawnEntity(getMovedLoc(blueItemShopPos, 0.5), blueIV);
        LivingEntity yellowShop = (LivingEntity) world.spawnEntity(getMovedLoc(yellowItemShopPos, 0.5), yellowIV);
        LivingEntity greenShop = (LivingEntity)  world.spawnEntity(getMovedLoc(greenItemShopPos, 0.5), greenIV);

        redShop.setAI(false);
        blueShop.setAI(false);
        yellowShop.setAI(false);
        greenShop.setAI(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (alivePlayers <= 1) {
                    cancel();
                    return;
                }
                if (redUnlockedGen1) {
                    if (redIronCount - 10 < ironMax) {
                    ItemStack item = dropItemStill(getExtentedLoc(getMovedLoc(redIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2)).getItemStack();
                        ironRed.add(item);
                        redIronCount += 2;
                    }
                    if (redGoldCount - 5 < goldMax && redGoldCooldown <= 0) {
                    ItemStack item = dropItemStill(getExtentedLoc(getMovedLoc(redGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1)).getItemStack();
                        goldRed.add(item);
                        redGoldCooldown = 5;
                        redGoldCount ++;
                    }
                    redGoldCooldown --;
                } else if (redUnlockedGen2) {
                    if (redIronCount - 20 < ironMax) {
                    ItemStack item = dropItemStill(getExtentedLoc(getMovedLoc(redIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2)).getItemStack();
                        ironRed.add(item);
                        redIronCount += 2;
                    }
                    if (redGoldCount - 5 < goldMax && redGoldCooldown <= 0) {
                    ItemStack item = dropItemStill(getExtentedLoc(getMovedLoc(redGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 2)).getItemStack();
                        goldRed.add(item);
                        redGoldCooldown = 5;
                        redGoldCount += 2;
                    }
                    if (redDiamondCount < diamonMax && redDiamondCooldown <= 0) {
                        ItemStack item = dropItemStill(getExtentedLoc(getMovedLoc(redDiamondGenerator, 0.5), 2), new ItemStack(Material.DIAMOND, 1)).getItemStack();
                        diamondRed.add(item);
                        redDiamondCooldown = 7;
                        redDiamondCount ++;
                    }
                    if (redEmeraldCount < emeraldMax && redEmeraldCooldown <= 0) {
                        ItemStack item = dropItemStill(getExtentedLoc(getMovedLoc(redEmeraldGenerator, 0.5), 2), new ItemStack(Material.EMERALD, 1)).getItemStack();
                        emeraldRed.add(item);
                        redEmeraldCooldown = 15;
                        redEmeraldCount ++;
                    }
                    redGoldCooldown -= 2;
                    redDiamondCooldown -= 1;
                    redEmeraldCooldown -= 1;
                } else {
                    if (redIronCount < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(redIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 1))).getItemStack();
                        ironRed.add(item);
                        redIronCount ++;
                    }
                    if (redGoldCount < goldMax && redGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(redGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1))).getItemStack();
                        goldRed.add(item);
                        redGoldCooldown = 5;
                        redGoldCount ++;
                    }
                    redGoldCooldown --;
                }
                if (blueUnlockedGen1) {
                    if (blueIronCount - 10 < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2))).getItemStack();
                        ironBlue.add(item);
                        blueIronCount += 2;
                    }
                    if (blueGoldCount - 5 < goldMax && blueGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1))).getItemStack();
                        goldBlue.add(item);
                        blueGoldCooldown = 5;
                        blueGoldCount ++;
                    }
                    blueGoldCooldown --;
                } else if (blueUnlockedGen2) {
                    if (blueIronCount + 20 < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2))).getItemStack();
                        ironBlue.add(item);
                        blueIronCount += 2;
                    }
                    if (blueGoldCount - 5 < goldMax && blueGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 2))).getItemStack();
                        goldBlue.add(item);
                        blueGoldCooldown = 5;
                        blueGoldCount += 2;
                    }
                    if (blueDiamondCount < diamonMax && blueDiamondCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueDiamondGenerator, 0.5), 2), new ItemStack(Material.DIAMOND, 1))).getItemStack();
                        diamondBlue.add(item);
                        blueDiamondCooldown = 7;
                        blueDiamondCount ++;
                    }
                    if (blueEmeraldCount < emeraldMax && blueEmeraldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueEmeraldGenerator, 0.5), 2), new ItemStack(Material.EMERALD, 1))).getItemStack();
                        emeraldBlue.add(item);
                        blueEmeraldCooldown = 15;
                        blueEmeraldCount ++;
                    }
                    blueGoldCooldown -= 2;
                    blueDiamondCooldown -= 1;
                    blueEmeraldCooldown -= 1;
                } else {
                    if (blueIronCount < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 1))).getItemStack();
                        ironBlue.add(item);
                        blueIronCount ++;
                    }
                    if (blueGoldCount < goldMax && blueGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(blueGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1))).getItemStack();
                        goldBlue.add(item);
                        blueGoldCooldown = 5;
                        blueGoldCount ++;
                    }
                    blueGoldCooldown --;
                }
                if (greenUnlockedGen1) {
                    if (greenIronCount - 10 < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2))).getItemStack();
                        ironGreen.add(item);
                        greenIronCount += 2;
                    }
                    if (greenGoldCount - 5 < goldMax && greenGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1))).getItemStack();
                        goldGreen.add(item);
                        greenGoldCooldown = 5;
                        greenGoldCount ++;
                    }
                    greenGoldCooldown --;
                } else if (greenUnlockedGen2) {
                    if (greenIronCount + 20 < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2))).getItemStack();
                        ironGreen.add(item);
                        greenIronCount += 2;
                    }
                    if (greenGoldCount - 5 < goldMax && greenGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 2))).getItemStack();
                        goldGreen.add(item);
                        greenGoldCooldown = 5;
                        greenGoldCount += 2;
                    }
                    if (greenDiamondCount < diamonMax && greenDiamondCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenDiamondGenerator, 0.5), 2), new ItemStack(Material.DIAMOND, 1))).getItemStack();
                        diamondGreen.add(item);
                        greenDiamondCooldown = 7;
                        greenDiamondCount ++;
                    }
                    if (greenEmeraldCount < emeraldMax && greenEmeraldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenEmeraldGenerator, 0.5), 2), new ItemStack(Material.EMERALD, 1))).getItemStack();
                        emeraldGreen.add(item);
                        greenEmeraldCooldown = 15;
                        greenEmeraldCount ++;
                    }
                    greenGoldCooldown -= 2;
                    greenDiamondCooldown -= 1;
                    greenEmeraldCooldown -= 1;
                } else {
                    if (greenIronCount < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 1))).getItemStack();
                        ironGreen.add(item);
                        greenIronCount ++;
                    }
                    if (greenGoldCount < goldMax && greenGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(greenGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1))).getItemStack();
                        goldGreen.add(item);
                        greenGoldCooldown = 5;
                        greenGoldCount ++;
                    }
                    greenGoldCooldown --;
                }
                if (yellowUnlockedGen1) {
                    if (yellowIronCount - 10 < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(yellowIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2))).getItemStack();
                        ironYellow.add(item);
                        yellowIronCount += 2;
                    }
                    if (yellowGoldCount - 5 < goldMax && yellowGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(yellowGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1))).getItemStack();
                        goldYellow.add(item);
                        yellowGoldCooldown = 5;
                        yellowGoldCount ++;
                    }
                    yellowGoldCooldown --;
                } else if (yellowUnlockedGen2) {
                    if (yellowIronCount + 20 < ironMax) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(yellowIronGenerator, 0.5), 2), new ItemStack(Material.IRON_INGOT, 2))).getItemStack();
                        ironYellow.add(item);
                        yellowIronCount += 2;
                    }
                    if (yellowGoldCount - 5 < goldMax && yellowGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(yellowGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 2))).getItemStack();
                        goldYellow.add(item);
                        yellowGoldCooldown = 5;
                        yellowGoldCount += 2;
                    }
                    if (yellowDiamondCount < diamonMax && yellowDiamondCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(yellowDiamondGenerator, 0.5), 2), new ItemStack(Material.DIAMOND, 1))).getItemStack();
                        diamondYellow.add(item);
                        yellowDiamondCooldown = 7;
                        yellowDiamondCount ++;
                    }
                    if (yellowEmeraldCount < emeraldMax && yellowEmeraldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(yellowEmeraldGenerator, 0.5), 2), new ItemStack(Material.EMERALD, 1))).getItemStack();
                        emeraldYellow.add(item);
                        yellowEmeraldCooldown = 15;
                        yellowEmeraldCount ++;
                    }
                    yellowGoldCooldown -= 2;
                    yellowDiamondCooldown -= 1;
                    yellowEmeraldCooldown -= 1;
                } else {
                    if (yellowIronCount < ironMax) {
                        ItemStack item = (dropItemStill(getMovedLoc(yellowIronGenerator, 0.5), new ItemStack(Material.IRON_INGOT, 1))).getItemStack();
                        ironYellow.add(item);
                        yellowIronCount ++;
                    }
                    if (yellowGoldCount < goldMax && yellowGoldCooldown <= 0) {
                        ItemStack item = (dropItemStill(getExtentedLoc(getMovedLoc(yellowGoldGenerator, 0.5), 2), new ItemStack(Material.GOLD_INGOT, 1))).getItemStack();
                        goldYellow.add(item);
                        yellowGoldCooldown = 5;
                        yellowGoldCount ++;
                    }
                    yellowGoldCooldown --;
                }
                goldCooldown = processMidGenerators(midGoldGenerators, midGoldCounts, goldMax, goldCooldown, 7, Material.GOLD_INGOT);
                diamondCooldown = processMidGenerators(midDiamondGenerators, midDiamondCounts, 15, diamondCooldown, 15, Material.DIAMOND);
                emeraldCooldown = processMidGenerators(midEmeraldGenerators, midEmeraldCounts, 15, emeraldCooldown, 30, Material.EMERALD);
//                for (Location loc : midGoldGenerators) {
//                    Entity entity = world.spawnEntity(loc, EntityType.ARMOR_STAND);
//                    entity.setVisibleByDefault(false);
//                    entity.setCustomName(ChatColor.GOLD + "" + goldCooldown  + "seconds remaining");
//                }
//                for (Location loc : midDiamondGenerators) {
//                    Entity entity = world.spawnEntity(loc, EntityType.ARMOR_STAND);
//                    entity.setVisibleByDefault(false);
//                    entity.setCustomName(ChatColor.AQUA + "" + diamondCooldown  + "seconds remaining");
//                }
//                for (Location loc : midEmeraldGenerators) {
//                    Entity entity = world.spawnEntity(loc, EntityType.ARMOR_STAND);
//                    entity.setVisibleByDefault(false);
//                    entity.setCustomName(ChatColor.GREEN + "" + emeraldCooldown  + "seconds remaining");
//                }
                // cooldowns are updated in processMidGenerators
            }
        }.runTaskTimer(plugin, 0L, 20L);
        return;
    }

    public Location getExtentedLoc(Location loc, double extension) {
        return new Location(loc.getWorld(), loc.getX(), loc.getY() + extension, loc.getZ());
    }
    public Location getMovedLoc(Location loc, double move) {
        return new Location(loc.getWorld(), loc.getX() + move, loc.getY(), loc.getZ() + move);
    }

    /**
     * Drops an item without giving it random velocity so that it stays on the generator.
     */
    private Item dropItemStill(Location loc, ItemStack stack) {
        Item item = world.dropItem(loc, stack);
        item.setVelocity(new Vector(0, 0, 0));
        return item;
    }

    private void initMidGeneratorCounts() {
        midGoldCounts = new ArrayList<>(Collections.nCopies(midGoldGenerators.size(), 0));
        midDiamondCounts = new ArrayList<>(Collections.nCopies(midDiamondGenerators.size(), 0));
        midEmeraldCounts = new ArrayList<>(Collections.nCopies(midEmeraldGenerators.size(), 0));
    }

    private int processMidGenerators(List<Location> generators, List<Integer> counts, int max,
                                     int cooldown, int reset, Material material) {
        if (cooldown <= 0) {
            for (int i = 0; i < generators.size(); i++) {
                if (counts.get(i) < max) {
                    Location loc = generators.get(i);
                    dropItemStill(getExtentedLoc(loc, 2), new ItemStack(material, 1));
                    counts.set(i, counts.get(i) + 1);
                }
            }
            cooldown = reset;
        }
        return cooldown - 1;
    }

    @EventHandler
    public void onShopInteract(PlayerInteractEntityEvent event) {
        if (event.getPlayer() == redPlayer) {
            event.getPlayer().openInventory(redI);
        } else if (event.getPlayer() == bluePlayer) {
            event.getPlayer().openInventory(blueI);
        } else if (event.getPlayer() == yellowPlayer) {
            event.getPlayer().openInventory(yellowI);
        } else if (event.getPlayer() == greenPlayer) {
            event.getPlayer().openInventory(greenI);
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
        switch (i) {
            case 0 -> player.teleport(redSpawn);
            case 1 -> player.teleport(blueSpawn);
            case 2 -> player.teleport(yellowSpawn);
            case 3 -> player.teleport(greenSpawn);
            default -> {
                if (i < spawns.size()) {
                    player.teleport(spawns.get(i));
                } else {
                    player.teleport(waitPos);
                }
            }
        }
        player.setRespawnLocation(player.getLocation());
        player.setGameMode(GameMode.SURVIVAL);
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
    public void onPlayerCollect(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (ironRed.contains(item)) {
            ironRed.remove(item);
            redIronCount -= item.getAmount();
        } else if (goldRed.contains(item)) {
            goldRed.remove(item);
            redGoldCount -= item.getAmount();
        } else if (diamondRed.contains(item)) {
            diamondRed.remove(item);
            redDiamondCount -= item.getAmount();
        } else if (emeraldRed.contains(item)) {
            emeraldRed.remove(item);
            redEmeraldCount -= item.getAmount();
        }
        if (ironBlue.contains(item)) {
            ironBlue.remove(item);
            blueIronCount -= item.getAmount();
        } else if (goldBlue.contains(item)) {
            goldBlue.remove(item);
            blueGoldCount -= item.getAmount();
        } else if (diamondBlue.contains(item)) {
            diamondBlue.remove(item);
            blueDiamondCount -= item.getAmount();
        } else if (emeraldBlue.contains(item)) {
            emeraldBlue.remove(item);
            blueEmeraldCount -= item.getAmount();
        }
        if (ironYellow.contains(item)) {
            ironYellow.remove(item);
            yellowIronCount -= item.getAmount();
        } else if (goldYellow.contains(item)) {
            goldYellow.remove(item);
            yellowGoldCount -= item.getAmount();
        } else if (diamondYellow.contains(item)) {
            diamondYellow.remove(item);
            yellowDiamondCount -= item.getAmount();
        } else if (emeraldYellow.contains(item)) {
            emeraldYellow.remove(item);
            yellowEmeraldCount -= item.getAmount();
        }
        if (ironGreen.contains(item)) {
            ironGreen.remove(item);
            greenIronCount -= item.getAmount();
        } else if (goldGreen.contains(item)) {
            goldGreen.remove(item);
            greenGoldCount -= item.getAmount();
        } else if (diamondGreen.contains(item)) {
            diamondGreen.remove(item);
            greenDiamondCount -= item.getAmount();
        } else if (emeraldGreen.contains(item)) {
            emeraldGreen.remove(item);
            greenEmeraldCount -= item.getAmount();
        }
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent e) {
        Material type = e.getBlock().getType();
        if(BEDS.contains(type)) {
            e.setDropItems(false);
        }
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
        assert(ITEM != null);
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
        redPlayer.getEnderChest().clear();
        bluePlayer.getEnderChest().clear();
        yellowPlayer.getEnderChest().clear();
        greenPlayer.getEnderChest().clear();
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
        if (event.getBlock().getType().equals(Material.TNT)) {
            event.setCancelled(true);
            world.getBlockAt(event.getBlockPlaced().getLocation()).breakNaturally();
            world.spawnEntity(event.getBlock().getLocation(), EntityType.TNT);
            return;
        }
        if (!event.isCancelled()) {
            placedBlocks.add(event.getBlock().getLocation());
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
        ItemStack remove = new ItemStack(i.getType(), amount);
        player.getInventory().removeItem(remove);
    }

    private void dropResources(Player player, Player killer) {
        if (killer == null) {
            return;
        }
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null) {
                continue;
            }
            Material type = stack.getType();
            if (type == Material.IRON_INGOT || type == Material.GOLD_INGOT
                    || type == Material.DIAMOND || type == Material.EMERALD) {
                killer.getInventory().addItem(stack.clone());
                player.getInventory().removeItem(stack);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().clear();
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
                                    dropResources(player, player.getKiller());
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
                                    dropResources(player, player.getKiller());
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
                                        dropResources(player, player.getKiller());
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
                                        dropResources(player, player.getKiller());
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
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (players.remove(player)) {
            player.getEnderChest().clear();
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
