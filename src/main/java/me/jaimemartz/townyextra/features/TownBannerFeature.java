package me.jaimemartz.townyextra.features;

import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import me.jaimemartz.townyextra.TownyExtra;
import me.jaimemartz.townyextra.items.*;
import me.jaimemartz.townyextra.utils.TownyUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TownBannerFeature implements Listener, CommandExecutor {
    private final Map<Player, ArmorStand> stands = new HashMap<>();
    private final Map<Player, Boolean> status = new HashMap<>();
    private final List<ItemStack> banners = new ArrayList<>();
    private final TownyExtra plugin;

    public TownBannerFeature(TownyExtra plugin) {
        this.plugin = plugin;

        banners.add(Banner1.getItemStack());
        banners.add(Banner2.getItemStack());
        banners.add(Banner3.getItemStack());
        banners.add(Banner4.getItemStack());

        banners.add(Banner5.getItemStack());
        banners.add(Banner6.getItemStack());
        banners.add(Banner7.getItemStack());
        banners.add(Banner8.getItemStack());

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("bannertoggle").setExecutor(this);

        plugin.getLogger().info("Initialized: " + getClass());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ArmorStand stand = stands.get(player);
            if (stand != null) {
                if (status.get(player)) {
                    status.put(player, false);
                    stand.setHelmet(new ItemStack(Material.AIR));
                    player.sendMessage(ChatColor.GREEN + "Has deshabilitado la bandera que tenias asignada");
                } else {
                    status.put(player, true);
                    showStand(player, stand);
                    player.sendMessage(ChatColor.GREEN + "Has habilitado la bandera que tenias asignada");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Este comando solo puede ser usado por jugadores con un banner");
            }
        } else {
            sender.sendMessage("This command can only be executed by a player");
        }
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR) //Making sure the Resident instance is created by towny
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Resident resident = TownyUtils.getResident(player);
            Town town = TownyUtils.getTown(resident);

            checkSetup(player, town);
        });
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ArmorStand stand = stands.get(player);
        if (stand == null) {
            Resident resident = TownyUtils.getResident(player);
            Town town = TownyUtils.getTown(resident);
            checkSetup(player, town);
            stand = stands.get(player);
        }

        if (stand == null) return;
        updatePos(stand, event.getRespawnLocation());
        showStand(player, stand);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ArmorStand stand = stands.get(player);
        if (stand == null) return;

        updatePos(stand, event.getTo());
    }

    @EventHandler
    public void on(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        ArmorStand stand = stands.get(player);
        if (stand == null) return;

        if (event.isSneaking()) {
            stand.setHelmet(new ItemStack(Material.AIR));
        } else {
            showStand(player, stand);
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ArmorStand stand = stands.get(player);
        if (stand == null) return;

        stand.setHelmet(new ItemStack(Material.AIR));
        tryRemove(player);
    }

    @EventHandler
    public void on(NewTownEvent event) {
        Town town = event.getTown();
        Resident resident = town.getMayor();
        Player player = TownyUtils.getPlayer(resident);

        setupStand(player);
    }

    @EventHandler
    public void on(RenameTownEvent event) {
        Town town = event.getTown();
        TownyUniverse.getOnlinePlayers(town).forEach(player -> {
            ArmorStand stand = stands.remove(player);

            //Clear previous armor stand if exists
            if (stand != null)
                stand.remove();

            checkSetup(player, town);
        });
    }

    @EventHandler
    public void on(TownRemoveResidentEvent event) {
        Resident resident = event.getResident();
        tryRemove(TownyUtils.getPlayer(resident));
    }

    @EventHandler
    public void on(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        ArmorStand stand = stands.get(player);
        if (stand == null) return;

        updatePos(stand, event.getTo());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        tryRemove(event.getPlayer());
    }

    @EventHandler
    public void on(PluginDisableEvent event) {
        if (event.getPlugin().getClass().equals(TownyExtra.class)) {
            Iterator<Map.Entry<Player, ArmorStand>> iterator = stands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Player, ArmorStand> entry = iterator.next();
                ArmorStand stand = entry.getValue();
                Chunk chunk = stand.getLocation().getChunk();

                if (!chunk.isLoaded()) //Make sure the chunk is loaded
                    chunk.load(true);

                stand.remove();
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void on(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        ArmorStand stand = stands.get(player);
        if (stand == null) return;

        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            stand.setHelmet(new ItemStack(Material.AIR));
        } else {
            showStand(player, stand);
        }
    }

    private void checkSetup(Player player, Town town) {
        if (town == null) return;

        Resident resident = TownyUtils.getResident(player);
        if (town.isMayor(resident) || town.hasAssistant(resident)) {
            setupStand(player);
        }
    }

    private void showStand(Player player, ArmorStand stand) {
        if (status.get(player) && player.getGameMode() != GameMode.SPECTATOR) {
            Resident resident = TownyUtils.getResident(player);
            if (resident == null) return;

            ItemStack item = getBanner(resident);
            if (item == null) return;

            stand.setHelmet(item);
        }
    }

    private void tryRemove(Player player) {
        if (player == null) return;

        ArmorStand stand = stands.remove(player);
        if (stand != null) {
            stand.getLocation().getChunk().load();
            stand.remove();
        }
    }

    private void setupStand(Player player) {
        Location location = player.getLocation();

        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        stand.setSmall(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);

        stands.put(player, stand);
        status.put(player, true);
        showStand(player, stand);

        plugin.getServer().getScheduler().runTask(plugin, () -> updatePos(stand, location));
    }

    private ItemStack getBanner(Resident resident) {
        Town town = TownyUtils.getTown(resident);
        if (town == null) return null;

        String path = "banners" + "." + town.getName() + ".";
        if (town.isMayor(resident)) {
            path += "rey";
        } else if (town.hasAssistant(resident)) {
            path += "lord";
        }

        if (!path.endsWith(".") && plugin.getConfig().isInt(path)) {
            int index = plugin.getConfig().getInt(path);
            return banners.get(index);
        }
        return null;
    }

    private void updatePos(ArmorStand stand, Location location) {
        location = location.clone();

        float angle = location.getYaw() + 90;
        if (angle < 0) {
            angle += 360;
        }

        double rad = Math.toRadians(angle);

        double x = Math.cos(rad);
        double z = Math.sin(rad);

        location.setX(location.getX() - x);
        location.setY(location.getY() + 0.25);
        location.setZ(location.getZ() - z);

        stand.teleport(location);
    }
}
