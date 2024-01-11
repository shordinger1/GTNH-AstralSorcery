/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.crafting.IGatedRecipe;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Random;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: DiscoveryRecipe
* Created by HellFirePvP
* Date: 25.09.2016 / 23:58
*/
public class DiscoveryRecipe extends AbstractAltarRecipe implements IGatedRecipe.Progression {

    protected DiscoveryRecipe(TileAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    public DiscoveryRecipe(AccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.DISCOVERY, recipe);
        setPassiveStarlightRequirement(700);
    }

    @Override
    public int craftingTickTime() {
        return 100;
    }

    @Override
    public void handleInputConsumption(TileAltar ta, ActiveCraftingTask craftingTask, ItemStackHandler inventory) {
        super.handleInputConsumption(ta, craftingTask, inventory);

        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.getByRowColumnIndex(i / 3, i % 3);
            if(mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, i);
            } else {
                handleItemConsumption(ta, slot);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            if(rand.nextInt(14) == 0) {
                Vector3 from = new Vector3(altar).add(0.5, 0.3, 0.5);
                MiscUtils.applyRandomOffset(from, rand, 0.4F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
                lightbeam.setMaxAge(64);
            }
        }
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.BASIC_CRAFT;
    }

}
