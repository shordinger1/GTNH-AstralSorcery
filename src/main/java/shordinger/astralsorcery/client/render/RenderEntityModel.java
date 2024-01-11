/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render;

import shordinger.wrapper.net.minecraft.client.renderer.entity.Render;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderManager;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityModel
 * Created by HellFirePvP
 * Date: 16.09.2016 / 15:46
 */
public abstract class RenderEntityModel<T extends Entity> extends Render<T> {

    protected RenderEntityModel(RenderManager renderManager) {
        super(renderManager);
    }

    public abstract void doModelRender(T entity, float pTicks);

    public static abstract class RenderEntityModelFactory<T extends Entity> implements IRenderFactory<T> {

        public abstract RenderEntityModel<? super T> createRenderFor(RenderManager manager);

    }

}
