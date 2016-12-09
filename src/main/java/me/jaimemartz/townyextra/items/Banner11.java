package me.jaimemartz.townyextra.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class Banner11 {
    /**
     * This method has been generated using ItemStackCoder
     * More info at https://www.spigotmc.org/resources/itemstackcoder-item2java.13053/ */
    public static ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.BANNER);
        BannerMeta meta = (BannerMeta) item.getItemMeta();
        meta.setBaseColor(DyeColor.WHITE);
        meta.addPattern(new Pattern(DyeColor.LIGHT_BLUE, PatternType.STRIPE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.CIRCLE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.CREEPER));
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.TRIANGLE_TOP));
        item.setItemMeta(meta);
        return item;
    }
}
