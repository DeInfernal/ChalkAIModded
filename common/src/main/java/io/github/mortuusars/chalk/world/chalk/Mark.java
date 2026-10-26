package io.github.mortuusars.chalk.world.chalk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.world.chalk.symbol.SymbolOrientation;
import io.github.mortuusars.chalk.world.chalk.symbol.MarkSymbol;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.FastColor;

import java.util.Objects;

public record Mark(Holder<MarkSymbol> symbol, int color, SymbolOrientation orientation, boolean glowing, boolean waxed, boolean permanent, boolean shining) {
    public static final Codec<Mark> CODEC = RecordCodecBuilder.create(i -> i.group(
          RegistryFixedCodec.create(Chalk.Registries.MARK_SYMBOL).fieldOf("symbol").forGetter(Mark::symbol),
          Codec.INT.optionalFieldOf("color", 0xFFFFFFFF).forGetter(Mark::color),
          SymbolOrientation.CODEC.optionalFieldOf("orientation", SymbolOrientation.CENTER).forGetter(Mark::orientation),
          Codec.BOOL.optionalFieldOf("glowing", false).forGetter(Mark::glowing),
          Codec.BOOL.optionalFieldOf("waxed", false).forGetter(Mark::waxed),
          Codec.BOOL.optionalFieldOf("permanent", false).forGetter(Mark::permanent),
          Codec.BOOL.optionalFieldOf("shining", false).forGetter(Mark::shining)
    ).apply(i, Mark::new));

    public Mark(Holder<MarkSymbol> symbol, int color, SymbolOrientation orientation, boolean glowing) {
        this(symbol, color, orientation, glowing, false, false, false);
    }

    public Mark withSymbol(Holder<MarkSymbol> symbol) {
        return new Mark(symbol, color, orientation, glowing, waxed, permanent, shining);
    }

    public Mark withColor(int color) {
        return new Mark(symbol, color, orientation, glowing, waxed, permanent, shining);
    }

    public Mark lerpColor(int targetColor, float delta) {
        return withColor(FastColor.ARGB32.lerp(delta, color, targetColor));
    }

    public Mark withOrientation(SymbolOrientation orientation) {
        return new Mark(symbol, color, orientation, glowing, waxed, permanent, shining);
    }

    public Mark withGlowing(boolean glowing) {
        return new Mark(symbol, color, orientation, glowing, waxed, permanent, shining);
    }

    public boolean isSameMarkDifferentColors(Mark other) {
        return other.symbol().equals(symbol())
              && other.orientation() == orientation()
              && other.color() != color();
    }

}
