package me.morpheus.metropolis.api.plot;

import me.morpheus.metropolis.api.flag.Flag;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface Plot extends DataSerializable {

    int getTown();

    Text getName();

    void setName(Text name);

    Optional<UUID> getOwner();

    void setOwner(@Nullable UUID owner);

    double getPrice();

    void setPrice(double price);

    double getRent();

    void setRent(double rent);

    boolean isForSale();

    void setForSale(boolean forSale);

    PlotType getType();

    void setType(PlotType type);

    boolean hasMobSpawn();

    void setMobSpawn(boolean mobSpawn);

    byte getPermission(Flag flag);

    void addPermission(Flag flag, byte value);

    void removePermission(Flag flag);
}
