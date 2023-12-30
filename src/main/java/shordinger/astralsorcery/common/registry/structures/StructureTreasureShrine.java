/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.structures;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.StructureBlockArray;
import shordinger.astralsorcery.common.tile.TileOreGenerator;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureTreasureShrine
 * Created by HellFirePvP
 * Date: 20.07.2017 / 22:27
 */
public class StructureTreasureShrine extends StructureBlockArray {

    public StructureTreasureShrine() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState mrw = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mru = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
        IBlockState mpl = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState mbr = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);

        addBlockCube(mrw, -4, 0, -4, 4, 8, 4);
        addAirCube(-3, 2, -3, 3, 7, 3);

        addBlock(0, 1, 0, mru);
        addBlock(0, 2, 0, mpl);
        addBlock(0, 3, 0, mpl);
        addBlock(0, 4, 0, Blocks.STONE.getDefaultState());
        addBlock(0, 5, 0, mpl);
        addBlock(0, 6, 0, mpl);
        addBlock(0, 7, 0, mru);

        addBlock(-1, 1, 1, Blocks.WATER.getDefaultState());
        addBlock(0, 1, 1, Blocks.WATER.getDefaultState());
        addBlock(1, 1, 1, Blocks.WATER.getDefaultState());
        addBlock(1, 1, 0, Blocks.WATER.getDefaultState());
        addBlock(1, 1, -1, Blocks.WATER.getDefaultState());
        addBlock(0, 1, -1, Blocks.WATER.getDefaultState());
        addBlock(-1, 1, -1, Blocks.WATER.getDefaultState());
        addBlock(-1, 1, 0, Blocks.WATER.getDefaultState());

        addBlock(2, 1, 0, Blocks.WATER.getDefaultState());
        addBlock(3, 1, 0, Blocks.WATER.getDefaultState());
        addBlock(-2, 1, 0, Blocks.WATER.getDefaultState());
        addBlock(-3, 1, 0, Blocks.WATER.getDefaultState());
        addBlock(0, 1, 2, Blocks.WATER.getDefaultState());
        addBlock(0, 1, 3, Blocks.WATER.getDefaultState());
        addBlock(0, 1, -2, Blocks.WATER.getDefaultState());
        addBlock(0, 1, -3, Blocks.WATER.getDefaultState());

        addBlock(2, 1, 1, mbr);
        addBlock(3, 1, 1, mbr);
        addBlock(2, 1, -1, mbr);
        addBlock(3, 1, -1, mbr);
        addBlock(-2, 1, 1, mbr);
        addBlock(-3, 1, 1, mbr);
        addBlock(-2, 1, -1, mbr);
        addBlock(-3, 1, -1, mbr);

        addBlock(1, 1, 2, mbr);
        addBlock(1, 1, 3, mbr);
        addBlock(-1, 1, 2, mbr);
        addBlock(-1, 1, 3, mbr);
        addBlock(1, 1, -2, mbr);
        addBlock(1, 1, -3, mbr);
        addBlock(-1, 1, -2, mbr);
        addBlock(-1, 1, -3, mbr);

        buildPillar(-3, -3);
        buildPillar(3, -3);
        buildPillar(3, 3);
        buildPillar(-3, 3);
    }

    private void buildPillar(int x, int z) {
        Block marble = BlocksAS.blockMarble;
        IBlockState mpl = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        for (int i = 0; i < 5; i++) {
            addBlock(x, 2 + i, z, mpl);
        }
        addBlock(x, 7, z, Blocks.SEA_LANTERN.getDefaultState());
    }

    @Override
    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, IBlockState> placed = super.placeInWorld(world, center);
        TileOreGenerator.createStructuralTile(world, center.add(0, 4, 0));
        return placed;
    }

}
