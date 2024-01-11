package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelSilverfish;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySilverfish;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSilverfish extends RenderLiving<EntitySilverfish> {

    private static final ResourceLocation SILVERFISH_TEXTURES = new ResourceLocation("textures/entity/silverfish.png");

    public RenderSilverfish(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSilverfish(), 0.3F);
    }

    protected float getDeathMaxRotation(EntitySilverfish entityLivingBaseIn) {
        return 180.0F;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySilverfish entity) {
        return SILVERFISH_TEXTURES;
    }
}
