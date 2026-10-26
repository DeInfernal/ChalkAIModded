package io.github.mortuusars.chalk.world.item;
import io.github.mortuusars.chalk.Chalk;
import net.minecraft.world.item.ItemStack;
public final class ChalkModifiers {
 public static final int GLOW=1, WAXED=2, PERMANENT=4, SHINING=8, REDSTONE=16;
 public static int flags(ItemStack stack) { return stack.getOrDefault(Chalk.DataComponents.MODIFIERS, 0); }
 public static boolean has(ItemStack stack, int flag) { return (flags(stack)&flag)!=0; }
 public static ItemStack selected(ItemStack stack) { return stack.getItem() instanceof ChalkBoxItem box ? box.getSelectedChalk(stack) : stack; }
 public static ItemStack redstone() {
  ItemStack stack = new ItemStack(Chalk.Items.CHALK.get());
  stack.set(Chalk.DataComponents.MODIFIERS, GLOW|WAXED|PERMANENT|REDSTONE);
  return stack;
 }
}
