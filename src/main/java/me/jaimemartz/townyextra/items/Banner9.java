package me.jaimemartz.townyextra.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class Banner9 {
    /**
     * This method has been generated using ItemStackCoder
     * More info at https://www.spigotmc.org/resources/itemstackcoder-item2java.13053/ */
    public static ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.BANNER);
        BannerMeta meta = (BannerMeta) item.getItemMeta();
        meta.setBaseColor(DyeColor.SILVER);
        meta.addPattern(new Pattern(DyeColor.GREEN, PatternType.STRIPE_BOTTOM));
        meta.addPattern(new Pattern(DyeColor.BROWN, PatternType.TRIANGLE_BOTTOM));
        meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.GRADIENT));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.BROWN, PatternType.STRIPE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.GREEN, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(DyeColor.BROWN, PatternType.CREEPER));
        meta.addPattern(new Pattern(DyeColor.GREEN, PatternType.TRIANGLE_TOP));
        item.setItemMeta(meta);
        return item;
    }
}

