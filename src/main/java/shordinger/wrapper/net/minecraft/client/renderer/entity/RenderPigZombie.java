package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelZombie;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import shordinger.wrapper.net.minecraft.entity.monster.EntityPigZombie;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderPigZombie extends RenderBiped<EntityPigZombie> {

    private static final ResourceLocation ZOMBIE_PIGMAN_TEXTURE = new ResourceLocation(
        "textures/entity/zombie_pigman.png");

    public RenderPigZombie(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelZombie(), 0.5F);
        this.addLayer(new LayerBipedArmor(this) {

            protected void initArmor() {
                this.modelLeggings = new ModelZombie(0.5F, true);
                this.modelArmor = new ModelZombie(1.0F, true);
            }
        });
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityPigZombie entity) {
        return ZOMBIE_PIGMAN_TEXTURE;
    }
}
