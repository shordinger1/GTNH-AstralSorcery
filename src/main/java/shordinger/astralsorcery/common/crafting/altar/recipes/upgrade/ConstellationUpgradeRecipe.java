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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.crafting.IAltarUpgradeRecipe;
import shordinger.astralsorcery.common.crafting.INighttimeRecipe;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.OreDictAlias;
import shordinger.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.particle.ParticleManager;
import shordinger.wrapper.net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationUpgradeRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 13:03
 */
public class ConstellationUpgradeRecipe extends AttunementRecipe
    implements IAltarUpgradeRecipe, INighttimeRecipe, ISpecialCraftingEffects {

    private static Vector3[] offsetPillars = new Vector3[]{new Vector3(3, 2, 3), new Vector3(-3, 2, 3),
        new Vector3(3, 2, -3), new Vector3(-3, 2, -3)};

    public ConstellationUpgradeRecipe() {
        super(
            shapedRecipe("upgrade_tier3", new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()))
                .addPart(
                    BlockMarble.MarbleBlockType.PILLAR.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(), ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.LEFT)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        setAttItem(
            BlockMarble.MarbleBlockType.CHISELED.asStack(),
            AttunementAltarSlot.LOWER_LEFT,
            AttunementAltarSlot.LOWER_RIGHT);
        setAttItem(OreDictAlias.ITEM_STARMETAL_DUST, AttunementAltarSlot.UPPER_RIGHT, AttunementAltarSlot.UPPER_LEFT);
    }

    @Override
    public TileAltar.AltarLevel getLevelUpgradingTo() {
        return TileAltar.AltarLevel.CONSTELLATION_CRAFT;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal());
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar tileAltar) {
        return ItemStack.EMPTY;
    }

    @Override
    public int craftingTickTime() {
        return super.craftingTickTime() * 4;
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new ConstellationUpgradeRecipe();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarVec = new Vector3(altar);
            Vector3 thisAltar = altarVec.clone()
                .add(0.5, 0.5, 0.5);
            for (int i = 0; i < 3; i++) {
                Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
                dir.multiply(rand.nextFloat())
                    .add(thisAltar.clone());

                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
                particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat()))
                    .scale(0.2F + (0.2F * rand.nextFloat()))
                    .gravity(0.004);
            }

            ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
            if (rand.nextInt(18) == 0) {
                pm.addBlockDestroyEffects(altar.getPos(), BlocksAS.blockMarble.getDefaultState());
            }
            if (tick % 48 == 0 && rand.nextInt(2) == 0) {
                EffectHandler.getInstance()
                    .textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone())
                    .setPosition(new Vector3(altar).add(0.5, 0.05, 0.5))
                    .setScale(5 + rand.nextInt(2))
                    .setNoRotation(rand.nextInt(360));
            }
        }
    }

}
