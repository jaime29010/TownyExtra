package me.jaimemartz.townyextra;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.stream.JsonReader;
import me.jaimemartz.faucet.ConfigUtil;
import me.jaimemartz.townyextra.features.BlockSpamFeature;
import me.jaimemartz.townyextra.features.TownBannerFeature;
import me.jaimemartz.townyextra.features.TownColorsFeature;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.UUID;

public final class TownyExtra extends JavaPlugin {
    private FileConfiguration config;
    private JsonDataPool database;
    private BukkitTask task;
    private Gson gson;

    public static int SAVE_INTERVAL = 10;

    @Override
    public void onEnable() {
        //Setting up gson
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.enableComplexMapKeySerialization();

        //fix for map key that is null
        //gson does UUID.fromString even if the object is null, and it does not accept nulls
        builder.registerTypeAdapter(UUID.class, (JsonDeserializer<UUID>) (element, type, context) -> {
            if (element.isJsonNull() || element.getAsString().equals("null")) {
                return null;
            }
            return UUID.fromString(element.getAsString());
        });
        gson = builder.create();

        //Loading database
        File file = new File(getDataFolder(), "data.json");
        if (file.exists()) {
            getLogger().info("Database exists, reading data...");
            try (JsonReader reader = new JsonReader(new FileReader(file))) {
                database = gson.fromJson(reader, JsonDataPool.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            getLogger().fine("Database does not exist, it will be created on server shutdown");
            database = new JsonDataPool();
        }

        //Database save task
        getLogger().info(String.format("The database will be saved every %s minutes", SAVE_INTERVAL));
        task = new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info("Periodically saving database...");
                saveDatabase();
            }
        }.runTaskTimerAsynchronously(this, SAVE_INTERVAL * 60 * 20, SAVE_INTERVAL * 60 * 20);

        if (getConfig().getBoolean("features.block-spam.enabled")) {
            new BlockSpamFeature(this);
        }

        if (getConfig().getBoolean("features.town-banner.enabled")) {
            new TownBannerFeature(this);
        }

        if (getConfig().getBoolean("features.town-colors.enabled")) {
            new TownColorsFeature(this);
        }
    }

    @Override
    public void onDisable() {
        task.cancel();
        if (database != null) {
            getLogger().info("Saving database...");
            saveDatabase();
        } else {
            getLogger().info("Database is null, not saving database...");
        }
    }

    private void saveDatabase() {
        try (Writer writer = new FileWriter(new File(getDataFolder(), "data.json"))) {
            String output = gson.toJson(database, JsonDataPool.class);
            writer.write(output);
        } catch (IOException e) {
            getLogger().severe("Something went terribly wrong, couldn't save the database");
            e.printStackTrace();
        }
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    @Override
    public void reloadConfig() {
        config = ConfigUtil.loadConfig("config.yml", this);
    }

    @Override
    public void saveDefaultConfig() {
        this.reloadConfig();
    }

    @Override
    public void saveConfig() {
        ConfigUtil.saveConfig(config, "config.yml", this);
    }

    public JsonDataPool getDataPool() {
        return database;
    }
}
