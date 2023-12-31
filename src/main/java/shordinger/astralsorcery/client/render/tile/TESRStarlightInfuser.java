/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.tile.TileStarlightInfuser;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRStarlightInfuser
 * Created by HellFirePvP
 * Date: 13.01.2017 / 12:17
 */
public class TESRStarlightInfuser extends TileEntitySpecialRenderer<TileStarlightInfuser> {

    @Override
    public void render(TileStarlightInfuser te, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {
        ItemStack in = te.getInputStack();
        if (in.isEmpty()) return;
        RenderingUtils.renderItemAsEntity(in, x, y, z, partialTicks, te.getTicksExisted());
    }
}
