package io.github.kvbc.infiniteredstone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class InfiniteRedstone extends JavaPlugin implements Listener {
    private final long REDSTONE_REFRESH_DELAY = 0; // in ticks
    private final List<Location> locations = new ArrayList<Location>();

    private void refresh_redstone () {
        BlockRedstoneEvent.getHandlerList().unregister((Listener)this);

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
                refresh_redstone();
            }
        }.runTaskTimer(this, 0, REDSTONE_REFRESH_DELAY);
    }

    @EventHandler
    public void redstoneEvent (BlockRedstoneEvent event) {
        if (event.getBlock().getType() == Material.REDSTONE_WIRE) {
            if ((event.getNewCurrent() > 0) && (event.getNewCurrent() < 15)) {
                event.setNewCurrent(15);
                if(!locations.contains(event.getBlock().getLocation()))
                    locations.add(0, event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.REDSTONE_WIRE) {
            if (locations.contains(block.getLocation()))
                locations.remove(block.getLocation());
        }
    }
}
