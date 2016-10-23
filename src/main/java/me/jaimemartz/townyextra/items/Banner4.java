package me.jaimemartz.townyextra.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class Banner4 {
  /**
   * This method has been generated using ItemStackCoder
   * More info at https://www.spigotmc.org/resources/itemstackcoder-item2java.13053/ */
  public static ItemStack getItemStack() {
    ItemStack item = new ItemStack(Material.BANNER);
    BannerMeta meta = (BannerMeta) item.getItemMeta();
    meta.setBaseColor(DyeColor.RED);
    meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CURLY_BORDER));
    meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
    meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM));
    meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CREEPER));
    meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_TOP));
    meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_TOP));
    meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
    item.setItemMeta(meta);
    return item;
  }
}
