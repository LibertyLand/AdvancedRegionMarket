package net.alex9849.arm.commands;

import net.alex9849.arm.AdvancedRegionMarket;
import net.alex9849.arm.Messages;
import net.alex9849.arm.Permission;
import net.alex9849.arm.exceptions.InputException;
import net.alex9849.arm.exceptions.NoSaveLocationException;
import net.alex9849.arm.minifeatures.PlayerRegionRelationship;
import net.alex9849.arm.minifeatures.teleporter.Teleporter;
import net.alex9849.arm.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TPCommand extends BasicArmCommand {

    public TPCommand() {
        super(true, "tp",
                Arrays.asList("(?i)tp [^;\n ]+", "(?i)tp [^;\n ]+ [^;\n ]+"),
                Arrays.asList("tp [REGION]", "tp [REGION] [USER]"),
                Arrays.asList(Permission.MEMBER_TP, Permission.ADMIN_TP));
    }

    @Override
    protected boolean runCommandLogic(CommandSender sender, String command, String commandLabel) throws InputException {
        Player player = sender instanceof Player ? (Player) sender : null;

        if(!(sender instanceof Player))
        {
            String[] args = command.split(" ");
            if(!(args.length > 2))
                throw new InputException(sender, Messages.COMMAND_ONLY_INGAME);

            player = Bukkit.getPlayer(args[2]);
        }

        if(player == null)
            throw new InputException(sender, Messages.PLAYER_NOT_FOUND);

        Region region = AdvancedRegionMarket.getInstance().getRegionManager()
                .getRegionbyNameAndWorldCommands(command.split(" ")[1], player.getWorld().getName());

        if (region == null) {
            throw new InputException(sender, Messages.REGION_DOES_NOT_EXIST);
        }

        if(!region.getRegion().hasMember(player.getUniqueId()) && !region.getRegion().hasOwner(player.getUniqueId())){
            if(!sender.hasPermission(Permission.ADMIN_TP)){
                throw new InputException(sender, Messages.NOT_A_MEMBER_OR_OWNER);
            }
        }

        try {
            Teleporter.teleport(player, region);
        } catch (NoSaveLocationException e) {
            throw new InputException(player, region.replaceVariables(Messages.TELEPORTER_NO_SAVE_LOCATION_FOUND));
        }
        return true;
    }

    protected List<String> onTabCompleteLogic(Player player, String[] args) {
        if(args.length != 2) {
            return new ArrayList<>();
        }
        PlayerRegionRelationship playerRegionRelationship = null;
        if (player.hasPermission(Permission.ADMIN_TP)) {
            playerRegionRelationship = PlayerRegionRelationship.ALL;
        } else {
            playerRegionRelationship = PlayerRegionRelationship.MEMBER_OR_OWNER;
        }
        return AdvancedRegionMarket.getInstance().getRegionManager()
                .completeTabRegions(player, args[1], playerRegionRelationship, true, true);
    }
}
