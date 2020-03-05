package me.morpheus.metropolis.plot.listeners;

import com.flowpowered.math.vector.Vector2i;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.block.ExplosionTownEvent;
import me.morpheus.metropolis.event.world.MPExplosionTownEventPre;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.util.VectorUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.explosive.DetonateExplosiveEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class InternalExplosionTownHandler {

    private final SimplePlotService ps;

    public InternalExplosionTownHandler(SimplePlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onExplosionPre(ExplosionEvent.Pre event) {
//        final Optional<User> ownerOpt = event.getContext().get(EventContextKeys.OWNER);
//        if (!ownerOpt.isPresent()) {
//            event.setCancelled(true);
//            return;
//        }
        final Explosion explosion = event.getExplosion();
        final Location<World> loc = explosion.getLocation();
        final UUID world = loc.getExtent().getUniqueId();
        final float radius = explosion.getRadius();
        final Vector2i nw = VectorUtil.toChunk2i(loc.sub(radius, 0, radius));
        final Vector2i se = VectorUtil.toChunk2i(loc.add(radius, 0, radius));

        if (nw.equals(se)) {
            final Optional<PlotData> pdOpt = this.ps.get(loc);
            if (pdOpt.isPresent()) {
                event.setCancelled(true);
            }
            return;
        }

        if (Stream.iterate(nw, v -> v.getX() < se.getX() ? v.add(Vector2i.UNIT_X) : Vector2i.from(nw.getX(), v.getY() + 1))
                .limit((se.getX() - nw.getX()) * (se.getY() * nw.getY()))
                .anyMatch(cp -> this.ps.get(world, cp) != null)) {
                event.setCancelled(true);
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onDetonateExplosive(DetonateExplosiveEvent event) {
//        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
//
//        final Optional<User> ownerOpt = event.getContext().get(EventContextKeys.OWNER);
//        if (!ownerOpt.isPresent()) {
//            event.setCancelled(true);
//            return;
//        }
//        final Explosion explosion = event.getOriginalExplosion();
//        final Location<World> loc = explosion.getLocation();
//        final UUID world = loc.getExtent().getUniqueId();
//        final float radius = explosion.getRadius();
//        final Vector2i nw = VectorUtil.toChunk2i(loc.sub(radius, 0, radius));
//        final Vector2i se = VectorUtil.toChunk2i(loc.add(radius, 0, radius));
//
//        if (nw.equals(se)) {
//            if (this.ps.get(loc).isPresent()) {
//                ExplosionTownEvent.Pre townEvent = new MPExplosionTownEventPre(event.getCause(), explosion);
//                if (Sponge.getEventManager().post(townEvent)) {
//                    event.setCancelled(true);
//                }
//            }
//            return;
//        }
//
//        if (Stream.iterate(nw, v -> v.getX() < se.getX() ? v.add(Vector2i.UNIT_X) : Vector2i.from(nw.getX(), v.getY() + 1))
//                .limit((se.getX() - nw.getX()) * (se.getY() * nw.getY()))
//                .anyMatch(cp -> this.ps.get(world, cp) != null)) {
//            ExplosionTownEvent.Pre townEvent = new MPExplosionTownEventPre(event.getCause(), explosion);
//            if (Sponge.getEventManager().post(townEvent)) {
//                event.setCancelled(true);
//            }
//        }
    }

}
