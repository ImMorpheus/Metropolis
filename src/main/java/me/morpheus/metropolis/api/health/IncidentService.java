package me.morpheus.metropolis.api.health;

import java.util.Collection;
import java.util.function.Supplier;

public interface IncidentService {

    void create(Incident i);

    Collection<Incident> getAll();

    boolean isEmpty();

    void setSafeMode(); //TODO

}
