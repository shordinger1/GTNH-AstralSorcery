package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelCow;
import shordinger.wrapper.net.minecraft.entity.passive.EntityCow;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCow extends RenderLiving<EntityCow> {

    private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

    public RenderCow(RenderManager p_i47210_1_) {
        super(p_i47210_1_, new ModelCow(), 0.7F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCow entity) {
        return COW_TEXTURES;
    }
}
