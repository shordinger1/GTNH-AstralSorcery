package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelBlaze;
import shordinger.wrapper.net.minecraft.entity.monster.EntityBlaze;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderBlaze extends RenderLiving<EntityBlaze> {

    private static final ResourceLocation BLAZE_TEXTURES = new ResourceLocation("textures/entity/blaze.png");

    public RenderBlaze(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBlaze(), 0.5F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBlaze entity) {
        return BLAZE_TEXTURES;
    }
}
