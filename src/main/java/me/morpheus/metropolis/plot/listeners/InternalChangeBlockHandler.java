package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.event.block.ChangeBlockTownEvent;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.event.block.MPChangeBlockTownEventBreak;
import me.morpheus.metropolis.event.block.MPChangeBlockTownEventPlace;
import me.morpheus.metropolis.event.block.MPChangeBlockTownEventPre;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.world.LocatableBlock;

public final class InternalChangeBlockHandler {

    private final PlotService ps;

    public InternalChangeBlockHandler(PlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onBlockPre(ChangeBlockEvent.Pre event) {
        final Object root = event.getCause().root();

//        if (event.getCause().getContext().asMap().isEmpty() && event.getCause().all().size() == 1
//                && root instanceof LocatableBlock) { //TODO Sponge ? Wtf ?
//            final List<Location<World>> locations = event.getLocations();
//            final long count = locations.stream().filter(l -> this.ps.get(((LocatableBlock) root).getLocation()).isPresent()).count();
//            if (count != 0L && count != locations.size()) {
//                System.err.println(event.getLocations());
//                System.err.println(event.getCause().all());
//                event.setCancelled(true);
//            }
//            return;
//        }

        if (event.getLocations().stream().anyMatch(location -> this.ps.get(location).isPresent())) {
            ChangeBlockTownEvent.Pre townEvent = new MPChangeBlockTownEventPre(event.getCause(), event.getLocations());
            if (Sponge.getEventManager().post(townEvent)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        final Object root = event.getCause().root();

        // piston extend inside a plot
        if (root instanceof TileEntity && ((TileEntity) root).getType() == TileEntityTypes.PISTON) {
            return;
        }

        // BlockTickPhaseState - randomTickBlock
        if (event.getCause().getContext().asMap().isEmpty() && event.getCause().all().size() == 1
                && root instanceof LocatableBlock) {
            //TODO Sponge ? Wtf ?
            return;
        }

//        if (event.getCause().getContext().asMap().isEmpty() && event.getCause().all().size() == 1
//                && root instanceof FallingBlock) {
//            //TODO Sponge ? Wtf ?
//            return;
//        }

        // piston retract inside a plot
        if (root == Sponge.getGame() && event.getCause().all().size() == 1
                && !event.getContext().asMap().containsKey(EventContextKeys.OWNER)
                && !event.getContext().asMap().containsKey(EventContextKeys.NOTIFIER)) {
            //TODO Sponge ? Wtf ?
            return;
        }

        if (event.getTransactions().stream().anyMatch(t -> this.ps.get(t.getOriginal().getLocation().get()).isPresent())) {
            ChangeBlockTownEvent.Place townEvent = new MPChangeBlockTownEventPlace(event.getCause(), event.getTransactions());
            if (Sponge.getEventManager().post(townEvent)) {
                event.filterAll();
                event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        final Object root = event.getCause().root();

//        if (root instanceof TileEntity && ((TileEntity) root).getType() == TileEntityTypes.PISTON) {
//            return;
//        }

        // piston retract inside a plot
        if (root == Sponge.getGame() && event.getCause().all().size() == 1
                && !event.getContext().asMap().containsKey(EventContextKeys.OWNER)
                && !event.getContext().asMap().containsKey(EventContextKeys.NOTIFIER)) {
            //TODO Sponge ? Wtf ?
            return;
        }

//        if (event.getCause().getContext().asMap().isEmpty() && event.getCause().all().size() == 1
//                && root instanceof FallingBlock) { //TODO Sponge ? Wtf ?
//            return;
//        }

        if (event.getTransactions().stream().anyMatch(t -> this.ps.get(t.getOriginal().getLocation().get()).isPresent())) {
            ChangeBlockTownEvent.Break townEvent = new MPChangeBlockTownEventBreak(event.getCause(), event.getTransactions());
            if (Sponge.getEventManager().post(townEvent)) {
                event.filterAll();
                event.setCancelled(true);
            }
        }
    }
}
