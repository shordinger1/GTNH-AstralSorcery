package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelPig;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerSaddle;
import shordinger.wrapper.net.minecraft.entity.passive.EntityPig;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderPig extends RenderLiving<EntityPig> {

    private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");

    public RenderPig(RenderManager p_i47198_1_) {
        super(p_i47198_1_, new ModelPig(), 0.7F);
        this.addLayer(new LayerSaddle(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityPig entity) {
        return PIG_TEXTURES;
    }
}
