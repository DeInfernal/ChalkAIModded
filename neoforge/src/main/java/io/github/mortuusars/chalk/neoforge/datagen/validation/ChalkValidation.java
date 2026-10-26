package io.github.mortuusars.chalk.neoforge.datagen.validation;

import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.world.item.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Regression checks run in the real NeoForge registry environment by runData. */
public final class ChalkValidation implements DataProvider {
 private final CompletableFuture<HolderLookup.Provider> lookup;
 public ChalkValidation(CompletableFuture<HolderLookup.Provider> lookup) { this.lookup=lookup; }
 @Override public String getName() { return "Chalk crafting regression checks"; }
 @Override public CompletableFuture<?> run(CachedOutput output) { return lookup.thenAccept(this::validate); }
 private static void require(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
 private void validate(HolderLookup.Provider lookup) {
  ChalkCraftingRecipe recipe = new ChalkCraftingRecipe(CraftingBookCategory.MISC);
  ItemStack chalk = new ItemStack(Chalk.Items.CHALK.get());
  ItemStack upgraded = craft(recipe, chalk, Items.GLOWSTONE_DUST, lookup);
  require(ChalkModifiers.has(upgraded, ChalkModifiers.GLOW), "Glowstone upgrade");
  require(ChalkModifiers.has(craft(recipe, chalk, Items.GLOW_INK_SAC, lookup), ChalkModifiers.GLOW), "Ink upgrade");
  upgraded = craft(recipe, upgraded, Items.HONEYCOMB, lookup);
  upgraded = craft(recipe, upgraded, Items.HONEY_BOTTLE, lookup);
  upgraded = craft(recipe, upgraded, Items.DRAGON_BREATH, lookup);
  require(ChalkModifiers.flags(upgraded)==15, "Stack all four upgrades");
  require(ChalkModifiers.flags(chalk)==0, "Crafting must not mutate input");
  upgraded = craft(recipe, upgraded, Items.BLUE_DYE, lookup);
  int blue = ChalkDyes.color(new ItemStack(Items.BLUE_DYE));
  require(upgraded.get(DataComponents.DYED_COLOR).rgb()==blue, "Dye color");
  require(ChalkModifiers.flags(upgraded)==15, "Dye preserves upgrades");
  upgraded.setDamageValue(52);
  ItemStack repaired = craft(recipe, upgraded, Items.CALCITE, lookup);
  require(repaired.getDamageValue()==0 && ChalkModifiers.flags(repaired)==15, "Repair preserves upgrades");
  require(repaired.get(DataComponents.DYED_COLOR).rgb()==blue, "Repair preserves color");
  require(upgraded.getDamageValue()==52, "Repair must not mutate input");
  ItemStack red = craft(recipe, new ItemStack(Items.CALCITE), Items.REDSTONE, lookup);
  require(!red.isEmpty() && ChalkModifiers.flags(red)==23, "Redstone chalk recipe");
  require(craft(recipe, red, Items.BLUE_DYE, lookup).isEmpty(), "Redstone cannot be dyed");
  red.setDamageValue(63);
  require(craft(recipe, red, Items.CALCITE, lookup).isEmpty(), "Redstone cannot be repaired by calcite");
  require(craft(recipe, red, Items.REDSTONE, lookup).getDamageValue()==0, "Redstone repair");
  require(craft(recipe, red, Items.GLOW_INK_SAC, lookup).isEmpty(), "Duplicate upgrade rejected");
  require(!craft(recipe, red, Items.DRAGON_BREATH, lookup).isEmpty(), "Redstone supports Shining");
  require(craft(recipe, chalk, Items.DIRT, lookup).isEmpty(), "Invalid ingredient rejected");
  require(recipe.assemble(CraftingInput.of(2, 2, List.of(chalk, new ItemStack(Items.CALCITE), new ItemStack(Items.DIRT), ItemStack.EMPTY)), lookup).isEmpty(), "Extra ingredient rejected");
  var bottles = recipe.getRemainingItems(CraftingInput.of(2,1,List.of(chalk,new ItemStack(Items.DRAGON_BREATH))));
  require(bottles.get(1).is(Items.GLASS_BOTTLE), "Dragon breath bottle returned");
  var honey = recipe.getRemainingItems(CraftingInput.of(2,1,List.of(chalk,new ItemStack(Items.HONEY_BOTTLE))));
  require(honey.get(1).is(Items.GLASS_BOTTLE), "Honey bottle returned");
  var encoded = ItemStack.CODEC.encodeStart(lookup.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), repaired).getOrThrow();
  ItemStack decoded = ItemStack.CODEC.parse(lookup.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), encoded).getOrThrow();
  require(ChalkModifiers.flags(decoded)==15 && decoded.get(DataComponents.DYED_COLOR).rgb()==blue, "Save/load preserves modifiers and color");
  Chalk.LOGGER.info("Chalk crafting regression checks passed");
 }
 private ItemStack craft(ChalkCraftingRecipe recipe, ItemStack chalk, Item additive, HolderLookup.Provider lookup) {
  return recipe.assemble(CraftingInput.of(2,1,List.of(chalk,new ItemStack(additive))),lookup);
 }
}
