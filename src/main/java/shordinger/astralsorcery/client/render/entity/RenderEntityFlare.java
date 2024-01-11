/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.entity;

import shordinger.astralsorcery.common.entities.EntityFlare;
import shordinger.wrapper.net.minecraft.client.renderer.entity.Render;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderManager;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityFlare
 * Created by HellFirePvP
 * Date: 07.02.2017 / 12:21
 */
public class RenderEntityFlare extends Render<EntityFlare> {

    public RenderEntityFlare(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityFlare entity, double x, double y, double z, float entityYaw, float partialTicks) {}

    @Override
    protected ResourceLocation getEntityTexture(EntityFlare entity) {
        return null;
    }


    public static class Factory implements IRenderFactory<EntityFlare> {

        @Override
        public Render<? super EntityFlare> createRenderFor(RenderManager manager) {
            return new RenderEntityFlare(manager);
        }

    }
}
