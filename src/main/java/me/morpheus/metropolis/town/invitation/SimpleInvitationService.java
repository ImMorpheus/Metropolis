package me.morpheus.metropolis.town.invitation;

import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.invitation.Invitation;
import me.morpheus.metropolis.api.town.invitation.InvitationService;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class SimpleInvitationService implements InvitationService {

    private final Map<UUID, Collection<Invitation>> map = new HashMap<>();

    @Override
    public Invitation create(@Nullable UUID source, UUID target, Town t) {
        Invitation invitation = new MPInvitation(source, target, t.getId(), Instant.now());
        Collection<Invitation> c = this.map.computeIfAbsent(target, k -> new HashSet<>());
        c.add(invitation);
        return invitation;
    }

    @Override
    public void remove(Predicate<Invitation> predicate) {
        for (Collection<Invitation> value : this.map.values()) {
            value.removeIf(predicate);
        }
    }

    @Override
    public void remove(UUID target, Predicate<Invitation> predicate) {
        Collection<Invitation> c = this.map.get(target);
        c.removeIf(predicate);
    }

    @Override
    public Collection<Invitation> getAll() {
        Set<Invitation> invitations = new HashSet<>();
        for (Collection<Invitation> collection : this.map.values()) {
            invitations.addAll(collection);
        }
        return Collections.unmodifiableSet(invitations);
    }

    @Override
    public Collection<Invitation> getAll(UUID target) {
        final Collection<Invitation> c = this.map.get(target);
        if (c == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(c);
    }
}
