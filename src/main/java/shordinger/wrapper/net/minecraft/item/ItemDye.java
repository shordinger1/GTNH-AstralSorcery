package shordinger.wrapper.net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockOldLog;
import shordinger.wrapper.net.minecraft.block.BlockPlanks;
import shordinger.wrapper.net.minecraft.block.IGrowable;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySheep;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemDye extends Item {

    public static final int[] DYE_COLORS = new int[]{1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799,
        11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    public ItemDye() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "."
            + EnumDyeColor.byDyeDamage(i)
            .getUnlocalizedName();
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
            return EnumActionResult.FAIL;
        } else {
            EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(itemstack.getMetadata());

            if (enumdyecolor == EnumDyeColor.WHITE) {
                if (applyBonemeal(itemstack, worldIn, pos, player, hand)) {
                    if (!worldIn.isRemote) {
                        worldIn.playEvent(2005, pos, 0);
                    }

                    return EnumActionResult.SUCCESS;
                }
            } else if (enumdyecolor == EnumDyeColor.BROWN) {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                Block block = iblockstate.getBlock();

                if (block == Blocks.LOG && iblockstate.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE) {
                    if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                        return EnumActionResult.FAIL;
                    }

                    pos = pos.offset(facing);

                    if (worldIn.isAirBlock(pos)) {
                        IBlockState iblockstate1 = Blocks.COCOA
                            .getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, player, hand);
                        worldIn.setBlockState(pos, iblockstate1, 10);

                        if (!player.capabilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }

                        return EnumActionResult.SUCCESS;
                    }
                }

                return EnumActionResult.FAIL;
            }

            return EnumActionResult.PASS;
        }
    }

    public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target) {
        if (worldIn instanceof net.minecraft.world.WorldServer) return applyBonemeal(
            stack,
            worldIn,
            target,
            net.minecraftforge.common.util.FakePlayerFactory.getMinecraft((net.minecraft.world.WorldServer) worldIn),
            null);
        return false;
    }

    public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target, EntityPlayer player,
                                        @javax.annotation.Nullable EnumHand hand) {
        IBlockState iblockstate = worldIn.getBlockState(target);

        int hook = net.minecraftforge.event.ForgeEventFactory
            .onApplyBonemeal(player, worldIn, target, iblockstate, stack, hand);
        if (hook != 0) return hook > 0;

        if (iblockstate.getBlock() instanceof IGrowable) {
            IGrowable igrowable = (IGrowable) iblockstate.getBlock();

            if (igrowable.canGrow(worldIn, target, iblockstate, worldIn.isRemote)) {
                if (!worldIn.isRemote) {
                    if (igrowable.canUseBonemeal(worldIn, worldIn.rand, target, iblockstate)) {
                        igrowable.grow(worldIn, worldIn.rand, target, iblockstate);
                    }

                    stack.shrink(1);
                }

                return true;
            }
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void spawnBonemealParticles(World worldIn, BlockPos pos, int amount) {
        if (amount == 0) {
            amount = 15;
        }

        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getMaterial() != Material.AIR) {
            for (int i = 0; i < amount; ++i) {
                double d0 = itemRand.nextGaussian() * 0.02D;
                double d1 = itemRand.nextGaussian() * 0.02D;
                double d2 = itemRand.nextGaussian() * 0.02D;
                worldIn.spawnParticle(
                    EnumParticleTypes.VILLAGER_HAPPY,
                    (double) ((float) pos.getX() + itemRand.nextFloat()),
                    (double) pos.getY() + (double) itemRand.nextFloat() * iblockstate.getBoundingBox(worldIn, pos).maxY,
                    (double) ((float) pos.getZ() + itemRand.nextFloat()),
                    d0,
                    d1,
                    d2);
            }
        } else {
            for (int i1 = 0; i1 < amount; ++i1) {
                double d0 = itemRand.nextGaussian() * 0.02D;
                double d1 = itemRand.nextGaussian() * 0.02D;
                double d2 = itemRand.nextGaussian() * 0.02D;
                worldIn.spawnParticle(
                    EnumParticleTypes.VILLAGER_HAPPY,
                    (double) ((float) pos.getX() + itemRand.nextFloat()),
                    (double) pos.getY() + (double) itemRand.nextFloat() * 1.0f,
                    (double) ((float) pos.getZ() + itemRand.nextFloat()),
                    d0,
                    d1,
                    d2,
                    new int[0]);
            }
        }
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target,
                                            EnumHand hand) {
        if (target instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep) target;
            EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(stack.getMetadata());

            if (!entitysheep.getSheared() && entitysheep.getFleeceColor() != enumdyecolor) {
                entitysheep.setFleeceColor(enumdyecolor);
                stack.shrink(1);
            }

            return true;
        } else {
            return false;
        }
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
