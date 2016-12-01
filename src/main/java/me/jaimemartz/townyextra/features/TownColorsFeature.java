package me.jaimemartz.townyextra.features;

import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Nation;
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
        /*
        int interval = plugin.getConfig().getInt("features.town-colors.update-interval");
        if (interval > 0) {
            plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                plugin.getLogger().info("Executing global board update");
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    Resident resident = TownyUtils.getResident(player);
                    Town town = TownyUtils.getTown(resident);

                    Scoreboard board = boards.get(town);
                    if (board == null) {
                        board = setupBoard(town);
                    }

                    player.setScoreboard(board);
                    updateBoard(resident);
                });
            }, 0, 20 * interval);
        }
        */
        plugin.getLogger().info("Initialized: " + getClass());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Resident resident = TownyUtils.getResident(player);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Town town = TownyUtils.getTown(resident);

            Scoreboard board = boards.get(town);
            if (board == null) {
                board = this.setupBoard(town);
            }

            player.setScoreboard(board);
            updateBoard(resident);
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            boards.values().stream()
                    .map(board -> board.getEntryTeam(player.getName()))
                    .filter(object -> object != null)
                    .forEach(team -> team.removeEntry(player.getName()));
            player.setScoreboard(manager.getMainScoreboard());
        });
    }

    @EventHandler
    public void on(TownAddResidentEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> updateBoard(event.getResident(), event.getTown()));
    }

    @EventHandler
    public void on(TownRemoveResidentEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> updateBoard(event.getResident(), null));
    }

    private Scoreboard setupBoard(Town town) {
        Scoreboard board = manager.getNewScoreboard();

        board.registerNewTeam("nation_mayors").setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.DARK_GREEN);
        board.registerNewTeam("nation_lords").setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.DARK_GREEN);
        board.registerNewTeam("nation_residents").setPrefix(ChatColor.DARK_GREEN.toString());

        board.registerNewTeam("town_mayors").setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.GREEN);
        board.registerNewTeam("town_lords").setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.GREEN);
        board.registerNewTeam("town_residents").setPrefix(ChatColor.GREEN.toString());

        board.registerNewTeam("enemy_mayors").setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.RED);
        board.registerNewTeam("enemy_lords").setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.RED);
        board.registerNewTeam("enemy_residents").setPrefix(ChatColor.RED.toString());

        board.registerNewTeam("unknown").setPrefix(ChatColor.RED.toString());

        boards.put(town, board);
        return board;
    }

    private void updateBoard(Resident resident) {
        Town town = TownyUtils.getTown(resident);
        updateBoard(resident, town);
    }

    private void updateBoard(Resident resident, Town town) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            boards.keySet().forEach(other -> {
                if (town == other) {
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(TownyUtils::getResident)
                            .filter(object -> object != null)
                            .forEach(res -> {
                                Team team = this.assignTeam(res, town);
                                team.addEntry(res.getName());
                            });
                }

                Team team = assignTeam(resident, other);
                team.addEntry(resident.getName());
            });
        });
    }

    private Team assignTeam(Resident resident, Town other) {
        Scoreboard board = boards.get(other);
        Town town = TownyUtils.getTown(resident);
        if (town != null) {
            if (other != null && other.hasResident(resident)) {
                if (other.isMayor(resident)) {
                    return board.getTeam("town_mayors");
                } else if (other.hasAssistant(resident)) {
                    return board.getTeam("town_lords");
                } else {
                    return board.getTeam("town_residents");
                }
            } else {
                if (other != null) {
                    Nation nation = TownyUtils.getNation(other);
                    Nation otherNation = TownyUtils.getNation(town);
                    if (nation != null && otherNation != null && nation.hasAlly(otherNation)) {
                        if (town.isMayor(resident)) {
                            return board.getTeam("nation_mayors");
                        } else if (town.hasAssistant(resident)) {
                            return board.getTeam("nation_lords");
                        } else {
                            return board.getTeam("nation_residents");
                        }
                    }
                }

                if (town.isMayor(resident)) {
                    return board.getTeam("enemy_mayors");
                } else if (town.hasAssistant(resident)) {
                    return board.getTeam("enemy_lords");
                } else {
                    return board.getTeam("enemy_residents");
                }
            }
        } else {
            return board.getTeam("unknown");
        }
    }
}
