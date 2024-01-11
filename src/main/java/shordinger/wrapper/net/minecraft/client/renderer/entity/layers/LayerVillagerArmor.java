package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelZombieVillager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderLivingBase;

@SideOnly(Side.CLIENT)
public class LayerVillagerArmor extends LayerBipedArmor {

    public LayerVillagerArmor(RenderLivingBase<?> rendererIn) {
        super(rendererIn);
    }

    protected void initArmor() {
        this.modelLeggings = new ModelZombieVillager(0.5F, 0.0F, true);
        this.modelArmor = new ModelZombieVillager(1.0F, 0.0F, true);
    }
}
