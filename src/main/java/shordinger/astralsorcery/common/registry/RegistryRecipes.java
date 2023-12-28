/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import static shordinger.astralsorcery.common.crafting.altar.AltarRecipeRegistry.*;
import static shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry.registerGrindstoneRecipe;
import static shordinger.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;
import static shordinger.astralsorcery.common.crafting.helper.ShapelessRecipe.Builder.newShapelessRecipe;
import static shordinger.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry.registerInfusionRecipe;
import static shordinger.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry.registerLowConsumptionInfusion;
import static shordinger.astralsorcery.common.lib.RecipesAS.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Iterables;

import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.block.*;
import shordinger.astralsorcery.common.block.BlockBlackMarble;
import shordinger.astralsorcery.common.block.BlockCustomOre;
import shordinger.astralsorcery.common.block.BlockCustomSandOre;
import shordinger.astralsorcery.common.block.BlockInfusedWood;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.block.BlockMarbleSlab;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.RecipeChangeWandColor;
import shordinger.astralsorcery.common.crafting.altar.recipes.*;
import shordinger.astralsorcery.common.crafting.altar.recipes.AttunementAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.CapeAttunementRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.CelestialGatewayRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.CollectorCrystalRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.ConstellationPaperRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.CrystalToolRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.DrawingTableRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.EnchantmentAmuletRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.LensRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.LiquidBoreRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.PrismLensRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.ReAttunementStarRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.RecipeRitualPedestal;
import shordinger.astralsorcery.common.crafting.altar.recipes.ResonatorLiquidRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.ResonatorStructureRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.SextantUpgradeRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.TelescopeRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.VortexBoreRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.WandAttunementRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.upgrade.AttunementUpgradeRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.upgrade.ConstellationUpgradeRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.upgrade.TraitUpgradeRecipe;
import shordinger.astralsorcery.common.crafting.grindstone.CrystalSharpeningRecipe;
import shordinger.astralsorcery.common.crafting.grindstone.CrystalToolSharpeningRecipe;
import shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import shordinger.astralsorcery.common.crafting.grindstone.SwordSharpeningRecipe;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.crafting.helper.SmeltingRecipe;
import shordinger.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import shordinger.astralsorcery.common.crafting.infusion.recipes.InfusionRecipeChargeTool;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.item.ItemColoredLens;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.item.useables.ItemUsableDust;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.lib.RecipesAS;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipes
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:49
 */
public class RegistryRecipes {

    public static DiscoveryRecipe rJournal;
    public static DiscoveryRecipe rHandTelescope;
    public static TelescopeRecipe rTelescope;
    public static shordinger.astralsorcery.common.crafting.altar.recipes.GrindstoneRecipe rGrindstone;
    public static DiscoveryRecipe rWoodEnriched;
    public static RecipeRitualPedestal rRitualPedestal;
    public static DiscoveryRecipe rLightwell;
    public static DiscoveryRecipe rSkyResonator;
    public static DiscoveryRecipe rIlluminator;
    public static DiscoveryRecipe rAttenuationAltarRelay;
    public static AttunementAltarRecipe rAttunementAltar;
    public static ConstellationRecipe rStarlightInfuser;
    public static ConstellationRecipe rTreeBeacon;
    public static ConstellationRecipe rIlluminationWand;
    public static AttunementRecipe rArchitectWand;
    public static AttunementRecipe rExchangeWand;
    public static AttunementRecipe rGrappleWand;
    public static AttunementRecipe rShiftStar;
    public static ConstellationRecipe rRitualLink;
    public static DiscoveryRecipe rIlluminationPowder;
    public static DiscoveryRecipe rNocturnalPowder;
    public static CelestialGatewayRecipe rCelestialGateway;
    public static DrawingTableRecipe rDrawingTable;
    public static ConstellationRecipe rInfusedGlass;
    public static AttunementRecipe rKnowledgeShare;
    public static TraitRecipe rCapeBase;
    public static TraitRecipe rChalice;
    public static TraitRecipe rBore;
    public static LiquidBoreRecipe rBoreHeadLiquid;
    public static VortexBoreRecipe rBoreHeadVortex;
    public static ConstellationRecipe rEnchAmuletOriginal, rEnchAmuletReroll;
    public static DiscoveryRecipe rSextant;
    public static SextantUpgradeRecipe rSextantUpgrade;
    public static TraitRecipe rObservatory;
    public static Map<IMajorConstellation, ReAttunementStarRecipe> rAttStarRecipes = new HashMap<>();
    public static AttunementRecipe rPerkSeal;

    public static ResonatorLiquidRecipe rResonatorLiquid;
    public static ResonatorStructureRecipe rResonatorStructure;

    public static WandAttunementRecipe rWandAugmentDiscidia, rWandAugmentAevitas, rWandAugmentVicio,
        rWandAugmentEvorsio, rWandAugmentArmara;

    public static LensRecipe rLens;
    public static PrismLensRecipe rPrism;
    public static CollectorCrystalRecipe rCollectRock, rCollectCel;

    public static AttunementUpgradeRecipe rAltarUpgradeAttenuation;
    public static ConstellationUpgradeRecipe rAltarUpgradeConstellation;
    public static TraitUpgradeRecipe rAltarUpgradeTrait;

    // CraftingComponents
    public static DiscoveryRecipe rCCGlassLens;
    public static ConstellationRecipe rGlassLensFire, rGlassLensBreak, rGlassLensGrowth, rGlassLensDamage,
        rGlassLensRegeneration, rGlassLensPush, rGlassLensSpectral;

    // Tools
    public static CrystalToolRecipe rCToolPick, rCToolShovel, rCToolAxe, rCToolSword;

    public static DiscoveryRecipe rWand;
    public static AttunementRecipe rLinkTool;

    public static void initVanillaRecipes() {
        initVanilla();
    }

    public static void initAstralRecipes() {
        initAltarRecipes();

        initInfusionRecipes();

        initCapeAttunementRecipes();

        initGrindstoneRecipes();

        InfusionRecipeRegistry.cacheLocalRecipes();
        cacheLocalRecipes();
    }

    private static void initInfusionRecipes() {
        registerLowConsumptionInfusion(
            ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
            ItemCraftingComponent.MetaType.AQUAMARINE.asStack());
        registerLowConsumptionInfusion(
            ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
            new ItemStack(Blocks.GLASS_PANE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.CLAY, 1, 0), new ItemStack(Blocks.SAND, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.GRASS, 1, 0), new ItemStack(Blocks.DIRT, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.ENDER_EYE, 1, 0), new ItemStack(Items.ENDER_PEARL, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GLOWSTONE_DUST, 1, 0), new ItemStack(Items.GUNPOWDER, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GUNPOWDER, 1, 0), new ItemStack(Items.REDSTONE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.DYE, 4, 15), new ItemStack(Items.BONE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.BLAZE_POWDER, 4, 0), new ItemStack(Items.BLAZE_ROD, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.MAGMA_CREAM, 1, 0), new ItemStack(Items.SLIME_BALL, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GOLDEN_CARROT, 1, 0), new ItemStack(Items.CARROT, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.SPECKLED_MELON, 1, 0), new ItemStack(Items.MELON, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.IRON_INGOT, 3, 0), new ItemStack(Blocks.IRON_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GOLD_INGOT, 3, 0), new ItemStack(Blocks.GOLD_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.LAPIS_BLOCK, 1, 0), new ItemStack(Blocks.LAPIS_ORE, 1, 0));
        registerLowConsumptionInfusion(
            new ItemStack(Blocks.REDSTONE_BLOCK, 1, 0),
            new ItemStack(Blocks.REDSTONE_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.DIAMOND, 4, 0), new ItemStack(Blocks.DIAMOND_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.EMERALD, 4, 0), new ItemStack(Blocks.EMERALD_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.ICE, 1, 0), new ItemStack(Blocks.GLASS, 1, 0));
        registerLowConsumptionInfusion(
            BlockInfusedWood.WoodType.INFUSED.asStack(),
            BlockInfusedWood.WoodType.RAW.asStack());

        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalAxe));
        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalPickaxe));
        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalShovel));
        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalSword));
    }

    private static void initGrindstoneRecipes() {
        registerGrindstoneRecipe(new CrystalToolSharpeningRecipe(1));
        registerGrindstoneRecipe(new CrystalSharpeningRecipe(1));
        registerGrindstoneRecipe(new SwordSharpeningRecipe());

        registerGrindstoneRecipe(
            ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),
            ItemCraftingComponent.MetaType.STARDUST.asStack(),
            20);

        GrindstoneRecipeRegistry.cacheLocalFallback();
    }

    public static void initGrindstoneOreRecipes() {
        if (!Config.grindstoneAddDustRecipes) {
            return;
        }

        String srcPrefix = "ore";
        String dstPrefix = "dust";

        for (String oreDict : OreDictionary.getOreNames()) {
            if (oreDict.startsWith(srcPrefix)) {
                String suffix = oreDict.substring(srcPrefix.length());
                attemptRegisterGrindstoneRecipe(oreDict, dstPrefix + suffix, 0.85F);
            }
        }
    }

    private static void attemptRegisterGrindstoneRecipe(String nameIn, String nameOut, float doubleChance) {
        boolean multi = nameIn.endsWith("Redstone");

        NonNullList<ItemStack> inputs = ItemUtils.getStacksOfOredict(nameIn);
        ItemStack output = Iterables.getFirst(ItemUtils.getStacksOfOredict(nameOut), null);
        if (!inputs.isEmpty() && !output.isEmpty()) {
            if (output.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                NonNullList<ItemStack> st = NonNullList.create();
                output.getItem()
                    .getSubItems(
                        output.getItem()
                            .getCreativeTab(),
                        st);
                if (!st.isEmpty()) {
                    output = Iterables.getFirst(st, null);
                }
            }

            if (!output.isEmpty()) {
                output = ItemUtils.copyStackWithSize(output, output.getCount());
                if (multi) {
                    output.setCount(output.getCount() * 4);
                }
            }
            registerGrindstoneRecipe(new GrindstoneRecipe(new ItemHandle(inputs), output, 12, doubleChance));
        }
    }

    private static void initVanilla() {
        // RecipeSorter.register("LightProximityCrafting", ShapedLightProximityRecipe.class,
        // RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        // RecipeSorter.register("ShapedRecipeAdapter", AccessibleRecipeAdapater.class, RecipeSorter.Category.SHAPED,
        // "after:minecraft:shaped");
        // RecipeSorter.register("RecipeChangeIlluminationWandColor", RecipeChangeWandColor.class,
        // RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        CommonProxy.registryPrimer.register(RecipeChangeWandColor.INSTANCE);

        if (Config.lightProximityAltarRecipe) {
            rLPRAltar = newShapedRecipe("altar_tier_1", BlocksAS.blockAltar)
                .addPart(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(
                    OreDictAlias.BLOCK_MARBLE,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_CRAFTING_TABLE, ShapedRecipeSlot.CENTER)
                .buildAndRegisterLightCraftingRecipe();
        }
        if (Config.lightProximityResonatingWandRecipe) {
            rLPRWand = newShapedRecipe("tool_basicwand", ItemsAS.wand)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_ENDERPEARL, ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.LOWER_LEFT)
                .buildAndRegisterLightCraftingRecipe();
        }

        rRJournal = newShapedRecipe("journal", ItemsAS.journal)
            .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), ShapedRecipeSlot.UPPER_CENTER)
            .addPart(Items.BOOK, ShapedRecipeSlot.CENTER)
            .addPart(
                OreDictAlias.ITEM_AQUAMARINE,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.RIGHT,
                ShapedRecipeSlot.LOWER_CENTER)
            .buildAndRegisterShapedRecipe();
        rCCParchment = newShapedRecipe(
            "cc_parchment",
            ItemUtils.copyStackWithSize(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), 4))
            .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.CENTER)
            .addPart(
                Items.PAPER,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LOWER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.RIGHT)
            .buildAndRegisterShapedRecipe();

        rBlackMarbleRaw = newShapedRecipe(
            "marble_black_raw",
            new ItemStack(BlocksAS.blockBlackMarble, 8, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
            .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.values())
            .addPart(Items.COAL, ShapedRecipeSlot.CENTER)
            .buildAndRegisterShapedRecipe();
        rBlackMarbleArch = newShapedRecipe(
            "black_marble_arch",
            new ItemStack(BlocksAS.blockBlackMarble, 3, BlockBlackMarble.BlackMarbleBlockType.ARCH.ordinal()))
            .addPart(
                BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT)
            .buildAndRegisterShapedRecipe();
        rBlackMarbleBricks = newShapedRecipe(
            "black_marble_bricks",
            new ItemStack(BlocksAS.blockBlackMarble, 4, BlockBlackMarble.BlackMarbleBlockType.BRICKS.ordinal()))
            .addPart(
                BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.CENTER)
            .buildAndRegisterShapedRecipe();
        rBlackMarbleChiseled = newShapedRecipe(
            "black_marble_chiseled",
            new ItemStack(BlocksAS.blockBlackMarble, 4, BlockBlackMarble.BlackMarbleBlockType.CHISELED.ordinal()))
            .addPart(
                BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.RIGHT,
                ShapedRecipeSlot.LOWER_CENTER)
            .buildAndRegisterShapedRecipe();
        rBlackMarbleEngraved = newShapedRecipe(
            "black_marble_engraved",
            new ItemStack(BlocksAS.blockBlackMarble, 5, BlockBlackMarble.BlackMarbleBlockType.ENGRAVED.ordinal()))
            .addPart(
                BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.RIGHT,
                ShapedRecipeSlot.LOWER_CENTER)
            .addPart(BlockBlackMarble.BlackMarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.CENTER)
            .buildAndRegisterShapedRecipe();
        rBlackMarbleRuned = newShapedRecipe(
            "black_marble_runed",
            new ItemStack(BlocksAS.blockBlackMarble, 3, BlockBlackMarble.BlackMarbleBlockType.RUNED.ordinal()))
            .addPart(
                BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_RIGHT)
            .addPart(BlockBlackMarble.BlackMarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.UPPER_CENTER)
            .buildAndRegisterShapedRecipe();
        rBlackMarblePillar = newShapedRecipe(
            "black_marble_pillar",
            new ItemStack(BlocksAS.blockBlackMarble, 2, BlockBlackMarble.BlackMarbleBlockType.PILLAR.ordinal()))
            .addPart(
                BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.LEFT)
            .buildAndRegisterShapedRecipe();

        rMarbleArch = newShapedRecipe(
            "marble_arch",
            new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
            .addPart(
                BlockMarble.MarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT)
            .buildAndRegisterShapedRecipe();
        rMarbleBricks = newShapedRecipe(
            "marble_bricks",
            new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
            .addPart(
                OreDictAlias.BLOCK_MARBLE,
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.CENTER)
            .buildAndRegisterShapedRecipe();
        rMarbleChiseled = newShapedRecipe(
            "marble_chiseled",
            new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
            .addPart(
                BlockMarble.MarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.RIGHT,
                ShapedRecipeSlot.LOWER_CENTER)
            .buildAndRegisterShapedRecipe();
        rMarbleEngraved = newShapedRecipe(
            "marble_engraved",
            new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
            .addPart(
                BlockMarble.MarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.RIGHT,
                ShapedRecipeSlot.LOWER_CENTER)
            .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.CENTER)
            .buildAndRegisterShapedRecipe();
        rMarbleRuned = newShapedRecipe(
            "marble_runed",
            new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
            .addPart(
                BlockMarble.MarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_RIGHT)
            .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.UPPER_CENTER)
            .buildAndRegisterShapedRecipe();
        rMarblePillar = newShapedRecipe(
            "marble_pillar",
            new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
            .addPart(BlockMarble.MarbleBlockType.RAW.asStack(), ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.LEFT)
            .buildAndRegisterShapedRecipe();
        rMarbleStairs = newShapedRecipe("marble_stairs", new ItemStack(BlocksAS.blockMarbleStairs, 4))
            .addPart(
                BlockMarble.MarbleBlockType.RAW.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.CENTER,
                ShapedRecipeSlot.LOWER_LEFT,
                ShapedRecipeSlot.LOWER_CENTER,
                ShapedRecipeSlot.LOWER_RIGHT)
            .buildAndRegisterShapedRecipe();
        rMarbleSlab = newShapedRecipe(
            "marble_slab",
            new ItemStack(BlocksAS.blockMarbleSlab, 6, BlockMarbleSlab.EnumType.BRICKS.ordinal()))
            .addPart(
                BlockMarble.MarbleBlockType.BRICKS.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT)
            .buildAndRegisterShapedRecipe();

        rWoodPlanks = newShapelessRecipe(
            "infused_wood_planks",
            ItemUtils.copyStackWithSize(BlockInfusedWood.WoodType.PLANKS.asStack(), 4))
            .addPart(new ItemHandle(BlockInfusedWood.WoodType.RAW.asStack()))
            .buildAndRegisterShapelessRecipe();
        rWoodArch = newShapedRecipe(
            "infused_wood_arch",
            ItemUtils.copyStackWithSize(BlockInfusedWood.WoodType.ARCH.asStack(), 3))
            .addPart(
                BlockInfusedWood.WoodType.PLANKS.asStack(),
                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT)
            .buildAndRegisterShapedRecipe();
        rWoodPillar = newShapedRecipe(
            "infused_wood_column",
            ItemUtils.copyStackWithSize(BlockInfusedWood.WoodType.COLUMN.asStack(), 3))
            .addPart(
                BlockInfusedWood.WoodType.PLANKS.asStack(),
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.CENTER,
                ShapedRecipeSlot.LOWER_CENTER)
            .buildAndRegisterShapedRecipe();
        rWoodEngraved = newShapedRecipe(
            "infused_wood_engraved",
            ItemUtils.copyStackWithSize(BlockInfusedWood.WoodType.ENGRAVED.asStack(), 4))
            .addPart(
                BlockInfusedWood.WoodType.PLANKS.asStack(),
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT,
                ShapedRecipeSlot.RIGHT,
                ShapedRecipeSlot.LOWER_CENTER)
            .buildAndRegisterShapedRecipe();

        rSmeltStarmetalOre = SmeltingRecipe.Builder
            .newSmelting("smelting_starmetal_ore", ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack())
            .setInput(new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal()))
            .setExp(2F)
            .buildAndRegister();
        rSmeltAquamarineOre = SmeltingRecipe.Builder
            .newSmelting(
                "smelting_aquamarine_ore",
                ItemUtils.copyStackWithSize(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(), 3))
            .setInput(new ItemStack(BlocksAS.customSandOre, 1, BlockCustomSandOre.OreType.AQUAMARINE.ordinal()))
            .setExp(1F)
            .buildAndRegister();
    }

    private static void initAltarRecipes() {
        rTelescope = registerAltarRecipe(new TelescopeRecipe());
        rGrindstone = registerAltarRecipe(
            new shordinger.astralsorcery.common.crafting.altar.recipes.GrindstoneRecipe());
        rGrindstone.setPassiveStarlightRequirement(150);

        rRitualPedestal = registerAltarRecipe(new RecipeRitualPedestal());

        rLens = registerAltarRecipe(new LensRecipe());
        rLens.setPassiveStarlightRequirement(1000);

        rPrism = registerAltarRecipe(new PrismLensRecipe());

        rCollectRock = registerAltarRecipe(new CollectorCrystalRecipe(false));
        rCollectCel = registerAltarRecipe(new CollectorCrystalRecipe(true));

        rAttunementAltar = registerAltarRecipe(new AttunementAltarRecipe());

        rAltarUpgradeAttenuation = registerAltarRecipe(new AttunementUpgradeRecipe());
        rAltarUpgradeConstellation = registerAltarRecipe(new ConstellationUpgradeRecipe());
        rAltarUpgradeTrait = registerAltarRecipe(new TraitUpgradeRecipe());

        DiscoveryRecipe dr = registerDiscoveryRecipe(rCCParchment);
        dr.setPassiveStarlightRequirement(50);

        rCelestialGateway = registerAltarRecipe(new CelestialGatewayRecipe());
        rDrawingTable = registerAltarRecipe(new DrawingTableRecipe());

        rResonatorLiquid = registerAltarRecipe(new ResonatorLiquidRecipe());
        rResonatorStructure = registerAltarRecipe(new ResonatorStructureRecipe());

        rBoreHeadLiquid = registerAltarRecipe(new LiquidBoreRecipe());
        rBoreHeadVortex = registerAltarRecipe(new VortexBoreRecipe());

        rSextantUpgrade = registerAltarRecipe(new SextantUpgradeRecipe());

        for (IMajorConstellation cst : ConstellationRegistry.getMajorConstellations()) {
            ReAttunementStarRecipe recipe = new ReAttunementStarRecipe(cst);
            registerAltarRecipe(recipe);
            rAttStarRecipes.put(cst, recipe);
        }

        rPerkSeal = registerAttenuationRecipe(
            newShapedRecipe("internal/altar/perkseal", ItemsAS.perkSeal)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(
                    ItemUsableDust.DustType.NOCTURNAL.asStack(),
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());

        rObservatory = registerTraitRecipe(
            newShapedRecipe("internal/altar/observatory", BlocksAS.blockObservatory)
                .addPart(OreDictAlias.ITEM_GOLD_NUGGET, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemsAS.infusedGlass, ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rObservatory.setAttItem(
            ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
            AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT);
        rObservatory.setAttItem(OreDictAlias.ITEM_GOLD_INGOT, AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rObservatory.setCstItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        rObservatory.setCstItem(
            OreDictAlias.ITEM_GOLD_INGOT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT);
        rObservatory.setInnerTraitItem(OreDictAlias.ITEM_GOLD_INGOT, TraitRecipe.TraitRecipeSlot.LOWER_CENTER);
        rObservatory.setInnerTraitItem(
            OreDictAlias.ITEM_GOLD_NUGGET,
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rObservatory.addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack())
            .addOuterTraitItem(OreDictAlias.ITEM_STARMETAL_DUST)
            .addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack())
            .addOuterTraitItem(ItemUsableDust.DustType.NOCTURNAL.asStack())
            .addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack())
            .addOuterTraitItem(OreDictAlias.ITEM_STARMETAL_DUST)
            .addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack())
            .addOuterTraitItem(ItemUsableDust.DustType.NOCTURNAL.asStack());

        rObservatory.setRequiredConstellation(Constellations.lucerna);

        rSextant = registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe("internal/altar/sextant", ItemsAS.sextant)
                    .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                    .addPart(
                        OreDictAlias.ITEM_STICKS,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                    .addPart(
                        ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.UPPER_CENTER)
                    .unregisteredAccessibleShapedRecipe()) {

                @Override
                public boolean allowsForChaining() {
                    return false;
                }
            });
        rSextant.setPassiveStarlightRequirement(450);

        rEnchAmuletOriginal = registerAltarRecipe(
            new EnchantmentAmuletRecipe(
                newShapedRecipe("internal/altar/enchantment_amulet_craft", ItemsAS.enchantmentAmulet)
                    .addPart(Items.string, ShapedRecipeSlot.UPPER_CENTER)
                    .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT)
                    .addPart(ItemsAS.shiftingStar, ShapedRecipeSlot.CENTER)
                    .addPart(Items.ender_eye, ShapedRecipeSlot.LOWER_CENTER)
                    .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                    .unregisteredAccessibleShapedRecipe()) {

                @Override
                public int craftingTickTime() {
                    return 600;
                }
            });
        rEnchAmuletOriginal.setCstItem(
            OreDictAlias.ITEM_STARMETAL_DUST,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);
        rEnchAmuletOriginal.setPassiveStarlightRequirement(3100);

        rEnchAmuletReroll = registerAltarRecipe(
            new EnchantmentAmuletRecipe(
                newShapedRecipe("internal/altar/enchantment_amulet_reroll", ItemsAS.enchantmentAmulet)
                    .addPart(BlocksAS.fluidLiquidStarlight, ShapedRecipeSlot.LOWER_CENTER)
                    .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), ShapedRecipeSlot.UPPER_CENTER)
                    .addPart(ItemsAS.enchantmentAmulet, ShapedRecipeSlot.CENTER)
                    .addPart(
                        OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                    .unregisteredAccessibleShapedRecipe()) {

                @Override
                public int craftingTickTime() {
                    return 250;
                }
            });
        rEnchAmuletReroll.setPassiveStarlightRequirement(1400);

        rBore = registerTraitRecipe(
            newShapedRecipe("internal/altar/bore_core", BlocksAS.blockBore)
                .addPart(
                    OreDictAlias.ITEM_GOLD_INGOT,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.RIGHT)
                .addPart(
                    ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                    ShapedRecipeSlot.CENTER,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        rBore.setAttItem(
            BlockInfusedWood.WoodType.ENRICHED.asStack(),
            AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT,
            AttunementRecipe.AttunementAltarSlot.UPPER_LEFT);
        rBore.setCstItem(
            BlockInfusedWood.WoodType.ENRICHED.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        rBore.setInnerTraitItem(
            BlockInfusedWood.WoodType.ENRICHED.asStack(),
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rBore.setInnerTraitItem(
            BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        rBore.setCstItem(
            BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        rBore.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        rBore.addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        rBore.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        rBore.addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        rBore.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        rBore.setPassiveStarlightRequirement(6500);

        rChalice = registerTraitRecipe(
            newShapedRecipe("internal/altar/chalice", BlocksAS.blockChalice)
                .addPart(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(), ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rChalice.setCstItem(
            OreDictAlias.ITEM_GOLD_INGOT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT);
        rChalice.setInnerTraitItem(
            ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        rChalice.setInnerTraitItem(
            ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rChalice.setInnerTraitItem(
            BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
            TraitRecipe.TraitRecipeSlot.LOWER_CENTER);
        for (int i = 0; i < 5; i++) {
            rChalice.addOuterTraitItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack());
        }
        rChalice.setPassiveStarlightRequirement(5500);

        for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
            List<ItemHandle> signature = c.getConstellationSignatureItems();
            if (!signature.isEmpty()) {
                AccessibleRecipeAdapater shapedPaper = newShapedRecipe(
                    "internal/altar/constellationpaper/" + c.getSimpleName()
                        .toLowerCase(),
                    ItemsAS.constellationPaper)
                    .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), ShapedRecipeSlot.CENTER)
                    .addPart(Items.feather, ShapedRecipeSlot.UPPER_CENTER)
                    .addPart(OreDictAlias.getDyeOreDict(EnumDyeColor.BLACK), ShapedRecipeSlot.LOWER_CENTER)
                    .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                    .unregisteredAccessibleShapedRecipe();

                ConstellationPaperRecipe recipe = new ConstellationPaperRecipe(shapedPaper, c);
                ItemHandle first = Iterables.getFirst(signature, null); // Never null.
                recipe.setInnerTraitItem(first, TraitRecipe.TraitRecipeSlot.values());
                for (ItemHandle s : signature) {
                    recipe.addOuterTraitItem(s);
                }
                registerAltarRecipe(recipe);
                RecipesAS.paperCraftingRecipes.put(c, recipe);
            }
        }

        rWandAugmentAevitas = registerAltarRecipe(
            new WandAttunementRecipe(
                Constellations.aevitas,
                newShapedRecipe("internal/altar/wand/aevitas", ItemsAS.wand)
                    .addPart(ItemsAS.wand, ShapedRecipeSlot.CENTER)
                    .addPart(
                        ItemUsableDust.DustType.ILLUMINATION.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe()));
        rWandAugmentAevitas.setCstItem(
            OreDictAlias.ITEM_GLOWSTONE_DUST,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        rWandAugmentAevitas.setInnerTraitItem(
            OreDictAlias.BLOCK_SAPLING,
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rWandAugmentAevitas.setInnerTraitItem(
            Items.PRISMARINE_CRYSTALS,
            TraitRecipe.TraitRecipeSlot.LOWER_CENTER,
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        ItemHandle first = Iterables.getFirst(Constellations.aevitas.getConstellationSignatureItems(), null);
        rWandAugmentAevitas.addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first);
        rWandAugmentAevitas.setRequiredConstellation(Constellations.aevitas);

        rWandAugmentArmara = registerAltarRecipe(
            new WandAttunementRecipe(
                Constellations.armara,
                newShapedRecipe("internal/altar/wand/armara", ItemsAS.wand)
                    .addPart(ItemsAS.wand, ShapedRecipeSlot.CENTER)
                    .addPart(
                        ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe()));
        rWandAugmentAevitas.setCstItem(
            OreDictAlias.ITEM_SUGAR_CANE,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        rWandAugmentArmara.setInnerTraitItem(
            Items.LEATHER,
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rWandAugmentArmara.setInnerTraitItem(
            Items.NETHERBRICK,
            TraitRecipe.TraitRecipeSlot.LOWER_CENTER,
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        first = Iterables.getFirst(Constellations.armara.getConstellationSignatureItems(), null);
        rWandAugmentArmara.addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first);
        rWandAugmentArmara.setRequiredConstellation(Constellations.armara);

        rWandAugmentDiscidia = registerAltarRecipe(
            new WandAttunementRecipe(
                Constellations.discidia,
                newShapedRecipe("internal/altar/wand/discidia", ItemsAS.wand)
                    .addPart(ItemsAS.wand, ShapedRecipeSlot.CENTER)
                    .addPart(
                        ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe()));
        rWandAugmentDiscidia.setCstItem(
            ItemUsableDust.DustType.ILLUMINATION.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        rWandAugmentDiscidia.setInnerTraitItem(
            OreDictAlias.ITEM_GLOWSTONE_DUST,
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rWandAugmentDiscidia.setInnerTraitItem(
            Items.BLAZE_ROD,
            TraitRecipe.TraitRecipeSlot.LOWER_CENTER,
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        first = Iterables.getFirst(Constellations.discidia.getConstellationSignatureItems(), null);
        rWandAugmentDiscidia.addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first);
        rWandAugmentDiscidia.setRequiredConstellation(Constellations.discidia);

        rWandAugmentEvorsio = registerAltarRecipe(
            new WandAttunementRecipe(
                Constellations.evorsio,
                newShapedRecipe("internal/altar/wand/evorsio", ItemsAS.wand)
                    .addPart(ItemsAS.wand, ShapedRecipeSlot.CENTER)
                    .addPart(
                        OreDictAlias.ITEM_NETHER_QUARTZ,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe()));
        rWandAugmentEvorsio.setCstItem(
            OreDictAlias.ITEM_GUNPOWDER,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        rWandAugmentEvorsio.setInnerTraitItem(
            Items.FLINT,
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rWandAugmentEvorsio.setInnerTraitItem(
            Items.BLAZE_POWDER,
            TraitRecipe.TraitRecipeSlot.LOWER_CENTER,
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        first = Iterables.getFirst(Constellations.evorsio.getConstellationSignatureItems(), null);
        rWandAugmentEvorsio.addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first);
        rWandAugmentEvorsio.setRequiredConstellation(Constellations.evorsio);

        rWandAugmentVicio = registerAltarRecipe(
            new WandAttunementRecipe(
                Constellations.vicio,
                newShapedRecipe("internal/altar/wand/vicio", ItemsAS.wand)
                    .addPart(ItemsAS.wand, ShapedRecipeSlot.CENTER)
                    .addPart(OreDictAlias.ITEM_SUGARCANE, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe()));
        rWandAugmentVicio.setCstItem(
            ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        rWandAugmentVicio.setInnerTraitItem(
            Items.FEATHER,
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rWandAugmentVicio.setInnerTraitItem(
            Items.ARROW,
            TraitRecipe.TraitRecipeSlot.LOWER_CENTER,
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        first = Iterables.getFirst(Constellations.vicio.getConstellationSignatureItems(), null);
        rWandAugmentVicio.addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first)
            .addOuterTraitItem(first);
        rWandAugmentVicio.setRequiredConstellation(Constellations.vicio);

        rCapeBase = registerTraitRecipe(
            newShapedRecipe("internal/altar/capebase", ItemsAS.armorImbuedCape)
                .addPart(Items.LEATHER_CHESTPLATE, ShapedRecipeSlot.CENTER)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(
                    ItemUsableDust.DustType.ILLUMINATION.asStack(),
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        rCapeBase.setInnerTraitItem(
            ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        rCapeBase.setCstItem(
            ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        rCapeBase.setCstItem(
            ItemCraftingComponent.MetaType.STARDUST.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);
        rCapeBase.setAttItem(
            ItemCraftingComponent.MetaType.STARDUST.asStack(),
            AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
            AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rCapeBase.addOuterTraitItem(Items.FEATHER)
            .addOuterTraitItem(Items.ENDER_PEARL)
            .addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack())
            .addOuterTraitItem(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack());

        rKnowledgeShare = registerAttenuationRecipe(
            newShapedRecipe("internal/altar/knowledgeshare", ItemsAS.knowledgeShare)
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(Items.FEATHER, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.getDyeOreDict(EnumDyeColor.BLACK), ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .unregisteredAccessibleShapedRecipe());
        rKnowledgeShare
            .setAttItem(ItemUsableDust.DustType.ILLUMINATION.asStack(), AttunementRecipe.AttunementAltarSlot.values());

        NonNullList<ItemStack> applicable = NonNullList.create();
        for (ItemColoredLens.ColorType type : ItemColoredLens.ColorType.values()) {
            applicable.add(type.asStack());
        }
        rInfusedGlass = registerConstellationRecipe(
            newShapedRecipe("internal/altar/infusedglass", ItemsAS.infusedGlass)
                .addPart(
                    ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT)
                .addPart(new ItemHandle(applicable), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rInfusedGlass.setAttItem(
            ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
            AttunementRecipe.AttunementAltarSlot.values());
        rInfusedGlass.setCstItem(
            OreDictAlias.ITEM_STARMETAL_DUST,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);

        rGlassLensSpectral = registerConstellationRecipe(
            newShapedRecipe("internal/altar/lens_spectral", ItemColoredLens.ColorType.SPECTRAL.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(), ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(
                    ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rGlassLensSpectral.setAttItem(OreDictAlias.ITEM_STARMETAL_DUST, AttunementRecipe.AttunementAltarSlot.values());

        rIlluminationPowder = registerDiscoveryRecipe(
            newShapedRecipe(
                "internal/altar/illuminationpowder",
                ItemUtils.copyStackWithSize(ItemUsableDust.DustType.ILLUMINATION.asStack(), 16))
                .addPart(
                    OreDictAlias.ITEM_GLOWSTONE_DUST,
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rIlluminationPowder.setPassiveStarlightRequirement(200);

        rNocturnalPowder = registerDiscoveryRecipe(
            newShapedRecipe(
                "internal/altar/nocturnalpowder",
                ItemUtils.copyStackWithSize(ItemUsableDust.DustType.NOCTURNAL.asStack(), 4))
                .addPart(Items.COAL, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.getDyeOreDict(EnumDyeColor.BLACK), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(
                    new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rNocturnalPowder.setPassiveStarlightRequirement(300);

        rRitualLink = registerConstellationRecipe(
            newShapedRecipe("internal/altar/rituallink", new ItemStack(BlocksAS.ritualLink, 2))
                .addPart(OreDictAlias.ITEM_GOLD_NUGGET, ShapedRecipeSlot.UPPER_RIGHT, ShapedRecipeSlot.UPPER_LEFT)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(
                    ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(
                    OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        rRitualLink.setCstItem(
            OreDictAlias.ITEM_GOLD_NUGGET,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        rRitualLink.setCstItem(
            OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        rRitualLink.setPassiveStarlightRequirement(2600);

        rGrappleWand = registerAttenuationRecipe(
            newShapedRecipe("internal/altar/tool_grapple", ItemsAS.grapplingWand)
                .addPart(OreDictAlias.ITEM_ENDERPEARL, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_DYE_PURPLE, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.RIGHT)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.CENTER,
                    ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());
        rGrappleWand
            .setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rGrappleWand.setPassiveStarlightRequirement(1600);

        rArchitectWand = registerAttenuationRecipe(
            newShapedRecipe("internal/altar/tool_architect", ItemsAS.architectWand)
                .addPart(OreDictAlias.ITEM_DYE_PURPLE, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.RIGHT)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.CENTER,
                    ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());
        rArchitectWand
            .setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rArchitectWand.setPassiveStarlightRequirement(1600);

        rExchangeWand = registerAttenuationRecipe(
            newShapedRecipe("internal/altar/tool_exchange", ItemsAS.exchangeWand)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND, ShapedRecipeSlot.UPPER_RIGHT, ShapedRecipeSlot.LEFT)
                .unregisteredAccessibleShapedRecipe());
        rExchangeWand
            .setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rExchangeWand.setPassiveStarlightRequirement(1600);

        rShiftStar = registerAttenuationRecipe(
            newShapedRecipe("internal/altar/tool_shiftstar", ItemsAS.shiftingStar)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(), ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.LOWER_RIGHT,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.LOWER_LEFT)
                .addPart(BlocksAS.fluidLiquidStarlight, ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rShiftStar.setAttItem(OreDictAlias.ITEM_AQUAMARINE, AttunementRecipe.AttunementAltarSlot.values());

        rIlluminationWand = registerConstellationRecipe(
            newShapedRecipe("internal/altar/tool_illumination", ItemsAS.illuminationWand)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.RIGHT)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.CENTER,
                    ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());
        rIlluminationWand.setCstItem(
            ItemHandle.getCrystalVariant(false, false),
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        rIlluminationWand
            .setCstItem(OreDictAlias.ITEM_STARMETAL_DUST, ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        rIlluminationWand.setAttItem(
            ItemUsableDust.DustType.ILLUMINATION.asStack(),
            AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);

        rSkyResonator = registerDiscoveryRecipe(
            newShapedRecipe("internal/altar/skyresonator", ItemsAS.skyResonator)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(), ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(BlocksAS.fluidLiquidStarlight, ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rSkyResonator.setPassiveStarlightRequirement(100);

        rTreeBeacon = registerConstellationRecipe(
            newShapedRecipe("internal/altar/treebeacon", BlocksAS.treeBeacon)
                .addPart(
                    OreDictAlias.BLOCK_LEAVES,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_SAPLING, ShapedRecipeSlot.CENTER)
                .addPart(BlocksAS.fluidLiquidStarlight, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), ShapedRecipeSlot.UPPER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rTreeBeacon.setAttItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
            AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rTreeBeacon.setCstItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        rTreeBeacon.setPassiveStarlightRequirement(2000);

        rStarlightInfuser = registerConstellationRecipe(
            newShapedRecipe("internal/altar/starlightinfuser", BlocksAS.starlightInfuser)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(), ShapedRecipeSlot.LOWER_CENTER)
                .addPart(
                    BlockMarble.MarbleBlockType.CHISELED.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlocksAS.fluidLiquidStarlight, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rStarlightInfuser.setCstItem(
            BlockMarble.MarbleBlockType.ENGRAVED.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT);
        rStarlightInfuser.setCstItem(
            BlockMarble.MarbleBlockType.PILLAR.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT);
        rStarlightInfuser.setAttItem(
            BlockMarble.MarbleBlockType.PILLAR.asStack(),
            AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
            AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rStarlightInfuser.setPassiveStarlightRequirement(2000);

        rHandTelescope = registerDiscoveryRecipe(
            newShapedRecipe("internal/altar/handtelescope", ItemsAS.handTelescope)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(
                    OreDictAlias.ITEM_STICKS,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS, ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());

        rGlassLensFire = registerConstellationRecipe(
            newShapedRecipe("internal/altar/lens_fire", ItemColoredLens.ColorType.FIRE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LOWER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensFire.setAttItem(Items.BLAZE_POWDER, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensFire.setCstItem(
            Items.BLAZE_POWDER,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);

        rGlassLensBreak = registerConstellationRecipe(
            newShapedRecipe("internal/altar/lens_break", ItemColoredLens.ColorType.BREAK.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.IRON_PICKAXE, ShapedRecipeSlot.LOWER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensBreak.setAttItem(
            OreDictAlias.ITEM_AQUAMARINE,
            AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT,
            AttunementRecipe.AttunementAltarSlot.UPPER_LEFT);
        rGlassLensBreak.setCstItem(
            OreDictAlias.ITEM_GOLD_INGOT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);

        rGlassLensDamage = registerConstellationRecipe(
            newShapedRecipe("internal/altar/lens_damage", ItemColoredLens.ColorType.DAMAGE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(Items.FLINT, ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.ITEM_DIAMOND, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_IRON_INGOT, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensDamage.setAttItem(
            OreDictAlias.ITEM_AQUAMARINE,
            AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
            AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rGlassLensDamage.setCstItem(
            OreDictAlias.ITEM_IRON_INGOT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);

        rGlassLensGrowth = registerConstellationRecipe(
            newShapedRecipe("internal/altar/lens_grow", ItemColoredLens.ColorType.GROW.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_CARROT, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.UPPER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensGrowth.setAttItem(OreDictAlias.ITEM_AQUAMARINE, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensGrowth.setCstItem(
            OreDictAlias.ITEM_SUGARCANE,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT);
        rGlassLensGrowth.setCstItem(
            Items.WHEAT_SEEDS,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT);

        rGlassLensRegeneration = registerConstellationRecipe(
            newShapedRecipe("internal/altar/lens_regen", ItemColoredLens.ColorType.REGEN.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(Items.GHAST_TEAR, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND, ShapedRecipeSlot.LOWER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensRegeneration.setAttItem(
            OreDictAlias.ITEM_AQUAMARINE,
            AttunementRecipe.AttunementAltarSlot.UPPER_LEFT,
            AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT);
        rGlassLensRegeneration.setCstItem(
            OreDictAlias.ITEM_STARMETAL_DUST,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);

        rGlassLensPush = registerConstellationRecipe(
            newShapedRecipe("internal/altar/lens_push", ItemColoredLens.ColorType.PUSH.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(Blocks.PISTON, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensPush.setAttItem(OreDictAlias.ITEM_GLOWSTONE_DUST, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensPush.setCstItem(
            OreDictAlias.ITEM_AQUAMARINE,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT);

        rAttenuationAltarRelay = registerDiscoveryRecipe(
            newShapedRecipe("internal/altar/attunementrelay", BlocksAS.attunementRelay)
                .addPart(OreDictAlias.ITEM_GOLD_NUGGET, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS, ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rAttenuationAltarRelay.setPassiveStarlightRequirement(250);

        rLinkTool = registerAttenuationRecipe(
            newShapedRecipe("internal/altar/tool_linking", ItemsAS.linkingTool)
                .addPart(OreDictAlias.BLOCK_WOOD_LOGS, ShapedRecipeSlot.LOWER_LEFT)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.RIGHT)
                .addPart(
                    OreDictAlias.ITEM_STICKS,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.LOWER_CENTER,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe())
            .setAttItem(OreDictAlias.BLOCK_WOOD_LOGS, AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rLinkTool.setPassiveStarlightRequirement(1000);

        rLightwell = registerDiscoveryRecipe(
            newShapedRecipe("internal/altar/lightwell", BlocksAS.blockWell)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rLightwell.setPassiveStarlightRequirement(200);

        rIlluminator = registerDiscoveryRecipe(
            newShapedRecipe("internal/altar/illuminator", BlocksAS.blockIlluminator)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(
                    ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT)
                .addPart(
                    ItemUsableDust.DustType.ILLUMINATION.asStack(),
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rIlluminator.setPassiveStarlightRequirement(600);

        rWand = registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe("internal/altar/tool_basicwand", ItemsAS.wand)
                    .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.UPPER_CENTER)
                    .addPart(OreDictAlias.ITEM_ENDERPEARL, ShapedRecipeSlot.UPPER_RIGHT)
                    .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.LOWER_LEFT)
                    .unregisteredAccessibleShapedRecipe()));

        rWand.setPassiveStarlightRequirement(200);

        rJournal = registerDiscoveryRecipe(
            newShapedRecipe("internal/altar/journal", ItemsAS.journal)
                .addPart(
                    OreDictAlias.ITEM_AQUAMARINE,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.BOOK, ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), ShapedRecipeSlot.UPPER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rJournal.setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(rWoodPlanks)).setPassiveStarlightRequirement(20);
        registerAltarRecipe(new DiscoveryRecipe(rWoodArch)).setPassiveStarlightRequirement(20);
        registerAltarRecipe(new DiscoveryRecipe(rWoodPillar)).setPassiveStarlightRequirement(20);
        registerAltarRecipe(new DiscoveryRecipe(rWoodEngraved)).setPassiveStarlightRequirement(20);

        rWoodEnriched = registerDiscoveryRecipe(
            newShapedRecipe(
                "internal/altar/enriched_wood",
                ItemUtils.copyStackWithSize(BlockInfusedWood.WoodType.ENRICHED.asStack(), 4))
                .addPart(
                    BlockInfusedWood.WoodType.PLANKS.asStack(),
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());

        registerDiscoveryRecipe(
            newShapedRecipe(
                "internal/altar/marble_black_raw",
                new ItemStack(BlocksAS.blockBlackMarble, 8, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.values())
                .addPart(Items.COAL, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe()).setPassiveStarlightRequirement(20);

        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/marble_runed",
                    new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                    .addPart(
                        BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                    .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.UPPER_CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(rMarbleStairs)).setPassiveStarlightRequirement(20);
        registerAltarRecipe(new DiscoveryRecipe(rMarbleSlab)).setPassiveStarlightRequirement(20);

        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/marble_engraved",
                    new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                    .addPart(
                        BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/marble_chiseled",
                    new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                    .addPart(
                        OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/marble_arch",
                    new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                    .addPart(
                        BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/marble_pillar",
                    new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                    .addPart(
                        BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/marble_bricks",
                    new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                    .addPart(
                        BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/black_marble_arch",
                    new ItemStack(BlocksAS.blockBlackMarble, 3, BlockBlackMarble.BlackMarbleBlockType.ARCH.ordinal()))
                    .addPart(
                        BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);
        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/black_marble_bricks",
                    new ItemStack(BlocksAS.blockBlackMarble, 4, BlockBlackMarble.BlackMarbleBlockType.BRICKS.ordinal()))
                    .addPart(
                        BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);
        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/black_marble_chiseled",
                    new ItemStack(
                        BlocksAS.blockBlackMarble,
                        4,
                        BlockBlackMarble.BlackMarbleBlockType.CHISELED.ordinal()))
                    .addPart(
                        BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);
        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/black_marble_engraved",
                    new ItemStack(
                        BlocksAS.blockBlackMarble,
                        5,
                        BlockBlackMarble.BlackMarbleBlockType.ENGRAVED.ordinal()))
                    .addPart(
                        BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .addPart(BlockBlackMarble.BlackMarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);
        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/black_marble_runed",
                    new ItemStack(BlocksAS.blockBlackMarble, 3, BlockBlackMarble.BlackMarbleBlockType.RUNED.ordinal()))
                    .addPart(
                        BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                    .addPart(
                        BlockBlackMarble.BlackMarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);
        registerAltarRecipe(
            new DiscoveryRecipe(
                newShapedRecipe(
                    "internal/altar/black_marble_pillar",
                    new ItemStack(BlocksAS.blockBlackMarble, 2, BlockBlackMarble.BlackMarbleBlockType.PILLAR.ordinal()))
                    .addPart(
                        BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT)
                    .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        rCCGlassLens = registerDiscoveryRecipe(
            newShapedRecipe("internal/altar/glasslens", ItemCraftingComponent.MetaType.GLASS_LENS.asStack())
                .addPart(
                    OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rCCGlassLens.setPassiveStarlightRequirement(100);

        rCToolSword = registerAltarRecipe(
            new CrystalToolRecipe(
                newShapedRecipe("internal/altar/crystaltool_sword", ItemsAS.crystalSword)
                    .addPart(OreDictAlias.ITEM_STICKS, ShapedRecipeSlot.LOWER_CENTER)
                    .addPart(
                        ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.UPPER_CENTER)
                    .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.CENTER,
                ShapedRecipeSlot.UPPER_CENTER));

        rCToolShovel = registerAltarRecipe(
            new CrystalToolRecipe(
                newShapedRecipe("internal/altar/crystaltool_shovel", ItemsAS.crystalShovel)
                    .addPart(OreDictAlias.ITEM_STICKS, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                    .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.UPPER_CENTER)
                    .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.UPPER_CENTER));

        rCToolPick = registerAltarRecipe(
            new CrystalToolRecipe(
                newShapedRecipe("internal/altar/crystaltool_pickaxe", ItemsAS.crystalPickaxe)
                    .addPart(OreDictAlias.ITEM_STICKS, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                    .addPart(
                        ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT)
                    .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT));

        rCToolAxe = registerAltarRecipe(
            new CrystalToolRecipe(
                newShapedRecipe("internal/altar/crystaltool_axe", ItemsAS.crystalAxe)
                    .addPart(OreDictAlias.ITEM_STICKS, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                    .addPart(
                        ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT)
                    .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT));
    }

    private static void initCapeAttunementRecipes() {
        for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
            if (c instanceof IMinorConstellation) continue;

            List<ItemHandle> signature = c.getConstellationSignatureItems();
            if (!signature.isEmpty()) {
                ItemHandle first = Iterables.getFirst(signature, null); // Never null.
                AccessibleRecipeAdapater ar = newShapedRecipe(
                    "internal/cape/att/" + c.getSimpleName()
                        .toLowerCase(),
                    ItemsAS.armorImbuedCape).addPart(ItemsAS.armorImbuedCape, ShapedRecipeSlot.CENTER)
                    .addPart(
                        first,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                    .unregisteredAccessibleShapedRecipe();
                CapeAttunementRecipe recipe = new CapeAttunementRecipe(c, ar);
                for (ItemHandle s : signature) {
                    recipe.addOuterTraitItem(s);
                }
                recipe.setInnerTraitItem(OreDictAlias.ITEM_STARMETAL_DUST, TraitRecipe.TraitRecipeSlot.values());
                registerAltarRecipe(recipe);
                RecipesAS.capeCraftingRecipes.put(c, recipe);
            }
        }
    }

}
