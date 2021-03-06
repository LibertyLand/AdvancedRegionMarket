package net.alex9849.arm.gui;

import com.sk89q.worldguard.protection.flags.*;
import net.alex9849.arm.AdvancedRegionMarket;
import net.alex9849.arm.Messages;
import net.alex9849.arm.Permission;
import net.alex9849.arm.entitylimit.EntityLimit;
import net.alex9849.arm.exceptions.*;
import net.alex9849.arm.flaggroups.FlagGroup;
import net.alex9849.arm.flaggroups.FlagSettings;
import net.alex9849.arm.gui.chathandler.GuiChatInputListener;
import net.alex9849.arm.minifeatures.teleporter.Teleporter;
import net.alex9849.arm.regionkind.RegionKind;
import net.alex9849.arm.regions.ContractRegion;
import net.alex9849.arm.regions.Region;
import net.alex9849.arm.regions.RentRegion;
import net.alex9849.arm.regions.SellRegion;
import net.alex9849.arm.regions.price.Price;
import net.alex9849.arm.util.MaterialFinder;
import net.alex9849.arm.util.TimeUtil;
import net.alex9849.inter.WGRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.logging.Level;

public class Gui implements Listener {
    private static Material REGION_OWNER_ITEM = MaterialFinder.getGuiRegionOwnerItem();
    private static Material REGION_MEMBER_ITEM = MaterialFinder.getGuiRegionMemberItem();
    private static Material REGION_FINDER_ITEM = MaterialFinder.getGuiRegionFinderItem();
    private static Material GO_BACK_ITEM = MaterialFinder.getGuiGoBackItem();
    private static Material WARNING_YES_ITEM = MaterialFinder.getGuiWarningYesItem();
    private static Material WARNING_NO_ITEM = MaterialFinder.getGuiWarningNoItem();
    private static Material TP_ITEM = MaterialFinder.getGuiTpItem();
    private static Material SELL_REGION_ITEM = MaterialFinder.getGuiSellRegionItem();
    private static Material RESET_ITEM = MaterialFinder.getGuiResetItem();
    private static Material EXTEND_ITEM = MaterialFinder.getGuiExtendItem();
    private static Material INFO_ITEM = MaterialFinder.getGuiInfoItem();
    private static Material PROMOTE_MEMBER_TO_OWNER_ITEM = MaterialFinder.getGuiPromoteMemberToOwnerItem();
    private static Material REMOVE_MEMBER_ITEM = MaterialFinder.getGuiRemoveMemberItem();
    private static Material CONTRACT_ITEM = MaterialFinder.getGuiContractItem();
    private static Material FILL_ITEM = MaterialFinder.getGuiFillItem();
    private static Material SUBREGION_ITEM = MaterialFinder.getGuiSubregionItem();
    private static Material DELETE_ITEM = MaterialFinder.getGuiDeleteItem();
    private static Material TELEPORT_TO_SIGN_ITEM = MaterialFinder.getGuiTeleportToSignItem();
    private static Material TELEPORT_TO_REGION_ITEM = MaterialFinder.getGuiTeleportToRegionItem();
    private static Material NEXT_PAGE_ITEM = MaterialFinder.getGuiNextPageItem();
    private static Material PREV_PAGE_ITEM = MaterialFinder.getGuiPrevPageItem();
    private static Material HOTEL_SETTING_ITEM = MaterialFinder.getGuiHotelSettingItem();
    private static Material UNSELL_ITEM = MaterialFinder.getGuiUnsellItem();
    private static Material FLAG_REMOVE_ITEM = MaterialFinder.getGuiFlagRemoveItem();
    private static Material FLAG_SETTING_SELECTED_ITEM = MaterialFinder.getGuiFlagSettingSelectedItem();
    private static Material FLAG_SETTING_NOT_SELECTED_ITEM = MaterialFinder.getGuiFlagSettingNotSelectedItem();
    private static Material FLAG_GROUP_SELECTED_ITEM = MaterialFinder.getGuiFlagGroupSelectedItem();
    private static Material FLAG_GROUP_NOT_SELECTED_ITEM = MaterialFinder.getGuiFlagGroupNotSelectedItem();
    private static Material FLAGEDITOR_ITEM = MaterialFinder.getGuiFlageditorItem();
    private static Material FLAG_ITEM = MaterialFinder.getGuiFlagItem();
    private static Material FLAG_USER_INPUT_ITEM = MaterialFinder.getGuiFlagUserInputItem();
    private static Material FLAGEDITOR_RESET_ITEM = MaterialFinder.getGuiFlageditorResetItem();

    public static void openARMGui(Player player) {
        GuiInventory menu = new GuiInventory(9, Messages.GUI_MAIN_MENU_NAME);
        FileConfiguration config = AdvancedRegionMarket.getInstance().getConfig();
        int itemcounter = 0;
        int actitem = 1;

        if (config.getBoolean("GUI.DisplayRegionOwnerButton")) {
            itemcounter++;
        }
        if (config.getBoolean("GUI.DisplayRegionMemberButton")) {
            itemcounter++;
        }
        if (config.getBoolean("GUI.DisplayRegionFinderButton") && player.hasPermission(Permission.MEMBER_REGIONFINDER)) {
            itemcounter++;
        }


        if (config.getBoolean("GUI.DisplayRegionOwnerButton")) {
            ClickItem regionMenu = new ClickItem(new ItemStack(Gui.REGION_OWNER_ITEM), Messages.GUI_MY_OWN_REGIONS).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openRegionOwnerGui(player, true);
                }
            });
            menu.addIcon(regionMenu, getPosition(actitem, itemcounter));
            actitem++;
            if (itemcounter == 1) {
                Gui.openRegionOwnerGui(player, false);
            }
        }
        if (config.getBoolean("GUI.DisplayRegionMemberButton")) {
            ClickItem regionMemberMenu = new ClickItem(new ItemStack(Gui.REGION_MEMBER_ITEM), Messages.GUI_MY_MEMBER_REGIONS).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openRegionMemberGui(player, true);
                }
            });

            menu.addIcon(regionMemberMenu, getPosition(actitem, itemcounter));
            actitem++;
            if (itemcounter == 1) {
                Gui.openRegionMemberGui(player, false);
            }
        }
        if (config.getBoolean("GUI.DisplayRegionFinderButton") && player.hasPermission(Permission.MEMBER_REGIONFINDER)) {
            ClickItem regionfinder = new ClickItem(new ItemStack(Gui.REGION_FINDER_ITEM), Messages.GUI_SEARCH_FREE_REGION).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openRegionFinder(player, true);
                }
            });

            menu.addIcon(regionfinder, getPosition(actitem, itemcounter));
            actitem++;
            if (itemcounter == 1) {
                Gui.openRegionFinder(player, false);
            }
        }

        menu = Gui.placeFillItems(menu);

        if (itemcounter != 1) {
            player.openInventory(menu.getInventory());
        }
    }

    public static void openRegionOwnerGui(Player player, Boolean withGoBack) {
        List<Region> regions = AdvancedRegionMarket.getInstance().getRegionManager().getRegionsByOwner(player.getUniqueId());
        List<ClickItem> clickItems = new ArrayList<>();

        for (int i = 0; i < regions.size(); i++) {
            ItemStack regionItem = Gui.getRegionDisplayItem(regions.get(i), Messages.GUI_RENT_REGION_LORE, new ArrayList<>(), Messages.GUI_CONTRACT_REGION_LORE);
            int finalI = i;
            ClickItem clickItem = new ClickItem(regionItem).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openRegionOwnerManager(player, regions.get(finalI));
                }
            });
            clickItems.add(clickItem);
        }

        ClickAction goBackAction = null;

        if (withGoBack) {
            goBackAction = new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openARMGui(player);
                }
            };
        }

        Gui.openInfiniteGuiList(player, clickItems, 0, Messages.GUI_OWN_REGIONS_MENU_NAME, goBackAction);
    }

    public static void openRegionOwnerManager(Player player, Region region) {
        int itemcounter = 2;
        int actitem = 1;

        if (player.hasPermission(Permission.MEMBER_TP)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_SELLBACK)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_RESTORE) && region.isUserRestorable()) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_ENTITYLIMIT_CHECK)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_INFO)) {
            itemcounter++;
        }
        if (Permission.hasAnySubregionPermission(player) && region.isAllowSubregions()) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_FLAGEDITOR) && FlagGroup.isFeatureEnabled()) {
            itemcounter++;
        }
        if (region instanceof RentRegion) {
            //Extend button
            itemcounter++;
        }
        if (region instanceof ContractRegion) {
            //Terminate Button
            itemcounter++;
        }

        int invsize = 9;
        while (itemcounter > invsize) {
            invsize += 9;
        }
        GuiInventory inv = new GuiInventory(invsize, region.getRegion().getId());

        ItemStack membersitem = new ItemStack(MaterialFinder.getPlayerHead(), 1, (short) 3);
        SkullMeta membersitemmeta = (SkullMeta) membersitem.getItemMeta();
        FileConfiguration config = AdvancedRegionMarket.getInstance().getConfig();
        if(config.getBoolean("GUI.DisplayPlayerSkins")) {
            membersitemmeta.setOwningPlayer(player);
        }
        membersitemmeta.setDisplayName(Messages.GUI_MEMBERS_BUTTON);
        membersitem.setItemMeta(membersitemmeta);
        ClickItem membersicon = new ClickItem(membersitem).addClickAction(new ClickAction() {
            @Override
            public void execute(Player player) {
                Gui.openMemberList(player, region);
            }
        });
        inv.addIcon(membersicon, getPosition(actitem, itemcounter));

        actitem++;

        if (Permission.hasAnySubregionPermission(player) && region.isAllowSubregions()) {
            ClickItem teleportericon = new ClickItem(new ItemStack(Gui.SUBREGION_ITEM), Messages.GUI_SUBREGION_ITEM_BUTTON, new ArrayList<>()).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openSubregionList(player, region);
                }
            });
            inv.addIcon(teleportericon, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_TP)) {
            ClickItem teleportericon = new ClickItem(new ItemStack(Gui.TP_ITEM), Messages.GUI_TELEPORT_TO_REGION_BUTTON, Messages.GUI_TELEPORT_TO_REGION_BUTTON_LORE);
            teleportericon = teleportericon.addClickAction(new TeleportToRegionClickAction(region));
            inv.addIcon(teleportericon, getPosition(actitem, itemcounter));

            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_RESTORE) && region.isUserRestorable()) {
            List<String> message = new ArrayList<>(Messages.GUI_RESET_REGION_BUTTON_LORE);
            for (int i = 0; i < message.size(); i++) {
                message.set(i, message.get(i).replace("%userresetcooldown%", TimeUtil.timeInMsToString(AdvancedRegionMarket.getInstance().getPluginSettings().getUserResetCooldown(), true, false)));
            }
            ClickItem reseticon = new ClickItem(new ItemStack(Gui.RESET_ITEM), Messages.GUI_RESET_REGION_BUTTON, message);
            reseticon = reseticon.addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    if ((new GregorianCalendar().getTimeInMillis()) >= AdvancedRegionMarket.getInstance().getPluginSettings().getUserResetCooldown() + region.getLastreset()) {
                        Gui.openRegionResetWarning(player, region, true);
                    } else {
                        String message = region.replaceVariables(Messages.RESET_REGION_COOLDOWN_ERROR);
                        throw new InputException(player, message);
                    }
                }
            });
            inv.addIcon(reseticon, getPosition(actitem, itemcounter));

            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_SELLBACK)) {
            List<String> message = new ArrayList<>(Messages.GUI_USER_SELL_BUTTON_LORE);
            for (int i = 0; i < message.size(); i++) {
                message.set(i, region.replaceVariables(message.get(i)));
            }
            ClickItem reseticon = new ClickItem(new ItemStack(Gui.SELL_REGION_ITEM), Messages.GUI_USER_SELL_BUTTON, message).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openSellWarning(player, region, false, true);
                }
            });
            inv.addIcon(reseticon, getPosition(actitem, itemcounter));

            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_FLAGEDITOR) && FlagGroup.isFeatureEnabled()) {
            ClickItem flagEditorItem = new ClickItem(new ItemStack(Gui.FLAGEDITOR_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_BUTTON)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openFlagEditor(player, region, 0, (p) -> {
                        openRegionOwnerManager(player, region);
                    });
                }
            });
            inv.addIcon(flagEditorItem, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (region instanceof RentRegion) {
            ClickItem extendicon = new ClickItem(new ItemStack(Gui.EXTEND_ITEM), Messages.GUI_EXTEND_BUTTON,
                    region.replaceVariables(Messages.GUI_EXTEND_BUTTON_LORE)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    try {
                        ((RentRegion) region).extend(player);
                        Gui.openRegionOwnerManager(player, region);
                    } catch (NoPermissionException | NotEnoughMoneyException
                            | RegionNotOwnException | NotSoldException e) {
                        if (e.hasMessage()) player.sendMessage(Messages.PREFIX + e.getMessage());
                    }
                }
            });
            inv.addIcon(extendicon, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (region instanceof ContractRegion) {
            ContractRegion cregion = (ContractRegion) region;
            ClickItem extendicon = new ClickItem(new ItemStack(Gui.CONTRACT_ITEM), Messages.GUI_CONTRACT_ITEM,
                    region.replaceVariables(Messages.GUI_CONTRACT_ITEM_LORE)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    try {
                        cregion.changeTerminated(player);
                        Gui.openRegionOwnerManager(player, region);
                    } catch (OutOfLimitExeption | NoPermissionException | NotSoldException | RegionNotOwnException e) {
                        if (e.hasMessage()) player.sendMessage(Messages.PREFIX + e.getMessage());
                    }
                }
            });
            inv.addIcon(extendicon, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_ENTITYLIMIT_CHECK)) {
            ClickItem infoicon = new ClickItem(getEntityLimtGroupItem(region)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    openRegionOwnerManager(player, region);
                    net.alex9849.arm.entitylimit.commands.InfoCommand.sendInfoToSender(player, region.getEntityLimitGroup());
                }
            });
            inv.addIcon(infoicon, getPosition(actitem, itemcounter));

            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_INFO)) {
            ClickItem infoicon = new ClickItem(new ItemStack(Gui.INFO_ITEM), Messages.GUI_SHOW_INFOS_BUTTON).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    region.regionInfo(player);
                    player.closeInventory();
                }
            });
            inv.addIcon(infoicon, getPosition(actitem, itemcounter));

            actitem++;
        }

        ClickItem gobackicon = new ClickItem(new ItemStack(Gui.GO_BACK_ITEM), Messages.GUI_GO_BACK).addClickAction(new ClickAction() {
            @Override
            public void execute(Player player) {
                Gui.openRegionOwnerGui(player, isMainPageMultipleItems());
            }
        });
        inv.addIcon(gobackicon, getPosition(actitem, itemcounter));

        actitem++;

        inv = Gui.placeFillItems(inv);

        player.openInventory(inv.getInventory());
    }

    public static void openFlagEditor(Player player, Region region, int start, ClickAction goBackAction) {
        List<FlagSettings> fullFlagSettingsList = region.getFlagGroup().getFlagSettingsSold();
        if (!region.isSold()) {
            fullFlagSettingsList = region.getFlagGroup().getFlagSettingsAvailable();
        }
        List<FlagSettings> flagSettingsList = new ArrayList<>();
        for (FlagSettings flagSettings : fullFlagSettingsList) {
            if (flagSettings.isEditable() && flagSettings.getApplyTo().contains(region.getSellType())) {
                if (flagSettings.hasEditPermission()) {
                    if (player.hasPermission(flagSettings.getEditPermission())) {
                        flagSettingsList.add(flagSettings);
                    }
                } else {
                    flagSettingsList.add(flagSettings);
                }

            }
        }

        Collections.sort(flagSettingsList, (o1, o2) -> {
            return o1.getFlag().getName().compareTo(o2.getFlag().getName());
        });

        int invsize = ((flagSettingsList.size() * 9) - (start * 9) < 54) ? ((flagSettingsList.size() - start) * 9 + 9) : 54;
        GuiInventory guiInventory = new GuiInventory(invsize, region.replaceVariables(Messages.GUI_FLAGEDITOR_MENU_NAME));

        for (int i = start; (i - start) * 9 < (invsize - 9); i++) {
            FlagSettings flagSettings = flagSettingsList.get(i);
            Flag rgFlag = flagSettings.getFlag();
            int invIndex = (i - start) * 9;

            List<String> flagSettingsDescription = flagSettings.getGuidescription();
            for (int j = 0; j < flagSettingsDescription.size(); j++) {
                flagSettingsDescription.set(j, region.replaceVariables(flagSettingsDescription.get(j)));
            }
            ClickItem flagItem = new ClickItem(new ItemStack(Gui.FLAG_ITEM), rgFlag.getName(), flagSettingsDescription);
            guiInventory.addIcon(flagItem, invIndex);

            ClickItem[] flagStateButtons = getFlagSettingItem(rgFlag, region, (p) -> {
                openFlagEditor(p, region, start, goBackAction);
            });

            if (flagStateButtons.length > 0) {
                guiInventory.addIcon(flagStateButtons[0], invIndex + 1);
            }
            if (flagStateButtons.length > 1) {
                guiInventory.addIcon(flagStateButtons[1], invIndex + 2);
            }

            ClickItem deleteButton = new ClickItem(new ItemStack(Gui.FLAG_REMOVE_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_DELETE_FLAG_BUTTON)).addClickAction((pl -> {
                region.getRegion().deleteFlags(rgFlag);
                openFlagEditor(pl, region, start, goBackAction);
                pl.sendMessage(Messages.PREFIX + region.replaceVariables(Messages.FlAGEDITOR_FLAG_HAS_BEEN_DELETED));
            }));
            guiInventory.addIcon(deleteButton, invIndex + 3);

            ClickAction afterFlagSetAction = (pl) -> {
                openFlagEditor(pl, region, start, goBackAction);
            };

            FlagSetter gfsAllButton = new FlagSetter(region, rgFlag.getRegionGroupFlag(), rgFlag, "all", afterFlagSetAction);
            ClickItem allButton = new ClickItem(gfsAllButton.isInputSelected() ? new ItemStack(Gui.FLAG_GROUP_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_GROUP_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_FLAG_GROUP_ALL_BUTTON)).addClickAction(gfsAllButton);
            guiInventory.addIcon(allButton, invIndex + 4);

            FlagSetter gfsMembersButton = new FlagSetter(region, rgFlag.getRegionGroupFlag(), rgFlag, "members", afterFlagSetAction);
            ClickItem membersButton = new ClickItem(gfsMembersButton.isInputSelected() ? new ItemStack(Gui.FLAG_GROUP_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_GROUP_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_FLAG_GROUP_MEMBERS_BUTTON)).addClickAction(gfsMembersButton);
            guiInventory.addIcon(membersButton, invIndex + 5);

            FlagSetter gfsOwnersButton = new FlagSetter(region, rgFlag.getRegionGroupFlag(), rgFlag, "owners", afterFlagSetAction);
            ClickItem ownersButton = new ClickItem(gfsOwnersButton.isInputSelected() ? new ItemStack(Gui.FLAG_GROUP_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_GROUP_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_FLAG_GROUP_OWNERS_BUTTON)).addClickAction(gfsOwnersButton);
            guiInventory.addIcon(ownersButton, invIndex + 6);

            FlagSetter gfsNonMembersButton = new FlagSetter(region, rgFlag.getRegionGroupFlag(), rgFlag, "non_members", afterFlagSetAction);
            ClickItem nonMembersButton = new ClickItem(gfsNonMembersButton.isInputSelected() ? new ItemStack(Gui.FLAG_GROUP_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_GROUP_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_FLAG_GROUP_NON_MEMBERS_BUTTON)).addClickAction(gfsNonMembersButton);
            guiInventory.addIcon(nonMembersButton, invIndex + 7);

            FlagSetter gfsNonOwnersButton = new FlagSetter(region, rgFlag.getRegionGroupFlag(), rgFlag, "non_owners", afterFlagSetAction);
            ClickItem nonOwnersButton = new ClickItem(gfsNonOwnersButton.isInputSelected() ? new ItemStack(Gui.FLAG_GROUP_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_GROUP_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_FLAG_GROUP_NON_OWNERS_BUTTON)).addClickAction(gfsNonOwnersButton);
            guiInventory.addIcon(nonOwnersButton, invIndex + 8);
        }

        if (start != 0) {
            final int newstart;
            newstart = (start - 5 < 0) ? 0 : start - 5;
            ClickItem prevButton = new ClickItem(new ItemStack(Gui.PREV_PAGE_ITEM), Messages.GUI_PREV_PAGE).addClickAction((p) -> {
                openFlagEditor(player, region, newstart, goBackAction);
            });
            guiInventory.addIcon(prevButton, guiInventory.getSize() - 9);
        }

        ClickItem resetButton = new ClickItem(new ItemStack(Gui.FLAGEDITOR_RESET_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_RESET_BUTTON)).addClickAction((p) -> {
            try {
                //Force apply, because menu can only be opened if the flagGroups feature is enabled
                region.applyFlagGroup(FlagGroup.ResetMode.COMPLETE, true);
            } catch (FeatureDisabledException e) {
                //Exception can't be thrown. Ignore
            }
            player.sendMessage(Messages.PREFIX + region.replaceVariables(Messages.FLAGEDITOR_FLAG_HAS_BEEN_UPDATED));
            openFlagEditor(player, region, start, goBackAction);
        });
        guiInventory.addIcon(resetButton, guiInventory.getSize() - 7);

        if (goBackAction != null) {
            ClickItem goBackButton = new ClickItem(new ItemStack(Gui.GO_BACK_ITEM), Messages.GUI_GO_BACK).addClickAction(goBackAction);
            guiInventory.addIcon(goBackButton, guiInventory.getSize() - 3);
        }

        if (flagSettingsList.size() > start + 5) {
            ClickItem prevButton = new ClickItem(new ItemStack(Gui.NEXT_PAGE_ITEM), Messages.GUI_NEXT_PAGE).addClickAction((p) -> {
                openFlagEditor(player, region, start + 5, goBackAction);
            });
            guiInventory.addIcon(prevButton, guiInventory.getSize() - 1);
        }

        guiInventory = Gui.placeFillItems(guiInventory);
        player.openInventory(guiInventory.getInventory());
    }

    public static void openRegionMemberManager(Player player, Region region) {

        int itemcounter = 1;
        int actitem = 1;

        if (player.hasPermission(Permission.MEMBER_TP)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_INFO)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_ENTITYLIMIT_CHECK)) {
            itemcounter++;
        }

        int invsize = 9;
        while (itemcounter > invsize) {
            invsize += 9;
        }

        GuiInventory inv = new GuiInventory(invsize, region.getRegion().getId());

        if (player.hasPermission(Permission.MEMBER_TP)) {
            ClickItem teleportericon = new ClickItem(new ItemStack(Gui.TP_ITEM), Messages.GUI_TELEPORT_TO_REGION_BUTTON,
                    region.replaceVariables(Messages.GUI_TELEPORT_TO_REGION_BUTTON_LORE)).addClickAction(new TeleportToRegionClickAction(region));
            inv.addIcon(teleportericon, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_ENTITYLIMIT_CHECK)) {
            ClickItem infoicon = new ClickItem(getEntityLimtGroupItem(region)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    openRegionMemberManager(player, region);
                    net.alex9849.arm.entitylimit.commands.InfoCommand.sendInfoToSender(player, region.getEntityLimitGroup());
                }
            });
            inv.addIcon(infoicon, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_INFO)) {
            ClickItem infoicon = new ClickItem(new ItemStack(Gui.INFO_ITEM), Messages.GUI_SHOW_INFOS_BUTTON)
                    .addClickAction(new ClickAction() {
                        @Override
                        public void execute(Player player) {
                            region.regionInfo(player);
                            player.closeInventory();
                        }
                    });
            inv.addIcon(infoicon, getPosition(actitem, itemcounter));
            actitem++;
        }

        ClickItem gobackicon = new ClickItem(new ItemStack(Gui.GO_BACK_ITEM), Messages.GUI_GO_BACK).addClickAction(new ClickAction() {
            @Override
            public void execute(Player player) {
                Gui.openRegionMemberGui(player, isMainPageMultipleItems());
            }
        });
        inv.addIcon(gobackicon, getPosition(actitem, itemcounter));
        actitem++;

        inv = Gui.placeFillItems(inv);

        player.openInventory(inv.getInventory());
    }

    public static void openSubregionList(Player player, Region region) {
        List<ClickItem> clickItems = new ArrayList<>();
        for (Region subregion : region.getSubregions()) {
            ItemStack subregionItem = Gui.getRegionDisplayItem(subregion, Messages.GUI_SUBREGION_REGION_INFO_RENT,
                    Messages.GUI_SUBREGION_REGION_INFO_SELL, Messages.GUI_SUBREGION_REGION_INFO_CONTRACT);
            ClickItem subregionClickItem = new ClickItem(subregionItem).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openSubregionManager(player, subregion, region);
                }
            });
            clickItems.add(subregionClickItem);
        }
        if (clickItems.size() == 0) {
            List<String> lore = new ArrayList<>(Messages.GUI_SUBREGION_MANAGER_NO_SUBREGION_ITEM_LORE);
            ClickItem infoItem = new ClickItem(new ItemStack(Gui.INFO_ITEM), Messages.GUI_SUBREGION_MANAGER_NO_SUBREGION_ITEM, lore);
            clickItems.add(infoItem);
        }
        Gui.openInfiniteGuiList(player, clickItems, 0, Messages.GUI_SUBREGION_LIST_MENU_NAME, new ClickAction() {
            @Override
            public void execute(Player player) throws InputException {
                Gui.openRegionOwnerManager(player, region);
            }
        });
    }

    public static void openSubregionManager(Player player, Region region, Region parentRegion) {

        GuiInventory inv = new GuiInventory(9, region.getRegion().getId());

        int itemcounter = 1;
        int actitem = 1;

        if (player.hasPermission(Permission.SUBREGION_SET_IS_HOTEL)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.SUBREGION_TP)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_INFO)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.SUBREGION_RESTORE) && region.isUserRestorable()) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.SUBREGION_UNSELL)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.SUBREGION_DELETE_AVAILABLE) || player.hasPermission(Permission.SUBREGION_DELETE_SOLD)) {
            itemcounter++;
        }

        if (player.hasPermission(Permission.SUBREGION_SET_IS_HOTEL)) {
            ClickItem isHotelItem = new ClickItem(new ItemStack(Gui.HOTEL_SETTING_ITEM), Messages.GUI_SUBREGION_HOTEL_BUTTON,
                    region.replaceVariables(Messages.GUI_SUBREGION_HOTEL_BUTTON_LORE)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    region.setHotel(!region.isHotel());
                    Gui.openSubregionManager(player, region, parentRegion);
                }
            });
            inv.addIcon(isHotelItem, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (player.hasPermission(Permission.SUBREGION_TP)) {
            ClickItem tpItem = new ClickItem(new ItemStack(Gui.TP_ITEM), Messages.GUI_TELEPORT_TO_REGION_BUTTON).addClickAction(new TeleportToRegionClickAction(region));
            inv.addIcon(tpItem, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_INFO)) {
            ClickItem infoItem = new ClickItem(new ItemStack(Gui.INFO_ITEM), Messages.GUI_SHOW_INFOS_BUTTON).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    region.regionInfo(player);
                    player.closeInventory();
                }
            });
            inv.addIcon(infoItem, getPosition(actitem, itemcounter));
            actitem++;
        }
        if (player.hasPermission(Permission.SUBREGION_RESTORE) && region.isUserRestorable()) {
            ClickItem resetItem = new ClickItem(new ItemStack(Gui.RESET_ITEM), Messages.GUI_RESET_REGION_BUTTON)
                    .addClickAction(new ClickAction() {
                        @Override
                        public void execute(Player player) throws InputException {
                            if(!((new GregorianCalendar().getTimeInMillis()) >= AdvancedRegionMarket.getInstance().getPluginSettings().getUserResetCooldown() + region.getLastreset())) {
                                String message = region.replaceVariables(Messages.RESET_REGION_COOLDOWN_ERROR);
                                throw new InputException(player, message);
                            }
                            Gui.openWarning(player,
                                    p -> region.userRestore(player),
                                    p -> Gui.openSubregionManager(player, region, parentRegion),
                                    Messages.GUI_RESET_REGION_WARNING_NAME,
                                    new ArrayList<>(), new ArrayList<>());
                        }
                    });
            inv.addIcon(resetItem, getPosition(actitem, itemcounter));
            actitem++;
        }
        if (player.hasPermission(Permission.SUBREGION_UNSELL)) {
            ClickItem unsellItem = new ClickItem(new ItemStack(Gui.UNSELL_ITEM), Messages.UNSELL_REGION_BUTTON,
                    region.replaceVariables(Messages.UNSELL_REGION_BUTTON_LORE)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openWarning(player, p -> {
                        region.unsell(Region.ActionReason.MANUALLY_BY_PARENT_REGION_OWNER, true, false);
                        player.closeInventory();
                        player.sendMessage(Messages.PREFIX + Messages.REGION_NOW_AVAILABLE);
                    }, p -> {
                        Gui.openSubregionManager(player, region, parentRegion);
                    }, Messages.UNSELL_REGION_WARNING_NAME, new ArrayList<>(), new ArrayList<>());
                }
            });
            inv.addIcon(unsellItem, getPosition(actitem, itemcounter));
            actitem++;
        }
        if (player.hasPermission(Permission.SUBREGION_DELETE_AVAILABLE) || player.hasPermission(Permission.SUBREGION_DELETE_SOLD)) {
            ClickItem deleteItem = new ClickItem(new ItemStack(Gui.DELETE_ITEM), Messages.GUI_SUBREGION_DELETE_REGION_BUTTON)
                    .addClickAction(new ClickAction() {
                        @Override
                        public void execute(Player player) throws InputException {
                            if (region.isSold() && (!player.hasPermission(Permission.SUBREGION_DELETE_SOLD))) {
                                throw new InputException(player, Messages.NOT_ALLOWED_TO_REMOVE_SUBREGION_SOLD);
                            }
                            if ((!region.isSold()) && (!player.hasPermission(Permission.SUBREGION_DELETE_AVAILABLE))) {
                                throw new InputException(player, Messages.NOT_ALLOWED_TO_REMOVE_SUBREGION_AVAILABLE);
                            }
                            Gui.openWarning(player, p -> {
                                        region.delete();
                                        player.closeInventory();
                                        player.sendMessage(Messages.PREFIX + Messages.REGION_DELETED);
                                    }, p -> Gui.openSubregionManager(player, region, parentRegion)
                                    , Messages.DELETE_REGION_WARNING_NAME, new ArrayList<>(), new ArrayList<>());
                        }
                    });
            inv.addIcon(deleteItem, getPosition(actitem, itemcounter));
            actitem++;
        }

        ClickItem goBack = new ClickItem(new ItemStack(Gui.GO_BACK_ITEM), Messages.GUI_GO_BACK).addClickAction(new ClickAction() {
            @Override
            public void execute(Player player) throws InputException {
                Gui.openSubregionList(player, parentRegion);
            }
        });
        inv.addIcon(goBack, getPosition(actitem, itemcounter));
        actitem++;


        inv = placeFillItems(inv);
        player.openInventory(inv.getInventory());
    }

    public static void openRegionFinder(Player player, Boolean withGoBack) {

        int itemcounter = 0;
        int actitem = 1;
        if (withGoBack) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_LIMIT)) {
            itemcounter++;
        }

        if (RegionKind.DEFAULT.isDisplayInRegionfinder()) {
            itemcounter++;
        }

        if (RegionKind.SUBREGION.isDisplayInRegionfinder()) {
            itemcounter++;
        }

        for (RegionKind regionKind : AdvancedRegionMarket.getInstance().getRegionKindManager()) {
            if (regionKind.isDisplayInRegionfinder()) {
                itemcounter++;
            }
        }

        int invsize = 0;

        while (itemcounter > invsize) {
            invsize = invsize + 9;
        }

        GuiInventory inv = new GuiInventory(invsize, Messages.GUI_REGION_FINDER_MENU_NAME);
        int itempos = 0;
        if (RegionKind.DEFAULT.isDisplayInRegionfinder()) {
            String displayName = Messages.GUI_REGIONFINDER_REGIONKIND_NAME;
            displayName = RegionKind.DEFAULT.replaceVariables(displayName);
            Material material = RegionKind.DEFAULT.getMaterial();
            ItemStack stack = new ItemStack(material);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(RegionKind.DEFAULT.getLore());
            stack.setItemMeta(meta);
            ClickItem icon = new ClickItem(stack).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openRegionFinderSellTypeSelector(player, AdvancedRegionMarket.getInstance().getRegionManager().getFreeRegions(RegionKind.DEFAULT), new ClickAction() {
                        @Override
                        public void execute(Player player) throws InputException {
                            Gui.openRegionFinder(player, withGoBack);
                        }
                    });
                }
            });
            inv.addIcon(icon, itempos);
            itempos++;
        }

        if (RegionKind.SUBREGION.isDisplayInRegionfinder()) {
            String displayName = Messages.GUI_REGIONFINDER_REGIONKIND_NAME;
            displayName = RegionKind.SUBREGION.replaceVariables(displayName);
            Material material = RegionKind.SUBREGION.getMaterial();
            ItemStack stack = new ItemStack(material);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(RegionKind.SUBREGION.getLore());
            stack.setItemMeta(meta);
            ClickItem icon = new ClickItem(stack).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openRegionFinderSellTypeSelector(player, AdvancedRegionMarket.getInstance().getRegionManager().getFreeRegions(RegionKind.SUBREGION), new ClickAction() {
                        @Override
                        public void execute(Player player) throws InputException {
                            Gui.openRegionFinder(player, withGoBack);
                        }
                    });
                }
            });
            inv.addIcon(icon, itempos);
            itempos++;
        }


        for (RegionKind regionKind : AdvancedRegionMarket.getInstance().getRegionKindManager()) {
            if (regionKind.isDisplayInRegionfinder()) {
                String displayName = Messages.GUI_REGIONFINDER_REGIONKIND_NAME;
                displayName = regionKind.replaceVariables(displayName);
                Material material = regionKind.getMaterial();
                ItemStack stack = new ItemStack(material);
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(displayName);
                meta.setLore(regionKind.getLore());
                stack.setItemMeta(meta);
                ClickItem icon = new ClickItem(stack).addClickAction(new ClickAction() {
                    @Override
                    public void execute(Player player) throws InputException {
                        Gui.openRegionFinderSellTypeSelector(player, AdvancedRegionMarket.getInstance().getRegionManager().getFreeRegions(regionKind), new ClickAction() {
                            @Override
                            public void execute(Player player) throws InputException {
                                Gui.openRegionFinder(player, withGoBack);
                            }
                        });
                    }
                });
                inv.addIcon(icon, itempos);
                itempos++;
            }

        }

        if (player.hasPermission(Permission.MEMBER_LIMIT)) {
            ItemStack goBack = new ItemStack(Gui.INFO_ITEM);
            ItemMeta goBackMeta = goBack.getItemMeta();
            goBackMeta.setDisplayName(Messages.GUI_MY_LIMITS_BUTTON);
            goBack.setItemMeta(goBackMeta);

            ClickItem gobackButton = new ClickItem(goBack).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    AdvancedRegionMarket.getInstance().getLimitGroupManager().printLimitInChat(player);
                }
            });
            int pos = 1;
            if (withGoBack) {
                pos = 2;
            }
            inv.addIcon(gobackButton, (invsize - pos));
        }


        if (withGoBack) {
            ItemStack goBack = new ItemStack(Gui.GO_BACK_ITEM);
            ItemMeta goBackMeta = goBack.getItemMeta();
            goBackMeta.setDisplayName(Messages.GUI_GO_BACK);
            goBack.setItemMeta(goBackMeta);

            ClickItem gobackButton = new ClickItem(goBack).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openARMGui(player);
                }
            });

            inv.addIcon(gobackButton, (invsize - 1));
        }

        inv = Gui.placeFillItems(inv);

        player.openInventory(inv.getInventory());

    }

    public static void openRegionFinderSellTypeSelector(Player player, List<Region> regions, ClickAction goBackAction) throws InputException {
        GuiInventory inv = new GuiInventory(9, Messages.GUI_REGION_FINDER_MENU_NAME);
        List<ClickItem> sellRegionClickItems = new ArrayList<>();
        List<ClickItem> rentRegionClickItems = new ArrayList<>();
        List<ClickItem> contractRegionClickItems = new ArrayList<>();
        ClickItem sellRegionList = null;
        ClickItem rentRegionList = null;
        ClickItem contractRegionList = null;
        int itemcounter = 0;
        int actitem = 1;

        regions.sort(new Comparator<Region>() {
            @Override
            public int compare(Region o1, Region o2) {
                if (o1.getPricePerPeriod() > o2.getPricePerPeriod()) {
                    return 1;
                }
                if (o1.getPricePerPeriod() == o2.getPricePerPeriod()) {
                    return 0;
                }
                return -1;
            }
        });


        for (Region region : regions) {
            ItemStack itemStack = getRegionDisplayItem(region, Messages.GUI_REGIONFINDER_REGION_INFO_RENT, Messages.GUI_REGIONFINDER_REGION_INFO_SELL, Messages.GUI_REGIONFINDER_REGION_INFO_CONTRACT);
            boolean tpToRegionPermission = player.hasPermission(Permission.MEMBER_REGIONFINDER_TP_TO_REGION);
            boolean tpToSignPermission = player.hasPermission(Permission.MEMBER_REGIONFINDER_TP_TO_SIGN);
            boolean hasSign = region.getNumberOfSigns() != 0;
            ClickItem clickItem = new ClickItem(itemStack).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    if (tpToRegionPermission && tpToSignPermission && hasSign) {
                        Gui.openRegionFinderTeleportLocationSeceltor(player, region);
                        return;
                    }
                    if (tpToRegionPermission) {
                        new TeleportToRegionClickAction(region).execute(player);

                    }
                    if (tpToSignPermission && hasSign) {
                        try {
                            region.teleport(player, true);
                            player.closeInventory();
                        } catch (NoSaveLocationException e) {
                            player.sendMessage(Messages.PREFIX + region.replaceVariables(Messages.TELEPORTER_NO_SAVE_LOCATION_FOUND));
                        }
                        return;
                    }
                }
            });
            if (region instanceof SellRegion) {
                sellRegionClickItems.add(clickItem);
            } else if (region instanceof RentRegion) {
                rentRegionClickItems.add(clickItem);
            } else if (region instanceof ContractRegion) {
                contractRegionClickItems.add(clickItem);
            }
        }
        if (sellRegionClickItems.size() > 0) {
            itemcounter++;
        }
        if (rentRegionClickItems.size() > 0) {
            itemcounter++;
        }
        if (contractRegionClickItems.size() > 0) {
            itemcounter++;
        }
        if (sellRegionClickItems.size() > 0) {
            ClickItem clickItem = new ClickItem(new ItemStack(MaterialFinder.getBRICKS()), Messages.SELLREGION_NAME);
            clickItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openInfiniteGuiList(player, sellRegionClickItems, 0, Messages.GUI_REGION_FINDER_MENU_NAME, goBackAction);
                }
            });
            if ((rentRegionClickItems.size() == 0) && (contractRegionClickItems.size() == 0)) {
                Gui.openInfiniteGuiList(player, sellRegionClickItems, 0, Messages.GUI_REGION_FINDER_MENU_NAME, goBackAction);
                return;
            }
            inv.addIcon(clickItem, getPosition(actitem, itemcounter));
            actitem++;
        }
        if (rentRegionClickItems.size() > 0) {
            ClickItem clickItem = new ClickItem(new ItemStack(MaterialFinder.getBRICKS()), Messages.RENTREGION_NAME);
            clickItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openInfiniteGuiList(player, rentRegionClickItems, 0, Messages.GUI_REGION_FINDER_MENU_NAME, goBackAction);
                }
            });
            if ((sellRegionClickItems.size() == 0) && (contractRegionClickItems.size() == 0)) {
                Gui.openInfiniteGuiList(player, rentRegionClickItems, 0, Messages.GUI_REGION_FINDER_MENU_NAME, goBackAction);
                return;
            }
            inv.addIcon(clickItem, getPosition(actitem, itemcounter));
            actitem++;
        }
        if (contractRegionClickItems.size() > 0) {
            ClickItem clickItem = new ClickItem(new ItemStack(MaterialFinder.getBRICKS()), Messages.CONTRACTREGION_NAME);
            clickItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openInfiniteGuiList(player, contractRegionClickItems, 0, Messages.GUI_REGION_FINDER_MENU_NAME, goBackAction);
                }
            });
            if ((sellRegionClickItems.size() == 0) && (rentRegionClickItems.size() == 0)) {
                Gui.openInfiniteGuiList(player, contractRegionClickItems, 0, Messages.GUI_REGION_FINDER_MENU_NAME, goBackAction);
                return;
            }
            inv.addIcon(clickItem, getPosition(actitem, itemcounter));
            actitem++;
        }
        if ((sellRegionClickItems.size() == 0) && (rentRegionClickItems.size() == 0) && (contractRegionClickItems.size() == 0)) {
            throw new InputException(player, Messages.NO_FREE_REGION_WITH_THIS_KIND);
        }
        inv = Gui.placeFillItems(inv);
        player.openInventory(inv.getInventory());
    }

    public static void openRegionFinderTeleportLocationSeceltor(Player player, Region region) throws InputException {
        GuiInventory inv = new GuiInventory(9, Messages.GUI_TELEPORT_TO_SIGN_OR_REGION);
        ClickItem clickSign = new ClickItem(new ItemStack(Gui.TELEPORT_TO_SIGN_ITEM), Messages.GUI_TELEPORT_TO_SIGN);
        clickSign.addClickAction(new ClickAction() {
            @Override
            public void execute(Player player) throws InputException {
                try {
                    region.teleport(player, true);
                    player.closeInventory();
                } catch (NoSaveLocationException e) {
                    player.sendMessage(Messages.PREFIX + region.replaceVariables(Messages.TELEPORTER_NO_SAVE_LOCATION_FOUND));
                }
            }
        });
        inv.addIcon(clickSign, getPosition(1, 2));

        ClickItem clickRegion = new ClickItem(new ItemStack(Gui.TELEPORT_TO_REGION_ITEM), Messages.GUI_TELEPORT_TO_REGION);
        clickRegion.addClickAction(new TeleportToRegionClickAction(region));
        inv.addIcon(clickRegion, getPosition(2, 2));
        inv = Gui.placeFillItems(inv);
        player.openInventory(inv.getInventory());
    }

    public static void openInfiniteGuiList(Player player, List<ClickItem> clickItems, int startitem, String name, ClickAction gobackAction) {

        int invsize = 0;
        int itemsize = 0;

        while (((clickItems.size() - startitem) > itemsize) && (itemsize < 45)) {
            itemsize = itemsize + 9;
        }
        invsize = itemsize;
        if (((gobackAction != null) && (clickItems.size() >= 9)) || ((startitem + 45) < (clickItems.size() - 1)) || (startitem != 0) || (itemsize == 0)) {
            invsize = itemsize + 9;
        }

        GuiInventory inv = new GuiInventory(invsize, name);

        int pos = 0;
        for (int i = startitem; ((i < startitem + itemsize) && (i < clickItems.size())); i++) {
            inv.addIcon(clickItems.get(i), pos);
            pos++;
        }
        if (startitem != 0) {
            int newStartItem = startitem - 45;
            if (newStartItem < 0) {
                newStartItem = 0;
            }
            final int finalnewStartItem = newStartItem;
            ClickItem prevPageButton = new ClickItem(new ItemStack(Gui.PREV_PAGE_ITEM), Messages.GUI_PREV_PAGE).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openInfiniteGuiList(player, clickItems, finalnewStartItem, name, gobackAction);
                }
            });
            inv.addIcon(prevPageButton, invsize - 9);
        }
        if ((startitem + 45) < clickItems.size()) {
            int newStartItem = startitem + 45;
            ClickItem nextPageButton = new ClickItem(new ItemStack(Gui.NEXT_PAGE_ITEM), Messages.GUI_NEXT_PAGE).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openInfiniteGuiList(player, clickItems, newStartItem, name, gobackAction);
                }
            });
            inv.addIcon(nextPageButton, invsize - 1);
        }
        if (gobackAction != null) {
            ClickItem gobackButton = new ClickItem(new ItemStack(Gui.GO_BACK_ITEM), Messages.GUI_GO_BACK).addClickAction(gobackAction);
            if (clickItems.size() >= 9) {
                inv.addIcon(gobackButton, invsize - 5);
            } else {
                inv.addIcon(gobackButton, 8);
            }

        }

        inv = Gui.placeFillItems(inv);
        player.openInventory(inv.getInventory());
    }

    public static void openMemberList(Player player, Region region) {
        ArrayList<UUID> members = region.getRegion().getMembers();

        List<ClickItem> clickItems = new ArrayList<>();

        String invname = Messages.GUI_MEMBER_LIST_MENU_NAME.replaceAll("%regionid%", region.getRegion().getId());
        FileConfiguration config = AdvancedRegionMarket.getInstance().getConfig();
        boolean showPlayerSkins = config.getBoolean("GUI.DisplayPlayerSkins");

        for (UUID memberUUID : members) {
            ItemStack membersitem = new ItemStack(MaterialFinder.getPlayerHead(), 1, (short) 3);
            SkullMeta membersitemmeta = (SkullMeta) membersitem.getItemMeta();
            OfflinePlayer memberPlayer = Bukkit.getOfflinePlayer(memberUUID);
            if (memberPlayer != null) {
                if(showPlayerSkins) {
                    membersitemmeta.setOwningPlayer(player);
                }
                membersitemmeta.setDisplayName(memberPlayer.getName());
            }
            membersitem.setItemMeta(membersitemmeta);
            ClickItem membersicon = new ClickItem(membersitem).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    Gui.openMemberManager(player, region, memberPlayer);
                }
            });
            clickItems.add(membersicon);
        }

        if (members.size() < region.getMaxMembers() || region.getMaxMembers() == -1) {
            ClickItem infoButton = new ClickItem(new ItemStack(Gui.INFO_ITEM), Messages.GUI_OWNER_MEMBER_INFO_ITEM,
                    region.replaceVariables(Messages.GUI_OWNER_MEMBER_INFO_LORE));
            clickItems.add(infoButton);
        }

        Gui.openInfiniteGuiList(player, clickItems, 0, invname, new ClickAction() {
            @Override
            public void execute(Player player) {
                Gui.openRegionOwnerManager(player, region);
            }
        });

    }

    public static void openMemberManager(Player player, Region region, OfflinePlayer member) {
        GuiInventory inv = new GuiInventory(9, region.getRegion().getId() + " - " + member.getName());

        int itemcounter = 1;
        int actitem = 1;

        if (player.hasPermission(Permission.MEMBER_REMOVEMEMBER)) {
            itemcounter++;
        }
        if (player.hasPermission(Permission.MEMBER_PROMOTE)) {
            itemcounter++;
        }

        if (player.hasPermission(Permission.MEMBER_PROMOTE)) {
            ClickItem makeOwnerMenu = new ClickItem(new ItemStack(Gui.PROMOTE_MEMBER_TO_OWNER_ITEM),
                    Messages.GUI_MAKE_OWNER_BUTTON, region.replaceVariables(Messages.GUI_MAKE_OWNER_BUTTON_LORE))
                    .addClickAction(new ClickAction() {
                        @Override
                        public void execute(Player player) {
                            Gui.openMakeOwnerWarning(player, region, member, true);
                        }
                    });
            inv.addIcon(makeOwnerMenu, getPosition(actitem, itemcounter));
            actitem++;
        }

        if (player.hasPermission(Permission.MEMBER_REMOVEMEMBER)) {
            ClickItem removeMenu = new ClickItem(new ItemStack(Gui.REMOVE_MEMBER_ITEM), Messages.GUI_REMOVE_MEMBER_BUTTON,
                    region.replaceVariables(Messages.GUI_REMOVE_MEMBER_BUTTON_LORE)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    region.getRegion().removeMember(member.getUniqueId());
                    player.sendMessage(Messages.PREFIX + Messages.REGION_REMOVE_MEMBER_REMOVED);
                    player.closeInventory();
                }
            });
            inv.addIcon(removeMenu, getPosition(actitem, itemcounter));
            actitem++;
        }

        ClickItem gobackButton = new ClickItem(new ItemStack(Gui.GO_BACK_ITEM), Messages.GUI_GO_BACK).addClickAction(new ClickAction() {
            @Override
            public void execute(Player player) {
                Gui.openMemberList(player, region);
            }
        });

        inv.addIcon(gobackButton, getPosition(actitem, itemcounter));
        actitem++;

        inv = Gui.placeFillItems(inv);

        player.openInventory(inv.getInventory());

    }

    public static void openMakeOwnerWarning(Player player, Region region, OfflinePlayer member, Boolean goback) {
        Player onlinemember = Bukkit.getPlayer(member.getUniqueId());

        openWarning(player, p -> {
            player.closeInventory();
            if (onlinemember == null) {
                throw new InputException(player, Messages.REGION_TRANSFER_MEMBER_NOT_ONLINE);
            }
            if (AdvancedRegionMarket.getInstance().getLimitGroupManager().isCanBuyAnother(onlinemember, region.getRegionKind())) {
                WGRegion wgRegion = region.getRegion();
                for (UUID oldOwner : wgRegion.getOwners()) {
                    wgRegion.addMember(oldOwner);
                }
                region.setOwner(onlinemember);
                player.sendMessage(Messages.PREFIX + Messages.REGION_TRANSFER_COMPLETE_MESSAGE);
            } else {
                throw new InputException(player, Messages.REGION_TRANSFER_LIMIT_ERROR);
            }
        }, p -> {
            if (goback) {
                Gui.openMemberManager(player, region, member);
            } else {
                player.closeInventory();
            }
        }, Messages.GUI_MAKE_OWNER_WARNING_NAME, new ArrayList<>(), new ArrayList<>());
    }

    public static void openWarning(Player player, ClickAction yesAction, ClickAction noAction, String title, List<String> yesLore, List<String> noLore) {
        GuiInventory inv = new GuiInventory(9, title);
        ClickItem yesButton = new ClickItem(new ItemStack(Gui.WARNING_YES_ITEM), Messages.GUI_YES, yesLore);
        if (yesAction != null) {
            yesButton.addClickAction(yesAction);
        }
        inv.addIcon(yesButton, 0);

        ClickItem noButton = new ClickItem(new ItemStack(Gui.WARNING_NO_ITEM), Messages.GUI_NO, noLore);
        if (yesAction != null) {
            noButton.addClickAction(noAction);
        }
        inv.addIcon(noButton, 8);

        inv = Gui.placeFillItems(inv);
        player.openInventory(inv.getInventory());
    }

    public static void openRegionMemberGui(Player player, Boolean withGoBack) {
        List<Region> regions = AdvancedRegionMarket.getInstance().getRegionManager().getRegionsByMember(player.getUniqueId());
        List<ClickItem> clickItems = new ArrayList<>();

        for (Region region : regions) {
            ItemStack regionItem = Gui.getRegionDisplayItem(region, Messages.GUI_RENT_REGION_LORE, new ArrayList<>(), Messages.GUI_CONTRACT_REGION_LORE);
            ClickItem clickItem = new ClickItem(regionItem).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openRegionMemberManager(player, region);
                }
            });
            clickItems.add(clickItem);
        }

        if (regions.size() == 0) {
            clickItems.add(new ClickItem(new ItemStack(Gui.INFO_ITEM), Messages.GUI_MEMBER_INFO_ITEM, Messages.GUI_MEMBER_INFO_LORE));
        }

        ClickAction goBackAction = null;
        if (withGoBack) {
            goBackAction = new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    Gui.openARMGui(player);
                }
            };
        }

        Gui.openInfiniteGuiList(player, clickItems, 0, Messages.GUI_MEMBER_REGIONS_MENU_NAME, goBackAction);
    }

    public static void openOvertakeGUI(Player player, List<Region> oldRegions) {

        int invsize = 0;
        while (oldRegions.size() + 1 > invsize) {
            invsize = invsize + 9;
        }

        GuiInventory inv = new GuiInventory(invsize, Messages.GUI_TAKEOVER_MENU_NAME);
        int position = 0;
        for (Region region : oldRegions) {
            Material displayItem = region.getRegionKind().getMaterial();
            ClickItem icon = new ClickItem(new ItemStack(displayItem), region.getRegion().getId(),
                    region.replaceVariables(Messages.GUI_TAKEOVER_ITEM_LORE)).addClickAction(new ClickAction() {
                @Override
                public void execute(Player player) {
                    WGRegion selectedWgRegion = region.getRegion();
                    for (UUID oldOwner : selectedWgRegion.getOwners()) {
                        selectedWgRegion.addMember(oldOwner);
                    }
                    region.setOwner(player);
                    oldRegions.remove(region);
                    player.sendMessage(Messages.PREFIX + Messages.REGION_TRANSFER_COMPLETE_MESSAGE);
                    Gui.openOvertakeGUI(player, oldRegions);
                }
            });
            inv.addIcon(icon, position++);
        }

        ClickItem icon = new ClickItem(new ItemStack(Gui.GO_BACK_ITEM), Messages.GUI_CLOSE).addClickAction(new ClickAction() {
            @Override
            public void execute(Player player) {
                player.closeInventory();
            }
        });
        inv.addIcon(icon, invsize - 1);
        inv = Gui.placeFillItems(inv);
        player.openInventory(inv.getInventory());
    }

    public static void openRegionResetWarning(Player player, Region region, Boolean goBack) {
        Gui.openWarning(player, p -> {
                    player.closeInventory();
                    region.userRestore(player);
                }, p -> {
                    if (goBack) {
                        Gui.openRegionOwnerManager(player, region);
                    } else {
                        player.closeInventory();
                    }
                }, Messages.GUI_RESET_REGION_WARNING_NAME,
                new ArrayList<>(), new ArrayList<>());
    }

    public static void openSellWarning(Player player, Region region, boolean noMoney, boolean goBack) {
        Gui.openWarning(player, p -> {
                    player.closeInventory();
                    if (region.getRegion().hasOwner(player.getUniqueId())) {
                        String soldSuccessfullyMessage = Messages.REGION_SOLD_BACK_SUCCESSFULLY;
                        if (noMoney)
                            soldSuccessfullyMessage = soldSuccessfullyMessage.replace("%paybackmoney%", Price.formatPrice(0));
                        soldSuccessfullyMessage = region.replaceVariables(soldSuccessfullyMessage);
                        try {
                            region.userSell(player, noMoney);
                            player.sendMessage(Messages.PREFIX + soldSuccessfullyMessage);
                        } catch (SchematicNotFoundException e) {
                            AdvancedRegionMarket.getInstance().getLogger().log(Level.WARNING, region.replaceVariables(Messages.COULD_NOT_FIND_OR_LOAD_SCHEMATIC_LOG));
                            player.sendMessage(Messages.PREFIX + Messages.SCHEMATIC_NOT_FOUND_ERROR_USER.replace("%regionid%", e.getRegion().getId()));
                        } catch (NotEnoughMoneyException e) {
                            player.sendMessage(Messages.PREFIX + e.getMessage());
                        }

                    } else {
                        throw new InputException(player, Messages.REGION_NOT_OWN);
                    }
                }, p -> {
                    if (goBack) {
                        Gui.openRegionOwnerManager(player, region);
                    } else {
                        player.closeInventory();
                    }
                }, Messages.GUI_USER_SELL_WARNING,
                new ArrayList<>(), new ArrayList<>());
    }

    private static ItemStack getRegionDisplayItem(Region region, List<String> rentLore, List<String> sellLore, List<String> contractLore) {
        String regionDisplayName = Messages.GUI_REGION_ITEM_NAME;
        regionDisplayName = region.replaceVariables(regionDisplayName);
        regionDisplayName = region.getRegionKind().replaceVariables(regionDisplayName);

        ItemStack stack = new ItemStack(region.getRegionKind().getMaterial());
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(regionDisplayName);

        List<String> regionLore = new ArrayList<>();
        if (region instanceof RentRegion) {
            regionLore = new ArrayList<>(rentLore);
        } else if (region instanceof SellRegion) {
            regionLore = new ArrayList<>(sellLore);
        } else if (region instanceof ContractRegion) {
            regionLore = new ArrayList<>(contractLore);
        }
        meta.setLore(region.replaceVariables(regionLore));
        stack.setItemMeta(meta);
        return stack;
    }

    private static boolean isMainPageMultipleItems() {
        FileConfiguration config = Bukkit.getPluginManager().getPlugin("AdvancedRegionMarket").getConfig();

        int itemcounter = 0;

        if (config.getBoolean("GUI.DisplayRegionOwnerButton")) {
            itemcounter++;
        }
        if (config.getBoolean("GUI.DisplayRegionMemberButton")) {
            itemcounter++;
        }
        if (config.getBoolean("GUI.DisplayRegionFinderButton")) {
            itemcounter++;
        }
        return (itemcounter > 1);
    }

    private static GuiInventory placeFillItems(GuiInventory inv) {
        if (Gui.FILL_ITEM != Material.AIR) {
            for (int i = 0; i < inv.getInventory().getSize(); i++) {
                if (inv.getIcon(i) == null) {
                    ClickItem fillIcon = new ClickItem(new ItemStack(Gui.FILL_ITEM), " ").addClickAction(new ClickAction() {
                        @Override
                        public void execute(Player player) {
                            return;
                        }
                    });
                    inv.addIcon(fillIcon, i);
                }
            }
            return inv;
        }
        return inv;
    }

    private static int getPosition(int itemNr, int maxItems) {
        if (maxItems < itemNr) {
            throw new IndexOutOfBoundsException("itemNr does not have to be larger than maxItems");
        }

        if (itemNr == 0) {
            return 4;
        }
        if (itemNr == 1) {
            if (maxItems == 1) return 4;
            if (maxItems == 2) return 2;
            if (maxItems == 3) return 0;
            if (maxItems == 4) return 0;
            if (maxItems == 5) return 0;
            if (maxItems == 6) return 0;
            if (maxItems == 7) return 0;
            if (maxItems == 8) return 0;
            if (maxItems == 9) return 0;
            if (maxItems > 9) return 0;
        } else if (itemNr == 2) {
            if (maxItems == 2) return 6;
            if (maxItems == 3) return 4;
            if (maxItems == 4) return 2;
            if (maxItems == 5) return 2;
            if (maxItems == 6) return 1;
            if (maxItems == 7) return 1;
            if (maxItems == 8) return 1;
            if (maxItems == 9) return 1;
            if (maxItems > 9) return 1;
        } else if (itemNr == 3) {
            if (maxItems == 3) return 8;
            if (maxItems == 4) return 6;
            if (maxItems == 5) return 4;
            if (maxItems == 6) return 3;
            if (maxItems == 7) return 3;
            if (maxItems == 8) return 2;
            if (maxItems == 9) return 2;
            if (maxItems > 9) return 2;
        } else if (itemNr == 4) {
            if (maxItems == 4) return 8;
            if (maxItems == 5) return 6;
            if (maxItems == 6) return 5;
            if (maxItems == 7) return 4;
            if (maxItems == 8) return 3;
            if (maxItems == 9) return 3;
            if (maxItems > 9) return 3;
        } else if (itemNr == 5) {
            if (maxItems == 5) return 8;
            if (maxItems == 6) return 7;
            if (maxItems == 7) return 5;
            if (maxItems == 8) return 5;
            if (maxItems == 9) return 4;
            if (maxItems > 9) return 4;
        } else if (itemNr == 6) {
            if (maxItems == 6) return 8;
            if (maxItems == 7) return 7;
            if (maxItems == 8) return 6;
            if (maxItems == 9) return 5;
            if (maxItems > 9) return 5;
        } else if (itemNr == 7) {
            if (maxItems == 7) return 8;
            if (maxItems == 8) return 7;
            if (maxItems == 9) return 6;
            if (maxItems > 9) return 6;
        } else if (itemNr == 8) {
            if (maxItems == 8) return 8;
            if (maxItems == 9) return 7;
            if (maxItems > 9) return 7;
        } else if (itemNr == 9) {
            if (maxItems == 9) return 8;
            if (maxItems > 9) return 8;
        }

        if (maxItems > 9) {
            maxItems -= 9;
        }
        if (itemNr > 9) {
            itemNr -= 9;
        }
        return getPosition(itemNr, maxItems) + 9;
    }

    public static void setRegionOwnerItem(Material regionOwnerItem) {
        if (regionOwnerItem == null) {
            return;
        }
        REGION_OWNER_ITEM = regionOwnerItem;
    }

    public static void setRegionMemberItem(Material regionMemberItem) {
        if (regionMemberItem == null) {
            return;
        }
        REGION_MEMBER_ITEM = regionMemberItem;
    }

    public static void setRegionFinderItem(Material regionFinderItem) {
        if (regionFinderItem == null) {
            return;
        }
        REGION_FINDER_ITEM = regionFinderItem;
    }

    public static void setFillItem(Material fillItem) {
        if (fillItem == null) {
            return;
        }
        FILL_ITEM = fillItem;
    }

    public static void setContractItem(Material contractItem) {
        if (contractItem == null) {
            return;
        }
        CONTRACT_ITEM = contractItem;
    }

    public static void setFlageditorResetItem(Material flageditorResetItem) {
        if (flageditorResetItem == null) {
            return;
        }
        FLAGEDITOR_RESET_ITEM = flageditorResetItem;
    }

    public static void setGoBackItem(Material goBackItem) {
        if (goBackItem == null) {
            return;
        }
        GO_BACK_ITEM = goBackItem;
    }

    public static void setWarningYesItem(Material warningYesItem) {
        if (warningYesItem == null) {
            return;
        }
        WARNING_YES_ITEM = warningYesItem;
    }

    public static void setWarningNoItem(Material warningNoItem) {
        if (warningNoItem == null) {
            return;
        }
        Gui.WARNING_NO_ITEM = warningNoItem;
    }

    public static void setTpItem(Material tpItem) {
        if (tpItem == null) {
            return;
        }
        TP_ITEM = tpItem;
    }

    public static void setSellRegionItem(Material sellRegionItem) {
        if (sellRegionItem == null) {
            return;
        }
        SELL_REGION_ITEM = sellRegionItem;
    }

    public static void setResetItem(Material resetRegion) {
        if (resetRegion == null) {
            return;
        }
        RESET_ITEM = resetRegion;
    }

    public static void setExtendItem(Material extendItem) {
        if (extendItem == null) {
            return;
        }
        EXTEND_ITEM = extendItem;
    }

    public static void setInfoItem(Material infoItem) {
        if (infoItem == null) {
            return;
        }
        INFO_ITEM = infoItem;
    }

    public static void setPromoteMemberToOwnerItem(Material promoteMemberToOwnerItem) {
        if (promoteMemberToOwnerItem == null) {
            return;
        }
        PROMOTE_MEMBER_TO_OWNER_ITEM = promoteMemberToOwnerItem;
    }

    public static void setRemoveMemberItem(Material removeMemberItem) {
        if (removeMemberItem == null) {
            return;
        }
        REMOVE_MEMBER_ITEM = removeMemberItem;
    }

    public static void setSubregionItem(Material subregionItem) {
        if (subregionItem == null) {
            return;
        }
        SUBREGION_ITEM = subregionItem;
    }

    public static void setDeleteItem(Material deleteItem) {
        if (deleteItem == null) {
            return;
        }
        DELETE_ITEM = deleteItem;
    }

    public static void setTeleportToSignItem(Material teleportToSignItem) {
        if (teleportToSignItem == null) {
            return;
        }
        TELEPORT_TO_SIGN_ITEM = teleportToSignItem;
    }

    public static void setTeleportToRegionItem(Material teleportToRegionItem) {
        if (teleportToRegionItem == null) {
            return;
        }
        TELEPORT_TO_REGION_ITEM = teleportToRegionItem;
    }

    public static void setNextPageItem(Material NextPageItem) {
        if (NextPageItem == null) {
            return;
        }
        NEXT_PAGE_ITEM = NextPageItem;
    }

    public static void setPrevPageItem(Material PrevPageItem) {
        if (PrevPageItem == null) {
            return;
        }
        PREV_PAGE_ITEM = PrevPageItem;
    }

    public static void setHotelSettingItem(Material HotelSettingItem) {
        if (HotelSettingItem == null) {
            return;
        }
        HOTEL_SETTING_ITEM = HotelSettingItem;
    }

    public static void setUnsellItem(Material UnsellItem) {
        if (UnsellItem == null) {
            return;
        }
        UNSELL_ITEM = UnsellItem;
    }

    public static void setFlagRemoveItem(Material flagRemoveItem) {
        if (flagRemoveItem == null) {
            return;
        }
        FLAG_REMOVE_ITEM = flagRemoveItem;
    }

    public static void setFlagSettingSelectedItem(Material flagSettingSelectedItem) {
        if (flagSettingSelectedItem == null) {
            return;
        }
        FLAG_SETTING_SELECTED_ITEM = flagSettingSelectedItem;
    }

    public static void setFlagSettingNotSelectedItem(Material flagSettingNotSelectedItem) {
        if (flagSettingNotSelectedItem == null) {
            return;
        }
        FLAG_SETTING_NOT_SELECTED_ITEM = flagSettingNotSelectedItem;
    }

    public static void setFlagGroupSelectedItem(Material flagGroupSelectedItem) {
        if (flagGroupSelectedItem == null) {
            return;
        }
        FLAG_GROUP_SELECTED_ITEM = flagGroupSelectedItem;
    }

    public static void setFlagGroupNotSelectedItem(Material flagGroupNotSelectedItem) {
        if (flagGroupNotSelectedItem == null) {
            return;
        }
        FLAG_GROUP_NOT_SELECTED_ITEM = flagGroupNotSelectedItem;
    }

    public static void setFlageditorItem(Material flageditorItem) {
        if (flageditorItem == null) {
            return;
        }
        FLAGEDITOR_ITEM = flageditorItem;
    }

    public static void setFlagItem(Material flagItem) {
        if (flagItem == null) {
            return;
        }
        FLAG_ITEM = flagItem;
    }

    public static void setFlagUserInputItem(Material flagUserInputItem) {
        if (flagUserInputItem == null) {
            return;
        }
        FLAG_USER_INPUT_ITEM = flagUserInputItem;
    }

    public static ItemStack getEntityLimtGroupItem(Region region) {
        ItemStack itemStack = new ItemStack(MaterialFinder.getChickenSpawnEgg());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Messages.GUI_ENTITYLIMIT_ITEM_BUTTON);
        List<String> lore = new ArrayList<>(Messages.GUI_ENTITYLIMIT_ITEM_LORE);
        List<String> limitlist = new ArrayList<>();

        String totalstatus = region.getEntityLimitGroup().replaceVariables(Messages.GUI_ENTITYLIMIT_ITEM_INFO_PATTERN, new ArrayList<>(), region.getExtraTotalEntitys());
        if (region.getEntityLimitGroup().getSoftLimit(region.getExtraTotalEntitys()) < region.getEntityLimitGroup().getHardLimit()) {
            totalstatus = totalstatus.replace("%entityextensioninfo%", region.getEntityLimitGroup().replaceVariables(Messages.GUI_ENTITYLIMIT_ITEM_INFO_EXTENSION_INFO, new ArrayList<>(), region.getExtraTotalEntitys()));
        } else {
            totalstatus = totalstatus.replace("%entityextensioninfo%", "");
        }

        limitlist.add(totalstatus);

        for (EntityLimit entityLimit : region.getEntityLimitGroup().getEntityLimits()) {
            String entitystatus = entityLimit.replaceVariables(Messages.GUI_ENTITYLIMIT_ITEM_INFO_PATTERN, new ArrayList<>(), region.getExtraEntityAmount(entityLimit.getLimitableEntityType()));
            if ((entityLimit.getSoftLimit(region.getExtraEntityAmount(entityLimit.getLimitableEntityType())) < entityLimit.getHardLimit()) && !region.isSubregion()) {
                entitystatus = entitystatus.replace("%entityextensioninfo%", entityLimit.replaceVariables(Messages.GUI_ENTITYLIMIT_ITEM_INFO_EXTENSION_INFO, new ArrayList<>(), region.getExtraEntityAmount(entityLimit.getLimitableEntityType())));
            } else {
                entitystatus = entitystatus.replace("%entityextensioninfo%", "");
            }
            limitlist.add(entitystatus);
        }

        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, region.replaceVariables(lore.get(i)));
            if (lore.get(i).contains("%entityinfopattern%")) {
                lore.remove(i);
                lore.addAll(i, limitlist);
            }
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ClickItem[] getFlagSettingItem(Flag flag, Region region, ClickAction afterFlagSetAction) {
        ClickItem[] clickItems;
        if (flag instanceof StateFlag) {
            clickItems = new ClickItem[2];
            FlagSetter fs0 = new FlagSetter(region, flag, null, "allow", afterFlagSetAction);
            clickItems[0] = new ClickItem(fs0.isInputSelected() ? new ItemStack(Gui.FLAG_SETTING_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_SETTING_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_STATEFLAG_ALLOW_BUTTON)).addClickAction(fs0);
            FlagSetter fs1 = new FlagSetter(region, flag, null, "deny", afterFlagSetAction);
            clickItems[1] = new ClickItem(fs1.isInputSelected() ? new ItemStack(Gui.FLAG_SETTING_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_SETTING_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_STATEFLAG_DENY_BUTTON)).addClickAction(fs1);

        } else if (flag instanceof BooleanFlag) {
            clickItems = new ClickItem[2];
            FlagSetter fs0 = new FlagSetter(region, flag, null, "true", afterFlagSetAction);
            clickItems[0] = new ClickItem(fs0.isInputSelected() ? new ItemStack(Gui.FLAG_SETTING_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_SETTING_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_BOOLEANFLAG_TRUE_BUTTON)).addClickAction(fs0);
            FlagSetter fs1 = new FlagSetter(region, flag, null, "false", afterFlagSetAction);
            clickItems[1] = new ClickItem(fs1.isInputSelected() ? new ItemStack(Gui.FLAG_SETTING_SELECTED_ITEM) :
                    new ItemStack(Gui.FLAG_SETTING_NOT_SELECTED_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_BOOLEANFLAG_FALSE_BUTTON)).addClickAction(fs1);

        } else if (flag instanceof StringFlag) {
            clickItems = new ClickItem[1];
            final FlagSetter flagSetter = new FlagSetter(region, flag, null, "", afterFlagSetAction);
            clickItems[0] = new ClickItem(new ItemStack(Gui.FLAG_USER_INPUT_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_STRINGFLAG_SET_MESSAGE_BUTTON)).addClickAction((new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {

                    player.closeInventory();
                    player.sendMessage(region.replaceVariables(Messages.FLAGEDITOR_STRINGFLAG_SET_MESSAGE_INFO));
                    GuiChatInputListener gcil = new GuiChatInputListener(player, (s) -> {
                        flagSetter.setInput(s);
                        flagSetter.execute(player);
                    });
                    Bukkit.getPluginManager().registerEvents(gcil, AdvancedRegionMarket.getInstance());

                }
            }));
        } else if (flag instanceof IntegerFlag) {

            clickItems = new ClickItem[1];
            final FlagSetter flagSetter = new FlagSetter(region, flag, null, "", afterFlagSetAction);
            clickItems[0] = new ClickItem(new ItemStack(Gui.FLAG_USER_INPUT_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_INTEGERFLAG_SET_INTEGER_BUTTON)).addClickAction((new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    player.closeInventory();
                    player.sendMessage(region.replaceVariables(Messages.FLAGEDITOR_INTEGERFLAG_SET_NUMBER_INFO));
                    GuiChatInputListener gcil = new GuiChatInputListener(player, (s) -> {
                        flagSetter.setInput(s);
                        flagSetter.execute(player);
                    });
                    Bukkit.getPluginManager().registerEvents(gcil, AdvancedRegionMarket.getInstance());
                }
            }));
        } else if (flag instanceof DoubleFlag) {

            clickItems = new ClickItem[1];
            final FlagSetter flagSetter = new FlagSetter(region, flag, null, "", afterFlagSetAction);
            clickItems[0] = new ClickItem(new ItemStack(Gui.FLAG_USER_INPUT_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_SET_DOUBLEFLAG_SET_DOUBLE_BUTTON)).addClickAction((new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    player.closeInventory();
                    player.sendMessage(region.replaceVariables(Messages.FLAGEDITOR_DOUBLEFLAG_SET_NUMBER_INFO));
                    GuiChatInputListener gcil = new GuiChatInputListener(player, (s) -> {
                        flagSetter.setInput(s);
                        flagSetter.execute(player);
                    });
                    Bukkit.getPluginManager().registerEvents(gcil, AdvancedRegionMarket.getInstance());
                }
            }));
        } else {
            clickItems = new ClickItem[1];
            final FlagSetter flagSetter = new FlagSetter(region, flag, null, "", afterFlagSetAction);
            clickItems[0] = new ClickItem(new ItemStack(Gui.FLAG_USER_INPUT_ITEM), region.replaceVariables(Messages.GUI_FLAGEDITOR_UNKNOWNFLAG_SET_PROPERTIES_BUTTON)).addClickAction((new ClickAction() {
                @Override
                public void execute(Player player) throws InputException {
                    player.closeInventory();
                    player.sendMessage(region.replaceVariables(Messages.FLAGEDITOR_UNKNOWNFLAG_SET_PROPERTIES_INFO));
                    GuiChatInputListener gcil = new GuiChatInputListener(player, (s) -> {
                        flagSetter.setInput(s);
                        flagSetter.execute(player);
                    });
                    Bukkit.getPluginManager().registerEvents(gcil, AdvancedRegionMarket.getInstance());
                }
            }));
        }
        return clickItems;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GuiInventory) {
            event.setCancelled(true);

            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();

                ItemStack itemStack = event.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    return;
                }

                GuiInventory customHolder = (GuiInventory) event.getView().getTopInventory().getHolder();

                if (customHolder.getSize() <= event.getRawSlot()) {
                    return;
                }

                ClickItem icon = customHolder.getIcon(event.getRawSlot());
                if (icon == null) return;

                for (ClickAction clickAction : icon.getClickActions()) {
                    try {
                        clickAction.execute(player);
                    } catch (InputException inputException) {
                        inputException.sendMessages(Messages.PREFIX);
                    }
                }
            }
        }
    }

    private static class TeleportToRegionClickAction implements ClickAction {
        private Region region;

        TeleportToRegionClickAction(Region region) {
            this.region = region;
        }

        public void execute(Player player) {
            try {
                Teleporter.teleport(player, this.region);
                player.closeInventory();
            } catch (NoSaveLocationException e) {
                player.sendMessage(Messages.PREFIX + this.region.replaceVariables(Messages.TELEPORTER_NO_SAVE_LOCATION_FOUND));
            }
        }
    }

    private static class FlagSetter implements ClickAction {
        private String input;
        private Region region;
        private Flag flag;
        private Flag parentFlag;
        private ClickAction afterFlagSetAction;

        FlagSetter(Region region, Flag flag, Flag parentFlag, String input, ClickAction afterFlagSetAction) {
            this.input = input;
            this.region = region;
            this.flag = flag;
            this.parentFlag = parentFlag;
            this.afterFlagSetAction = afterFlagSetAction;
        }

        public void setInput(String s) {
            this.input = s;
        }

        public boolean isInputSelected() {
            if (this.flag == null) {
                return false;
            }
            try {
                Object settingsObj = getParsedSettingsObject();
                Object regionFlagSetting = region.getRegion().getFlagSetting(flag);

                if (parentFlag == null) {
                    if (regionFlagSetting == settingsObj) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (settingsObj == flag.getDefault()
                            && region.getRegion().getFlagSetting(parentFlag) != null
                            && regionFlagSetting == null) {
                        return true;
                    }
                    if (regionFlagSetting == settingsObj) {
                        return true;
                    }
                    return false;
                }


            } catch (InvalidFlagFormat e) {
                return false;
            }
        }

        private Object getParsedSettingsObject() throws InvalidFlagFormat {
            return AdvancedRegionMarket.getInstance().getWorldGuardInterface().parseFlagInput(flag, region.replaceVariables(this.input));
        }

        @Override
        public void execute(Player player) throws InputException {
            if (parentFlag != null && this.region.getRegion().getFlagSetting(this.parentFlag) == null) {
                throw new InputException(player, Messages.FlAGEDITOR_FLAG_NOT_ACTIVATED);
            }
            try {
                if (flag == null) {
                    throw new InvalidFlagFormat("");
                }
                Object flagSetting = getParsedSettingsObject();
                region.getRegion().setFlag(flag, flagSetting);
                afterFlagSetAction.execute(player);
                player.sendMessage(Messages.PREFIX + region.replaceVariables(Messages.FLAGEDITOR_FLAG_HAS_BEEN_UPDATED));
            } catch (InvalidFlagFormat invalidFlagFormat) {
                String flagname = "";
                if (flag != null) {
                    flagname = flag.getName();
                }
                Bukkit.getLogger().info("Could not modify flag " + flagname + " via player flageditor!");
                throw new InputException(player, Messages.FLAGEDITOR_FLAG_COULD_NOT_BE_MODIFIED.replace("%flag%", flag.getName()));
            }
        }
    }
}