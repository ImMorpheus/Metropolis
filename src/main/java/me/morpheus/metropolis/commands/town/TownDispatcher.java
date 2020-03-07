package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.citizen.CitizenDispatcher;
import me.morpheus.metropolis.commands.town.friend.FriendDispatcher;
import me.morpheus.metropolis.commands.town.invitation.InvitationDispatcher;
import me.morpheus.metropolis.commands.town.plot.PlotDispatcher;
import me.morpheus.metropolis.commands.town.rank.RankDispatcher;
import me.morpheus.metropolis.commands.town.set.SetDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class TownDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new ChatCommand(), "chat");
        register(new ClaimCommand(), "claim");
        register(new DepositCommand(), "deposit");
        register(new DisbandCommand(), "disband");
        register(new InfoCommand(), "info");
        register(new InviteCommand(), "invite");
        register(new KickCommand(), "kick");
        register(new LeaveCommand(), "leave");
        register(new ListCommand(), "list");
        register(new NewCommand(), "new", "create");
        register(new OutpostCommand(), "outpost");
        register(new OutpostsCommand(), "outposts");
        register(new PricesCommand(), "prices");
        register(new SpawnCommand(), "spawn");
        register(new TreeCommand(), "tree");
        register(new UnclaimCommand(), "unclaim");
        register(new UpgradeCommand(), "upgrade");
        register(new UpgradesCommand(), "upgrades");
        register(new WithdrawCommand(), "withdraw");

        registerManager(new CitizenDispatcher(), "citizen");
        registerManager(new FriendDispatcher(), "friend");
        registerManager(new InvitationDispatcher(), "invitation");
        registerManager(new PlotDispatcher(), "plot");
        registerManager(new RankDispatcher(), "rank");
        registerManager(new SetDispatcher(), "set");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
