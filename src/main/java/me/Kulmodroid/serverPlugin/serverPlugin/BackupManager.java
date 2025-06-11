package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Creates periodic backups of the world when admins place many blocks.
 */
public class BackupManager implements Listener {

    private final JavaPlugin plugin;
    private int adminBlocks;
    private long lastBackup;

    public BackupManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp() && !player.hasPermission("serverPlugin.admin")) {
            return;
        }
        adminBlocks++;
        if (adminBlocks > 100) {
            adminBlocks = 0;
            attemptBackup();
        }
    }

    private synchronized void attemptBackup() {
        long now = System.currentTimeMillis();
        if (now - lastBackup < 10 * 60 * 1000L) {
            return;
        }
        lastBackup = now;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.save();
            }
            Path base = plugin.getServer().getWorldContainer().toPath().resolve("backups");
            try {
                Files.createDirectories(base);
                String stamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(now));
                Path dest = base.resolve(stamp);
                Files.createDirectories(dest);
                for (World world : Bukkit.getServer().getWorlds()) {
                    copyDirectory(world.getWorldFolder().toPath(), dest.resolve(world.getName()));
                }
                cleanupOldBackups(base);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create backup: " + e.getMessage());
            }
        });
    }

    private void copyDirectory(Path src, Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path target = dst.resolve(src.relativize(dir).toString());
                Files.createDirectories(target);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path target = dst.resolve(src.relativize(file).toString());
                Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
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

    private void cleanupOldBackups(Path base) throws IOException {
        File[] dirs = base.toFile().listFiles(File::isDirectory);
        if (dirs == null || dirs.length <= 12) {
            return;
        }
        Arrays.sort(dirs, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
        for (int i = 0; i < dirs.length - 12; i++) {
            deleteDirectory(dirs[i].toPath());
        }
    }
}
