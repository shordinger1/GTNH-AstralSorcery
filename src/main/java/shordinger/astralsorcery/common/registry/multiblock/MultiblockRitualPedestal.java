/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.multiblock;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

import static hellfirepvp.astralsorcery.common.block.BlockMarble.MARBLE_TYPE;
import static hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockRitualPedestal
 * Created by HellFirePvP
 * Date: 02.10.2016 / 16:48
 */
public class MultiblockRitualPedestal extends PatternBlockArray {

    public MultiblockRitualPedestal() {
        super(new ResourceLocation(AstralSorcery.MODID, "pattern_ritual_pedestal"));
        load();
    }

    public MultiblockRitualPedestal(ResourceLocation name) {
        super(name);
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;

        addAirCube(-2, 0, -2, 2, 2, 2);
        addAirCube(-3, 0, -1, 3, 2, 1);
        addAirCube(-1, 0, -3, 1, 2, 3);

        addBlock(0, 0, 0, BlocksAS.ritualPedestal.getDefaultState());

        IBlockState mch = marble.getDefaultState().withProperty(MARBLE_TYPE, MarbleBlockType.CHISELED);
        IBlockState mbr = marble.getDefaultState().withProperty(MARBLE_TYPE, MarbleBlockType.BRICKS);
        IBlockState mrw = marble.getDefaultState().withProperty(MARBLE_TYPE, MarbleBlockType.RAW);
        IBlockState mar = marble.getDefaultState().withProperty(MARBLE_TYPE, MarbleBlockType.ARCH);

        addBlock(0, -1, 0, mch);

        addBlock( 0, -1,  1, mbr);
        addBlock( 0, -1,  2, mbr);
        addBlock( 0, -1,  3, mbr);
        addBlock( 1, -1,  3, mbr);
        addBlock(-1, -1,  3, mbr);

        addBlock( 0, -1, -1, mbr);
        addBlock( 0, -1, -2, mbr);
        addBlock( 0, -1, -3, mbr);
        addBlock( 1, -1, -3, mbr);
        addBlock(-1, -1, -3, mbr);

        addBlock( 1, -1,  0, mbr);
        addBlock( 2, -1,  0, mbr);
        addBlock( 3, -1,  0, mbr);
        addBlock( 3, -1,  1, mbr);
        addBlock( 3, -1, -1, mbr);

        addBlock(-1, -1,  0, mbr);
        addBlock(-2, -1,  0, mbr);
        addBlock(-3, -1,  0, mbr);
        addBlock(-3, -1,  1, mbr);
        addBlock(-3, -1, -1, mbr);

        addBlock( 2, -1,  2, mbr);
        addBlock(-2, -1,  2, mbr);
        addBlock( 2, -1, -2, mbr);
        addBlock(-2, -1, -2, mbr);

        addBlock( 1, -1,  1, mrw);
        addBlock( 1, -1,  2, mrw);
        addBlock( 2, -1,  1, mrw);

        addBlock(-1, -1,  1, mrw);
        addBlock(-1, -1,  2, mrw);
        addBlock(-2, -1,  1, mrw);

        addBlock( 1, -1, -1, mrw);
        addBlock( 1, -1, -2, mrw);
        addBlock( 2, -1, -1, mrw);

        addBlock(-1, -1, -1, mrw);
        addBlock(-1, -1, -2, mrw);
        addBlock(-2, -1, -1, mrw);

        addBlock( 0, -1,  4, mar);
        addBlock( 1, -1,  4, mar);
        addBlock(-1, -1,  4, mar);

        addBlock( 0, -1, -4, mar);
        addBlock( 1, -1, -4, mar);
        addBlock(-1, -1, -4, mar);

        addBlock( 4, -1,  0, mar);
        addBlock( 4, -1,  1, mar);
        addBlock( 4, -1, -1, mar);

        addBlock(-4, -1,  0, mar);
        addBlock(-4, -1,  1, mar);
        addBlock(-4, -1, -1, mar);

        addBlock( 3, -1,  2, mar);
        addBlock( 3, -1, -2, mar);
        addBlock(-3, -1,  2, mar);
        addBlock(-3, -1, -2, mar);

        addBlock( 2, -1,  3, mar);
        addBlock(-2, -1,  3, mar);
        addBlock( 2, -1, -3, mar);
        addBlock(-2, -1, -3, mar);
    }

}
