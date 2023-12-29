/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.entity;

import net.minecraft.client.Minecraft;
import com.gtnewhorizons.modularui.api.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.entities.EntityItemHighlighted;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityItemHighlight
 * Created by HellFirePvP
 * Date: 13.05.2016 / 13:59
 */
public class RenderEntityItemHighlight extends Render<EntityItemHighlighted> {

    private final RenderEntityItem renderItem;

    public RenderEntityItemHighlight(RenderManager renderManager) {
        super(renderManager);
        renderItem = new RenderEntityItem(
            renderManager,
            Minecraft.getMinecraft()
                .getRenderItem());
    }

    @Override
    public void doRender(EntityItemHighlighted entity, double x, double y, double z, float entityYaw,
                         float partialTicks) {
        RenderingUtils
            .renderLightRayEffects(x, y + 0.5, z, entity.getHighlightColor(), 16024L, entity.getAge(), 16, 20, 5);

        GlStateManager.pushMatrix();
        ItemStack stack = entity.getItem();
        if (!stack.isEmpty()) {
            EntityItem ei = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack);
            ei.age = entity.getAge();
            ei.hoverStart = entity.hoverStart;
            if (RenderingUtils.itemPhysics_fieldSkipRenderHook != null) {
                try {
                    RenderingUtils.itemPhysics_fieldSkipRenderHook.set(ei, true);
                } catch (Exception ignored) {
                }
            }
            renderItem.doRender(ei, x, y, z, entityYaw, partialTicks);
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityItemHighlighted entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityItemHighlighted> {

        @Override
        public Render<? super EntityItemHighlighted> createRenderFor(RenderManager manager) {
            return new RenderEntityItemHighlight(manager);
        }

    }

}
