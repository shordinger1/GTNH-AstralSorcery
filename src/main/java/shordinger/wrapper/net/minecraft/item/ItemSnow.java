package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockSnow;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemSnow extends ItemBlock {

    public ItemSnow(Block block) {
        super(block);
        this.setMaxDamage(0);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack)) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            BlockPos blockpos = pos;

            if ((facing != EnumFacing.UP || block != this.block) && !block.isReplaceable(worldIn, pos)) {
                blockpos = pos.offset(facing);
                iblockstate = worldIn.getBlockState(blockpos);
                block = iblockstate.getBlock();
            }

            if (block == this.block) {
                int i = ((Integer) iblockstate.getValue(BlockSnow.LAYERS)).intValue();

                if (i < 8) {
                    IBlockState iblockstate1 = iblockstate.withProperty(BlockSnow.LAYERS, Integer.valueOf(i + 1));
                    AxisAlignedBB axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, blockpos);

                    if (axisalignedbb != Block.NULL_AABB
                        && worldIn.checkNoEntityCollision(axisalignedbb.offset(blockpos))
                        && worldIn.setBlockState(blockpos, iblockstate1, 10)) {
                        SoundType soundtype = this.block.getSoundType(iblockstate1, worldIn, pos, player);
                        worldIn.playSound(
                            player,
                            blockpos,
                            soundtype.getPlaceSound(),
                            SoundCategory.BLOCKS,
                            (soundtype.getVolume() + 1.0F) / 2.0F,
                            soundtype.getPitch() * 0.8F);

                        if (player instanceof EntityPlayerMP) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
                        }

                        itemstack.shrink(1);
                        return EnumActionResult.SUCCESS;
                    }
                }
            }

            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        } else {
            return EnumActionResult.FAIL;
        }
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage) {
        return damage;
    }

    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player,
                                       ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        return (state.getBlock() != net.minecraft.init.Blocks.SNOW_LAYER
            || ((Integer) state.getValue(BlockSnow.LAYERS)) > 7)
            ? super.canPlaceBlockOnSide(world, pos, side, player, stack)
            : true;
    }
}
