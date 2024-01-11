/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.tile;

import java.awt.*;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.models.obj.OBJModelLibrary;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.item.IItemRenderer;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.base.PtEffectCorruptedCelestialCrystal;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.item.block.ItemCollectorCrystal;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.GLAllocation;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.RenderHelper;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:42
 */
public class TESRCollectorCrystal extends TileEntitySpecialRenderer<TileCollectorCrystal> implements IItemRenderer {

    private static final BindableResource texWhite = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_white");
    private static final BindableResource texBlue = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_blue");
    private static final BindableResource texRed = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_red");

    private static int dlCrystal = -1;

    @Override
    public void render(TileCollectorCrystal te, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {
        UUID playerUUID = te.getPlayerReference();

        BlockCollectorCrystalBase.CollectorCrystalType type = te.getType();
        if (te.doesSeeSky()) {
            long sBase = 1553015L;
            sBase ^= (long) te.getPos()
                .getX();
            sBase ^= (long) te.getPos()
                .getY();
            sBase ^= (long) te.getPos()
                .getZ();
            Color c = type == null ? BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor
                : type.displayColor;
            if (te.getType() == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL && playerUUID != null
                && MiscUtils.contains(
                PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerUUID),
                pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                c = Color.RED;
            }
            if (te.isEnhanced()) {
                c = te.getConstellation() != null ? te.getConstellation()
                    .getConstellationColor() : c;
                RenderingUtils.renderLightRayEffects(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    c,
                    sBase,
                    ClientScheduler.getClientTick(),
                    20,
                    1.4F,
                    50,
                    25);
                RenderingUtils.renderLightRayEffects(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    Color.WHITE,
                    sBase,
                    ClientScheduler.getClientTick(),
                    40,
                    2,
                    15,
                    15);
            } else {
                RenderingUtils.renderLightRayEffects(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    c,
                    sBase,
                    ClientScheduler.getClientTick(),
                    20,
                    50,
                    25);
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        renderCrystal(playerUUID, type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL, true);
        GlStateManager.popMatrix();
    }

    public static void renderCrystal(@Nullable UUID playerUUID, boolean isCelestial, boolean bounce) {
        GlStateManager.pushMatrix();
        if (bounce) {
            int t = (int) (Minecraft.getMinecraft().world.getTotalWorldTime() & 255);
            float perc = (256 - t) / 256F;
            perc = MathHelper.cos((float) (perc * 2 * Math.PI));
            GlStateManager.translate(0, 0.03 * perc, 0);
        }
        TextureHelper.refreshTextureBindState();
        RenderHelper.disableStandardItemLighting();
        if (isCelestial) {
            if (playerUUID != null && MiscUtils.contains(
                PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerUUID),
                pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                renderTile(texRed);
            } else {
                renderTile(texBlue);
            }
        } else {
            renderTile(texWhite);
        }
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private static void renderTile(BindableResource tex) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.13F, 0.13F, 0.13F);
        tex.bind();
        if (dlCrystal == -1) {
            dlCrystal = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(dlCrystal, GL11.GL_COMPILE);
            OBJModelLibrary.bigCrystal.renderAll(true);
            GlStateManager.glEndList();
        }
        GlStateManager.callList(dlCrystal);

        GlStateManager.popMatrix();
    }

    @Override
    public void render(ItemStack stack) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0, 0.5);
        RenderHelper.disableStandardItemLighting();
        BlockCollectorCrystalBase.CollectorCrystalType type = ItemCollectorCrystal.getType(stack);
        switch (type) {
            case ROCK_CRYSTAL:
                renderTile(texWhite);
                break;
            case CELESTIAL_CRYSTAL:
                renderTile(texBlue);
                break;
            default:
                break;
        }
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

}
