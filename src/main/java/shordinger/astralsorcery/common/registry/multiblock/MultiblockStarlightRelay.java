/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.multiblock;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.block.BlockBlackMarble;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockStarlightRelay
 * Created by HellFirePvP
 * Date: 30.03.2017 / 14:07
 */
public class MultiblockStarlightRelay extends PatternBlockArray {

    public MultiblockStarlightRelay() {
        super(new ResourceLocation(Tags.MODID, "pattern_starlight_relay"));
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState chiseled = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState arch = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState sooty = BlocksAS.blockBlackMarble.getDefaultState()
            .withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.RAW);

        addBlock(0, 0, 0, BlocksAS.attunementRelay.getDefaultState());

        addBlock(-1, -1, -1, chiseled);
        addBlock(1, -1, -1, chiseled);
        addBlock(1, -1, 1, chiseled);
        addBlock(-1, -1, 1, chiseled);

        addBlock(-1, -1, 0, arch);
        addBlock(1, -1, 0, arch);
        addBlock(0, -1, 1, arch);
        addBlock(0, -1, -1, arch);
        addBlock(0, -1, 0, sooty);
    }

}
