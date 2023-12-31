/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.item;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRendererTESR
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:04
 */
public class ItemRendererTESR<T extends TileEntity> implements IItemRenderer {

    private final TileEntitySpecialRenderer<T> tesr;
    private final T tile;

    public ItemRendererTESR(TileEntitySpecialRenderer<T> tesr, T tile) {
        this.tesr = tesr;
        this.tile = tile;
    }

    @Override
    public void render(ItemStack stack) {
        tesr.render(tile, 0, 0, 0, 0, 0, 1F);
    }

}
