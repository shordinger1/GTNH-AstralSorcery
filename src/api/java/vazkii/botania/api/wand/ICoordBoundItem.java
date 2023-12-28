package vazkii.botania.api.wand;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.migration.BlockPos;

/**
 * The item equivalent of ITileBound, renders when the
 * item is in hand.
 *
 * @see ITileBound
 */
public interface ICoordBoundItem {

    public BlockPos getBinding(ItemStack stack);

}
