/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting;

import shordinger.astralsorcery.common.crafting.helper.BasePlainRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.data.DataLightBlockEndpoints;
import shordinger.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.client.gui.inventory.GuiCrafting;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.inventory.ContainerWorkbench;
import shordinger.wrapper.net.minecraft.inventory.InventoryCrafting;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.item.crafting.Ingredient;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.ForgeHooks;
import shordinger.wrapper.net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedLightProximityRecipe
 * Created by HellFirePvP
 * Date: 02.08.2016 / 22:57
 */
public class ShapedLightProximityRecipe extends BasePlainRecipe {

    public static BlockPos clientWorkbenchPosition = null;

    private final ItemStack out;
    private final ShapeMap.Baked grid;

    public ShapedLightProximityRecipe(ResourceLocation name, ItemStack out, ShapeMap.Baked grid) {
        super(name);
        this.out = out;
        this.grid = grid;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!vanillaMatch(inv)) return false;

        Container c = inv.eventHandler;
        if (!(c instanceof ContainerWorkbench)) return false;
        ContainerWorkbench workbench = (ContainerWorkbench) c;
        BlockPos pos = workbench.pos;
        if (pos == null) return false;
        if (world.isRemote) {
            GuiScreen sc = Minecraft.getMinecraft().currentScreen;
            if (sc == null || !(sc instanceof GuiCrafting) || clientWorkbenchPosition == null) return false;
            if (!((DataLightBlockEndpoints) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS))
                .doesPositionReceiveStarlightClient(world, clientWorkbenchPosition)) return false;
        } else {
            if (!((DataLightBlockEndpoints) SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS))
                .doesPositionReceiveStarlightServer(world, pos)) return false;
        }
        return true;
    }

    private boolean vanillaMatch(InventoryCrafting inv) {
        for (int x = 0; x <= ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH - grid.getWidth(); x++) {
            for (int y = 0; y <= ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT - grid.getHeight(); ++y) {
                if (checkMatch(inv, x, y)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY) {
        for (int x = 0; x < ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH; x++) {
            for (int y = 0; y < ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT; y++) {
                int subX = x - startX;
                int subY = y - startY;
                Ingredient target;

                if (subX >= 0 && subY >= 0 && subX < grid.getWidth() && subY < grid.getHeight()) {
                    target = grid.get(ShapedRecipeSlot.getByRowColumnIndex(subX, subY));

                    if (!target.apply(inv.getStackInRowAndColumn(y, x))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return out.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= grid.getWidth() && height >= grid.getHeight();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return out.copy();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return grid.getRawIngredientList();
    }

}
