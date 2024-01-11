package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;

@SideOnly(Side.CLIENT)
public interface LayerRenderer<E extends EntityLivingBase> {

    void doRenderLayer(E entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale);

    boolean shouldCombineTextures();
}
