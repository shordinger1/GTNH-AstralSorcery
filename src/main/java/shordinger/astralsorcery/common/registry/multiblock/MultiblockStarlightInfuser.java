/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.multiblock;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 16:35
 */
public class MultiblockStarlightInfuser extends PatternBlockArray {

    public MultiblockStarlightInfuser() {
        super(new ResourceLocation(Tags.MODID, "pattern_starlight_infuser"));
        load();
    }

    private void load() {
        IBlockState mar = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mrw = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mpl = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState mch = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mru = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);

        IBlockState liquidStarlight = BlocksAS.blockLiquidStarlight.getDefaultState();

        addBlock(0, 0, 0, BlocksAS.starlightInfuser.getDefaultState());

        addBlockCube(mar, -2, -1, -3, 2, -1, 3);
        addBlockCube(mar, -3, -1, -2, 3, -1, 2);

        addBlockCube(mru, -2, -1, -2, 2, -1, 2);

        addBlock(0, -1, 0, Blocks.LAPIS_BLOCK.getDefaultState());
        addBlock(-2, -1, -1, liquidStarlight);
        addBlock(-2, -1, 0, liquidStarlight);
        addBlock(-2, -1, 1, liquidStarlight);
        addBlock(2, -1, -1, liquidStarlight);
        addBlock(2, -1, 0, liquidStarlight);
        addBlock(2, -1, 1, liquidStarlight);
        addBlock(-1, -1, -2, liquidStarlight);
        addBlock(0, -1, -2, liquidStarlight);
        addBlock(1, -1, -2, liquidStarlight);
        addBlock(-1, -1, 2, liquidStarlight);
        addBlock(0, -1, 2, liquidStarlight);
        addBlock(1, -1, 2, liquidStarlight);

        addBlock(-2, -2, -1, mrw);
        addBlock(-2, -2, 0, mrw);
        addBlock(-2, -2, 1, mrw);
        addBlock(2, -2, -1, mrw);
        addBlock(2, -2, 0, mrw);
        addBlock(2, -2, 1, mrw);
        addBlock(-1, -2, -2, mrw);
        addBlock(0, -2, -2, mrw);
        addBlock(1, -2, -2, mrw);
        addBlock(-1, -2, 2, mrw);
        addBlock(0, -2, 2, mrw);
        addBlock(1, -2, 2, mrw);

        addBlock(3, 3, 2, mar);
        addBlock(3, 3, -2, mar);
        addBlock(-3, 3, 2, mar);
        addBlock(-3, 3, -2, mar);
        addBlock(2, 3, 3, mar);
        addBlock(-2, 3, 3, mar);
        addBlock(2, 3, -3, mar);
        addBlock(-2, 3, -3, mar);

        addBlock(3, 4, 1, mar);
        addBlock(3, 4, 0, mar);
        addBlock(3, 4, -1, mar);
        addBlock(-3, 4, 1, mar);
        addBlock(-3, 4, 0, mar);
        addBlock(-3, 4, -1, mar);
        addBlock(1, 4, 3, mar);
        addBlock(0, 4, 3, mar);
        addBlock(-1, 4, 3, mar);
        addBlock(1, 4, -3, mar);
        addBlock(0, 4, -3, mar);
        addBlock(-1, 4, -3, mar);

        addBlock(-3, 0, 3, mpl);
        addBlock(-3, 1, 3, mpl);
        addBlock(-3, 2, 3, mch);
        addBlock(-3, 0, -3, mpl);
        addBlock(-3, 1, -3, mpl);
        addBlock(-3, 2, -3, mch);
        addBlock(3, 0, 3, mpl);
        addBlock(3, 1, 3, mpl);
        addBlock(3, 2, 3, mch);
        addBlock(3, 0, -3, mpl);
        addBlock(3, 1, -3, mpl);
        addBlock(3, 2, -3, mch);
    }

}
