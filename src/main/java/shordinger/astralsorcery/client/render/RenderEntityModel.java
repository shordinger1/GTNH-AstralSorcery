/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;


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
