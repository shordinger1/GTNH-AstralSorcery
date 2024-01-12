/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.geolosys;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.orestages.api.OreTiersAPI;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.client.models.obj.OBJModelLibrary;
import shordinger.astralsorcery.client.util.item.IItemRenderer;
import shordinger.astralsorcery.client.util.obj.WavefrontObject;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.integrations.ModIntegrationGeolosys;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.GLAllocation;
import shordinger.wrapper.net.minecraft.client.renderer.RenderHelper;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.Tuple;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRGeolosysSampleCluster
 * Created by HellFirePvP
 * Date: 03.10.2017 / 17:39
 */
public class TESRGeolosysSampleCluster extends TileEntitySpecialRenderer<TileGeolosysSampleCluster>
    implements IItemRenderer {

    private static int dlC1 = -1;
    private static final BindableResource texCelestialCrystals = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "c_crystal_tex_colorless");

    private static int[] rotMapping = new int[]{45, 135, 270, 90, 315, 0, 180, 225};

    @Override
    public void render(TileGeolosysSampleCluster te, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {
        IBlockState relevantState = ModIntegrationGeolosys.geolosysSample.getDefaultState();
        if (OreTiersAPI.hasReplacement(relevantState)) {
            Tuple<String, IBlockState> info = OreTiersAPI.getStageInfo(relevantState);
            if (info != null && Minecraft.getMinecraft().thePlayer != null
                && !GameStageHelper.hasStage(Minecraft.getMinecraft().thePlayer, info.getFirst())) {
                return;
            }
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslated(x + 0.5, y + 0.1, z + 0.5);
        float size = 0.2F;
        GL11.glScalef(size, size, size);

        int r = 0x59A51481;
        BlockPos at = te.getPos();
        r ^= at.getX();
        r ^= at.getY();
        r ^= at.getZ();
        r = Math.abs(r);
        r = rotMapping[r % rotMapping.length];
        GL11.glRotated(r, 0, 1, 0);

        renderCrystals();
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderCrystals() {
        GL11.glPushMatrix();
        texCelestialCrystals.bind();
        int dlSelected = dlC1;
        WavefrontObject obj = OBJModelLibrary.crystalsStage1;
        if (dlSelected == -1) {
            dlSelected = GLAllocation.generateDisplayLists(1);
            dlC1 = dlSelected;
            GL11.glNewList(dlSelected, GL11.GL_COMPILE);
            obj.renderAll(true);
            GL11.glEndList();
        }
        GL11.glCallList(dlSelected);

        GL11.glPopMatrix();
    }

    @Override
    public void render(ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 0.25, 0.5);
        GL11.glScalef(0.2F, 0.2F, 0.2F);
        GL11.glRotated(-10, 0, 0, 1);
        GL11.glRotated(20, 1, 0, 0);
        GL11.glRotated(-70, 0, 1, 0);
        GL11.glDisable(GL11.GL_CULL_FACE);
        RenderHelper.disableStandardItemLighting();
        renderCrystals();
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

}
