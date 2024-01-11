package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.monster.EntityCaveSpider;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCaveSpider extends RenderSpider<EntityCaveSpider> {

    private static final ResourceLocation CAVE_SPIDER_TEXTURES = new ResourceLocation(
        "textures/entity/spider/cave_spider.png");

    public RenderCaveSpider(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize *= 0.7F;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityCaveSpider entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCaveSpider entity) {
        return CAVE_SPIDER_TEXTURES;
    }
}
