package me.morpheus.metropolis.listeners.debug;

import me.morpheus.metropolis.MPLog;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.util.Tristate;

public final class ChangeBlockDebugHandler {

    public static Tristate cancelled = Tristate.UNDEFINED;
    public static boolean enabled = false;

    @IsCancelled(Tristate.UNDEFINED)
    @Listener(beforeModifications = true, order = Order.POST)
    public void onChangeBlockDebug(Event event) {
        if (!ChangeBlockDebugHandler.enabled) {
            return;
        }
        if (!(event instanceof ChangeBlockEvent.Pre
                || event instanceof ChangeBlockEvent.Break
                || event instanceof ChangeBlockEvent.Place)) {
            return;
        }

        final boolean canceled = ((Cancellable) event).isCancelled();
        if (ChangeBlockDebugHandler.cancelled != Tristate.UNDEFINED && ChangeBlockDebugHandler.cancelled != Tristate.fromBoolean(canceled)) {
            return;
        }

        if (event instanceof ChangeBlockEvent) {
            StringBuilder b = new StringBuilder();
            for (Transaction<BlockSnapshot> transaction : ((ChangeBlockEvent) event).getTransactions()) {
                b.append(transaction.toString()).append("\n");
            }
            MPLog.getLogger().error("Class: {} \n| Root: {} \n| Cause: {} \n| Context: {} \n| Canceled: {} \n| Trans: {} \n",
                    event.getClass().getSimpleName(), event.getCause().root(), event.getCause().all(), event.getCause().getContext(), canceled, b.toString());
        } else {
            MPLog.getLogger().error("Class: {} \n| Root: {} \n| Cause: {} \n| Context: {} \n| Canceled: {} \n",
                    event.getClass().getSimpleName(), event.getCause().root(), event.getCause().all(), event.getCause().getContext(), canceled);
        }
    }


}
