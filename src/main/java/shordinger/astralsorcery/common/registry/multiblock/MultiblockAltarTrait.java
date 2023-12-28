/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.multiblock;

import net.minecraft.util.ResourceLocation;
import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the 1.11.2 port of Reika's mods.
 * Original code for this project on Minecraft 1.7.10
 * is available under the same licence on Github:
 * https://github.com/ReikaKalseki/DragonAPI
 * Class: MultiblockAltarTrait
 * Author: HellFirePvP
 * Owner & Author: Reika Kalseki
 * Date: 24.02.2017 / 17:33
 */
public class MultiblockAltarTrait extends PatternBlockArray {

    public MultiblockAltarTrait() {
        super(new ResourceLocation(Tags.MODID, "pattern_altar_t4"));
        addAll(MultiBlockArrays.patternAltarConstellation);
        load();
    }

    private void load() {
        IBlockState mBrick = BlocksAS.blockMarble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);

        addBlock(4, 3, 3, mBrick);
        addBlock(4, 3, -3, mBrick);
        addBlock(-4, 3, 3, mBrick);
        addBlock(-4, 3, -3, mBrick);

        addBlock(3, 3, 4, mBrick);
        addBlock(-3, 3, 4, mBrick);
        addBlock(3, 3, -4, mBrick);
        addBlock(-3, 3, -4, mBrick);

        addBlock(3, 4, 3, mBrick);
        addBlock(3, 4, 2, mBrick);
        addBlock(3, 4, 1, mBrick);
        addBlock(3, 4, -1, mBrick);
        addBlock(3, 4, -2, mBrick);
        addBlock(3, 4, -3, mBrick);
        addBlock(2, 4, -3, mBrick);
        addBlock(1, 4, -3, mBrick);
        addBlock(-1, 4, -3, mBrick);
        addBlock(-2, 4, -3, mBrick);
        addBlock(-3, 4, -3, mBrick);
        addBlock(-3, 4, -2, mBrick);
        addBlock(-3, 4, -1, mBrick);
        addBlock(-3, 4, 1, mBrick);
        addBlock(-3, 4, 2, mBrick);
        addBlock(-3, 4, 3, mBrick);
        addBlock(-2, 4, 3, mBrick);
        addBlock(-1, 4, 3, mBrick);
        addBlock(1, 4, 3, mBrick);
        addBlock(2, 4, 3, mBrick);

        addBlock(
            0,
            0,
            0,
            BlocksAS.blockAltar.getDefaultState()
                .withProperty(BlockAltar.ALTAR_TYPE, BlockAltar.AltarType.ALTAR_4));
    }

}
