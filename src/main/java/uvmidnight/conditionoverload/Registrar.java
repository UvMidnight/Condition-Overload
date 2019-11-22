package uvmidnight.conditionoverload;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static uvmidnight.conditionoverload.ConditionOverload.coItem;
import static uvmidnight.conditionoverload.ConditionOverload.modConditionOverload;

@Mod.EventBusSubscriber
public class Registrar {
    @SubscribeEvent
    public static void initItems(RegistryEvent.Register<Item> event) {
//        modConditionOverload = registerModifier(new ModConditionOverload());
        coItem = new ModConditionOverload.ItemCO();
        coItem.setRegistryName("wicked_jewel");
        coItem.setTranslationKey("wicked_jewel");
        modConditionOverload.addItem(coItem);
        event.getRegistry().register(coItem);
    }
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent evt) {
        initModel(coItem);
    }
    @SideOnly(Side.CLIENT)
    private static void initModel(Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
    }

}
