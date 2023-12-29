/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.container;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiAltarAttunement
 * Created by HellFirePvP
 * Date: 16.10.2016 / 17:13
 */
public class GuiAltarAttunement extends GuiAltarBase {

    private static final BindableResource texAltarAttenuation = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guialtar2");
    private static final BindableResource texBlack = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MISC, "black");

    public GuiAltarAttunement(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    public void initGui() {
        this.xSize = 256;
        this.ySize = 202;
        super.initGui();
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        AbstractAltarRecipe rec = findCraftableRecipe();
        if (rec != null) {
            ItemStack out = rec.getOutputForRender();
            zLevel = 10F;
            itemRender.zLevel = 10F;

            RenderHelper.enableGUIStandardItemLighting();

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glTranslated(190, 35, 0);
            GL11.glScaled(2.5, 2.5, 2.5);

            itemRender.renderItemAndEffectIntoGUI(mc.player, out, 0, 0);
            itemRender.renderItemOverlayIntoGUI(fontRenderer, out, 0, 0, null);

            GL11.glPopMatrix();
            GL11.glPopAttrib();

            // RenderHelper.disableStandardItemLighting();

            zLevel = 0F;
            itemRender.zLevel = 0F;

            TextureHelper.refreshTextureBindState();
        }
    }

    @Override
    public void renderGuiBackground(float partialTicks, int mouseX, int mouseY) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float percFilled;
        if (containerAltarBase.tileAltar.getMultiblockState()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            percFilled = containerAltarBase.tileAltar.getAmbientStarlightPercent();
        } else {
            GL11.glColor4f(1.0F, 0F, 0F, 1.0F);
            percFilled = 1.0F;
        }

        texBlack.bind();
        drawRect(guiLeft + 11, guiTop + 104, 232, 10);

        if (percFilled > 0) {
            SpriteSheetResource spriteStarlight = SpriteLibrary.spriteStarlight;
            spriteStarlight.getResource()
                .bindTexture();
            int t = containerAltarBase.tileAltar.getTicksExisted();
            Tuple<Double, Double> uvOffset = spriteStarlight.getUVOffset(t);
            drawRect(
                guiLeft + 11,
                guiTop + 104,
                (int) (232 * percFilled),
                10,
                uvOffset.key,
                uvOffset.value,
                spriteStarlight.getULength() * percFilled,
                spriteStarlight.getVLength());

            AbstractAltarRecipe aar = findCraftableRecipe(true);
            if (aar != null) {
                int req = aar.getPassiveStarlightRequired();
                int has = containerAltarBase.tileAltar.getStarlightStored();
                if (has < req) {
                    int max = containerAltarBase.tileAltar.getMaxStarlightStorage();
                    float percReq = (float) (req - has) / (float) max;
                    int from = (int) (232 * percFilled);
                    int to = (int) (232 * percReq);
                    GL11.glColor4f(0.2F, 0.5F, 1.0F, 0.4F);

                    drawRect(
                        guiLeft + 11 + from,
                        guiTop + 104,
                        to,
                        10,
                        uvOffset.key + spriteStarlight.getULength() * percFilled,
                        uvOffset.value,
                        spriteStarlight.getULength() * percReq,
                        spriteStarlight.getVLength());
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        texAltarAttenuation.bind();
        drawRect(guiLeft, guiTop, xSize, ySize);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
