package shordinger.wrapper.net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.gui.FontRenderer;
import shordinger.wrapper.net.minecraft.client.renderer.EntityRenderer;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.OpenGlHelper;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureManager;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public abstract class TileEntitySpecialRenderer<T extends TileEntity> {

    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{
        new ResourceLocation("textures/blocks/destroy_stage_0.png"),
        new ResourceLocation("textures/blocks/destroy_stage_1.png"),
        new ResourceLocation("textures/blocks/destroy_stage_2.png"),
        new ResourceLocation("textures/blocks/destroy_stage_3.png"),
        new ResourceLocation("textures/blocks/destroy_stage_4.png"),
        new ResourceLocation("textures/blocks/destroy_stage_5.png"),
        new ResourceLocation("textures/blocks/destroy_stage_6.png"),
        new ResourceLocation("textures/blocks/destroy_stage_7.png"),
        new ResourceLocation("textures/blocks/destroy_stage_8.png"),
        new ResourceLocation("textures/blocks/destroy_stage_9.png")};
    protected TileEntityRendererDispatcher rendererDispatcher;

    public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ITextComponent itextcomponent = te.getDisplayName();

        if (itextcomponent != null && this.rendererDispatcher.cameraHitResult != null
            && te.getPos()
            .equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
            this.setLightmapDisabled(true);
            this.drawNameplate(te, itextcomponent.getFormattedText(), x, y, z, 12);
            this.setLightmapDisabled(false);
        }
    }

    /**
     * Sets whether to use the light map when rendering. Disabling this allows rendering ignoring lighting, which can be
     * useful for floating text, e.g.
     */
    protected void setLightmapDisabled(boolean disabled) {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);

        if (disabled) {
            GlStateManager.disableTexture2D();
        } else {
            GlStateManager.enableTexture2D();
        }

        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    protected void bindTexture(ResourceLocation location) {
        TextureManager texturemanager = this.rendererDispatcher.renderEngine;

        if (texturemanager != null) {
            texturemanager.bindTexture(location);
        }
    }

    protected World getWorld() {
        return this.rendererDispatcher.world;
    }

    public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
        this.rendererDispatcher = rendererDispatcherIn;
    }

    public FontRenderer getFontRenderer() {
        return this.rendererDispatcher.getFontRenderer();
    }

    public boolean isGlobalRenderer(T te) {
        return false;
    }

    public void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage,
                                     float partial, net.minecraft.client.renderer.BufferBuilder buffer) {
    }

    protected void drawNameplate(T te, String str, double x, double y, double z, int maxDistance) {
        Entity entity = this.rendererDispatcher.entity;
        double d0 = te.getDistanceSq(entity.posX, entity.posY, entity.posZ);

        if (d0 <= (double) (maxDistance * maxDistance)) {
            float f = this.rendererDispatcher.entityYaw;
            float f1 = this.rendererDispatcher.entityPitch;
            boolean flag = false;
            EntityRenderer.drawNameplate(
                this.getFontRenderer(),
                str,
                (float) x + 0.5F,
                (float) y + 1.5F,
                (float) z + 0.5F,
                0,
                f,
                f1,
                false,
                false);
        }
    }
}
