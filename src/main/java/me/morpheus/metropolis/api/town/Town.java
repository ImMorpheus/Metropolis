package me.morpheus.metropolis.api.town;

import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
import me.morpheus.metropolis.api.town.visibility.Visibility;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Town extends DataHolder, MessageReceiver {

    int getId();

    Instant getFounded();

    Text getName();

    void setName(Text name);

    TownType getType();

    void setType(TownType type);

    boolean upgrade(Upgrade upgrade);

    Text getTag();

    void setTag(Text tag);

    Location<World> getSpawn();

    void setSpawn(Location<World> spawn);

    PvPOption getPvP();

    void setPvP(PvPOption pvp);

    Visibility getVisibility();

    void setVisibility(Visibility visibility);

    Optional<Account> getBank();

    BigDecimal getUpkeep();

    List<Text> getTownScreen(@Nullable MessageReceiver receiver);

    boolean accept(UUID user, Rank rank);

    boolean kick(UUID user);

    boolean claim(Location<World> location, PlotType type, @Nullable Text name);

    boolean unclaim(Location<World> location);

    boolean disband();

}
