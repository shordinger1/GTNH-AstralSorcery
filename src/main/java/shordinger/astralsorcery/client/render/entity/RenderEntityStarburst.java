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

import shordinger.astralsorcery.common.entities.EntityStarburst;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityStarburst
 * Created by HellFirePvP
 * Date: 12.03.2017 / 10:51
 */
public class RenderEntityStarburst extends Render<EntityStarburst> {

    public RenderEntityStarburst(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityStarburst entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityStarburst entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityStarburst> {

        @Override
        public Render<EntityStarburst> createRenderFor(RenderManager manager) {
            return new RenderEntityStarburst(manager);
        }

    }

}
