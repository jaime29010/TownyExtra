package me.jaimemartz.townyextra.features;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import me.jaimemartz.townyextra.TownyExtra;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockSpamFeature implements Listener {
    private final List<Location> locs = new ArrayList<>();
    private final TownyExtra plugin;

    public BlockSpamFeature(TownyExtra plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Initialized: " + getClass());
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BlockState previous = event.getBlockReplacedState();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        Location location = block.getLocation();
        TownBlock cell = TownyUniverse.getTownBlock(location);

        if (player.isOp()) return;
        if (location.getWorld().equals(plugin.getServer().getWorld("world"))) {
            if (cell == null || cell.getType() == TownBlockType.WILDS) {
                if (location.getY() <= 90) {
                    if (block.getType() == Material.DIRT || block.getType() == Material.COBBLESTONE) {
                        if (locs.contains(location)) {
                            event.setCancelled(true);
                            return;
                        }

                        locs.add(location);
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            if (!chunk.isLoaded()) //Make sure the chunk is loaded
                                chunk.load(true);

                            block.setType(previous.getType());
                            block.setData(previous.getRawData());
                            locs.remove(location);
                        }, 20 * 4);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Has superado el limite de la altura permitida");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Chunk chunk = block.getChunk();
        BlockState previous = block.getState();
        Location location = block.getLocation();
        TownBlock cell = TownyUniverse.getTownBlock(location);

        if (player.isOp()) return;
        if (location.getWorld().equals(plugin.getServer().getWorlds().get(0))) {
            if (cell == null || cell.getType() == TownBlockType.WILDS) {
                if (location.getY() <= 90) {
                    if (event.getBucket() == Material.WATER_BUCKET && (block.getType() != Material.WATER || block.getType() != Material.STATIONARY_WATER)) {
                        if (locs.contains(location)) {
                            event.setCancelled(true);
                            return;
                        }

                        locs.add(location);
                        ItemStack item = event.getItemStack();
                        item.setType(Material.WATER_BUCKET);
                        block.setType(Material.AIR);
                        event.setItemStack(item); //Move inside the delayed task?
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            if (!chunk.isLoaded()) //Make sure the chunk is loaded
                                chunk.load(true);

                            block.setType(previous.getType());
                            block.setData(previous.getRawData());
                            locs.remove(location);
                        }, 5);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Has superado el limite de la altura permitida");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        Block block = event.getBlock();
        locs.remove(block.getLocation());
    }

    @EventHandler
    public void on(PluginDisableEvent event) {
        if (event.getPlugin().getClass().equals(TownyExtra.class)) {
            locs.stream().map(Location::getBlock).forEach(block -> block.setType(Material.AIR));
        }
    }
}
