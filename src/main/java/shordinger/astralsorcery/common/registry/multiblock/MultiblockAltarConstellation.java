/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.multiblock;

import static shordinger.astralsorcery.common.block.BlockMarble.MARBLE_TYPE;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.block.BlockBlackMarble;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAltarConstellation
 * Created by HellFirePvP
 * Date: 22.10.2016 / 12:48
 */
public class MultiblockAltarConstellation extends PatternBlockArray {

    public MultiblockAltarConstellation() {
        super(new ResourceLocation(AstralSorcery.MODID, "pattern_altar_t3"));
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState mch = marble.getDefaultState()
            .withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mbr = marble.getDefaultState()
            .withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);
        IBlockState mrw = marble.getDefaultState()
            .withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mru = marble.getDefaultState()
            .withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
        IBlockState mpl = marble.getDefaultState()
            .withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState bml = BlocksAS.blockBlackMarble.getDefaultState()
            .withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.RAW);

        // addBlockCube(air, -4, 0, -4, 4, 3, 4);
        addBlockCube(mbr, -4, -1, -4, 4, -1, 4);
        addBlockCube(bml, -3, -1, -3, 3, -1, 3);

        addBlock(
            0,
            0,
            0,
            BlocksAS.blockAltar.getDefaultState()
                .withProperty(BlockAltar.ALTAR_TYPE, BlockAltar.AltarType.ALTAR_3));

        addBlock(-4, -1, -4, mrw);
        addBlock(-4, -1, -3, mrw);
        addBlock(-3, -1, -4, mrw);
        addBlock(4, -1, -4, mrw);
        addBlock(4, -1, -3, mrw);
        addBlock(3, -1, -4, mrw);
        addBlock(-4, -1, 4, mrw);
        addBlock(-4, -1, 3, mrw);
        addBlock(-3, -1, 4, mrw);
        addBlock(4, -1, 4, mrw);
        addBlock(4, -1, 3, mrw);
        addBlock(3, -1, 4, mrw);

        addBlock(-5, -1, -5, mbr);
        addBlock(-5, -1, -4, mbr);
        addBlock(-5, -1, -3, mbr);
        addBlock(-4, -1, -5, mbr);
        addBlock(-3, -1, -5, mbr);
        addBlock(5, -1, -5, mbr);
        addBlock(5, -1, -4, mbr);
        addBlock(5, -1, -3, mbr);
        addBlock(4, -1, -5, mbr);
        addBlock(3, -1, -5, mbr);
        addBlock(-5, -1, 5, mbr);
        addBlock(-5, -1, 4, mbr);
        addBlock(-5, -1, 3, mbr);
        addBlock(-4, -1, 5, mbr);
        addBlock(-3, -1, 5, mbr);
        addBlock(5, -1, 5, mbr);
        addBlock(5, -1, 4, mbr);
        addBlock(5, -1, 3, mbr);
        addBlock(4, -1, 5, mbr);
        addBlock(3, -1, 5, mbr);

        /*
         * addBlock(-4, -1, -2, mbr);
         * addBlock(-2, -1, -4, mbr);
         * addBlock(-4, -1, 2, mbr);
         * addBlock(-2, -1, 4, mbr);
         * addBlock( 4, -1, -2, mbr);
         * addBlock( 2, -1, -4, mbr);
         * addBlock( 4, -1, 2, mbr);
         * addBlock( 2, -1, 4, mbr);
         */

        addBlock(-4, 0, -4, mru);
        addBlock(-4, 0, 4, mru);
        addBlock(4, 0, -4, mru);
        addBlock(4, 0, 4, mru);
        addBlock(-4, 1, -4, mpl);
        addBlock(-4, 1, 4, mpl);
        addBlock(4, 1, -4, mpl);
        addBlock(4, 1, 4, mpl);
        addBlock(-4, 2, -4, mpl);
        addBlock(-4, 2, 4, mpl);
        addBlock(4, 2, -4, mpl);
        addBlock(4, 2, 4, mpl);
        addBlock(-4, 3, -4, mch);
        addBlock(-4, 3, 4, mch);
        addBlock(4, 3, -4, mch);
        addBlock(4, 3, 4, mch);
    }

}
