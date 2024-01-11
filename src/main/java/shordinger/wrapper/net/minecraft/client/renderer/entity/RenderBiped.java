package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelBiped;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerElytra;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderBiped<T extends EntityLiving> extends RenderLiving<T> {

    private static final ResourceLocation DEFAULT_RES_LOC = new ResourceLocation("textures/entity/steve.png");

    public RenderBiped(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize) {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.addLayer(new LayerCustomHead(modelBipedIn.bipedHead));
        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerHeldItem(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(T entity) {
        return DEFAULT_RES_LOC;
    }

    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0F, 0.1875F, 0.0F);
    }
}
