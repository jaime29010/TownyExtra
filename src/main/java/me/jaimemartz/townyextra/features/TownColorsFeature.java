package me.jaimemartz.townyextra.features;

import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.jaimemartz.townyextra.TownyExtra;
import me.jaimemartz.townyextra.utils.TownyUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class TownColorsFeature implements Listener {
    private final Map<Town, Scoreboard> boards = new HashMap<>();
    private final ScoreboardManager manager;
    private final TownyExtra plugin;

    public TownColorsFeature(TownyExtra plugin) {
        this.plugin = plugin;
        manager = plugin.getServer().getScoreboardManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Initialized: " + getClass());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        //UPDATE BOARD ONLY TO PLAYER INITIALY
        Player player = event.getPlayer();
        Resident resident = TownyUtils.getResident(player);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Town town = TownyUtils.getTown(resident);

            Scoreboard board = boards.get(town);
            if (board == null) {
                board = this.setupBoard(town);
            }

            player.setScoreboard(board);
            updateBoard(player, town);
        }, 20 * 2);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            boards.values().stream().map(board -> board.getEntryTeam(player.getName())).filter(t -> t != null).forEach(team -> team.removeEntry(player.getName()));
            player.setScoreboard(manager.getMainScoreboard());
        });
    }

    @EventHandler
    public void on(TownAddResidentEvent event) {
        Player player = TownyUtils.getPlayer(event.getResident());
        if (player != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updateBoard(player, event.getTown());
                }
            }.runTaskLater(plugin, 20 * 5);
        }
    }

    @EventHandler
    public void on(TownRemoveResidentEvent event) {
        Player player = TownyUtils.getPlayer(event.getResident());
        if (player == null) return;

        plugin.getServer().getScheduler().runTask(plugin, () -> updateBoard(player, null));
    }

    private Scoreboard setupBoard(Town town) {
        Scoreboard board = manager.getNewScoreboard();

        board.registerNewTeam("mayor_ally").setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.GREEN);
        board.registerNewTeam("assist_ally").setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.GREEN);
        board.registerNewTeam("vip_ally").setPrefix("[" + ChatColor.GREEN + "VIP" + ChatColor.WHITE + "]" + " " + ChatColor.GREEN);
        board.registerNewTeam("normal_ally").setPrefix(ChatColor.GREEN.toString());

        board.registerNewTeam("mayor_enemy").setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.RED);
        board.registerNewTeam("assist_enemy").setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.RED);
        board.registerNewTeam("vip_enemy").setPrefix("[" + ChatColor.GREEN + "VIP" + ChatColor.WHITE + "]" + " " + ChatColor.RED);
        board.registerNewTeam("normal_enemy").setPrefix(ChatColor.RED.toString());

        boards.put(town, board);
        return board;
    }

    /**
     * @param player the resident
     * @param town his town (may not actually be reside in it)
     */


    //UPDATES ALL BOARDS UPDATING THE INFO OF 'resident'
    private void updateBoard(Player player, Town town) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            boards.forEach((other, board) -> {
                Team team = assignTeam(player, town, other);
                team.addEntry(player.getName());
            });
        });
    }

    /**
     *
     * @param player the player
     * @param town the town of the player
     * @param other town to compare with
     * @return team to be assigned within the board of the other town
     */
    private Team assignTeam(Player player, Town town, Town other) {
        Scoreboard board = boards.get(other); //SETUP BOARD INSTEAD OF GETTING
        Resident resident = TownyUtils.getResident(player);
        if (town != null) {
            if (town.equals(other)) {
                if (town.isMayor(resident)) {
                    return board.getTeam("mayor_ally");
                } else if (town.hasAssistant(resident)) {
                    return board.getTeam("assist_ally");
                } else if (player.hasPermission("townyextra.vip")) {
                    return board.getTeam("vip_ally");
                } else {
                    return board.getTeam("normal_ally");
                }
            } else {
                if (town.isMayor(resident)) {
                    return board.getTeam("mayor_enemy");
                } else if (town.hasAssistant(resident)) {
                    return board.getTeam("assist_enemy");
                } else if (player.hasPermission("townyextra.vip")) {
                    return board.getTeam("vip_enemy");
                } else {
                    return board.getTeam("normal_enemy");
                }
            }
        } else {
            return board.getTeam("normal_enemy");
        }
    }
}
