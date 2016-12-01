package me.jaimemartz.townyextra;

import me.jaimemartz.faucet.ConfigUtil;
import me.jaimemartz.townyextra.features.BlockSpamFeature;
import me.jaimemartz.townyextra.features.TownBannerFeature;
import me.jaimemartz.townyextra.features.TownColorsFeature;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyExtra extends JavaPlugin {
    private FileConfiguration config;

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("features.block-spam.enabled"))
            new BlockSpamFeature(this);

        if (getConfig().getBoolean("features.town-banner.enabled"))
            new TownBannerFeature(this);

        if (getConfig().getBoolean("features.town-colors.enabled"))
            new TownColorsFeature(this);
    }

    @Override
    public void onDisable() {
        //Nothing to do
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
}
