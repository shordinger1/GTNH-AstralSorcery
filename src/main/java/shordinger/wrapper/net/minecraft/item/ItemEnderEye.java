package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.block.BlockEndPortalFrame;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.block.state.pattern.BlockPattern;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.item.EntityEnderEye;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;

public class ItemEnderEye extends Item {

    public ItemEnderEye() {
        this.setCreativeTab(CreativeTabs.MISC);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        ItemStack itemstack = player.getHeldItem(hand);

        if (player.canPlayerEdit(pos.offset(facing), facing, itemstack)
            && iblockstate.getBlock() == Blocks.END_PORTAL_FRAME
            && !((Boolean) iblockstate.getValue(BlockEndPortalFrame.EYE)).booleanValue()) {
            if (worldIn.isRemote) {
                return EnumActionResult.SUCCESS;
            } else {
                worldIn.setBlockState(pos, iblockstate.withProperty(BlockEndPortalFrame.EYE, Boolean.valueOf(true)), 2);
                worldIn.updateComparatorOutputLevel(pos, Blocks.END_PORTAL_FRAME);
                itemstack.shrink(1);

                for (int i = 0; i < 16; ++i) {
                    double d0 = (double) ((float) pos.getX() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double d1 = (double) ((float) pos.getY() + 0.8125F);
                    double d2 = (double) ((float) pos.getZ() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double d3 = 0.0D;
                    double d4 = 0.0D;
                    double d5 = 0.0D;
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }

                worldIn.playSound(
                    (EntityPlayer) null,
                    pos,
                    SoundEvents.BLOCK_END_PORTAL_FRAME_FILL,
                    SoundCategory.BLOCKS,
                    1.0F,
                    1.0F);
                BlockPattern.PatternHelper blockpattern$patternhelper = BlockEndPortalFrame.getOrCreatePortalShape()
                    .match(worldIn, pos);

                if (blockpattern$patternhelper != null) {
                    BlockPos blockpos = blockpattern$patternhelper.getFrontTopLeft()
                        .add(-3, 0, -3);

                    for (int j = 0; j < 3; ++j) {
                        for (int k = 0; k < 3; ++k) {
                            worldIn.setBlockState(blockpos.add(j, 0, k), Blocks.END_PORTAL.getDefaultState(), 2);
                        }
                    }

                    worldIn.playBroadcastSound(1038, blockpos.add(1, 0, 1), 0);
                }

                return EnumActionResult.SUCCESS;
            }
        } else {
            return EnumActionResult.FAIL;
        }
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, false);

        if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK
            && worldIn.getBlockState(raytraceresult.getBlockPos())
            .getBlock() == Blocks.END_PORTAL_FRAME) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
        } else {
            playerIn.setActiveHand(handIn);

            if (!worldIn.isRemote) {
                BlockPos blockpos = ((WorldServer) worldIn).getChunkProvider()
                    .getNearestStructurePos(worldIn, "Stronghold", new BlockPos(playerIn), false);

                if (blockpos != null) {
                    EntityEnderEye entityendereye = new EntityEnderEye(
                        worldIn,
                        playerIn.posX,
                        playerIn.posY + (double) (playerIn.height / 2.0F),
                        playerIn.posZ);
                    entityendereye.moveTowards(blockpos);
                    worldIn.spawnEntity(entityendereye);

                    if (playerIn instanceof EntityPlayerMP) {
                        CriteriaTriggers.USED_ENDER_EYE.trigger((EntityPlayerMP) playerIn, blockpos);
                    }

                    worldIn.playSound(
                        (EntityPlayer) null,
                        playerIn.posX,
                        playerIn.posY,
                        playerIn.posZ,
                        SoundEvents.ENTITY_ENDEREYE_LAUNCH,
                        SoundCategory.NEUTRAL,
                        0.5F,
                        0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                    worldIn.playEvent((EntityPlayer) null, 1003, new BlockPos(playerIn), 0);

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
                }
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
    }
}
