package me.jaimemartz.townyextra;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import me.jaimemartz.townyextra.utils.TownyUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public final class TownManager {
    private final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Map<Town, Scoreboard> boards = new HashMap<>();
    private final TownyExtra plugin;

    public TownManager(TownyExtra plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new TownListener(), plugin);
    }

    public void setupBoard(Town town) {
        Scoreboard board = manager.getNewScoreboard();

        Team nationMayors = board.registerNewTeam("nation_mayors");
        Team nationLords = board.registerNewTeam("nation_lords");
        Team nationResidents = board.registerNewTeam("nation_residents");
        nationMayors.setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.DARK_GREEN); //12
        nationLords.setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.DARK_GREEN); //13
        nationResidents.setPrefix(ChatColor.DARK_GREEN.toString()); //2

        Team townMayors = board.registerNewTeam("town_mayors");
        Team townLords = board.registerNewTeam("town_lords");
        Team townResidents = board.registerNewTeam("town_residents");
        townMayors.setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.GREEN); //12
        townLords.setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.GREEN); //13
        townResidents.setPrefix(ChatColor.GREEN.toString()); //2

        Team enemyMayors = board.registerNewTeam("enemy_mayors");
        Team enemyLords = board.registerNewTeam("enemy_lords");
        Team enemyResidents = board.registerNewTeam("enemy_residents");
        enemyMayors.setPrefix("[" + ChatColor.RED + "Rey" + ChatColor.WHITE + "]" + " " + ChatColor.RED); //12
        enemyLords.setPrefix("[" + ChatColor.BLUE + "Lord" + ChatColor.WHITE + "]" + " " + ChatColor.RED); //13
        enemyResidents.setPrefix(ChatColor.RED.toString()); //2

        Team unknown = board.registerNewTeam("unknown");
        unknown.setPrefix(ChatColor.GRAY + ChatColor.ITALIC.toString());

        TownyUniverse.getOnlinePlayers(town).forEach(player -> player.setScoreboard(board));
        boards.put(town, board);

        updateBoard();
    }

    public void updateBoard(Resident resident) {
        boards.forEach((other, board) -> {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                Resident resident1 = TownyUtils.getResident(player);
                Team team = assignTeam(resident1, other);
                team.addEntry(player.getName());
            });
        });

    }

    public void clearBoard(Town town) {
        TownyUniverse.getOnlinePlayers(town).forEach(player -> {
            player.setScoreboard(manager.getMainScoreboard());
        });

        Scoreboard board = boards.remove(town);
        board.getTeams().forEach(Team::unregister);
    }

    public Team assignTeam(Resident resident, Town other) {
        Scoreboard board = boards.get(other);
        Town town = TownyUtils.getTown(resident);
        if (town != null) {
            if (other.hasResident(resident)) {
                if (other.isMayor(resident)) {
                    return board.getTeam("town_mayors");
                } else if (other.hasAssistant(resident)) {
                    return board.getTeam("town_lords");
                } else {
                    return board.getTeam("town_residents");
                }
            } else {
                Nation nation = TownyUtils.getNation(other);
                if (nation != null && nation.hasTown(town)) {
                    if (town.isMayor(resident)) {
                        return board.getTeam("nation_mayors");
                    } else if (town.hasAssistant(resident)) {
                        return board.getTeam("nation_lords");
                    } else {
                        return board.getTeam("nation_residents");
                    }
                } else {
                    if (town.isMayor(resident)) {
                        return board.getTeam("enemy_mayors");
                    } else if (town.hasAssistant(resident)) {
                        return board.getTeam("enemy_lords");
                    } else {
                        return board.getTeam("enemy_residents");
                    }
                }
            }
        } else {
            return board.getTeam("unknown");
        }
    }

    public class TownListener implements Listener {
        @EventHandler
        public void on(PlayerJoinEvent event) {

        }

        @EventHandler
        public void on(PlayerQuitEvent event) {

        }
    }
}
