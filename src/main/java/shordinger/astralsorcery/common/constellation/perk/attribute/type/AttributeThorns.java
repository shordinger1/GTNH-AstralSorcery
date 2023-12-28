/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.constellation.perk.attribute.modifier.AttributeModifierThorns;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.util.DamageUtil;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeThorns
 * Created by HellFirePvP
 * Date: 11.08.2018 / 18:24
 */
public class AttributeThorns extends PerkAttributeType {

    public AttributeThorns() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_THORNS);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        return new AttributeModifierThorns(getTypeString(), mode, modifier);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        PlayerProgress prog = ResearchManager.getProgress(player, side);

        float reflectAmount = PerkAttributeHelper.getOrCreateMap(player, side)
            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_THORNS, 0F);
        reflectAmount = AttributeEvent.postProcessModded(player, this, reflectAmount);
        reflectAmount /= 100.0F;
        if (reflectAmount <= 0) {
            return;
        }
        reflectAmount = MathHelper.clamp(reflectAmount, 0F, 1F);

        DamageSource source = event.getSource();
        EntityLivingBase reflectTarget = null;
        if (source.getImmediateSource() != null && source.getImmediateSource() instanceof EntityLivingBase
            && !source.getImmediateSource().isDead) {
            reflectTarget = (EntityLivingBase) source.getImmediateSource();
        }

        if (reflectTarget == null && AttributeEvent.postProcessModded(
            player,
            this,
            PerkAttributeHelper.getOrCreateMap(player, side)
                .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_THORNS_RANGED))
            > 1) {
            if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityLivingBase
                && !source.getTrueSource().isDead) {
                reflectTarget = (EntityLivingBase) source.getTrueSource();
            }
        }

        if (reflectTarget != null) {
            float dmgReflected = event.getAmount() * reflectAmount;
            if (dmgReflected > 0 && !event.getEntityLiving()
                .equals(reflectTarget)) {
                if (MiscUtils.canPlayerAttackServer(event.getEntityLiving(), reflectTarget)) {
                    DamageUtil.attackEntityFrom(reflectTarget, CommonProxy.dmgSourceReflect, dmgReflected, player);
                }
            }
        }
    }

}
