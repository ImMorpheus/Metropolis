package me.morpheus.metropolis.health;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.health.IncidentService;
import me.morpheus.metropolis.api.health.Incident;
import me.morpheus.metropolis.error.WarningLoginHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SimpleIncidentService implements IncidentService {

    private final List<Incident> incidents = new ArrayList<>();

    @Override
    public void create(Incident i) {
        this.incidents.add(i);
    }

    @Override
    public Collection<Incident> getAll() {
        return Collections.unmodifiableList(this.incidents);
    }

    @Override
    public void setSafeMode() {
        final PluginContainer container = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        Sponge.getEventManager().unregisterPluginListeners(container);
        Sponge.getEventManager().registerListeners(container, new WarningLoginHandler());

    }
}
