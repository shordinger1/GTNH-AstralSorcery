/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.entity;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import shordinger.astralsorcery.common.entities.EntityLiquidSpark;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderLiquidSpark
 * Created by HellFirePvP
 * Date: 28.10.2017 / 23:58
 */
public class RenderLiquidSpark extends Render<EntityLiquidSpark> {

    protected RenderLiquidSpark(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityLiquidSpark entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityLiquidSpark entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityLiquidSpark> {

        @Override
        public Render<? super EntityLiquidSpark> createRenderFor(RenderManager manager) {
            return new RenderLiquidSpark(manager);
        }

    }
}
