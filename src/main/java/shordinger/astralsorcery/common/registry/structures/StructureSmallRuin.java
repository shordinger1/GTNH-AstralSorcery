/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.structures;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.IBlockAccess;

import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.block.BlockMarbleStairs;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.StructureBlockArray;
import shordinger.astralsorcery.common.tile.TileStructController;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallRuin
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:07
 */
public class StructureSmallRuin extends StructureBlockArray {

    public StructureSmallRuin() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState mrw = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mch = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mar = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mbr = marble.getDefaultState()
            .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);

        IBlockState stairsSouth = BlocksAS.blockMarbleStairs.getDefaultState()
            .withProperty(BlockMarbleStairs.FACING, ForgeDirection.SOUTH);
        IBlockState stairsNorth = BlocksAS.blockMarbleStairs.getDefaultState()
            .withProperty(BlockMarbleStairs.FACING, ForgeDirection.NORTH);

        addBlock(0, 3, 0, BlocksAS.blockPortalNode.getDefaultState());
        addTileCallback(new BlockPos(0, 3, 0), new TileEntityCallback() {

            @Override
            public boolean isApplicable(TileEntity te) {
                return te != null && te instanceof TileStructController;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                if (te instanceof TileStructController) {
                    ((TileStructController) te).setType(TileStructController.StructType.GATE);
                }
            }
        });

        addBlock(1, 0, 0, mrw);
        addBlock(1, 0, 1, mrw);
        addBlock(1, 0, 2, mrw);
        addBlock(1, 0, 3, mrw);
        addBlock(2, 0, 2, mrw);
        addBlock(0, 0, 2, mrw);
        addBlock(-1, 0, 2, mrw);
        addBlock(0, 0, 3, mrw);
        addBlock(-1, 0, 3, mrw);
        addBlock(0, 0, 4, mrw);
        addBlock(-1, 0, 4, mrw);
        addBlock(0, 0, 5, mrw);

        addBlock(0, 0, -2, mrw);
        addBlock(0, 0, -3, mrw);
        addBlock(0, 0, -4, mrw);
        addBlock(0, 0, -5, mrw);
        addBlock(0, 0, -6, mrw);
        addBlock(1, 0, -1, mrw);
        addBlock(1, 0, -2, mbr);
        addBlock(1, 0, -3, mrw);
        addBlock(1, 0, -4, mrw);
        addBlock(2, 0, -2, mrw);
        addBlock(2, 0, -3, mrw);
        addBlock(-1, 0, -3, mbr);
        addBlock(-1, 0, -4, mrw);
        addBlock(-2, 0, -4, mrw);

        addBlock(0, 1, 1, stairsSouth);
        addBlock(0, 1, -1, stairsNorth);

        addBlock(0, 1, -2, mrw);
        addBlock(0, 1, -3, mrw);
        addBlock(0, 1, -4, mrw);
        addBlock(1, 1, -2, mrw);
        addBlock(-1, 1, -2, mrw);
        addBlock(1, 1, 0, mar);
        addBlock(1, 1, -1, mar);
        addBlock(1, 1, -3, stairsSouth);

        addBlock(-1, 1, 1, mar);
        addBlock(-1, 1, 2, mrw);
        addBlock(0, 1, 2, mrw);
        addBlock(1, 1, 2, mbr);
        addBlock(1, 1, 3, stairsNorth);
        addBlock(0, 1, 3, mrw);

        addBlock(0, 2, 2, mch);
        addBlock(0, 3, 2, mrw);
        addBlock(0, 2, -2, mrw);
        addBlock(0, 3, -2, mch);

        addBlock(0, 4, 2, stairsNorth);
        addBlock(0, 4, -2, stairsSouth);
    }

}
