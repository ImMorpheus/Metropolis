package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.MPLog;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.TickBlockEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.RotateEntityEvent;
import org.spongepowered.api.event.entity.ai.AITaskEvent;
import org.spongepowered.api.event.entity.living.humanoid.AnimateHandEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.sound.PlaySoundEvent;
import org.spongepowered.api.event.statistic.ChangeStatisticEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.util.Tristate;

public class Debug {

    @IsCancelled(Tristate.UNDEFINED)
    @Listener
    public void onDebug(Event event) { //TODO
        if (false) {
            if (event instanceof LoadChunkEvent || event instanceof MoveEntityEvent
                    || event instanceof RotateEntityEvent || event instanceof AnimateHandEvent
                    || event instanceof CollideBlockEvent || event instanceof CollideEntityEvent
                    || event instanceof SendCommandEvent || event instanceof TabCompleteEvent
                    || event instanceof TickBlockEvent || event instanceof ConstructEntityEvent
                    || event instanceof AITaskEvent || event instanceof MessageChannelEvent
                    || event instanceof SaveWorldEvent || event instanceof UnloadChunkEvent
                    || event instanceof UseItemStackEvent.Tick || event instanceof ChangeDataHolderEvent
                    || event instanceof PlaySoundEvent || event instanceof CriterionEvent

                    || event instanceof ChangeStatisticEvent.TargetPlayer
                    //|| !(event instanceof SpawnEntityEvent)


            ) {
                return;
            }

            boolean canceled = event instanceof Cancellable && ((Cancellable) event).isCancelled();
            if (!canceled) {
                return;
            }
            if (event instanceof ChangeBlockEvent) {
                StringBuilder b = new StringBuilder();
                for (Transaction<BlockSnapshot> transaction : ((ChangeBlockEvent) event).getTransactions()) {
                    b.append(transaction.toString()).append("\n");
                }
                MPLog.getLogger().error("Class: {} \n| Root: {} \n| Cause: {} \n| Context: {} \n| Canceled: {} \n| Trans: {} \n| toS: {} \n",
                        event.getClass().getSimpleName(), event.getCause().root(), event.getCause().all(), event.getCause().getContext(), canceled, b.toString(), event.toString());
            } else {
                MPLog.getLogger().error("Class: {} \n| Root: {} \n| Cause: {} \n| Context: {} \n|  Canceled: {} \n| toS: {} \n",
                        event.getClass().getSimpleName(), event.getCause().root(), event.getCause().all(), event.getCause().getContext(), canceled, event.toString());
            }
        }
    }


}
