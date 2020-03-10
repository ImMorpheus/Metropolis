package me.morpheus.metropolis.api.rank;

import me.morpheus.metropolis.api.flag.Flag;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(Ranks.class)
public interface Rank extends CatalogType {

    boolean isTaxExempt();

    boolean canBeKickedForInactivity();

    boolean canLeave();

    byte getPermission(Flag flag);

}

