/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.container;

import shordinger.astralsorcery.migration.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import shordinger.astralsorcery.migration.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;

import shordinger.astralsorcery.client.gui.base.GuiInventoryContainerBase;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.common.container.*;
import shordinger.astralsorcery.common.container.ContainerAltarAttunement;
import shordinger.astralsorcery.common.container.ContainerAltarBase;
import shordinger.astralsorcery.common.container.ContainerAltarConstellation;
import shordinger.astralsorcery.common.container.ContainerAltarDiscovery;
import shordinger.astralsorcery.common.container.ContainerAltarTrait;
import shordinger.astralsorcery.common.crafting.IGatedRecipe;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import shordinger.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiAltarBase
 * Created by HellFirePvP
 * Date: 16.10.2016 / 19:28
 */
public abstract class GuiAltarBase extends GuiInventoryContainerBase {

    public final ContainerAltarBase containerAltarBase;

    public GuiAltarBase(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(buildContainer(playerInv, tileAltar), tileAltar);
        this.containerAltarBase = (ContainerAltarBase) super.inventorySlots;
    }

    public AbstractAltarRecipe findCraftableRecipe() {
        return findCraftableRecipe(false);
    }

    public AbstractAltarRecipe findCraftableRecipe(boolean ignoreStarlightRequirement) {
        AbstractAltarRecipe rec = AltarRecipeRegistry
            .findMatchingRecipe(containerAltarBase.tileAltar, ignoreStarlightRequirement);
        if (rec != null) {
            if (rec instanceof IGatedRecipe) {
                if (((IGatedRecipe) rec).hasProgressionClient()) {
                    return rec;
                } else {
                    return null;
                }
            }
            return rec;
        }
        return null;
    }

    @Override
    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        renderGuiBackground(partialTicks, mouseX, mouseY);
        TextureHelper.refreshTextureBindState();
    }

    public abstract void renderGuiBackground(float partialTicks, int mouseX, int mouseY);

    private static ContainerAltarBase buildContainer(InventoryPlayer playerInv, TileAltar tileAltar) {
        switch (tileAltar.getAltarLevel()) {
            case DISCOVERY:
                return new ContainerAltarDiscovery(playerInv, tileAltar);
            case ATTUNEMENT:
                return new ContainerAltarAttunement(playerInv, tileAltar);
            case CONSTELLATION_CRAFT:
                return new ContainerAltarConstellation(playerInv, tileAltar);
            case TRAIT_CRAFT:
                return new ContainerAltarTrait(playerInv, tileAltar);
            case BRILLIANCE:
                break;
            default:
                break;
        }
        return new ContainerAltarDiscovery(playerInv, tileAltar);
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height, double u, double v, double uLength,
                            double vLength) {
        Tessellator tes = Tessellator.instance;
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX, offsetY + height, zLevel)
            .tex(u, v + vLength)
            .endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel)
            .tex(u + uLength, v + vLength)
            .endVertex();
        vb.pos(offsetX + width, offsetY, zLevel)
            .tex(u + uLength, v)
            .endVertex();
        vb.pos(offsetX, offsetY, zLevel)
            .tex(u, v)
            .endVertex();
        tes.draw();
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.instance;
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX, offsetY + height, zLevel)
            .tex(0, 1)
            .endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel)
            .tex(1, 1)
            .endVertex();
        vb.pos(offsetX + width, offsetY, zLevel)
            .tex(1, 0)
            .endVertex();
        vb.pos(offsetX, offsetY, zLevel)
            .tex(0, 0)
            .endVertex();
        tes.draw();
    }

}
