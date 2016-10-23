package me.jaimemartz.townyextra.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class Banner2 {
  /**
   * This method has been generated using ItemStackCoder
   * More info at https://www.spigotmc.org/resources/itemstackcoder-item2java.13053/ */
  public static ItemStack getItemStack() {
    ItemStack item = new ItemStack(Material.BANNER);
    BannerMeta meta = (BannerMeta) item.getItemMeta();
    meta.setBaseColor(DyeColor.BLACK);
    meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CURLY_BORDER));
    meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CREEPER));
    meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.TRIANGLE_TOP));
    meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_TOP));
    meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.TRIANGLES_TOP));
    item.setItemMeta(meta);
    return item;
  }
}
