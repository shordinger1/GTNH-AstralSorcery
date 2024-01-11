package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelSnowMan;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerSnowmanHead;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySnowman;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSnowMan extends RenderLiving<EntitySnowman> {

    private static final ResourceLocation SNOW_MAN_TEXTURES = new ResourceLocation("textures/entity/snowman.png");

    public RenderSnowMan(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSnowMan(), 0.5F);
        this.addLayer(new LayerSnowmanHead(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySnowman entity) {
        return SNOW_MAN_TEXTURES;
    }

    public ModelSnowMan getMainModel() {
        return (ModelSnowMan) super.getMainModel();
    }
}
