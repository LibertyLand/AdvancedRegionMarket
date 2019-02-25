package net.alex9849.arm.entitylimit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class EntityLimitGroupManager {
    private static List<EntityLimitGroup> entityLimitGroups = new ArrayList<>();
    private static YamlConfiguration entityLimitConf;

    public static EntityLimitGroup getEntityLimitGroup(String name) {
        for(EntityLimitGroup entityLimitGroup : entityLimitGroups) {
            if(entityLimitGroup.getName().equalsIgnoreCase(name)) {
                return entityLimitGroup;
            }
        }
        if(EntityLimitGroup.DEFAULT.getName().equalsIgnoreCase(name) || "default".equalsIgnoreCase(name)) {
            return EntityLimitGroup.DEFAULT;
        }

        if(EntityLimitGroup.SUBREGION.getName().equalsIgnoreCase(name) || "subregion".equalsIgnoreCase(name)) {
            return EntityLimitGroup.SUBREGION;
        }

        return null;
    }

    public static void loadEntityLimits() {
        setFileConf();
        updateConfig();
        if(entityLimitConf.get("EntityLimits") == null) {
            return;
        }
        ConfigurationSection entityLimitsSection = entityLimitConf.getConfigurationSection("EntityLimits");
        List<String> limitnames = new ArrayList<>(entityLimitsSection.getKeys(false));

        for(String limitname : limitnames) {
            if(entityLimitsSection.get(limitname) != null) {
                ConfigurationSection limitSection = entityLimitsSection.getConfigurationSection(limitname);
                entityLimitGroups.add(parseEntityLimitGroup(limitSection, limitname));
            }
        }

        ConfigurationSection entityLimitDEFAULTSection = entityLimitConf.getConfigurationSection("DefaultEntityLimit");
        EntityLimitGroup.setDEFAULT(parseEntityLimitGroup(entityLimitDEFAULTSection, entityLimitDEFAULTSection.getString("name")));

        ConfigurationSection entityLimitSUBREGIONSection = entityLimitConf.getConfigurationSection("DefaultEntityLimit");
        EntityLimitGroup.setSUBREGION(parseEntityLimitGroup(entityLimitSUBREGIONSection, entityLimitSUBREGIONSection.getString("name")));
    }

    public static void saveEntityLimits() {
        boolean writeToCfg = false;

        for(EntityLimitGroup entityLimitGroup : entityLimitGroups) {
            if(entityLimitGroup.needsSave()) {
                saveEntityLimit("EntityLimits." + entityLimitGroup.getName(), entityLimitGroup);
                entityLimitGroup.setSaved();
                writeToCfg = true;
            }
        }
        if(EntityLimitGroup.DEFAULT.needsSave()) {
            saveEntityLimit("DefaultEntityLimit", EntityLimitGroup.DEFAULT);
            EntityLimitGroup.DEFAULT.setSaved();
            writeToCfg = true;
        }
        if(EntityLimitGroup.SUBREGION.needsSave()) {
            saveEntityLimit("SubregionEntityLimit", EntityLimitGroup.SUBREGION);
            EntityLimitGroup.DEFAULT.setSaved();
            writeToCfg = true;
        }

        if(writeToCfg) {
            saveEntityLimitsConf();
        }
    }

    private static void saveEntityLimit(String path, EntityLimitGroup entityLimitGroup) {
        entityLimitConf.set(path, null);
        int totallimit = entityLimitGroup.getTotalLimit();
        if(totallimit == Integer.MAX_VALUE) {
            totallimit = -1;
        }
        entityLimitConf.set(path + ".total", totallimit);
        for(EntityLimit entityLimit : entityLimitGroup.getEntityLimits()) {
            entityLimitConf.set(path + "." + entityLimit.getEntityType(), entityLimit.getAmount());
        }
    }

    public static void add(EntityLimitGroup entityLimitGroup) {
        entityLimitGroups.add(entityLimitGroup);
        saveEntityLimit("EntityLimits." + entityLimitGroup.getName() , entityLimitGroup);
        saveEntityLimitsConf();
    }

    public static void remove(EntityLimitGroup entityLimitGroup) {
        entityLimitGroups.remove(entityLimitGroup);
        entityLimitConf.set("EntityLimits." + entityLimitGroup.getName(), null);
        saveEntityLimitsConf();
    }

    private static EntityLimitGroup parseEntityLimitGroup(ConfigurationSection section, String name) {
        List<String> entityNames = new ArrayList<>(section.getKeys(false));
        List<EntityLimit> entityLimits = new ArrayList<>();
        int totalMax = Integer.MAX_VALUE;
        for(String entityName : entityNames) {
            if(entityName.equalsIgnoreCase("total")) {
                totalMax = section.getInt(entityName);
            } else {
                try {
                    EntityType entityType = EntityType.valueOf(entityName);
                    if(entityType != null) {
                        int maxLimit = section.getInt(entityName);
                        entityLimits.add(new EntityLimit(entityType, maxLimit));
                    }
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().log(Level.WARNING, "[AdvancedRegionMarket] Could not find EntityType " + entityName + " for EntityLimitGroup " + name + "! Ignoring it");
                }
            }
        }
        if(totalMax == -1) {
            totalMax = Integer.MAX_VALUE;
        }
        return new EntityLimitGroup(entityLimits, totalMax, name);
    }

    public static void reset() {
        entityLimitGroups = new ArrayList<>();
    }

    public static void generatedefaultConfig(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdvancedRegionMarket");
        File pluginfolder = Bukkit.getPluginManager().getPlugin("AdvancedRegionMarket").getDataFolder();
        File filedic = new File(pluginfolder + "/entitylimits.yml");
        if(!filedic.exists()){
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                InputStream stream = plugin.getResource("entitylimits.yml");
                byte[] buffer = new byte[stream.available()];
                stream.read(buffer);
                OutputStream output = new FileOutputStream(filedic);
                output.write(buffer);
                output.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setFileConf(){
        generatedefaultConfig();
        File pluginfolder = Bukkit.getPluginManager().getPlugin("AdvancedRegionMarket").getDataFolder();
        File regionsconfigdic = new File(pluginfolder + "/entitylimits.yml");
        EntityLimitGroupManager.entityLimitConf = YamlConfiguration.loadConfiguration(regionsconfigdic);
    }

    private static void updateConfig() {
        if(entityLimitConf.get("EntityLimits") == null) {
            return;
        }
        List<String> limts = new ArrayList<>(entityLimitConf.getConfigurationSection("EntityLimits").getKeys(false));

        for(String limitName : limts) {
            entityLimitConf.addDefault("EntityLimits." + limitName + ".total", -1);
        }
        entityLimitConf.addDefault("DefaultEntityLimit.total", -1);
        entityLimitConf.addDefault("DefaultEntityLimit.name", "DefaultLimit");
        entityLimitConf.addDefault("SubregionEntityLimit.total", -1);
        entityLimitConf.addDefault("SubregionEntityLimit.name", "SubregionLimit");

        entityLimitConf.options().copyDefaults(true);
        saveEntityLimitsConf();
        entityLimitConf.options().copyDefaults(false);
    }

    private static void saveEntityLimitsConf() {
        File pluginfolder = Bukkit.getPluginManager().getPlugin("AdvancedRegionMarket").getDataFolder();
        File regionsconfigdic = new File(pluginfolder + "/entitylimits.yml");
        try {
            entityLimitConf.save(regionsconfigdic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<EntityLimitGroup> getEntityLimitGroups() {
        return entityLimitGroups;
    }

    public static List<String> tabCompleteEntityLimitGroups(String name) {
        List<String> returnme = new ArrayList<>();

        for(EntityLimitGroup entityLimitGroup : entityLimitGroups) {
            if(entityLimitGroup.getName().startsWith(name)) {
                returnme.add(entityLimitGroup.getName());
            }
        }

        if("default".startsWith(name)) {
            returnme.add("Default");
        }

        if("subregion".startsWith(name)) {
            returnme.add("Subregion");
        }

        return returnme;
    }
}
