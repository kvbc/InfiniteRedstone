package io.github.kvbc.infiniteredstone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class InfiniteRedstone extends JavaPlugin implements Listener {
    private List<Location> redstone_locations = new ArrayList<Location>();

    @Nullable
    private World get_overworld () {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                return world;
            }
        }
        return null;
    }

    private void refresh_redstone () {
        if (redstone_locations.isEmpty())
            return;

        List<Location> redstone_locationsCopy = new ArrayList<Location>(redstone_locations);
        redstone_locations.clear();

        for (Location location : redstone_locationsCopy) {
            Block block = get_overworld().getBlockAt(location);
            block.setType(Material.AIR);
        }

        for (Location location : redstone_locationsCopy) {
            Block block = get_overworld().getBlockAt(location);
            block.setType(Material.REDSTONE_WIRE);
        }
    }

    @Override
    public void onEnable () {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                refresh_redstone();
            }
        }.runTaskTimer(this, 0, getConfig().getLong("refresh_delay"));
    }

    @EventHandler
    public void redstoneEvent (BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.REDSTONE_WIRE) {
            // TODO: trigger if (event.getNewCurrent() == 1)
            if ((event.getNewCurrent() > 0) && (event.getNewCurrent() < 15)) {
                event.setNewCurrent(15);
                if(!redstone_locations.contains(block.getLocation()))
                    redstone_locations.add(block.getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.REDSTONE_WIRE) {
            redstone_locations.remove(block.getLocation());
        }
    }
}
