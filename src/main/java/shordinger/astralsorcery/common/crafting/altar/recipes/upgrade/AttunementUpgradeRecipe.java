/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes.upgrade;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.crafting.IAltarUpgradeRecipe;
import shordinger.astralsorcery.common.crafting.INighttimeRecipe;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunementUpgradeRecipe
 * Created by HellFirePvP
 * Date: 09.10.2016 / 11:40
 */
public class AttunementUpgradeRecipe extends DiscoveryRecipe
    implements IAltarUpgradeRecipe, INighttimeRecipe, ISpecialCraftingEffects {

    public AttunementUpgradeRecipe() {
        super(
            shapedRecipe("upgrade_tier2", new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()))
                .addPart(
                    BlockMarble.MarbleBlockType.PILLAR.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.LEFT)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlocksAS.fluidLiquidStarlight, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
    }

    @Override
    public TileAltar.AltarLevel getLevelUpgradingTo() {
        return TileAltar.AltarLevel.ATTUNEMENT;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal());
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar tileAltar) {
        return null;
    }

    @Override
    public int craftingTickTime() {
        return super.craftingTickTime() * 4;
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new AttunementUpgradeRecipe();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
            if (rand.nextInt(6) == 0) {
                pm.addBlockDestroyEffects(altar.getPos(), BlocksAS.blockMarble.getDefaultState());
            }
        }
    }

}
