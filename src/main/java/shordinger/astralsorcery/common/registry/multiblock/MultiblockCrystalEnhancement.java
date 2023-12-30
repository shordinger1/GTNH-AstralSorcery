/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.multiblock;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;

import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockCrystalEnhancement
 * Created by HellFirePvP
 * Date: 22.04.2017 / 11:23
 */
public class MultiblockCrystalEnhancement extends PatternBlockArray {

    public MultiblockCrystalEnhancement() {
        super(new ResourceLocation(Tags.MODID, "pattern_collector_crystal_enhanced"));
        load();
    }

    private void load() {
        IBlockState mru = BlockMarble.MarbleBlockType.RUNED.asBlock();
        IBlockState mpl = BlockMarble.MarbleBlockType.PILLAR.asBlock();
        IBlockState mch = BlockMarble.MarbleBlockType.CHISELED.asBlock();
        IBlockState mgr = BlockMarble.MarbleBlockType.ENGRAVED.asBlock();
        IBlockState mrw = BlockMarble.MarbleBlockType.RAW.asBlock();

        addBlockCube(mrw, -1, -5, -1, 1, -5, 1);
        for (BlockPos offset : TileCollectorCrystal.offsetsLiquidStarlight) {
            addBlock(
                offset,
                BlocksAS.blockLiquidStarlight.getDefaultState(),
                (state) -> state.getBlock()
                    .equals(BlocksAS.blockLiquidStarlight) && state.getValue(BlockFluidBase.LEVEL) == 0);
        }
        addAirCube(1, 1, 1, -1, -1, -1);
        addBlock(0, 0, 0, BlocksAS.celestialCollectorCrystal.getDefaultState());
        addTileCallback(BlockPos.ORIGIN, new TileEntityCallback() {

            @Override
            public boolean isApplicable(TileEntity te) {
                return te instanceof TileCollectorCrystal;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                IWeakConstellation rand = ConstellationRegistry.getWeakConstellations()
                    .get(
                        new Random().nextInt(
                            ConstellationRegistry.getWeakConstellations()
                                .size()));
                ((TileCollectorCrystal) te).onPlace(
                    rand,
                    null,
                    CrystalProperties.getMaxCelestialProperties(),
                    null,
                    BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL);
            }
        });

        addBlock(0, -2, 0, mch);
        addBlock(0, -3, 0, mpl);
        addBlock(0, -4, 0, mgr);

        addBlock(-2, -4, -2, mch);
        addBlock(-2, -4, 2, mch);
        addBlock(2, -4, 2, mch);
        addBlock(2, -4, -2, mch);
        addBlock(-2, -3, -2, mgr);
        addBlock(-2, -3, 2, mgr);
        addBlock(2, -3, 2, mgr);
        addBlock(2, -3, -2, mgr);

        addBlock(-2, -4, -1, mru);
        addBlock(-2, -4, 0, mru);
        addBlock(-2, -4, 1, mru);
        addBlock(2, -4, -1, mru);
        addBlock(2, -4, 0, mru);
        addBlock(2, -4, 1, mru);
        addBlock(-1, -4, -2, mru);
        addBlock(0, -4, -2, mru);
        addBlock(1, -4, -2, mru);
        addBlock(-1, -4, 2, mru);
        addBlock(0, -4, 2, mru);
        addBlock(1, -4, 2, mru);
    }

}
