package io.github.mortuusars.chalk.mixin.supporters;

import io.github.mortuusars.mortaar.util.supporter.Supporters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Chalk no longer uses remote supporter lists; prevent the bundled library's startup query. */
@Mixin(value = Supporters.class, remap = false)
public class SupportersMixin {
 @Inject(method = "query", at = @At("HEAD"), cancellable = true)
 private static void skipRemoteSupporterQuery(CallbackInfo ci) { ci.cancel(); }
}
