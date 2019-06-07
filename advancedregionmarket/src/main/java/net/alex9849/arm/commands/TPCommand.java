package net.alex9849.arm.commands;

import net.alex9849.arm.AdvancedRegionMarket;
import net.alex9849.arm.Messages;
import net.alex9849.arm.Permission;
import net.alex9849.exceptions.InputException;
import net.alex9849.arm.minifeatures.PlayerRegionRelationship;
import net.alex9849.arm.minifeatures.teleporter.Teleporter;
import net.alex9849.arm.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TPCommand extends BasicArmCommand {

    private final String rootCommand = "tp";
    private final List<String> usage = new ArrayList<>(Collections.singletonList("tp [REGION] [USER]"));

    @Override
    public boolean matchesRegex(String command) {
        return true; // fuck this shitty regex
        //String regex = "(?i)tp [^;\n]+[^;\n]+";
        //return command.matches(regex);
    }

    @Override
    public String getRootCommand() {
        return this.rootCommand;
    }

    @Override
    public List<String> getUsage() {
        return this.usage;
    }

    @Override
    public boolean runCommand(CommandSender sender, Command cmd, String commandsLabel, String[] args, String allargs) throws InputException {
        if (!sender.hasPermission(Permission.ADMIN_TP) && !sender.hasPermission(Permission.MEMBER_TP)) {
            throw new InputException(sender, Messages.NO_PERMISSION);
        }

        Player player;
        Region region;

        if (!(sender instanceof Player)) {
            if(args.length > 2) {
                Player target = Bukkit.getPlayer(args[2]);

                if(target == null) {
                    throw new InputException(sender, Messages.PLAYER_NOT_FOUND);
                }

                region = AdvancedRegionMarket.getRegionManager().getRegionbyNameAndWorldCommands(args[1], target.getWorld().getName());

                if (region == null) {
                    throw new InputException(sender, Messages.REGION_DOES_NOT_EXIST);
                }

                if(!region.getRegion().hasMember(target.getUniqueId()) && !region.getRegion().hasOwner(target.getUniqueId())){
                    if(!sender.hasPermission(Permission.ADMIN_TP)){
                        throw new InputException(sender, Messages.NOT_A_MEMBER_OR_OWNER);
                    }
                }

                Teleporter.teleport(target, region);
            } else {
                throw new InputException(sender, Messages.COMMAND_ONLY_INGAME);
            }
        } else {
            player = (Player) sender;

            region = AdvancedRegionMarket.getRegionManager().getRegionbyNameAndWorldCommands(args[1], player.getWorld().getName());

            if (region == null) {
                throw new InputException(sender, Messages.REGION_DOES_NOT_EXIST);
            }

            if(!region.getRegion().hasMember(player.getUniqueId()) && !region.getRegion().hasOwner(player.getUniqueId())){
                if(!sender.hasPermission(Permission.ADMIN_TP)){
                    throw new InputException(sender, Messages.NOT_A_MEMBER_OR_OWNER);
                }
            }

            Teleporter.teleport(player, region);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        List<String> returnme = new ArrayList<>();

        if(args.length >= 1) {
            if(this.rootCommand.startsWith(args[0])) {
                if (player.hasPermission(Permission.ADMIN_TP) || player.hasPermission(Permission.MEMBER_TP)) {
                    if(args.length == 1) {
                        returnme.add(this.rootCommand);
                    } else if(args.length == 2 && (args[0].equalsIgnoreCase(this.rootCommand))) {
                        PlayerRegionRelationship playerRegionRelationship;
                        if(player.hasPermission(Permission.ADMIN_TP)) {
                            playerRegionRelationship = PlayerRegionRelationship.ALL;
                        } else {
                            playerRegionRelationship = PlayerRegionRelationship.MEMBER_OR_OWNER;
                        }
                        returnme.addAll(AdvancedRegionMarket.getRegionManager().completeTabRegions(player, args[1], playerRegionRelationship, true,true));
                    } else if(args.length == 3 && (args[0].equalsIgnoreCase(this.rootCommand))) {
                        if(player.hasPermission(Permission.ADMIN_TP)) {
                            Set<String> players = new HashSet<>();

                            for(Player p : Bukkit.getOnlinePlayers()) {
                                if(!player.canSee(p) || player.equals(p)) {
                                    continue;
                                }

                                players.add(p.getName());
                            }

                            returnme.addAll(players);
                        }
                    }
                }
            }
        }
        return returnme;
    }
}
