package io.github.mortuusars.chalk.world.item;

import io.github.mortuusars.chalk.Chalk;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Exactly one chalk and one additive, or calcite + redstone, in either crafting grid. */
public class ChalkCraftingRecipe extends CustomRecipe {
 public ChalkCraftingRecipe(CraftingBookCategory category) { super(category); }
 @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
 @Override public RecipeSerializer<?> getSerializer() { return Chalk.Recipes.CRAFTING.get(); }
 @Override public boolean matches(CraftingInput input, Level level) { return !result(input).isEmpty(); }
 @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) { return result(input); }
 private ItemStack result(CraftingInput input) {
  int occupied = 0;
  boolean calcite = false, redstoneDust = false;
  for (int i=0;i<input.size();i++) {
   ItemStack stack = input.getItem(i);
   if (!stack.isEmpty()) occupied++;
   calcite |= stack.is(Items.CALCITE);
   redstoneDust |= stack.is(Items.REDSTONE);
  }
  if (occupied == 2 && calcite && redstoneDust) return ChalkModifiers.redstone();
  ItemStack chalk = ItemStack.EMPTY, additive = ItemStack.EMPTY;
  int count = 0;
  for (int i=0;i<input.size();i++) {
   ItemStack stack = input.getItem(i);
   if (stack.isEmpty()) continue;
   count++;
   if (stack.getItem() instanceof ChalkItem) {
    if (!chalk.isEmpty()) return ItemStack.EMPTY;
    chalk = stack;
   } else {
    if (!additive.isEmpty()) return ItemStack.EMPTY;
    additive = stack;
   }
  }
  if (count != 2) return ItemStack.EMPTY;
  if (chalk.isEmpty()) return ItemStack.EMPTY;
  if (additive.isEmpty()) return ItemStack.EMPTY;
  ItemStack result = chalk.copyWithCount(1);
  boolean redstone = ChalkModifiers.has(chalk, ChalkModifiers.REDSTONE);
  if (additive.is(redstone ? Items.REDSTONE : Items.CALCITE)) {
   if (!chalk.isDamaged()) return ItemStack.EMPTY;
   result.setDamageValue(0);
   return result;
  }
  int flag = additive.is(Items.GLOWSTONE_DUST) || additive.is(Items.GLOW_INK_SAC) ? ChalkModifiers.GLOW
    : additive.is(Items.HONEYCOMB) ? ChalkModifiers.WAXED
    : additive.is(Items.HONEY_BOTTLE) ? ChalkModifiers.PERMANENT
    : additive.is(Items.DRAGON_BREATH) ? ChalkModifiers.SHINING : 0;
  if (flag != 0) {
   if (ChalkModifiers.has(chalk, flag)) return ItemStack.EMPTY;
   result.set(Chalk.DataComponents.MODIFIERS, ChalkModifiers.flags(chalk) | flag);
   return result;
  }
  if (redstone) return ItemStack.EMPTY;
  Integer color = ChalkDyes.color(additive);
  if (color == null) return ItemStack.EMPTY;
  result.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
  return result;
 }
 @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
  NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
  for (int i=0;i<input.size();i++) {
   if (input.getItem(i).is(Items.HONEY_BOTTLE) || input.getItem(i).is(Items.DRAGON_BREATH)) remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
  }
  return remaining;
 }
}
