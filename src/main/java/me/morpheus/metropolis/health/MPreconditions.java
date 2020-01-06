package me.morpheus.metropolis.health;

import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.health.Incident;
import me.morpheus.metropolis.api.health.IncidentService;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.error.MPGenericErrors;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.permission.PermissionService;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MPreconditions {

    public static void checkDefaultRanks() {
        Optional<Rank> mayorOpt = Sponge.getRegistry().getType(Rank.class, "mayor");
        Optional<Rank> citizenOpt = Sponge.getRegistry().getType(Rank.class, "citizen");

        if (!mayorOpt.isPresent() || !citizenOpt.isPresent()) {
            String mayor = mayorOpt.map(Rank::toString).orElse("NONE");
            String citizen = citizenOpt.map(Rank::toString).orElse("NONE");
            Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                    .create(new MPIncident(MPGenericErrors.rank(mayor, citizen)));
        }
    }

    public static void checkEconomyIntegration() {
        final GlobalConfig g = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        final boolean economyAvailable = Sponge.getServiceManager().provide(EconomyService.class).isPresent();
        if (g.getEconomyCategory().isEnabled() && !economyAvailable) {
            Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                    .create(new MPIncident(MPGenericErrors.economyService()));
        }
    }

    private MPreconditions() {}
}
