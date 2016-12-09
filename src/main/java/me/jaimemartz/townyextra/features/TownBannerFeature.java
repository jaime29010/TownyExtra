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
    private final List<ItemStack> banners = new ArrayList<>();
    private final TownyExtra plugin;

    public TownBannerFeature(TownyExtra plugin) {
        this.plugin = plugin;

        banners.add(0, Banner1.getItemStack());
        banners.add(1, Banner2.getItemStack());
        banners.add(2, Banner3.getItemStack());
        banners.add(3, Banner4.getItemStack());

        banners.add(4, Banner5.getItemStack());
        banners.add(5, Banner6.getItemStack());
        banners.add(6, Banner7.getItemStack());
        banners.add(7, Banner8.getItemStack());

        banners.add(8, Banner9.getItemStack());
        banners.add(9, Banner10.getItemStack());
        banners.add(10, Banner11.getItemStack());
        banners.add(11, Banner12.getItemStack());

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("bandera").setExecutor(this);

        plugin.getLogger().info("Initialized: " + getClass());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ArmorStand stand = stands.get(player);
            if (stand != null) {
                if (plugin.getDataPool().getToggleStatus().get(player.getUniqueId())) {
                    plugin.getDataPool().getToggleStatus().put(player.getUniqueId(), false);
                    stand.setHelmet(new ItemStack(Material.AIR));
                    player.sendMessage(ChatColor.GREEN + "Has deshabilitado la bandera que tenias asignada");
                } else {
                    plugin.getDataPool().getToggleStatus().put(player.getUniqueId(), true);
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
        if (town.isMayor(resident) || town.hasAssistant(resident) || player.hasPermission("townyextra.vip")) {
            setupStand(player);
        }
    }

    private void showStand(Player player, ArmorStand stand) {
        if (plugin.getDataPool().getToggleStatus().get(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR) {
            ItemStack item = getBanner(player);
            if (item == null) return;

            stand.setHelmet(item);
        }
    }

    private void tryRemove(Player player) {
        if (player == null) return;

        ArmorStand stand = stands.remove(player);
        if (stand != null) {
            Chunk chunk = stand.getLocation().getChunk();
            if (!chunk.isLoaded())
                chunk.load();

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

        plugin.getDataPool().getToggleStatus().put(player.getUniqueId(), true);
        showStand(player, stand);

        plugin.getServer().getScheduler().runTask(plugin, () -> updatePos(stand, location));
    }

    private ItemStack getBanner(Player player) {
        Resident resident = TownyUtils.getResident(player);
        if (resident == null) return null;

        Town town = TownyUtils.getTown(resident);
        if (town == null) return null;

        String path = "banners" + "." + town.getName() + ".";
        if (town.isMayor(resident)) {
            path += "rey";
        } else if (town.hasAssistant(resident)) {
            path += "lord";
        } else if (player.hasPermission("townyextra.vip")) {
            path += "vip";
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
