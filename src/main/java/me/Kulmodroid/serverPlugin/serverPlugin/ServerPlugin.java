package me.Kulmodroid.serverPlugin.serverPlugin;

import jdk.internal.access.JavaLangInvokeAccess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerPlugin extends JavaPlugin {

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent player) {
        player.getPlayer().sendMessage("Welcome to our server, " + player.getPlayer().getName() + ", your current ping is: " + player.getPlayer().getPing());
    }

    @Override
    public void onEnable()
    {

        getCommand("ping").setExecutor(new PingCommand());
        getServer().getPluginManager().registerEvents(new GameSelection(), this);
        getServer().getPluginCommand("gameselection").setExecutor(new GameSelectionCommand());
        new GameSelection().isDuelOpponentWaiting = false;
        new GameSelection().isDuelOpponentHere = false;
    }

    @Override
    public void onDisable() {

    }
}
