/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.block.BlockCustomName;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockMultiState
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:30
 */
public class ItemBlockCustomName extends ItemBlock {

    public ItemBlockCustomName(Block block) {
        super(block);
        setHasSubtypes(true); // Normally the case if you're using multi-type blocks.
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Block b = getBlock();
        if (b instanceof BlockCustomName) {
            String identifier = ((BlockCustomName) b).getIdentifierForMeta(stack.getItemDamage());
            return super.getUnlocalizedName(stack) + "." + identifier;
        }
        return super.getUnlocalizedName(stack);
    }
}
