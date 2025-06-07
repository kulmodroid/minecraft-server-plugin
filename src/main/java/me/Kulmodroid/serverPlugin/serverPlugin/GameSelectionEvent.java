package me.Kulmodroid.serverPlugin.serverPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameSelectionEvent extends Event
{

    private static final HandlerList HANDLERS = new HandlerList();

    Player player;

    public GameSelectionEvent(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
