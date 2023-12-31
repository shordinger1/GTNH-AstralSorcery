/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeAdapter
 * Created by HellFirePvP
 * Date: 22.09.2016 / 15:41
 */
public class RecipeAdapter extends InventoryCrafting {

    private static final AdapterContainer emptyContainer = new AdapterContainer();

    public RecipeAdapter(int width, int height) {
        super(emptyContainer, width, height);
    }

    public void fill(ItemStack[] stacks) {
        if (stacks.length != getWidth() * getHeight()) return; // Ugh... ?

        for (int xx = 0; xx < getWidth(); xx++) {
            for (int zz = 0; zz < getHeight(); zz++) {
                setInventorySlotContents(xx * getHeight() + zz, stacks[xx * getHeight() + zz]);
            }
        }
    }

    private static class AdapterContainer extends Container {

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return false;
        }

        @Override
        public void onContainerClosed(EntityPlayer playerIn) {
            super.onContainerClosed(playerIn);
        }

    }

}
