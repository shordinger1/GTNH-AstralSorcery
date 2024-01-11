package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelSpider;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySpider;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSpider<T extends EntitySpider> extends RenderLiving<T> {

    private static final ResourceLocation SPIDER_TEXTURES = new ResourceLocation("textures/entity/spider/spider.png");

    public RenderSpider(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSpider(), 1.0F);
        this.addLayer(new LayerSpiderEyes(this));
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn) {
        return 180.0F;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(T entity) {
        return SPIDER_TEXTURES;
    }
}
