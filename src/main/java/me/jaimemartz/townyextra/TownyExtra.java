package me.jaimemartz.townyextra;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.jaimemartz.faucet.ConfigUtil;
import me.jaimemartz.townyextra.items.Banner1;
import me.jaimemartz.townyextra.items.Banner2;
import me.jaimemartz.townyextra.items.Banner3;
import me.jaimemartz.townyextra.items.Banner4;
import me.jaimemartz.townyextra.utils.TownyUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TownyExtra extends JavaPlugin implements Listener {
    private final List<ItemStack> banners = new ArrayList<>();
    private final Map<Player, ArmorStand> stands = new HashMap<>();
    private FileConfiguration config;
    private TownManager manager;

    @Override
    public void onEnable() {
        config = ConfigUtil.loadConfig("config.yml", this);

        manager = new TownManager(this);

        banners.add(Banner1.getItemStack());
        banners.add(Banner2.getItemStack());
        banners.add(Banner3.getItemStack());
        banners.add(Banner4.getItemStack());

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        banners.clear();
    }

    public TownManager getTownManager() {
        return manager;
    }

    public void setupStand(Player player) {
        Location location = player.getLocation();

        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        stand.setSmall(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);

        stands.put(player, stand);
        showStand(player, stand);

        getServer().getScheduler().runTask(this, () -> updatePos(stand, location));
    }

    public void updatePos(ArmorStand stand, Location location) {
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

    public ItemStack getBanner(Town town) {
        String path = "banners" + "." + town.getName();
        if (config.isInt(path)) {
            int index = config.getInt(path);
            return banners.get(index);
        }
        return null;
    }

    public void showStand(Player player, ArmorStand stand) {
        Resident resident = TownyUtils.getResident(player);
        if (resident == null) return;

        Town town = TownyUtils.getTown(resident);
        if (town == null) return;

        ItemStack item = getBanner(town);
        if (item == null) return;

        stand.setHelmet(getBanner(town));
    }

    public void hideStand(ArmorStand stand) {
        stand.setHelmet(new ItemStack(Material.AIR));
    }

    public Map<Player, ArmorStand> getStands() {
        return stands;
    }
}
