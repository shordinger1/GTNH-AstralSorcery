/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.tile.TileFakeTree;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.struct.BlockDiscoverer;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalShovel
 * Created by HellFirePvP
 * Date: 14.03.2017 / 12:43
 */
public class ItemChargedCrystalShovel extends ItemCrystalShovel implements ChargedCrystalToolBase {

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        World world = player.getEntityWorld();
        if (!world.isRemote && !player.isSneaking()) {
            IBlockState at = WorldHelper.getBlockState(world, pos);
            if (at.getBlock()
                .isToolEffective("shovel", at)) {
                BlockArray shovelables = BlockDiscoverer
                    .discoverBlocksWithSameStateAround(world, pos, true, 8, 100, true);
                Map<BlockPos, BlockArray.BlockInformation> pattern = shovelables.getPattern();
                for (Map.Entry<BlockPos, BlockArray.BlockInformation> blocks : pattern.entrySet()) {
                    if (world.setBlockState(blocks.getKey(), BlocksAS.blockFakeTree.getDefaultState())) {
                        TileFakeTree tt = MiscUtils.getTileAt(world, blocks.getKey(), TileFakeTree.class, true);
                        if (tt != null) {
                            tt.setupTile(player, itemstack, blocks.getValue().state);
                            itemstack.damageItem(1, player);
                        } else {
                            world.setBlockState(blocks.getKey(), blocks.getValue().state);
                        }
                    }
                }
                return true;
            }
        }
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalShovel;
    }

}
