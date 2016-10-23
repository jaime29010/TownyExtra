package me.jaimemartz.townyextra.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class Banner3 {
  /**
   * This method has been generated using ItemStackCoder
   * More info at https://www.spigotmc.org/resources/itemstackcoder-item2java.13053/ */
  public static ItemStack getItemStack() {
    ItemStack item = new ItemStack(Material.BANNER);
    BannerMeta meta = (BannerMeta) item.getItemMeta();
    meta.setBaseColor(DyeColor.SILVER);
    meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CREEPER));
    meta.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_MIDDLE));
    meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
    meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CIRCLE_MIDDLE));
    meta.addPattern(new Pattern(DyeColor.SILVER, PatternType.TRIANGLE_TOP));
    meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
    meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.SQUARE_TOP_LEFT));
    meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.SQUARE_TOP_RIGHT));
    meta.addPattern(new Pattern(DyeColor.SILVER, PatternType.TRIANGLES_TOP));
    item.setItemMeta(meta);
    return item;
  }
}
