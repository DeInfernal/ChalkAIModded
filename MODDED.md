# Chalk Modded — Minecraft 1.21.1

All symbols are available immediately. Chalk advancements and supporter gates are removed.

Shapeless recipes (one chalk + one additive, 2×2 or 3×3):

| Additive | Result |
| --- | --- |
| Dye | Sets the chalk/mark RGB color; keeps modifiers and damage |
| Glowstone dust or glow ink sac | Glow: subsequent marks have the glow-ink effect |
| Honeycomb | Waxed: can draw into water; marks survive water |
| Honey bottle | Permanent: survival players must hold attack for 40 ticks per mark |
| Dragon's breath | Shining: marks emit block light level 15 |
| Calcite block | Fully repairs ordinary chalk without removing color/modifiers |

Calcite + redstone dust creates Redstone Chalk: fixed dark red color, built-in
Glow, Waxed and Permanent. Repair it with redstone dust. Shining can be added.
Honey/dragon's breath bottles return an empty glass bottle. Modifiers stack.
Creative players can still remove marks immediately.

Dyes extending vanilla DyeItem work, including those supplied by mods. Dyenamics
uses its public getDyeColor/getColorComponentValue API without a hard dependency.
Other mods may also expose that API, or provide a c:dyes item with dyed_color.
The chalk item is deliberately excluded from minecraft:dyeable to avoid other
mods' armor coloring recipes bypassing the fixed redstone color.

Build NeoForge: `bash gradlew :neoforge:build` with JDK 21.
Regression checks run in NeoForge's real registry environment during datagen:
`bash gradlew -PchalkDatagenOutput=/tmp/chalk-validation-data :neoforge:runData`.
Set GRADLE_USER_HOME=/workspace/.gradle and
JAVA_HOME=/workspace/toolchains/usr/lib/jvm/java-21-openjdk-amd64 in this cloud environment.
