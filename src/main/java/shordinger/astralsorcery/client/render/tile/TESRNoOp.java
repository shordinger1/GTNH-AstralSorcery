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
import net.minecraft.tileentity.TileEntity;

import shordinger.astralsorcery.client.util.item.IItemRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRNoOp
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:39
 */
public class TESRNoOp<T> extends TileEntitySpecialRenderer implements IItemRenderer {

    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

    }

    @Override
    public void render(ItemStack stack) {

    }
}
