package shordinger.wrapper.net.minecraft.client.tutorial;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.multiplayer.WorldClient;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.MouseHelper;
import shordinger.wrapper.net.minecraft.util.MovementInput;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;

@SideOnly(Side.CLIENT)
public interface ITutorialStep {

    default void onStop() {
    }

    default void update() {
    }

    default void handleMovement(MovementInput input) {
    }

    default void handleMouse(MouseHelper mouseHelperIn) {
    }

    default void onMouseHover(WorldClient worldIn, RayTraceResult result) {
    }

    default void onHitBlock(WorldClient worldIn, BlockPos pos, IBlockState state, float diggingStage) {
    }

    default void openInventory() {
    }

    default void handleSetSlot(ItemStack stack) {
    }
}
