package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelCow;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import shordinger.wrapper.net.minecraft.entity.passive.EntityMooshroom;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderMooshroom extends RenderLiving<EntityMooshroom> {

    private static final ResourceLocation MOOSHROOM_TEXTURES = new ResourceLocation(
        "textures/entity/cow/mooshroom.png");

    public RenderMooshroom(RenderManager p_i47200_1_) {
        super(p_i47200_1_, new ModelCow(), 0.7F);
        this.addLayer(new LayerMooshroomMushroom(this));
    }

    public ModelCow getMainModel() {
        return (ModelCow) super.getMainModel();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityMooshroom entity) {
        return MOOSHROOM_TEXTURES;
    }
}
