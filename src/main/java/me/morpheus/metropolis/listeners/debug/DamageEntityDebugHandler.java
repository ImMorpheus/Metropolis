package me.morpheus.metropolis.listeners.debug;

import me.morpheus.metropolis.MPLog;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.util.Tristate;

public final class DamageEntityDebugHandler {

    public static Tristate cancelled = Tristate.UNDEFINED;
    public static boolean enabled = false;

    @IsCancelled(Tristate.UNDEFINED)
    @Listener(beforeModifications = true, order = Order.POST)
    public void onDamageDebug(Event event) {
        if (!(event instanceof AttackEntityEvent || event instanceof DamageEntityEvent)) {
            return;
        }
        if (!DamageEntityDebugHandler.enabled) {
            return;
        }

        final boolean canceled = ((Cancellable) event).isCancelled();
        if (DamageEntityDebugHandler.cancelled != Tristate.UNDEFINED && DamageEntityDebugHandler.cancelled != Tristate.fromBoolean(canceled)) {
            return;
        }

        MPLog.getLogger().error("Class: {} \n| Root: {} \n| Cause: {} \n| Context: {} \n| Canceled: {} \n",
                    event.getClass().getSimpleName(), event.getCause().root(), event.getCause().all(), event.getCause().getContext(), canceled);
    }
}
