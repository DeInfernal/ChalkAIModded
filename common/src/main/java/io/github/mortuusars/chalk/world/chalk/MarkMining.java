package io.github.mortuusars.chalk.world.chalk;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
/** Server validates continuous mining; repeated packets in one tick never accelerate it. */
public final class MarkMining {
 private record Attempt(BlockPos pos, Direction face, Mark mark, long start, long last) {}
 private static final Map<ServerPlayer, Attempt> ATTEMPTS = new WeakHashMap<>();
 public static boolean canRemove(ServerPlayer player, BlockPos pos, Direction face, Mark mark) {
  if (!mark.permanent() || player.isCreative()) { ATTEMPTS.remove(player); return true; }
  long now = player.level().getGameTime();
  Attempt old = ATTEMPTS.get(player);
  if (old == null || !old.pos.equals(pos) || old.face != face || !old.mark.equals(mark) || now-old.last > 2 || now < old.last) {
   ATTEMPTS.put(player, new Attempt(pos.immutable(), face, mark, now, now)); return false;
  }
  ATTEMPTS.put(player, new Attempt(pos.immutable(), face, mark, old.start, now));
  if (now-old.start >= 39) { ATTEMPTS.remove(player); return true; }
  return false;
 }
}
