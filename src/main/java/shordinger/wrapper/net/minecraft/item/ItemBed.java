package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockBed;
import shordinger.wrapper.net.minecraft.block.SoundType;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityBed;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemBed extends Item {

    public ItemBed() {
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        } else if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        } else {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            boolean flag = block.isReplaceable(worldIn, pos);

            if (!flag) {
                pos = pos.up();
            }

            int i = MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            EnumFacing enumfacing = EnumFacing.getHorizontal(i);
            BlockPos blockpos = pos.offset(enumfacing);
            ItemStack itemstack = player.getHeldItem(hand);

            if (player.canPlayerEdit(pos, facing, itemstack) && player.canPlayerEdit(blockpos, facing, itemstack)) {
                IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                boolean flag1 = iblockstate1.getBlock()
                    .isReplaceable(worldIn, blockpos);
                boolean flag2 = flag || worldIn.isAirBlock(pos);
                boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

                if (flag2 && flag3
                    && worldIn.getBlockState(pos.down())
                    .isTopSolid()
                    && worldIn.getBlockState(blockpos.down())
                    .isTopSolid()) {
                    IBlockState iblockstate2 = Blocks.BED.getDefaultState()
                        .withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false))
                        .withProperty(BlockBed.FACING, enumfacing)
                        .withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
                    worldIn.setBlockState(pos, iblockstate2, 10);
                    worldIn.setBlockState(
                        blockpos,
                        iblockstate2.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD),
                        10);
                    SoundType soundtype = iblockstate2.getBlock()
                        .getSoundType(iblockstate2, worldIn, pos, player);
                    worldIn.playSound(
                        (EntityPlayer) null,
                        pos,
                        soundtype.getPlaceSound(),
                        SoundCategory.BLOCKS,
                        (soundtype.getVolume() + 1.0F) / 2.0F,
                        soundtype.getPitch() * 0.8F);
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityBed) {
                        ((TileEntityBed) tileentity).setItemValues(itemstack);
                    }

                    TileEntity tileentity1 = worldIn.getTileEntity(pos);

                    if (tileentity1 instanceof TileEntityBed) {
                        ((TileEntityBed) tileentity1).setItemValues(itemstack);
                    }

                    worldIn.notifyNeighborsRespectDebug(pos, block, false);
                    worldIn.notifyNeighborsRespectDebug(blockpos, iblockstate1.getBlock(), false);

                    if (player instanceof EntityPlayerMP) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
                    }

                    itemstack.shrink(1);
                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.FAIL;
                }
            } else {
                return EnumActionResult.FAIL;
            }
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "."
            + EnumDyeColor.byMetadata(stack.getMetadata())
            .getUnlocalizedName();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < 16; ++i) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }
}
