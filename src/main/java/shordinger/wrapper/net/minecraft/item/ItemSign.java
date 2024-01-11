package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.block.BlockStandingSign;
import shordinger.wrapper.net.minecraft.block.BlockWallSign;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntitySign;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemSign extends Item {

    public ItemSign() {
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        boolean flag = iblockstate.getBlock()
            .isReplaceable(worldIn, pos);

        if (facing != EnumFacing.DOWN && (iblockstate.getMaterial()
            .isSolid() || flag) && (!flag || facing == EnumFacing.UP)) {
            pos = pos.offset(facing);
            ItemStack itemstack = player.getHeldItem(hand);

            if (player.canPlayerEdit(pos, facing, itemstack) && Blocks.STANDING_SIGN.canPlaceBlockAt(worldIn, pos)) {
                if (worldIn.isRemote) {
                    return EnumActionResult.SUCCESS;
                } else {
                    pos = flag ? pos.down() : pos;

                    if (facing == EnumFacing.UP) {
                        int i = MathHelper.floor((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                        worldIn.setBlockState(
                            pos,
                            Blocks.STANDING_SIGN.getDefaultState()
                                .withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)),
                            11);
                    } else {
                        worldIn.setBlockState(
                            pos,
                            Blocks.WALL_SIGN.getDefaultState()
                                .withProperty(BlockWallSign.FACING, facing),
                            11);
                    }

                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntitySign
                        && !ItemBlock.setTileEntityNBT(worldIn, player, pos, itemstack)) {
                        player.openEditSign((TileEntitySign) tileentity);
                    }

                    if (player instanceof EntityPlayerMP) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
                    }

                    itemstack.shrink(1);
                    return EnumActionResult.SUCCESS;
                }
            } else {
                return EnumActionResult.FAIL;
            }
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
