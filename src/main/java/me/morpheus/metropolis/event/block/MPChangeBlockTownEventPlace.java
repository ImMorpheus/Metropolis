package me.morpheus.metropolis.event.block;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.block.ChangeBlockTownEvent;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.cause.Cause;

import java.util.List;
import java.util.stream.Stream;

public final class MPChangeBlockTownEventPlace implements ChangeBlockTownEvent.Place {

    private final Cause cause;
    private final List<Transaction<BlockSnapshot>> transactions;
    private boolean cancelled = false;

    public MPChangeBlockTownEventPlace(Cause cause, List<Transaction<BlockSnapshot>> transactions) {
        this.cause = cause;
        this.transactions = transactions;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Stream<Transaction<BlockSnapshot>> getTransactions() {
        return this.transactions.stream();
    }
}
