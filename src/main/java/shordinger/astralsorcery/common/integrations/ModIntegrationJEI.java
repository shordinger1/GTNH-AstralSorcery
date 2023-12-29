/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations;

import java.util.*;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.base.LightOreTransmutations;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.base.WellLiquefaction;
import shordinger.astralsorcery.common.block.BlockMachine;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.container.ContainerAltarAttunement;
import shordinger.astralsorcery.common.container.ContainerAltarConstellation;
import shordinger.astralsorcery.common.container.ContainerAltarDiscovery;
import shordinger.astralsorcery.common.container.ContainerAltarTrait;
import shordinger.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import shordinger.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import shordinger.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import shordinger.astralsorcery.common.crafting.helper.RecipeHelper;
import shordinger.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import shordinger.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import shordinger.astralsorcery.common.integrations.mods.jei.ASRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.CategoryGrindstone;
import shordinger.astralsorcery.common.integrations.mods.jei.CategoryInfuser;
import shordinger.astralsorcery.common.integrations.mods.jei.CategoryTransmutation;
import shordinger.astralsorcery.common.integrations.mods.jei.CategoryWell;
import shordinger.astralsorcery.common.integrations.mods.jei.GrindstoneRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.InfuserRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.TransmutationRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.WellRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.AltarAttunementRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.AltarConstellationRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.AltarDiscoveryRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.AltarTraitRecipeWrapper;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.CategoryAltarAttunement;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.CategoryAltarConstellation;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.CategoryAltarDiscovery;
import shordinger.astralsorcery.common.integrations.mods.jei.altar.CategoryAltarTrait;
import shordinger.astralsorcery.common.integrations.mods.jei.util.JEISessionHandler;
import shordinger.astralsorcery.common.integrations.mods.jei.util.TieredAltarRecipeTransferHandler;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationJEI
 * Created by HellFirePvP
 * Date: 10.01.2017 / 23:21
 */
@JEIPlugin
public class ModIntegrationJEI implements IModPlugin {

    private static Map<Class<?>, Tuple<IRecipeWrapperFactory, String>> factoryMap = new HashMap<>();
    private static List<RecipeChange> recipePrimer = new LinkedList<>();
    private static List<Tuple<Object, ModificationAction>> unresolvedRecipes = new LinkedList<>();

    public static boolean jeiRegistrationPhase = true;

    public static final String idWell = "astralsorcery.lightwell";
    public static final String idGrindstone = "astralsorcery.grindstone";
    public static final String idInfuser = "astralsorcery.infuser";
    public static final String idTransmutation = "astralsorcery.lightTransmutation";

    public static final String idAltarDiscovery = "astralsorcery.altar.discovery";
    public static final String idAltarAttunement = "astralsorcery.altar.attunement";
    public static final String idAltarConstellation = "astralsorcery.altar.constellation";
    public static final String idAltarTrait = "astralsorcery.altar.trait";

    public static IStackHelper stackHelper;
    public static IJeiHelpers jeiHelpers;
    public static IRecipeRegistry recipeRegistry;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(ItemsAS.wand, ItemsAS.armorImbuedCape, ItemsAS.shiftingStar);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers()
            .getGuiHelper();

        registry.addRecipeCategories(
            new CategoryWell(guiHelper),
            new CategoryGrindstone(guiHelper),
            new CategoryInfuser(guiHelper),
            new CategoryTransmutation(guiHelper),

            new CategoryAltarTrait(guiHelper),
            new CategoryAltarConstellation(guiHelper),
            new CategoryAltarAttunement(guiHelper),
            new CategoryAltarDiscovery(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        jeiHelpers = registry.getJeiHelpers();
        stackHelper = jeiHelpers.getStackHelper();

        MinecraftForge.EVENT_BUS.register(JEISessionHandler.getInstance());

        hideItems(
            registry.getJeiHelpers()
                .getIngredientBlacklist());

        IRecipeTransferRegistry rtr = registry.getRecipeTransferRegistry();

        registerRecipeHandle(registry, WellLiquefaction.LiquefactionEntry.class, WellRecipeWrapper::new, idWell);
        registerRecipeHandle(registry, GrindstoneRecipe.class, GrindstoneRecipeWrapper::new, idGrindstone);
        registerRecipeHandle(registry, AbstractInfusionRecipe.class, InfuserRecipeWrapper::new, idInfuser);
        registerRecipeHandle(
            registry,
            LightOreTransmutations.Transmutation.class,
            TransmutationRecipeWrapper::new,
            idTransmutation);
        registerRecipeHandle(registry, TraitRecipe.class, AltarTraitRecipeWrapper::new, idAltarTrait);
        registerRecipeHandle(
            registry,
            ConstellationRecipe.class,
            AltarConstellationRecipeWrapper::new,
            idAltarConstellation);
        registerRecipeHandle(registry, AttunementRecipe.class, AltarAttunementRecipeWrapper::new, idAltarAttunement);
        registerRecipeHandle(registry, DiscoveryRecipe.class, AltarDiscoveryRecipeWrapper::new, idAltarDiscovery);

        registry.addRecipeCatalyst(new ItemStack(BlocksAS.blockWell), idWell);
        registry.addRecipeCatalyst(BlockMachine.MachineType.GRINDSTONE.asStack(), idGrindstone);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.starlightInfuser), idInfuser);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.lens), idTransmutation);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.lensPrism), idTransmutation);
        registry.addRecipeCatalyst(
            new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()),
            idAltarDiscovery);
        registry.addRecipeCatalyst(
            new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()),
            idAltarAttunement);
        registry.addRecipeCatalyst(
            new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()),
            idAltarConstellation);
        registry.addRecipeCatalyst(
            new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()),
            idAltarTrait);

        addTransferHandlers(rtr, jeiHelpers.recipeTransferHandlerHelper());

        registry.addRecipes(InfusionRecipeRegistry.recipes, idInfuser);
        registry.addRecipes(GrindstoneRecipeRegistry.getValidRecipes(), idGrindstone);
        registry.addRecipes(LightOreTransmutations.getRegisteredTransmutations(), idTransmutation);
        registry.addRecipes(WellLiquefaction.getRegisteredLiquefactions(), idWell);

        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.DISCOVERY), idAltarDiscovery);
        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.ATTUNEMENT), idAltarAttunement);
        registry.addRecipes(
            AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.CONSTELLATION_CRAFT),
            idAltarConstellation);
        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.TRAIT_CRAFT), idAltarTrait);

        registry.handleRecipes(
            RecipeHelper.ShapedIngredientRecipe.class,
            ASRecipeWrapper.ShapedRecipe::new,
            VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(
            RecipeHelper.ShapelessIngredientRecipe.class,
            ASRecipeWrapper::new,
            VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(
            ShapedLightProximityRecipe.class,
            ASRecipeWrapper.LightRecipe::new,
            VanillaRecipeCategoryUid.CRAFTING);

        jeiRegistrationPhase = false;
    }

    private void addTransferHandlers(IRecipeTransferRegistry rtr, IRecipeTransferHandlerHelper trHelper) {
        if (!(stackHelper instanceof StackHelper)) {
            return;
        }
        StackHelper sHelper = (StackHelper) stackHelper;

        // T1 recipes
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarDiscovery.class, sHelper, trHelper, 9),
            idAltarDiscovery);
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarAttunement.class, sHelper, trHelper, 9),
            idAltarDiscovery);
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class, sHelper, trHelper, 9),
            idAltarDiscovery);
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class, sHelper, trHelper, 9),
            idAltarDiscovery);

        // T2 recipes
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarAttunement.class, sHelper, trHelper, 13),
            idAltarAttunement);
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class, sHelper, trHelper, 13),
            idAltarAttunement);
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class, sHelper, trHelper, 13),
            idAltarAttunement);

        // T3 recipes
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class, sHelper, trHelper, 21),
            idAltarConstellation);
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class, sHelper, trHelper, 21),
            idAltarConstellation);

        // T4 recipes
        rtr.addRecipeTransferHandler(
            new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class, sHelper, trHelper, 25),
            idAltarTrait);
    }

    private void hideItems(IIngredientBlacklist blacklist) {
        blacklist.addIngredientToBlacklist(new ItemStack(ItemsAS.knowledgeFragment));
        blacklist.addIngredientToBlacklist(new ItemStack(ItemsAS.fragmentCapsule));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockFakeTree));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.translucentBlock));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockVanishing));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockStructural));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockPortalNode));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockAltar, 1, 4));
        if (Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.hideJEIGeolosysSample(blacklist);
        }
    }

    private <T> void registerRecipeHandle(IModRegistry registry, Class<T> recipeClass, IRecipeWrapperFactory<T> factory,
                                          String categoryId) {
        factoryMap.put(recipeClass, new Tuple<>(factory, categoryId));
        registry.handleRecipes(recipeClass, factory, categoryId);
    }

    public static boolean addRecipe(Object recipe) {
        Tuple<IRecipeWrapperFactory, String> factoryTuple = findRecipeWrapperFor(recipe);
        if (factoryTuple != null) {
            RecipeChange change = new RecipeChange(
                factoryTuple.key.getRecipeWrapper(recipe),
                factoryTuple.value,
                ModificationAction.ADDITION);
            if (recipeRegistry == null) {
                recipePrimer.add(change);
            } else {
                change.apply(recipeRegistry);
            }
            return true;
        }
        unresolvedRecipes.add(new Tuple<>(recipe, ModificationAction.ADDITION));
        return false;
    }

    public static boolean removeRecipe(Object recipe) {
        Tuple<IRecipeWrapperFactory, String> factoryTuple = findRecipeWrapperFor(recipe);
        if (factoryTuple != null) {
            RecipeChange change = new RecipeChange(
                factoryTuple.key.getRecipeWrapper(recipe),
                factoryTuple.value,
                ModificationAction.REMOVAL);
            if (recipeRegistry == null) {
                recipePrimer.add(change);
            } else {
                change.apply(recipeRegistry);
            }
            return true;
        }
        unresolvedRecipes.add(new Tuple<>(recipe, ModificationAction.REMOVAL));
        return false;
    }

    @Nullable
    private static Tuple<IRecipeWrapperFactory, String> findRecipeWrapperFor(Object recipe) {
        Class<?> recipeClass = recipe.getClass();
        Tuple<IRecipeWrapperFactory, String> factoryTuple = factoryMap.get(recipeClass);
        while (factoryTuple == null && !recipeClass.equals(Object.class)) {
            recipeClass = recipeClass.getSuperclass();
            factoryTuple = factoryMap.get(recipeClass);
        }
        return factoryTuple;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        recipeRegistry = jeiRuntime.getRecipeRegistry();

        for (RecipeChange change : recipePrimer) {
            change.apply(recipeRegistry);
        }
        recipePrimer.clear();

        int assignedRecipes = 0;
        for (Tuple<Object, ModificationAction> action : unresolvedRecipes) {
            switch (action.value) {
                case ADDITION:
                    if (addRecipe(action.key)) {
                        assignedRecipes++;
                    }
                    break;
                case REMOVAL:
                    if (removeRecipe(action.key)) {
                        assignedRecipes++;
                    }
                    break;
                default:
                    break;
            }
        }
        if ((unresolvedRecipes.size() - assignedRecipes) > 0) {
            AstralSorcery.log.warn(
                "JEI Initialization Ended up with " + (unresolvedRecipes.size() - assignedRecipes)
                    + " unresolvable crafttweaker recipes!");
        }
    }

    private static class RecipeChange {

        private final IRecipeWrapper recipe;
        private final String category;
        private final ModificationAction action;

        private RecipeChange(IRecipeWrapper recipe, String category, ModificationAction action) {
            this.recipe = recipe;
            this.category = category;
            this.action = action;
        }

        private void apply(IRecipeRegistry recipeRegistry) {
            if (action == ModificationAction.ADDITION) {
                recipeRegistry.addRecipe(this.recipe, this.category);
            } else {
                recipeRegistry.removeRecipe(this.recipe, this.category);
            }
        }

    }

    private static enum ModificationAction {

        ADDITION,
        REMOVAL

    }

}
