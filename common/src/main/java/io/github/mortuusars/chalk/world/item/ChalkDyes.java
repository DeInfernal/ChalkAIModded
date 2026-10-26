package io.github.mortuusars.chalk.world.item;

import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

/** Optional color API integration; no hard dependency on Dyenamics. */
public final class ChalkDyes {
 public static Integer color(ItemStack stack) {
  if (stack.getItem() instanceof DyeItem dye) return ChalkItem.getColorFromDye(dye.getDyeColor());
  // Dyenamics 1.21.1 exposes getDyeColor().getColorComponentValue().
  // Other dye mods can expose the same API, or supply a dyed_color on a c:dyes item.
  try {
   Object color = stack.getItem().getClass().getMethod("getDyeColor").invoke(stack.getItem());
   return (Integer) color.getClass().getMethod("getColorComponentValue").invoke(color);
  } catch (ReflectiveOperationException | ClassCastException ignored) { }
  if (stack.is(TagKey.create(Registries.ITEM, ResourceLocation.parse("c:dyes")))) {
   DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
   if (color != null) return color.rgb();
  }
  return null;
 }
}
