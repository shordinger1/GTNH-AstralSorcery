/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.multiblock;

import java.util.Map;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.block.BlockBlackMarble;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAttunementFrame
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:36
 */
public class MultiblockAttunementFrame extends PatternBlockArray {

    public MultiblockAttunementFrame() {
        super(new ResourceLocation(Tags.MODID, "pattern_attunement_frame"));
        load();
    }

    private void load() {
        IBlockState mar = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mbl = BlocksAS.blockBlackMarble.getDefaultState()
            .withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.RAW);

        addBlock(0, 0, 0, BlocksAS.attunementAltar.getDefaultState());
        // addBlock(0, 1, 0, BlocksAS.blockStructural.getDefaultState().withProperty(BlockStructural.BLOCK_TYPE,
        // BlockStructural.BlockType.ATTUNEMENT_ALTAR_STRUCT));

        addBlockCube(mar, -7, -1, -8, 7, -1, -8);
        addBlockCube(mar, -7, -1, 8, 7, -1, 8);
        addBlockCube(mar, -8, -1, -7, -8, -1, 7);
        addBlockCube(mar, 8, -1, -7, 8, -1, 7);

        addBlockCube(mbl, -7, -1, -7, 7, -1, 7);

        pillarAt(-8, -0, -8);
        pillarAt(-8, -0, 8);
        pillarAt(8, -0, -8);
        pillarAt(8, -0, 8);

        addBlock(-9, -1, -9, mar);
        addBlock(-9, -1, -8, mar);
        addBlock(-9, -1, -7, mar);
        addBlock(-8, -1, -9, mar);
        addBlock(-7, -1, -9, mar);

        addBlock(-9, -1, 9, mar);
        addBlock(-9, -1, 8, mar);
        addBlock(-9, -1, 7, mar);
        addBlock(-8, -1, 9, mar);
        addBlock(-7, -1, 9, mar);

        addBlock(9, -1, -9, mar);
        addBlock(9, -1, -8, mar);
        addBlock(9, -1, -7, mar);
        addBlock(8, -1, -9, mar);
        addBlock(7, -1, -9, mar);

        addBlock(9, -1, 9, mar);
        addBlock(9, -1, 8, mar);
        addBlock(9, -1, 7, mar);
        addBlock(8, -1, 9, mar);
        addBlock(7, -1, 9, mar);
    }

    private void pillarAt(int x, int y, int z) {
        IBlockState mru = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
        IBlockState mpl = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState mch = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);

        addBlock(x, y, z, mru);
        addBlock(x, y + 1, z, mpl);
        addBlock(x, y + 2, z, mpl);
        addBlock(x, y + 3, z, mpl);
        addBlock(x, y + 4, z, mch);
    }

    @Override
    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, IBlockState> placed = super.placeInWorld(world, center);
        if (world.setBlockToAir(center)) {
            placed.remove(center);
        }
        if (world.setBlockToAir(center.offset(EnumFacing.UP, 1))) {
            placed.remove(center.offset(EnumFacing.UP, 1));
        }
        return placed;
    }
}
