/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import static shordinger.astralsorcery.common.lib.BlocksAS.*;

import java.util.LinkedList;
import java.util.List;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.block.*;
import shordinger.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import shordinger.astralsorcery.common.block.fluid.FluidLiquidStarlight;
import shordinger.astralsorcery.common.block.network.*;
import shordinger.astralsorcery.common.integrations.ModIntegrationGeolosys;
import shordinger.astralsorcery.common.migration.MappingMigrationHandler;
import shordinger.astralsorcery.common.tile.*;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.common.tile.network.TileCrystalLens;
import shordinger.astralsorcery.common.tile.network.TileCrystalPrismLens;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.renderer.ItemMeshDefinition;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelResourceLocation;
import shordinger.wrapper.net.minecraft.client.renderer.block.statemap.StateMapperBase;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fluids.FluidRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBlocks
 * Created by HellFirePvP
 * Date: 07.05.2016 / 18:16
 */
public class RegistryBlocks {

    public static List<Block> defaultItemBlocksToRegister = new LinkedList<>();
    public static List<Block> customNameItemBlocksToRegister = new LinkedList<>();
    public static List<BlockDynamicColor> pendingIBlockColorBlocks = new LinkedList<>();

    public static void init() {
        registerFluids();

        registerBlocks();

        registerTileEntities();

        if (Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleBlock();
        }
    }

    private static void registerFluids() {
        FluidLiquidStarlight f = new FluidLiquidStarlight();
        FluidRegistry.registerFluid(f);
        fluidLiquidStarlight = FluidRegistry.getFluid(f.getName());
        blockLiquidStarlight = new FluidBlockLiquidStarlight();
        CommonProxy.registryPrimer.register(
            blockLiquidStarlight.setUnlocalizedName(
                    blockLiquidStarlight.getClass()
                        .getSimpleName()
                        .toLowerCase())
                .setRegistryName(
                    blockLiquidStarlight.getClass()
                        .getSimpleName()
                        .toLowerCase()));
        fluidLiquidStarlight.setBlock(blockLiquidStarlight);

        FluidRegistry.addBucketForFluid(fluidLiquidStarlight);
    }

    // Blocks
    private static void registerBlocks() {
        // WorldGen&Related
        customOre = registerBlock(new BlockCustomOre());
        queueCustomNameItemBlock(customOre);
        customSandOre = registerBlock(new BlockCustomSandOre());
        queueCustomNameItemBlock(customSandOre);
        customFlower = registerBlock(new BlockCustomFlower());
        queueCustomNameItemBlock(customFlower);
        blockMarble = registerBlock(new BlockMarble());
        queueCustomNameItemBlock(blockMarble);
        blockMarbleStairs = registerBlock(new BlockMarbleStairs());
        queueDefaultItemBlock(blockMarbleStairs);
        blockMarbleSlab = registerBlock(new BlockMarbleSlab());
        blockMarbleDoubleSlab = registerBlock(new BlockMarbleDoubleSlab());
        blockBlackMarble = registerBlock(new BlockBlackMarble());
        queueCustomNameItemBlock(blockBlackMarble);
        blockInfusedWood = registerBlock(new BlockInfusedWood());
        queueCustomNameItemBlock(blockInfusedWood);
        blockVolatileLight = registerBlock(new BlockFlareLight());
        queueDefaultItemBlock(blockVolatileLight);
        blockVanishing = registerBlock(new BlockVanishing());
        queueDefaultItemBlock(blockVanishing);
        blockChalice = registerBlock(new BlockChalice());
        queueDefaultItemBlock(blockChalice);
        blockBore = registerBlock(new BlockBore());
        queueDefaultItemBlock(blockBore);
        blockBoreHead = registerBlock(new BlockBoreHead());
        queueCustomNameItemBlock(blockBoreHead);

        // Mechanics
        blockAltar = registerBlock(new BlockAltar());
        attunementAltar = registerBlock(new BlockAttunementAltar());
        queueDefaultItemBlock(attunementAltar);
        attunementRelay = registerBlock(new BlockAttunementRelay());
        queueDefaultItemBlock(attunementRelay);
        ritualPedestal = registerBlock(new BlockRitualPedestal());
        blockWell = registerBlock(new BlockWell());
        queueDefaultItemBlock(blockWell);
        blockIlluminator = registerBlock(new BlockWorldIlluminator());
        queueDefaultItemBlock(blockIlluminator);
        blockMachine = registerBlock(new BlockMachine());
        queueCustomNameItemBlock(blockMachine);
        blockFakeTree = registerBlock(new BlockFakeTree());
        queueDefaultItemBlock(blockFakeTree);
        starlightInfuser = registerBlock(new BlockStarlightInfuser());
        queueDefaultItemBlock(starlightInfuser);
        ritualLink = registerBlock(new BlockRitualLink());
        queueDefaultItemBlock(ritualLink);
        blockPortalNode = registerBlock(new BlockPortalNode());
        queueDefaultItemBlock(blockPortalNode);

        treeBeacon = registerBlock(new BlockTreeBeacon());
        queueDefaultItemBlock(treeBeacon);
        translucentBlock = registerBlock(new BlockTranslucentBlock());
        queueDefaultItemBlock(translucentBlock);
        drawingTable = registerBlock(new BlockMapDrawingTable());
        queueDefaultItemBlock(drawingTable);
        celestialGateway = registerBlock(new BlockCelestialGateway());
        queueDefaultItemBlock(celestialGateway);
        blockObservatory = registerBlock(new BlockObservatory());
        queueDefaultItemBlock(blockObservatory);

        lens = registerBlock(new BlockLens());
        lensPrism = registerBlock(new BlockPrism());
        queueDefaultItemBlock(lens);
        queueDefaultItemBlock(lensPrism);

        celestialCrystals = registerBlock(new BlockCelestialCrystals());
        queueCustomNameItemBlock(celestialCrystals);
        gemCrystals = registerBlock(new BlockGemCrystals());
        queueCustomNameItemBlock(gemCrystals);

        // Machines&Related
        // stoneMachine = registerBlock(new BlockStoneMachine());
        collectorCrystal = registerBlock(new BlockCollectorCrystal());
        celestialCollectorCrystal = registerBlock(new BlockCelestialCollectorCrystal());

        blockStructural = registerBlock(new BlockStructural());
        queueCustomNameItemBlock(blockStructural);
    }

    // Called after items are registered.
    // Necessary for blocks that require different models/renders for different metadata values
    public static void initRenderRegistry() {
        registerBlockRender(blockMarble);
        registerBlockRender(blockBlackMarble);
        registerBlockRender(blockInfusedWood);
        registerBlockRender(blockAltar);
        registerBlockRender(blockBoreHead);
        registerBlockRender(customOre);
        registerBlockRender(customSandOre);
        registerBlockRender(customFlower);
        registerBlockRender(blockStructural);
        registerBlockRender(blockMachine);
        registerBlockRender(treeBeacon);

        registerBlockRender(celestialCrystals);
        registerBlockRender(gemCrystals);
    }

    // Tiles
    private static void registerTileEntities() {
        registerTile(TileAltar.class);
        registerTile(TileRitualPedestal.class);
        registerTile(TileCollectorCrystal.class);
        registerTile(TileCelestialCrystals.class);
        registerTile(TileGemCrystals.class);
        registerTile(TileWell.class);
        registerTile(TileIlluminator.class);
        registerTile(TileTelescope.class);
        registerTile(TileGrindstone.class);
        registerTile(TileStructuralConnector.class);
        registerTile(TileFakeTree.class);
        registerTile(TileAttunementAltar.class);
        registerTile(TileStarlightInfuser.class);
        registerTile(TileTreeBeacon.class);
        registerTile(TileRitualLink.class);
        registerTile(TileTranslucent.class);
        registerTile(TileAttunementRelay.class);
        registerTile(TileMapDrawingTable.class);
        registerTile(TileCelestialGateway.class);
        registerTile(TileOreGenerator.class);
        registerTile(TileVanishing.class);
        registerTile(TileChalice.class);
        registerTile(TileBore.class);
        registerTile(TileStructController.class);
        registerTile(TileObservatory.class);

        registerTile(TileCrystalLens.class);
        registerTile(TileCrystalPrismLens.class);
    }

    public static void queueCustomNameItemBlock(Block block) {
        customNameItemBlocksToRegister.add(block);
    }

    public static void queueDefaultItemBlock(Block block) {
        defaultItemBlocksToRegister.add(block);
    }

    private static <T extends Block> T registerBlock(T block, String name) {
        CommonProxy.registryPrimer.register(
            block.setUnlocalizedName(name)
                .setRegistryName(name));
        if (block instanceof BlockDynamicColor) {
            pendingIBlockColorBlocks.add((BlockDynamicColor) block);
        }
        return block;
    }

    public static <T extends Block> T registerBlock(T block) {
        return registerBlock(
            block,
            block.getClass()
                .getSimpleName()
                .toLowerCase());
    }

    private static void registerBlockRender(Block block) {
        if (block instanceof BlockVariants) {
            for (IBlockState state : ((BlockVariants) block).getValidStates()) {
                String unlocName = ((BlockVariants) block).getBlockName(state);
                String name = unlocName + "_" + ((BlockVariants) block).getStateName(state);
                AstralSorcery.proxy.registerVariantName(Item.getItemFromBlock(block), name);
                AstralSorcery.proxy.registerBlockRender(block, block.getMetaFromState(state), name);
            }
        } else {
            AstralSorcery.proxy.registerVariantName(Item.getItemFromBlock(block), block.getUnlocalizedName());
            AstralSorcery.proxy.registerBlockRender(block, 0, block.getUnlocalizedName());
        }
    }

    private static void registerTile(Class<? extends TileEntity> tile, String name) {
        GameRegistry.registerTileEntity(tile, new ResourceLocation(AstralSorcery.MODID, name));

        MappingMigrationHandler.listenTileMigration(name);
    }

    public static void registerTile(Class<? extends TileEntity> tile) {
        registerTile(
            tile,
            tile.getSimpleName()
                .toLowerCase());
    }

    public static class FluidCustomModelMapper extends StateMapperBase implements ItemMeshDefinition {

        private final ModelResourceLocation res;

        public FluidCustomModelMapper(Fluid f) {
            this.res = new ModelResourceLocation(AstralSorcery.MODID.toLowerCase() + ":blockfluids", f.getName());
        }

        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return res;
        }

        @Override
        public ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return res;
        }

    }

}
