package uvmidnight.conditionoverload;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;


public class ModConditionOverload extends ModifierTrait {

    public ModConditionOverload() {
        super("condition_overload", 0xCCCC00);
        addAspects(new ModifierAspect.DataAspect(this), new ModifierAspect.SingleAspect(this));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        int cappedPotionEffects = ConditionOverload.potCap == -1 ?
                target.getActivePotionEffects().stream().filter((PotionEffect potionEffect) -> potionEffect.getPotion().isBadEffect()).collect(Collectors.toList()).size() :
                Math.max(ConditionOverload.potCap, target.getActivePotionEffects().stream().filter((PotionEffect potionEffect) -> potionEffect.getPotion().isBadEffect()).collect(Collectors.toList()).size());
        cappedPotionEffects += target.isBurning() && ConditionOverload.isFireIncluded ? 1 : 0;
        if (ConditionOverload.isExpo) {
//            System.out.println("Number of negative potion effects: " +  target.ge tActivePotionEffects().stream().filter((PotionEffect potionEffect) -> potionEffect.getPotion().isBadEffect()).collect(Collectors.toList()).size());
//            System.out.println("Damage: " + (float) (damage * Math.min( ConditionOverload.multiCap == -1 ? Integer.MAX_VALUE : ConditionOverload.multiCap,
//                    Math.pow(ConditionOverload.rate,  cappedPotionEffects))));

            return (float) (damage * Math.min( ConditionOverload.multiCap == -1 ? Integer.MAX_VALUE : ConditionOverload.multiCap,
                    Math.pow(ConditionOverload.rate,  cappedPotionEffects)));
        } else {
            return (float) (damage * Math.min( ConditionOverload.multiCap == -1 ? Integer.MAX_VALUE : ConditionOverload.multiCap,
                    (1 + (ConditionOverload.rate- 1)  *  cappedPotionEffects)));
        }
    }

    @SuppressWarnings("deprecated")
    public static class ItemCO extends Item {
        ItemCO() {
            this.setCreativeTab(TinkerRegistry.tabGeneral);
        }
        @SideOnly(Side.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
            tooltip.add(I18n.translateToLocal("item.conditionoverload.wicked_jewel.desc"));
            tooltip.add(I18n.translateToLocal("item.conditionoverload.wicked_jewel.desc2"));

        }
    }
}
