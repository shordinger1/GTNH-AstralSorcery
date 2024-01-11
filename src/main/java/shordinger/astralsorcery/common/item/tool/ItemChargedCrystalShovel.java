/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.util.Map;

import javax.annotation.Nonnull;

import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.tile.TileFakeTree;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.struct.BlockDiscoverer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

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
        if (!world.isRemote && !player.isSneaking()
            && !player.getCooldownTracker()
            .hasCooldown(ItemsAS.chargedCrystalShovel)) {
            IBlockState at = world.getBlockState(pos);
            if (at.getBlock()
                .isToolEffective("shovel", at)) {
                BlockArray shovelables = BlockDiscoverer
                    .discoverBlocksWithSameStateAround(world, pos, true, 8, 100, true);
                if (shovelables != null) {
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
                    if (!ChargedCrystalToolBase.tryRevertMainHand(player, itemstack)) {
                        player.getCooldownTracker()
                            .setCooldown(ItemsAS.chargedCrystalShovel, 40);
                    }
                    return true;
                }
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
