/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.infusion.recipes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import shordinger.astralsorcery.common.tile.TileStarlightInfuser;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BasicInfusionRecipe
 * Created by HellFirePvP
 * Date: 11.12.2016 / 18:11
 */
public class BasicInfusionRecipe extends AbstractInfusionRecipe {

    public BasicInfusionRecipe(ItemStack output, String oreDictInput) {
        this(output, new ItemHandle(oreDictInput));
    }

    public BasicInfusionRecipe(ItemStack output, ItemStack input) {
        this(output, new ItemHandle(input));
    }

    public BasicInfusionRecipe(ItemStack output, ItemHandle input) {
        super(output, input);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileStarlightInfuser infuser, long tick, Random rand) {
        super.onCraftClientTick(infuser, tick, rand);

        if (rand.nextInt(10) == 0) {
            Vector3 from = new Vector3(infuser).add(0.5, 0.3, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 0.4F);
            EffectLightbeam lightbeam = EffectHandler.getInstance()
                .lightbeam(
                    from.clone()
                        .addY(4 + rand.nextInt(2)),
                    from,
                    1);
            lightbeam.setMaxAge(64);
        }

        BlockPos randPos = TileStarlightInfuser.offsetsLiquidStarlight[rand
            .nextInt(TileStarlightInfuser.offsetsLiquidStarlight.length)];
        Vector3 from = new Vector3(infuser).add(randPos);
        from.add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        Vector3 dir = new Vector3(infuser).add(0.5, 1.6, 0.5)
            .subtract(from);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
        p.setColor(Color.WHITE)
            .scale(0.2F + rand.nextFloat() * 0.1F)
            .gravity(0.004)
            .motion(dir.getX() / 40D, dir.getY() / 40D, dir.getZ() / 40D);
    }

}
