package io.github.mortuusars.chalk.mixin.chalk_dye_colors;
import io.github.mortuusars.chalk.world.item.ChalkItem;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ArmorDyeRecipe.class)
public class ArmorDyeRecipeMixin {
 @Inject(method="matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at=@At("HEAD"), cancellable=true)
 private void chalkRecipe(CraftingInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
  for (int i=0;i<input.size();i++) if (input.getItem(i).getItem() instanceof ChalkItem) { cir.setReturnValue(false); return; }
 }
}
