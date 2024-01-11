/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.infusion.recipes;

import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.item.tool.ChargedCrystalToolBase;
import shordinger.astralsorcery.common.item.tool.ItemCrystalSword;
import shordinger.astralsorcery.common.item.tool.ItemCrystalToolBase;
import shordinger.astralsorcery.common.tile.TileStarlightInfuser;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusionRecipeChargeTool
 * Created by HellFirePvP
 * Date: 14.03.2017 / 12:33
 */
public class InfusionRecipeChargeTool extends BasicInfusionRecipe {

    public InfusionRecipeChargeTool(@Nonnull ChargedCrystalToolBase output) {
        super(new ItemStack((Item) output), new ItemHandle(new ItemStack(output.getInertVariant())));
        setConsumeMultiple();
        setLiquidStarlightConsumptionChance(1F);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nullable TileStarlightInfuser infuser) {
        if (infuser != null) {
            ItemStack in = infuser.getInputStack();
            if(!in.isEmpty()) {
                if(in.getItem() instanceof ChargedCrystalToolBase) {
                    ToolCrystalProperties prop = ChargedCrystalToolBase.getToolProperties(in);
                    ItemStack out = output.copy();
                    out.setTagCompound(in.getTagCompound());
                    ChargedCrystalToolBase.applyToolProperties(out, new ToolCrystalProperties(
                            prop.getSize(),
                            prop.getPurity(),
                            Math.max(100, prop.getCollectiveCapability()),
                            prop.getFracturation(),
                            prop.getSizeOverride()));
                    ChargedCrystalToolBase.removeChargeRevertCounter(out);
                    return out;
                }
                if(in.getItem() instanceof ItemCrystalToolBase || in.getItem() instanceof ItemCrystalSword) {
                    ToolCrystalProperties prop = in.getItem() instanceof ItemCrystalToolBase ? ItemCrystalToolBase.getToolProperties(in) : ItemCrystalSword.getToolProperties(in);
                    ItemStack out = output.copy();
                    out.setTagCompound(in.getTagCompound());
                    prop = new ToolCrystalProperties(
                            prop.getSize(),
                            prop.getPurity(),
                            Math.max(100, prop.getCollectiveCapability()),
                            prop.getFracturation(),
                            prop.getSizeOverride());
                    ChargedCrystalToolBase.applyToolProperties(out, prop);
                    return out;
                }
            }
        }
        ItemStack out = output.copy();
        ChargedCrystalToolBase.applyToolProperties(out, ToolCrystalProperties.merge(CrystalProperties.getMaxCelestialProperties()));
        return out;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForMatching() {
        return getOutput(null);
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return getOutput(null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileStarlightInfuser infuser, long tick, Random rand) {
        super.onCraftClientTick(infuser, tick, rand);

        BlockPos at = infuser.getPos();
        EffectHelper.genericFlareParticle(at.getX() + 0.5, at.getY() + 0.85, at.getZ() + 0.5)
                .motion(rand.nextFloat() * 0.1 - rand.nextFloat() * 0.1,
                        rand.nextFloat() * 0.4,
                        rand.nextFloat() * 0.1 - rand.nextFloat() * 0.1)
                .scale(0.4F);

        for (int i = 0; i < 3; i++) {
            at = TileStarlightInfuser.offsetsLiquidStarlight[rand.nextInt(TileStarlightInfuser.offsetsLiquidStarlight.length)];
            at = at.add(infuser.getPos());
            EffectHelper.genericFlareParticle(at.getX() + 0.5, at.getY() + 0.85, at.getZ() + 0.5)
                    .motion(rand.nextFloat() * 0.1 - rand.nextFloat() * 0.1,
                            rand.nextFloat() * 0.4,
                            rand.nextFloat() * 0.1 - rand.nextFloat() * 0.1)
                    .scale(0.4F);
        }
        if(rand.nextInt(3) == 0) {
            at = TileStarlightInfuser.offsetsLiquidStarlight[rand.nextInt(TileStarlightInfuser.offsetsLiquidStarlight.length)];
            at = at.add(infuser.getPos());
            Vector3 from = new Vector3(at).add(0.5, 0, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 0.4F);
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
            lightbeam.setMaxAge(64);
        }
    }

}

