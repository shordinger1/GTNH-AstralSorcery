package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelLlama;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerLlamaDecor;
import shordinger.wrapper.net.minecraft.entity.passive.EntityLlama;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderLlama extends RenderLiving<EntityLlama> {

    private static final ResourceLocation[] LLAMA_TEXTURES = new ResourceLocation[]{
        new ResourceLocation("textures/entity/llama/llama_creamy.png"),
        new ResourceLocation("textures/entity/llama/llama_white.png"),
        new ResourceLocation("textures/entity/llama/llama_brown.png"),
        new ResourceLocation("textures/entity/llama/llama_gray.png")};

    public RenderLlama(RenderManager p_i47203_1_) {
        super(p_i47203_1_, new ModelLlama(0.0F), 0.7F);
        this.addLayer(new LayerLlamaDecor(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityLlama entity) {
        return LLAMA_TEXTURES[entity.getVariant()];
    }
}
