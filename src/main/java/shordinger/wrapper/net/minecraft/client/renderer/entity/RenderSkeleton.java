package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelSkeleton;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import shordinger.wrapper.net.minecraft.entity.monster.AbstractSkeleton;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSkeleton extends RenderBiped<AbstractSkeleton> {

    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation(
        "textures/entity/skeleton/skeleton.png");

    public RenderSkeleton(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSkeleton(), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this) {

            protected void initArmor() {
                this.modelLeggings = new ModelSkeleton(0.5F, true);
                this.modelArmor = new ModelSkeleton(1.0F, true);
            }
        });
    }

    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
        return SKELETON_TEXTURES;
    }
}
