package shordinger.wrapper.net.minecraft.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.item.EntityEnderCrystal;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldProviderEnd;
import shordinger.wrapper.net.minecraft.world.end.DragonFightManager;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal() {
        this.setUnlocalizedName("end_crystal");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() != Blocks.OBSIDIAN && iblockstate.getBlock() != Blocks.BEDROCK) {
            return EnumActionResult.FAIL;
        } else {
            BlockPos blockpos = pos.up();
            ItemStack itemstack = player.getHeldItem(hand);

            if (!player.canPlayerEdit(blockpos, facing, itemstack)) {
                return EnumActionResult.FAIL;
            } else {
                BlockPos blockpos1 = blockpos.up();
                boolean flag = !worldIn.isAirBlock(blockpos) && !worldIn.getBlockState(blockpos)
                    .getBlock()
                    .isReplaceable(worldIn, blockpos);
                flag = flag | (!worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1)
                    .getBlock()
                    .isReplaceable(worldIn, blockpos1));

                if (flag) {
                    return EnumActionResult.FAIL;
                } else {
                    double d0 = (double) blockpos.getX();
                    double d1 = (double) blockpos.getY();
                    double d2 = (double) blockpos.getZ();
                    List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(
                        (Entity) null,
                        new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

                    if (!list.isEmpty()) {
                        return EnumActionResult.FAIL;
                    } else {
                        if (!worldIn.isRemote) {
                            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(
                                worldIn,
                                (double) ((float) pos.getX() + 0.5F),
                                (double) (pos.getY() + 1),
                                (double) ((float) pos.getZ() + 0.5F));
                            entityendercrystal.setShowBottom(false);
                            worldIn.spawnEntity(entityendercrystal);

                            if (worldIn.provider instanceof WorldProviderEnd) {
                                DragonFightManager dragonfightmanager = ((WorldProviderEnd) worldIn.provider)
                                    .getDragonFightManager();
                                dragonfightmanager.respawnDragon();
                            }
                        }

                        itemstack.shrink(1);
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     * <p>
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
