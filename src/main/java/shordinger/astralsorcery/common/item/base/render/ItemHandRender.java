/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base.render;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandRender
 * Created by HellFirePvP
 * Date: 06.02.2017 / 23:21
 */
public interface ItemHandRender {

    @SideOnly(Side.CLIENT)
    public void onRenderWhileInHand(ItemStack stack, EnumHand hand, float pTicks);

    @Nullable
    default public RayTraceResult getLookBlock(Entity e, boolean stopTraceOnLiquids,
                                               boolean ignoreBlockWithoutBoundingBox, double range) {
        float pitch = e.rotationPitch;
        float yaw = e.rotationYaw;
        Vec3d entityVec = new Vec3d(e.posX, e.posY + e.getEyeHeight(), e.posZ);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3d vec3d1 = entityVec.addVector((double) f6 * range, (double) f5 * range, (double) f7 * range);
        RayTraceResult rtr = e.getEntityWorld()
            .rayTraceBlocks(entityVec, vec3d1, stopTraceOnLiquids, ignoreBlockWithoutBoundingBox, false);
        if (rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK) {
            return null;
        }
        return rtr;
    }

}
