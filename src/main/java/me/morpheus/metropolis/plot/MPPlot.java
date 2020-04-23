package me.morpheus.metropolis.plot;

import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.data.DataVersions;
import me.morpheus.metropolis.util.Hacks;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public final class MPPlot implements Plot {

    private int town;
    private Text name;
    @Nullable private UUID owner;
    @Nullable private String ownerName;
    private double price;
    private double rent;
    private boolean forSale;
    private PlotType type;
    private boolean mobSpawn;
    @Nullable private Reference2ByteMap<Flag> permissions;

    MPPlot(Town town) {
        this(town.getId(), Text.of(), null, 0, 0, false, PlotTypes.PLOT, false, null);
    }

    MPPlot(int town, Text name, @Nullable UUID owner, double price, double rent, boolean forSale, PlotType type, boolean mobSpawn, @Nullable Reference2ByteMap<Flag> permissions) {
        this.town = town;
        this.name = name;
        this.owner = owner;
        this.price = price;
        this.rent = rent;
        this.forSale = forSale;
        this.type = type;
        this.mobSpawn = mobSpawn;
        this.permissions = permissions;
    }

    @Override
    public int getTown() {
        return this.town;
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public void setName(Text name) {
        this.name = name;
    }

    @Override
    public Optional<UUID> getOwner() {
        return Optional.ofNullable(this.owner);
    }

    public String getOwnerName() {
        if (this.ownerName != null) {
            return this.ownerName;
        }
        if (this.owner == null) {
            this.ownerName = "";
        } else {
            final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            final Optional<User> userOpt = uss.get(this.owner);
            this.ownerName = userOpt.map(User::getName).orElse("");
        }
        return this.ownerName;
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        this.ownerName = null;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public double getRent() {
        return this.rent;
    }

    @Override
    public void setRent(double rent) {
        this.rent = rent;
    }

    @Override
    public boolean isForSale() {
        return this.forSale;
    }

    @Override
    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    @Override
    public PlotType getType() {
        return this.type;
    }

    @Override
    public void setType(PlotType type) {
        this.type = type;
    }

    @Override
    public boolean hasMobSpawn() {
        return this.mobSpawn;
    }

    @Override
    public void setMobSpawn(boolean mobSpawn) {
        this.mobSpawn = mobSpawn;
    }

    @Override
    public byte getPermission(Flag flag) {
        if (this.permissions == null) {
            return Byte.MIN_VALUE;
        }
        return this.permissions.getByte(flag);
    }

    @Override
    public void addPermission(Flag flag, byte value) {
        if (this.permissions == null) {
            this.permissions = new Reference2ByteOpenHashMap<>();
            this.permissions.defaultReturnValue(Byte.MIN_VALUE);
        }
        this.permissions.put(flag, value);

    }

    @Override
    public void removePermission(Flag flag) {
        if (this.permissions == null) {
            return;
        }
        this.permissions.removeByte(flag);
    }

    @Override
    public int getContentVersion() {
        return DataVersions.Plot.CURRENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer container = DataContainer.createNew();
        container.set(Queries.CONTENT_VERSION, getContentVersion());
        container.set(DataQuery.of("Town"), this.town);
        if (!this.name.isEmpty()) {
            container.set(DataQuery.of("Name"), this.name);
        }
        if (this.owner != null) {
            container.set(DataQuery.of("Owner"), this.owner);
        }
        container.set(DataQuery.of("Price"), this.price);
        container.set(DataQuery.of("Rent"), this.rent);
        container.set(DataQuery.of("ForSale"), this.forSale);
        container.set(DataQuery.of("type"), this.type);
        container.set(DataQuery.of("mobspawn"), this.mobSpawn);
        if (this.permissions != null && !this.permissions.isEmpty()) {
            container.set(DataQuery.of("permissions"), Hacks.toContainer(this.permissions));
        }

        return container;
    }

    public static Optional<MPPlot> from(DataContainer container) {
        final Optional<Integer> town = container.getInt(DataQuery.of("Town"));
        if (!town.isPresent()) {
            return Optional.empty();
        }
        final Text name = container.getSerializable(DataQuery.of("Name"), Text.class).orElse(Text.of());
        final UUID owner = container.getObject(DataQuery.of("Owner"), UUID.class).orElse(null);
        final double price = container.getDouble(DataQuery.of("Price")).orElse(0.0);
        final double rent = container.getDouble(DataQuery.of("Rent")).orElse(0.0);
        final boolean forSale = container.getBoolean(DataQuery.of("ForSale")).orElse(false);
        final PlotType type = container.getCatalogType(DataQuery.of("type"), PlotType.class).orElse(PlotTypes.PLOT);
        final boolean mobSpawn = container.getBoolean(DataQuery.of("mobspawn")).orElse(false);
        final Reference2ByteMap<Flag> permissions = Hacks.permissionsFrom(container);
        if (!permissions.isEmpty()) {
            permissions.defaultReturnValue(Byte.MIN_VALUE);
        }
        final Reference2ByteMap<Flag> perms = permissions.isEmpty() ? null : permissions;

        final MPPlot plot = new MPPlot(town.get(), name, owner, price, rent, forSale, type, mobSpawn, perms);
        return Optional.of(plot);
    }

}
