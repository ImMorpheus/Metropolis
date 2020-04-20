package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.event.block.InteractBlockTownEvent;
import me.morpheus.metropolis.api.event.entity.InteractEntityTownEvent;
import me.morpheus.metropolis.api.event.item.inventory.InteractInventoryTownEvent;
import me.morpheus.metropolis.api.event.item.inventory.InteractItemTownEvent;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.flag.Flags;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.EventUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import org.spongepowered.api.data.property.item.SaturationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public final class InteractTownHandler {

    @Listener(beforeModifications = true)
    public void onInteractTownBlockPrimary(InteractBlockTownEvent.Primary event) {
        onInteractBlock(event, Flags.BLOCK_BREAK);
    }

    @Listener(beforeModifications = true)
    public void onInteractTownBlockSecondary(InteractBlockTownEvent.Secondary event) {
        final Optional<ItemStackSnapshot> isOpt = event.getContext().get(EventContextKeys.USED_ITEM);
        if (isOpt.isPresent() && !isOpt.get().isEmpty()) {
            onInteractBlock(event, Flags.BLOCK_PLACE);
        } else {
            onInteractBlock(event, Flags.INTERACT_BLOCK);
        }
    }

    private void onInteractBlock(InteractBlockTownEvent event, Flag flag) {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(event.getTargetBlock().getLocation().get());

        if (!plotOpt.isPresent()) {
            return;
        }

        final Object root = event.getCause().root();

        if (!(root instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        if (!ps.hasPermission((Player) root, plotOpt.get(), flag)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) root);
        }
    }

    @Listener(beforeModifications = true)
    public void onInteractTownItemPrimary(InteractItemTownEvent.Primary event) {
        onInteractItem(event, Flags.INTERACT_BLOCK);
    }

    @Listener(beforeModifications = true)
    public void onInteractTownItemSecondary(InteractItemTownEvent.Secondary event) {
        if (event.getItemStack().getProperty(SaturationProperty.class).isPresent()) {
            return;
        }
        if (event.getItemStack().isEmpty()) {
            onInteractItem(event, Flags.INTERACT_BLOCK);
        } else {
            onInteractItem(event, Flags.BLOCK_PLACE);
        }
    }

    private void onInteractItem(InteractItemTownEvent event, Flag flag) {
        final Object root = event.getCause().root();
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(((Locatable) root).getLocation().add(event.getInteractionPoint().get()));

        if (!plotOpt.isPresent()) {
            return;
        }
        if (!(root instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        if (!ps.hasPermission((Player) root, plotOpt.get(), flag)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) root);
        }
    }

    @Listener(beforeModifications = true)
    public void onInteractTownEntityPrimary(InteractEntityTownEvent.Primary event) {
        final Entity entity = event.getTargetEntity();
        if (entity instanceof Hostile) { //TODO
            return;
        }
        if (entity.getType() == EntityTypes.PLAYER && event.getCause().root() instanceof Player) { //TODO
            final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
            final Plot plot = ps.get(entity.getLocation()).get();
            final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
            final Town t = ts.get(plot.getTown()).get();
            if (!t.getPvP().canDamage((Player) event.getCause().root(), (Player) entity)) {
                event.setCancelled(true);
                EventUtil.sendNoPermissionMessage((Player) event.getCause().root());
            }
            return;
        }
        if (entity instanceof Living) {
            onInteractEntity(event, Flags.DAMAGE);
        } else {
            onInteractEntity(event, Flags.INTERACT_ENTITY);
        }
    }

    @Listener(beforeModifications = true)
    public void onInteractTownEntitySecondary(InteractEntityTownEvent.Secondary event) {
        onInteractEntity(event, Flags.INTERACT_ENTITY);
    }

    private void onInteractEntity(InteractEntityTownEvent event, Flag flag) {
        final Entity entity = event.getTargetEntity();
        final Object root = event.getCause().root();

        final Optional<TameableData> optTameData = entity.get(TameableData.class);
        if (optTameData.isPresent() && optTameData.get().owner().get().isPresent() && optTameData.get().owner().get().get().equals(((Player) root).getUniqueId())) {
            return;
        }

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(entity.getLocation());

        if (!plotOpt.isPresent()) {
            return;
        }

        if (!(root instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        if (!ps.hasPermission((Player) root, plotOpt.get(), flag)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) root);
        }

    }

    @Listener(beforeModifications = true)
    public void onInteractTownInventoryOpen(InteractInventoryTownEvent.Open event) {
        final BlockSnapshot block = event.getContext().get(EventContextKeys.BLOCK_HIT).get();
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Plot plot = ps.get(block.getLocation().get()).get();

        final Object root = event.getCause().root();

        if (!(root instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        if (!ps.hasPermission((Player) root, plot, Flags.INTERACT_INVENTORY)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) root);
        }
    }

}
