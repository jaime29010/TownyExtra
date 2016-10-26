package me.jaimemartz.townyextra;

import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.jaimemartz.townyextra.utils.TownyUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class PlayerListener implements Listener {
    private final TownyExtra plugin;
    public PlayerListener(TownyExtra plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Resident resident = TownyUtils.getResident(player);
        if (resident == null) return;

        Town town = TownyUtils.getTown(resident);
        if (town == null) return;

        if (town.getMayor().equals(resident)) {
            plugin.setupStand(player);
        }
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        ArmorStand stand = plugin.getStands().get(player);
        if (stand == null) return;

        plugin.updatePos(stand, player.getLocation());
        plugin.showStand(player, stand);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = event.getTo();

        ArmorStand stand = plugin.getStands().get(player);
        if (stand == null) return;

        plugin.updatePos(stand, location);
    }

    @EventHandler
    public void on(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        ArmorStand stand = plugin.getStands().get(player);
        if (stand == null) return;

        if (event.isSneaking()) {
            plugin.hideStand(stand);
        } else {
            plugin.showStand(player, stand);
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ArmorStand stand = plugin.getStands().get(player);
        if (stand == null) return;

        plugin.hideStand(stand);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ArmorStand stand = plugin.getStands().remove(player);
        if (stand != null) {
            stand.remove();
        }
    }

    @EventHandler
    public void on(NewTownEvent event) {
        Town town = event.getTown();
        Resident resident = town.getMayor();
        Player player = TownyUtils.getPlayer(resident);

        plugin.setupStand(player);
    }

    @EventHandler
    public void on(RenameTownEvent event) {
        Town town = event.getTown();

        for (Resident resident : town.getResidents()) {
            Player player = TownyUtils.getPlayer(resident);
            ArmorStand stand = plugin.getStands().remove(player);
            if (stand != null) {
                stand.remove();
            }

            if (town.getMayor().equals(resident)) {
                plugin.setupStand(player);
            }
        }
    }

    @EventHandler
    public void on(TownRemoveResidentEvent event) {
        Resident resident = event.getResident();
        Player player = TownyUtils.getPlayer(resident);
        if (player == null) return;

        ArmorStand stand = plugin.getStands().remove(player);
        if (stand != null) {
            stand.remove();
        }
    }

    @EventHandler
    public void on(ChunkUnloadEvent event) {
        //FIXME
    }

    @EventHandler
    public void on(ChunkLoadEvent event) {
        //FIXME
    }
}
