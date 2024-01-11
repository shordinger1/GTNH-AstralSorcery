package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.entity.AbstractClientPlayer;
import shordinger.wrapper.net.minecraft.client.model.ModelElytra;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderLivingBase;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EnumPlayerModelParts;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class LayerElytra implements LayerRenderer<EntityLivingBase> {

    /**
     * The basic Elytra texture.
     */
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
    /**
     * Instance of the player renderer.
     */
    protected final RenderLivingBase<?> renderPlayer;
    /**
     * The model used by the Elytra.
     */
    private final ModelElytra modelElytra = new ModelElytra();

    public LayerElytra(RenderLivingBase<?> p_i47185_1_) {
        this.renderPlayer = p_i47185_1_;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            if (entitylivingbaseIn instanceof AbstractClientPlayer) {
                AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) entitylivingbaseIn;

                if (abstractclientplayer.isPlayerInfoSet() && abstractclientplayer.getLocationElytra() != null) {
                    this.renderPlayer.bindTexture(abstractclientplayer.getLocationElytra());
                } else if (abstractclientplayer.hasPlayerInfo() && abstractclientplayer.getLocationCape() != null
                    && abstractclientplayer.isWearing(EnumPlayerModelParts.CAPE)) {
                    this.renderPlayer.bindTexture(abstractclientplayer.getLocationCape());
                } else {
                    this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
                }
            } else {
                this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            this.modelElytra.setRotationAngles(
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scale,
                entitylivingbaseIn);
            this.modelElytra
                .render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            if (itemstack.isItemEnchanted()) {
                LayerArmorBase.renderEnchantedGlint(
                    this.renderPlayer,
                    entitylivingbaseIn,
                    this.modelElytra,
                    limbSwing,
                    limbSwingAmount,
                    partialTicks,
                    ageInTicks,
                    netHeadYaw,
                    headPitch,
                    scale);
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
