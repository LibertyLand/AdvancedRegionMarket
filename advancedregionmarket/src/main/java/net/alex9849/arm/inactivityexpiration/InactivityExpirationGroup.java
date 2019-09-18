package net.alex9849.arm.inactivityexpiration;

import net.alex9849.arm.AdvancedRegionMarket;
import net.alex9849.arm.Permission;
import net.alex9849.arm.util.Utilities;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class InactivityExpirationGroup {
    public static InactivityExpirationGroup DEFAULT = new InactivityExpirationGroup("Default", -1, -1);
    private static InactivityExpirationGroup UNLIMITED = new InactivityExpirationGroup("Unlimited", -1, -1);
    private static Set<InactivityExpirationGroup> inactivityExpirationGroupSet = new HashSet<>();
    private String name;
    private long resetAfterMs;
    private long takeOverAfterMs;

    public InactivityExpirationGroup(String name, long resetAfterMs, long takeOverAfterMs) {
        this.name = name;
        this.resetAfterMs = resetAfterMs;
        this.takeOverAfterMs = takeOverAfterMs;
    }

    public String getName() {
        return this.name;
    }

    public long getResetAfterMs() {
        return resetAfterMs;
    }

    public boolean isResetDisabled() {
        return this.resetAfterMs <= 0;
    }

    public boolean isTakeOverDisabled() {
        return this.takeOverAfterMs <= 0;
    }

    public long getTakeOverAfterMs() {
        return takeOverAfterMs;
    }

    public static void reset() {
        inactivityExpirationGroupSet = new HashSet<>();
    }

    public static void add(InactivityExpirationGroup inactivityExpirationGroup) {
        inactivityExpirationGroupSet.add(inactivityExpirationGroup);
    }

    public static InactivityExpirationGroup getBestResetAfterMs(OfflinePlayer oPlayer, World world) {
        if(!AdvancedRegionMarket.getVaultPerms().isEnabled()) {
            return UNLIMITED;
        }
        if(oPlayer == null) {
            return DEFAULT;
        }
        if(AdvancedRegionMarket.getVaultPerms().playerHas(world.getName(), oPlayer, Permission.ARM_INACTIVITY_EXPIRATION + "unlimited")) {
            return UNLIMITED;
        }
        InactivityExpirationGroup selectedGroup = DEFAULT;

        for(InactivityExpirationGroup ieGroup : inactivityExpirationGroupSet) {
            if(AdvancedRegionMarket.getVaultPerms().playerHas(world.getName(), oPlayer, Permission.ARM_INACTIVITY_EXPIRATION + ieGroup.getName())) {
                if(selectedGroup == DEFAULT || ((!selectedGroup.isResetDisabled() && (selectedGroup.getResetAfterMs() < ieGroup.getResetAfterMs())) || ieGroup.isResetDisabled())) {
                    selectedGroup = ieGroup;
                }
            }
        }
        return selectedGroup;
    }

    public static InactivityExpirationGroup getBestTakeOverAfterMs(OfflinePlayer oPlayer, World world) {
        if(!AdvancedRegionMarket.getVaultPerms().isEnabled()) {
            return UNLIMITED;
        }
        if(oPlayer == null) {
            return DEFAULT;
        }
        if(AdvancedRegionMarket.getVaultPerms().playerHas(world.getName(), oPlayer, Permission.ARM_INACTIVITY_EXPIRATION + "unlimited")) {
            return UNLIMITED;
        }
        InactivityExpirationGroup selectedGroup = DEFAULT;

        for(InactivityExpirationGroup ieGroup : inactivityExpirationGroupSet) {
            if(AdvancedRegionMarket.getVaultPerms().playerHas(world.getName(), oPlayer, Permission.ARM_INACTIVITY_EXPIRATION + ieGroup.getName())) {
                if(selectedGroup == DEFAULT || ((!selectedGroup.isTakeOverDisabled() && (selectedGroup.getTakeOverAfterMs() < ieGroup.getTakeOverAfterMs())) || ieGroup.isTakeOverDisabled())) {
                    selectedGroup = ieGroup;
                }
            }
        }
        return selectedGroup;
    }

    public static InactivityExpirationGroup parse(ConfigurationSection configurationSection, String name) {
        long resetAfterMs;
        if(configurationSection.getString("resetAfter").equalsIgnoreCase("none")) {
            resetAfterMs = -1;
        } else {
            try {
                resetAfterMs = Utilities.stringToTime(configurationSection.getString("resetAfter"));
            } catch (IllegalArgumentException e) {
                AdvancedRegionMarket.getARM().getLogger().log(Level.WARNING, "Could parse resetAfter for " +
                        "InactivityExpirationGroup " + name + "! Please check! ResetAfter has been set to unlimited to prevent unwanted region-resets" );
                resetAfterMs = -1;
            }

        }
        long takeOverAfterMs;
        if(configurationSection.getString("takeOverAfter").equalsIgnoreCase("none")) {
            takeOverAfterMs = -1;
        } else {
            try {
                takeOverAfterMs = Utilities.stringToTime(configurationSection.getString("takeOverAfter"));
            } catch (IllegalArgumentException e) {
                AdvancedRegionMarket.getARM().getLogger().log(Level.WARNING, "Could parse resetAfter for " +
                        "InactivityExpirationGroup " + name + "! Please check! ResetAfter has been set to unlimited to prevent unwanted region-resets" );
                takeOverAfterMs = -1;
            }

        }

        return new InactivityExpirationGroup(name, resetAfterMs, takeOverAfterMs);
    }
}
