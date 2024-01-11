package shordinger.wrapper.net.minecraft.block;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockButtonWood extends BlockButton {

    protected BlockButtonWood() {
        super(true);
    }

    protected void playClickSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
    }

    protected void playReleaseSound(World worldIn, BlockPos pos) {
        worldIn.playSound(
            (EntityPlayer) null,
            pos,
            SoundEvents.BLOCK_WOOD_BUTTON_CLICK_OFF,
            SoundCategory.BLOCKS,
            0.3F,
            0.5F);
    }
}
