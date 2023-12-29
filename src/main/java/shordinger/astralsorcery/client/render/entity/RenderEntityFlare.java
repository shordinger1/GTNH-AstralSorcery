/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import shordinger.astralsorcery.common.entities.EntityFlare;

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
    public void doRender(EntityFlare entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

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
