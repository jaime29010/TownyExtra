package me.jaimemartz.townyextra;

import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.jaimemartz.townyextra.utils.TownyUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public final class TownyExtra extends JavaPlugin implements Listener {
    private final Map<Resident, Scoreboard> boards = new HashMap<>();
    private ScoreboardManager manager = null;

    @Override
    public void onEnable() {
        manager = getServer().getScoreboardManager();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        //Nothing to do? I guess?
    }

    public void setupBoard(Player player) {
        Scoreboard board = manager.getNewScoreboard();
        Resident resident = TownyUtils.getResident(player);

        Team mayor = board.registerNewTeam("mayor");
        mayor.setPrefix(ChatColor.GRAY + "[" + ChatColor.RED + "Rey" + ChatColor.GRAY + "]" + " " + ChatColor.GREEN);
        Team allies = board.registerNewTeam("allies");
        allies.setPrefix(ChatColor.GREEN.toString());
        Team enemies = board.registerNewTeam("enemies");
        enemies.setPrefix(ChatColor.RED.toString());
        Team unknown = board.registerNewTeam("unknown");
        unknown.setPrefix(ChatColor.GRAY + ChatColor.ITALIC.toString());

        player.setScoreboard(board);
        boards.put(resident, board);

        updateBoard(player);
    }

    public void updateBoard(Player player) {
        Resident resident = TownyUtils.getResident(player);
        //Update to both other and player
        boards.forEach((other, board) -> {
            Town team = TownyUtils.getTown(other);

        });
    }

    public void clearBoard(Player player) {
        Resident resident = TownyUtils.getResident(player);
        player.setScoreboard(manager.getMainScoreboard());

        boards.forEach((other, board) -> {
            Team mayor = board.getTeam("mayor");
            Team allies = board.getTeam("allies");
            Team enemies = board.getTeam("enemies");
            Team unknown = board.getTeam("unknown");

            mayor.removeEntry(player.getName());
            allies.removeEntry(player.getName());
            enemies.removeEntry(player.getName());
            unknown.removeEntry(player.getName());
        });

        Scoreboard board = boards.remove(resident);
        if (board != null) {
            Team mayor = board.getTeam("mayor");
            Team allies = board.getTeam("allies");
            Team enemies = board.getTeam("enemies");
            Team unknown = board.getTeam("unknown");

            mayor.unregister();
            allies.unregister();
            enemies.unregister();
            unknown.unregister();
        }
    }


    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        setupBoard(player);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        clearBoard(player);
    }

    @EventHandler
    public void on(TownAddResidentEvent event) {
        Resident resident = event.getResident();
        Player player = TownyUtils.getPlayer(resident);
        updateBoard(player);
    }

    @EventHandler
    public void on(TownRemoveResidentEvent event) {
        Resident resident = event.getResident();
        Player player = TownyUtils.getPlayer(resident);
        updateBoard(player);
    }
}
