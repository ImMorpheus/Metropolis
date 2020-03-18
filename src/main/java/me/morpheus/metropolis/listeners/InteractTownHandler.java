package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.block.InteractBlockTownEvent;
import me.morpheus.metropolis.api.event.entity.InteractEntityTownEvent;
import me.morpheus.metropolis.api.event.item.inventory.InteractItemTownEvent;
import me.morpheus.metropolis.api.flag.Flags;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.util.EventUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import org.spongepowered.api.data.property.item.SaturationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Hostile;
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
    public void onInteractBlock(InteractBlockTownEvent.Secondary event) {
        final Optional<ItemStackSnapshot> isOpt = event.getContext().get(EventContextKeys.USED_ITEM);
        if (isOpt.isPresent() && isOpt.get().getProperty(SaturationProperty.class).isPresent()) {
            return;
        }

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<PlotData> pdOpt = ps.get(event.getTargetBlock().getLocation().get());

        if (!pdOpt.isPresent()) {
            return;
        }

        final Object root = event.getCause().root();

        if (!(root instanceof Player)) {
            event.setCancelled(true);
            return;
        }
// TODO Sponge is broken
//        if (event.getContext().containsKey(EventContextKeys.PLAYER_PLACE)) {
//            if (!EventUtil.hasPermission((Player) root, pdOpt.get(), Flags.BLOCK_PLACE)) {
//                event.setCancelled(true);
//                EventUtil.sendNoPermissionMessage((Player) root);
//            }
//        } else {
            if (!EventUtil.hasPermission((Player) root, pdOpt.get(), Flags.INTERACT_BLOCK)) {
                event.setCancelled(true);
                EventUtil.sendNoPermissionMessage((Player) root);
//            }
        }
    }

    @Listener(beforeModifications = true)
    public void onInteractItem(InteractItemTownEvent event) {
        if (event.getItemStack().getProperty(SaturationProperty.class).isPresent()) {
            return;
        }

        final Object root = event.getCause().root();
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<PlotData> pdOpt = ps.get(((Locatable) root).getLocation().add(event.getInteractionPoint().get()));

        if (!pdOpt.isPresent()) {
            return;
        }
        if (!(root instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        if (!EventUtil.hasPermission((Player) root, pdOpt.get(), Flags.INTERACT_BLOCK)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) root);
        }
    }

    @Listener(beforeModifications = true)
    public void onInteractEntity(InteractEntityTownEvent event) {
        final Entity entity = event.getTargetEntity();

        if (entity instanceof Hostile) {
            return;
        }

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<PlotData> pdOpt = ps.get(entity.getLocation());

        if (!pdOpt.isPresent()) {
            return;
        }

        final Object root = event.getCause().root();

        if (!(root instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        final Optional<TameableData> optTameData = entity.get(TameableData.class);
        if (optTameData.isPresent() && optTameData.get().owner().get().isPresent() && optTameData.get().owner().get().get().equals(((Player) root).getUniqueId())) {
            return;
        }

        if (!EventUtil.hasPermission((Player) root, pdOpt.get(), Flags.INTERACT_ENTITY)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) root);
        }
    }


}
