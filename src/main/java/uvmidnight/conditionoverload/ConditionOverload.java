package uvmidnight.conditionoverload;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@Mod(modid = ConditionOverload.MODID, version = ConditionOverload.VERSION, name = ConditionOverload.NAME, dependencies = "after:tconstruct", acceptedMinecraftVersions = "[1.12, 1.13)")
public class ConditionOverload {
    public static final String MODID = "conditionoverload";
    public static final String NAME = "Condition: Overload";
    public static final String VERSION = "1.2.0";

    public static Configuration config;

    public static Logger logger = LogManager.getLogger(MODID);

    public static double rate = 1.30;
    public static boolean isExpo = true;
    public static int potCap = -1;
    public static int multiCap = -1;
    public static boolean isFireIncluded;

    public static boolean additiveEnabled;
    public static float additiveNumber;
    public static float maxAdditive;

    public static boolean isDebugEnabled;
    public static ArrayList<String> anyList = new ArrayList<>();
    public static boolean isWhiteOrBlack;

    public static Modifier modConditionOverload = new ModConditionOverload();
    public static Item coItem;


    @Mod.Instance(MODID)
    public static ConditionOverload instance;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e) {
        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "conditionoverload.cfg"));
        Configuration cfg = ConditionOverload.config;
        try {
            cfg.load();
            initGeneralConfig(cfg);
        } catch (Exception exception) {
            ConditionOverload.logger.warn("Problem loading config file!", exception);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg) {
        cfg.addCustomCategoryComment("General Config", "Config for Condition: Overload");
        rate = cfg.getFloat("percent_multi", "General Config", 1.3F, 1, 100, "Direct multiplication per negative potion effect. For comparisons sake, superheat, which gives a bonus for targets set on fire, is a value of 1.35.");
        potCap = cfg.getInt("potion_cap", "General Config", -1, -1, Integer.MAX_VALUE, "Number of potion effects that will be calculated at max. Set to -1 to disable the cap. Still affects additive mode.");
        isExpo = cfg.getBoolean("is_exponential", "General Config", true, "If Condition Overload will exponentially rise in power as more potion effects are applied. Setting this to false will cause condition overload to be multiplicative in its scaling. NOTE THAT SETTING ADDITIVE MODE ON WILL OVERRIDE THIS. Formula for damage in exponential mode is [weaponBaseDamage * (flat_bonus_per_potion_effect ^ numNegPotions)]. Formula for damage in exponential mode is [weaponBaseDamage * (flat_bonus_per_potion_effect * numNegPotions)].");
        multiCap = cfg.getInt("multiplier_cap", "General Config", -1, -1, Integer.MAX_VALUE, "The cap on the percent that Condition: Overload will add. For example, a value of 2 would be 200% of the original damage. Set to -1 to disable the cap. Does not affect additive mode.");
        isFireIncluded = cfg.getBoolean("is_fire_included", "General Config", true, "If this is set to true, fire will count as an effect for Condition Overload.");
        isDebugEnabled = cfg.getBoolean("debug_mode", "General Config", false, "If debug mode is enabled. Prints out information about potions on the target.");

        isWhiteOrBlack = cfg.getBoolean("whitelist_or_blacklist", "General Config", true, "true = blacklist, false = whitelist");
        anyList.addAll(Arrays.asList(cfg.getStringList("blacklist", "General Config", new String[] {""}, "negative potion names that will be ignored by the modifier. names can be determined with various mods or debug mode.")));

        cfg.addCustomCategoryComment("Additive Mode", "Additive mode adds a flat amount of damage per stack of potion. MUST BE ENABLED IN CONFIG. Formula for damage is [weaponBaseDamage + flat_bonus_per_potion_effect * numNegPotions]");
        additiveEnabled = cfg.getBoolean("is_additive" ,"Additive Mode", false, "If Condition Overload adds on flat damage per potion effect on the enemy, rather then multiplying the damage of the weapon.");
        additiveNumber = cfg.getFloat("flat_bonus_per_potion_effect", "Additive Mode", 1.5f, 0, 10000, "How much damage Condition Overload adds in additive mode per potion effect. Note that the cap of potions still applies.");
        maxAdditive = cfg.getFloat("max_damage_added_additive", "Additive Mode", -1, -1, Float.MAX_VALUE, "What the bonus damage that additive mode can do. Set to -1 to disable.");
    }
}
