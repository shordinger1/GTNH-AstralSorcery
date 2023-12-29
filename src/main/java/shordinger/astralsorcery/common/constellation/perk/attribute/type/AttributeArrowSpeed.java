/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeArrowSpeed
 * Created by HellFirePvP
 * Date: 14.07.2018 / 13:39
 */
public class AttributeArrowSpeed extends PerkAttributeType {

    public AttributeArrowSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_PROJ_SPEED, true);
    }

    @SubscribeEvent
    public void onArrowFire(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityArrow arrow) {
            if (arrow.shootingEntity instanceof EntityPlayer player) {
                Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                Vector3 motion = new Vector3(arrow.motionX, arrow.motionY, arrow.motionZ);
                float mul = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
                mul = AttributeEvent.postProcessModded(player, this, mul);
                motion.multiply(mul);
                arrow.motionX = motion.getX();
                arrow.motionY = motion.getY();
                arrow.motionZ = motion.getZ();
            }
        }
    }

}
