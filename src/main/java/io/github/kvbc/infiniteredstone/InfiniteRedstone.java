package io.github.kvbc.infiniteredstone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class InfiniteRedstone extends JavaPlugin implements Listener {
    private List<Location> locations = new ArrayList<Location>();

    private void clear_locations () {
        HandlerList.unregisterAll((Plugin)this);

        List<Location> locationsCopy = new ArrayList<Location>(locations);
        locations.clear();

        for (Location location : locationsCopy) {
            Block block = getServer().getWorld("world").getBlockAt(location);
            block.setType(Material.REDSTONE_WIRE);
        }

        getServer().getPluginManager().registerEvents(this, this);

        for (Location location : locationsCopy) {
            Block block = getServer().getWorld("world").getBlockAt(location);
            block.setType(Material.REDSTONE_WIRE);
        }
    }

    @Override
    public void onEnable () {
        new BukkitRunnable() {
            @Override
            public void run() {
                clear_locations();
            }
        }.runTaskTimer(this, 0, 0);
    }

    @EventHandler
    public void redstoneEvent (BlockRedstoneEvent event) {
        if (event.getBlock().getType() == Material.REDSTONE_WIRE) {
            if ((event.getNewCurrent() > 0) && (event.getNewCurrent() < 15)) {
                event.setNewCurrent(15);
                if(!locations.contains(event.getBlock().getLocation()))
                    locations.add(event.getBlock().getLocation());
            }
        }
    }
}
