/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.data.KnowledgeFragmentData;
import shordinger.astralsorcery.client.data.PersistentDataManager;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.light.ClientLightbeamHandler;
import shordinger.astralsorcery.client.effect.light.EffectLightning;
import shordinger.astralsorcery.client.event.ClientConnectionEventHandler;
import shordinger.astralsorcery.client.event.ClientGatewayHandler;
import shordinger.astralsorcery.client.event.ClientRenderEventHandler;
import shordinger.astralsorcery.client.gui.GuiJournalConstellationCluster;
import shordinger.astralsorcery.client.gui.GuiJournalKnowledgeIndex;
import shordinger.astralsorcery.client.gui.GuiJournalPerkTree;
import shordinger.astralsorcery.client.gui.GuiJournalProgression;
import shordinger.astralsorcery.client.gui.journal.GuiScreenJournal;
import shordinger.astralsorcery.client.gui.journal.bookmark.BookmarkProvider;
import shordinger.astralsorcery.client.models.obj.OBJModelLibrary;
import shordinger.astralsorcery.client.render.entity.*;
import shordinger.astralsorcery.client.render.tile.*;
import shordinger.astralsorcery.client.util.ItemColorizationHelper;
import shordinger.astralsorcery.client.util.JournalRecipeDisplayRecovery;
import shordinger.astralsorcery.client.util.camera.ClientCameraManager;
import shordinger.astralsorcery.client.util.item.AstralTEISR;
import shordinger.astralsorcery.client.util.item.DummyModelLoader;
import shordinger.astralsorcery.client.util.item.ItemRenderRegistry;
import shordinger.astralsorcery.client.util.item.ItemRendererFilteredTESR;
import shordinger.astralsorcery.client.util.mappings.ClientJournalMapping;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.word.RandomWordGenerator;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.auxiliary.tick.TickManager;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.base.patreon.flare.PatreonFlareManagerClient;
import shordinger.astralsorcery.common.block.BlockDynamicColor;
import shordinger.astralsorcery.common.block.BlockDynamicStateMapper;
import shordinger.astralsorcery.common.block.BlockMachine;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.entities.*;
import shordinger.astralsorcery.common.integrations.ModIntegrationGeolosys;
import shordinger.astralsorcery.common.item.base.IMetaItem;
import shordinger.astralsorcery.common.item.base.IOBJItem;
import shordinger.astralsorcery.common.item.base.render.INBTModel;
import shordinger.astralsorcery.common.item.base.render.ItemDynamicColor;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.registry.RegistryBlocks;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.*;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.common.tile.network.TileCrystalLens;
import shordinger.astralsorcery.common.tile.network.TileCrystalPrismLens;
import shordinger.astralsorcery.common.util.FileStorageUtil;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelBakery;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelResourceLocation;
import shordinger.wrapper.net.minecraft.client.renderer.color.BlockColors;
import shordinger.wrapper.net.minecraft.client.renderer.color.ItemColors;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import shordinger.wrapper.net.minecraft.client.resources.IReloadableResourceManager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.client.event.ModelRegistryEvent;
import shordinger.wrapper.net.minecraftforge.client.model.ModelLoader;
import shordinger.wrapper.net.minecraftforge.client.model.ModelLoaderRegistry;
import shordinger.wrapper.net.minecraftforge.client.model.obj.OBJLoader;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fml.client.registry.ClientRegistry;
import shordinger.wrapper.net.minecraftforge.fml.client.registry.RenderingRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:23
 */
public class ClientProxy extends CommonProxy {

    //Marks if the client is connected and received all server data from AS' serverside
    public static boolean connected = false;
    private final ClientScheduler scheduler = new ClientScheduler();

    private static List<RenderInfoBlock> blockRegister = new ArrayList<>();
    private static List<RenderInfoItem> itemRegister = new ArrayList<>();

    @Override
    public void setupConfiguration() {
        super.setupConfiguration();

        Config.addDynamicEntry(new PersistentDataManager.ConfigPersistency());
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
        try {
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(AssetLibrary.resReloadInstance);
        } catch (Exception exc) {
            AstralSorcery.log.warn("Could not add AssetLibrary to resource manager! Texture reloading will have no effect on AstralSorcery textures.");
            AssetLibrary.resReloadInstance.onResourceManagerReload(null);
        }
        ModelLoaderRegistry.registerLoader(new DummyModelLoader()); //IItemRenderer Hook ModelLoader
        OBJLoader.INSTANCE.addDomain(AstralSorcery.MODID);

        super.preInit();

        RandomWordGenerator.init();
        CraftingAccessManager.ignoreJEI = false;
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerFluidRenderers();
        registerEntityRenderers();
        registerDisplayInformationInit();
        registerTileRenderers();
        registerItemRenderers();
    }

    private void registerPendingIBlockColorBlocks() {
        BlockColors colors = Minecraft.getMinecraft().getBlockColors();
        for (BlockDynamicColor b : RegistryBlocks.pendingIBlockColorBlocks) {
            colors.registerBlockColorHandler(b::getColorMultiplier, (Block) b);
        }
    }

    private void registerPendingIItemColorItems() {
        ItemColors colors = Minecraft.getMinecraft().getItemColors();
        for (ItemDynamicColor i : RegistryItems.pendingDynamicColorItems) {
            colors.registerItemColorHandler(i::getColorForItemStack, (Item) i);
        }
    }

    private void registerFluidRenderers() {
        registerFluidRender(BlocksAS.fluidLiquidStarlight);
    }

    private void registerFluidRender(Fluid f) {
        RegistryBlocks.FluidCustomModelMapper mapper = new RegistryBlocks.FluidCustomModelMapper(f);
        Block block = f.getBlock();
        if(block != null) {
            Item item = Item.getItemFromBlock(block);
            if (item != Items.AIR) {
                ModelLoader.registerItemVariants(item);
                ModelLoader.setCustomMeshDefinition(item, mapper);
            } else {
                ModelLoader.setCustomStateMapper(block, mapper);
            }
        }
    }

    @Override
    public void init() {
        super.init();

        PersistentDataManager.INSTANCE.init(FileStorageUtil.getGeneralSubDirectory("astralsorcery_persistent"));

        GuiJournalPerkTree.initializeDrawBuffer();

        registerPendingIBlockColorBlocks();
        registerPendingIItemColorItems();

        MinecraftForge.EVENT_BUS.register(new ClientRenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientConnectionEventHandler());
        MinecraftForge.EVENT_BUS.register(EffectHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(new ClientGatewayHandler());

        GuiScreenJournal.addBookmark(new BookmarkProvider("gui.journal.bm.research.name", 10,
                GuiJournalProgression::getJournalInstance,
                () -> true));
        GuiScreenJournal.addBookmark(new BookmarkProvider("gui.journal.bm.constellations.name", 20,
                GuiJournalConstellationCluster::getConstellationScreen,
                () -> !ResearchManager.clientProgress.getSeenConstellations().isEmpty()));
        GuiScreenJournal.addBookmark(new BookmarkProvider("gui.journal.bm.perks.name", 30,
                GuiJournalPerkTree::new,
                () -> ResearchManager.clientProgress.getAttunedConstellation() != null));
        GuiScreenJournal.addBookmark(new BookmarkProvider("gui.journal.bm.knowledge.name", 40,
                GuiJournalKnowledgeIndex::new,
                () -> !((KnowledgeFragmentData) PersistentDataManager.INSTANCE
                        .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS))
                        .getAllFragments().isEmpty()));
    }

    @Override
    public void postInit() {
        super.postInit();

        TileEntityItemStackRenderer.instance = new AstralTEISR(TileEntityItemStackRenderer.instance); //Wrapping TEISR

        //TexturePreloader.doPreloadRoutine();

        ClientJournalMapping.init();
        OBJModelLibrary.init();

        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(ItemColorizationHelper.instance);

        //Clears tooltip on langfile change or texture changes
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
                .registerReloadListener((mgr) -> PerkTree.PERK_TREE.getPerkPoints().stream().map(PerkTreePoint::getPerk).forEach(AbstractPerk::clearClientCaches));

        JournalRecipeDisplayRecovery.attemptRecipeRecovery();
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if(id < 0 || id >= EnumGuiId.values().length) return null; //Out of range.
        EnumGuiId guiType = EnumGuiId.values()[id];
        return ClientGuiHandler.openGui(guiType, player, world, x, y, z);
    }

    public void registerItemRenderers() {
        //RenderTransformsHelper.init();

        ItemRendererFilteredTESR blockMachineRender = new ItemRendererFilteredTESR();
        blockMachineRender.addRender(BlockMachine.MachineType.TELESCOPE.getMeta(), new TESRTelescope(), new TileTelescope());
        blockMachineRender.addRender(BlockMachine.MachineType.GRINDSTONE.getMeta(), new TESRGrindstone(), new TileGrindstone());
        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.blockMachine), blockMachineRender);

        //ItemRenderRegistry.registerCameraTransforms(Item.getItemFromBlock(BlocksAS.blockMachine), RenderTransformsHelper.BLOCK_TRANSFORMS);

        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.collectorCrystal), new TESRCollectorCrystal());
        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.celestialCollectorCrystal), new TESRCollectorCrystal());

        if(Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleItemRenderer();
        }

        //ItemRenderRegistry.register(ItemsAS.something, new ? implements IItemRenderer());
    }

    @Override
    protected void registerTickHandlers(TickManager manager) {
        super.registerTickHandlers(manager);
        manager.register(new ClientLightbeamHandler());
        manager.register(scheduler);
        manager.register(ClientCameraManager.getInstance());
        manager.register(PatreonFlareManagerClient.INSTANCE);
    }

    @Override
    public void scheduleClientside(Runnable r, int tickDelay) {
        scheduler.addRunnable(r, tickDelay);
    }

    private void registerTileRenderers() {
        registerTESR(TileAltar.class, new TESRAltar());
        registerTESR(TileRitualPedestal.class, new TESRRitualPedestal());
        registerTESR(TileCollectorCrystal.class, new TESRCollectorCrystal());
        registerTESR(TileWell.class, new TESRWell());
        registerTESR(TileGrindstone.class, new TESRGrindstone());
        registerTESR(TileTelescope.class, new TESRTelescope());
        registerTESR(TileFakeTree.class, new TESRFakeTree());
        registerTESR(TileAttunementAltar.class, new TESRAttunementAltar());
        registerTESR(TileCrystalLens.class, new TESRLens());
        registerTESR(TileCrystalPrismLens.class, new TESRPrismLens());
        registerTESR(TileStarlightInfuser.class, new TESRStarlightInfuser());
        registerTESR(TileTranslucent.class, new TESRTranslucentBlock());
        registerTESR(TileAttunementRelay.class, new TESRAttunementRelay());
        registerTESR(TileMapDrawingTable.class, new TESRMapDrawingTable());
        registerTESR(TileChalice.class, new TESRChalice());
        registerTESR(TileObservatory.class, new TESRObservatory());
        if(Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleRender();
        }
    }

    private <T extends TileEntity> void registerTESR(Class<T> tile, TileEntitySpecialRenderer<T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
    }

    public void registerEntityRenderers() {
        //RenderingRegistry.registerEntityRenderingHandler(EntityTelescope.class, new RenderEntityTelescope.Factory());
        //RenderingRegistry.registerEntityRenderingHandler(EntityGrindstone.class, new RenderEntityGrindstone.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityItemHighlighted.class, new RenderEntityItemHighlight.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityFlare.class, new RenderEntityFlare.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityStarburst.class, new RenderEntityStarburst.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityNocturnalSpark.class, new RenderEntityNoOp.Factory<>());
        RenderingRegistry.registerEntityRenderingHandler(EntityIlluminationSpark.class, new RenderEntityNoOp.Factory<>());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrapplingHook.class, new RenderEntityHook.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntitySpectralTool.class, new RenderSpectralTool.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityLiquidSpark.class, new RenderLiquidSpark.Factory());
        //RenderingRegistry.registerEntityRenderingHandler(SpellProjectile.class, new RenderEntitySpellProjectile.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityShootingStar.class, new RenderEntityShootingStar.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityItemExplosionResistant.class, new RenderEntityItemHighlight.Factory());
    }

    public void registerDisplayInformationInit() {
        for (RenderInfoItem modelEntry : itemRegister) {
            if (modelEntry.variant) {
                registerVariantName(modelEntry.item, modelEntry.name);
            }
            if(modelEntry.item instanceof IOBJItem) {
                if(!((IOBJItem) modelEntry.item).hasOBJAsSubmodelDefinition()) {
                    String[] models = ((IOBJItem) modelEntry.item).getOBJModelNames();
                    if(models != null) {
                        for (String modelDef : models) {
                            ModelResourceLocation mrl = new ModelResourceLocation(AstralSorcery.MODID + ":obj/" + modelDef + ".obj", "inventory");
                            ModelBakery.registerItemVariants(modelEntry.item, mrl);
                            ModelLoader.setCustomModelResourceLocation(modelEntry.item, modelEntry.metadata, mrl);
                        }
                    }
                } else { //We expect a wrapper in the blockstates..
                    ModelResourceLocation mrl = new ModelResourceLocation(AstralSorcery.MODID + ":obj/" + modelEntry.name, "inventory");
                    ModelBakery.registerItemVariants(modelEntry.item, mrl);
                    ModelLoader.setCustomModelResourceLocation(modelEntry.item, modelEntry.metadata, mrl);
                }
            } else {
                Item item = modelEntry.item;
                ModelResourceLocation def = new ModelResourceLocation(AstralSorcery.MODID + ":" + modelEntry.name, "inventory");
                if(item instanceof INBTModel) {
                    List<ResourceLocation> out = ((INBTModel) item).getAllPossibleLocations(def);
                    ResourceLocation[] arr = new ResourceLocation[out.size()];
                    arr = out.toArray(arr);
                    ModelBakery.registerItemVariants(item, arr);
                    ModelLoader.setCustomMeshDefinition(item, stack -> ((INBTModel) item).getModelLocation(stack, def));
                } else {
                    ModelLoader.setCustomModelResourceLocation(item, modelEntry.metadata, def);
                }
            }
        }

        for (RenderInfoBlock modelEntry : blockRegister) {
            if(modelEntry.block instanceof BlockDynamicStateMapper) {
                if(((BlockDynamicStateMapper) modelEntry.block).handleRegisterStateMapper()) {
                    ((BlockDynamicStateMapper) modelEntry.block).registerStateMapper();
                }
            }

            Item item = Item.getItemFromBlock(modelEntry.block);
            ModelResourceLocation def = new ModelResourceLocation(AstralSorcery.MODID + ":" + modelEntry.name, "inventory");
            if(item instanceof INBTModel) {
                List<ResourceLocation> out = ((INBTModel) item).getAllPossibleLocations(def);
                ResourceLocation[] arr = new ResourceLocation[out.size()];
                arr = out.toArray(arr);
                ModelBakery.registerItemVariants(item, arr);
                ModelLoader.setCustomMeshDefinition(item, (stack -> ((INBTModel) item).getModelLocation(stack, def)));
            } else {
                ModelLoader.setCustomModelResourceLocation(item, modelEntry.metadata, def);
            }
        }
    }

    @Override
    public void fireLightning(World world, Vector3 from, Vector3 to, Color overlay) {
        EffectLightning lightning = EffectHandler.getInstance().lightning(from, to);
        if(overlay != null) {
            lightning.setOverlayColor(overlay);
        }
    }

    @Override
    public void registerFromSubItems(Item item, String name) {
        if (item instanceof IMetaItem) {
            int[] additionalMetas = ((IMetaItem) item).getSubItems();
            if (additionalMetas != null) {
                for (int meta : additionalMetas) {
                    registerItemRender(item, meta, name);
                }
            }
            return;
        }
        NonNullList<ItemStack> list = NonNullList.create();
        item.getSubItems(item.getCreativeTab(), list);
        if (list.size() > 0) {
            for (ItemStack i : list) {
                registerItemRender(item, i.getItemDamage(), name);
            }
        } else {
            registerItemRender(item, 0, name);
        }
    }

    public void registerVariantName(Item item, String name) {
        ModelBakery.registerItemVariants(item, new ResourceLocation(AstralSorcery.MODID, name));
    }

    public void registerBlockRender(Block block, int metadata, String name) {
        blockRegister.add(new RenderInfoBlock(block, metadata, name));
    }

    public void registerItemRender(Item item, int metadata, String name) {
        registerItemRender(item, metadata, name, false);
    }

    public void registerItemRender(Item item, int metadata, String name, boolean variant) {
        itemRegister.add(new RenderInfoItem(item, metadata, name, variant));
    }

    private static class RenderInfoBlock {

        public Block block;
        public int metadata;
        public String name;

        public RenderInfoBlock(Block block, int metadata, String name) {
            this.block = block;
            this.metadata = metadata;
            this.name = name;
        }
    }

    private static class RenderInfoItem {

        public Item item;
        public int metadata;
        public String name;
        public boolean variant;

        public RenderInfoItem(Item item, int metadata, String name, boolean variant) {
            this.item = item;
            this.metadata = metadata;
            this.name = name;
            this.variant = variant;
        }
    }

}
