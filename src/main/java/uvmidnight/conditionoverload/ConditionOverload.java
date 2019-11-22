package uvmidnight.conditionoverload;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.io.File;

@Mod(modid = ConditionOverload.MODID, version = ConditionOverload.VERSION, name = ConditionOverload.NAME, dependencies = "after:tconstruct", acceptedMinecraftVersions = "[1.12, 1.13)")
public class ConditionOverload {
    public static final String MODID = "conditionoverload";
    public static final String NAME = "Condition: Overload";
    public static final String VERSION = "1.0.0";

    public static Configuration config;

    public static Logger logger = LogManager.getLogger(MODID);

    public static double rate = 1.30;
    public static boolean isExpo = true;
    public static int potCap = -1;
    public static int multiCap = -1;
    public static boolean isFireIncluded;

    public static boolean addativeEnabled;
    public static float addativeNumber;


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
        rate = cfg.getFloat("percent_multi", "General Config", 1.3F, 1, 100, "Percent multiplication per negative potion effect. Note that superheat, which gives a bonus for targets set on fire, is a value of 1.35.");
        potCap = cfg.getInt("potion_cap", "General Config", -1, -1, Integer.MAX_VALUE, "Number of potion effects that will be calculated at max. Set to -1 to disable the cap.");
        isExpo = cfg.getBoolean("is_exponential", "General Config", true, "If Condition Overload will exponentially rise in power as more potion effects are applied. Setting this to false will cause condition overload to be linear in its scaling. NOTE THAT SETTING ADDITIVE MODE ON WILL OVERRIDE THIS");
        multiCap = cfg.getInt("multiplier_cap", "General Config", -1, -1, Integer.MAX_VALUE, "The cap on the percent that Condition: Overload will add. For example, a value of 1 would be 1%. Set to -1 to disable the cap.");
        isFireIncluded = cfg.getBoolean("is_fire_included", "General Config", true, "If this is set to true, fire will count as an effect for Condition Overload.");

        addativeEnabled = cfg.getBoolean("is_addative" ,"Additive Mode", false, "If Condition Overload adds on flat damage per potion effect on the enemy, rather then multiplying the damage of the weapon.");
        addativeNumber = cfg.getFloat("flat_bonus_per_potion_effect", "Additive Mode", 1.5f, 0, 10000, "How much damage Condition Overload adds in additive mode per potion effect. Note that the cap of potions still applies.");
    }
}
