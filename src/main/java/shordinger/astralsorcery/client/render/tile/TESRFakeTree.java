/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.tile;

import java.awt.*;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import shordinger.astralsorcery.common.tile.TileFakeTree;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 21:13
 */
public class TESRFakeTree extends TileEntitySpecialRenderer<TileFakeTree> {

    @Override
    public void render(TileFakeTree te, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {
        if (te.getFakedState() == null) return;
        IBlockState renderState = te.getFakedState();
        if (x * x + y * y + z * z >= 64 * 64) return;
        Color effect = null;
        if (te.getPlayerEffectRef() != null) {
            PatreonEffectHelper.PatreonEffect pe = PatreonEffectHelper
                .getPatreonEffects(Side.CLIENT, te.getPlayerEffectRef())
                .stream()
                .filter(p -> p instanceof PtEffectTreeBeacon)
                .findFirst()
                .orElse(null);
            if (pe instanceof PtEffectTreeBeacon) {
                effect = new Color(((PtEffectTreeBeacon) pe).getColorTranslucentOverlay(), true);
            }
        }
        TESRTranslucentBlock.addForRender(effect, renderState, te.getPos());
    }

}
