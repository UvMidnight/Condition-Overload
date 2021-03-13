package uvmidnight.conditionoverload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
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
        //go through the potion effects on the target, see which ones are negative
        int cappedPotionEffects = target.getActivePotionEffects().stream().filter(
                (PotionEffect potionEffect) -> (potionEffect.getPotion().isBadEffect() && ConditionOverload.isWhiteOrBlack) != ConditionOverload.anyList.contains(potionEffect.getEffectName())).collect(Collectors.toList()).size();

        //if there is a cap, take it into account.
        if (ConditionOverload.potCap != -1) {
            cappedPotionEffects = Math.min(ConditionOverload.potCap, cappedPotionEffects);
        }

        //factor in if the target is burning
        cappedPotionEffects += target.isBurning() && ConditionOverload.isFireIncluded ? 1 : 0;

        //debug stuff
        if (ConditionOverload.isDebugEnabled && player instanceof EntityPlayer && !player.world.isRemote) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.player.sendMessage(new TextComponentString("Number of potion effects (capped): " + cappedPotionEffects));
            int uncappedPotionEffects = target.getActivePotionEffects().stream().filter((PotionEffect potionEffect) -> potionEffect.getPotion().isBadEffect()).collect(Collectors.toList()).size();
            mc.player.sendMessage(new TextComponentString("Number of uncapped potion effects: " + uncappedPotionEffects));
            mc.player.sendMessage(new TextComponentString("Would be Exponential Damage: " + (float) (damage * Math.min( ConditionOverload.multiCap == -1 ? Integer.MAX_VALUE : ConditionOverload.multiCap,
                    Math.pow(ConditionOverload.rate,  cappedPotionEffects)))));
            mc.player.sendMessage(new TextComponentString("Would be Multiplicative Damage: " + (float) (damage * Math.min( ConditionOverload.multiCap == -1 ? Integer.MAX_VALUE : ConditionOverload.multiCap,
                    (1 + (ConditionOverload.rate- 1)  *  cappedPotionEffects)))));
            mc.player.sendMessage(new TextComponentString("Would be Additive Damage: " + (damage + ConditionOverload.maxAdditive == -1 ? ConditionOverload.additiveNumber * cappedPotionEffects : Math.min((ConditionOverload.additiveNumber * cappedPotionEffects), ConditionOverload.maxAdditive))));
            //display out potion effects
            //I have also forgotten how to use streams. probably for the better.
            for (PotionEffect effect : target.getActivePotionEffects()) {
                mc.player.sendMessage(new TextComponentString("Potion effect: " + effect.getEffectName()));
                mc.player.sendMessage(new TextComponentString("Is Negative: " + effect.getPotion().isBadEffect()));
                if (ConditionOverload.anyList.contains(effect.getEffectName())) {
                    if (ConditionOverload.isWhiteOrBlack) {
                        mc.player.sendMessage(new TextComponentString("Potion is blacklisted and would be ignored."));
                    } else {
                        mc.player.sendMessage(new TextComponentString("Potion is whitelisted and would be allowed."));
                    }
                } else {
                    if (!ConditionOverload.isWhiteOrBlack) {
                        mc.player.sendMessage(new TextComponentString("Potion is not whitelisted and would be ignored."));
                    }
                }
            }
        }

        if (ConditionOverload.additiveEnabled) {
            return damage + ConditionOverload.maxAdditive == -1 ? ConditionOverload.additiveNumber * cappedPotionEffects : Math.min((ConditionOverload.additiveNumber * cappedPotionEffects), ConditionOverload.maxAdditive);
        } else {
            if (ConditionOverload.isExpo) {
                    return (float) (damage * Math.min( ConditionOverload.multiCap == -1 ? Integer.MAX_VALUE : ConditionOverload.multiCap,
                            Math.pow(ConditionOverload.rate,  cappedPotionEffects)));
                } else {
                    return (float) (damage * Math.min( ConditionOverload.multiCap == -1 ? Integer.MAX_VALUE : ConditionOverload.multiCap,
                            (1 + (ConditionOverload.rate- 1)  *  cappedPotionEffects)));
                }
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
