package shordinger.wrapper.net.minecraft.item;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityHanging;
import shordinger.wrapper.net.minecraft.entity.item.EntityItemFrame;
import shordinger.wrapper.net.minecraft.entity.item.EntityPainting;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemHangingEntity extends Item {

    private final Class<? extends EntityHanging> hangingEntityClass;

    public ItemHangingEntity(Class<? extends EntityHanging> entityClass) {
        this.hangingEntityClass = entityClass;
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);
        BlockPos blockpos = pos.offset(facing);

        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(blockpos, facing, itemstack)) {
            EntityHanging entityhanging = this.createEntity(worldIn, blockpos, facing);

            if (entityhanging != null && entityhanging.onValidSurface()) {
                if (!worldIn.isRemote) {
                    entityhanging.playPlaceSound();
                    worldIn.spawnEntity(entityhanging);
                }

                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Nullable
    private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide) {
        if (this.hangingEntityClass == EntityPainting.class) {
            return new EntityPainting(worldIn, pos, clickedSide);
        } else {
            return this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(worldIn, pos, clickedSide)
                : null;
        }
    }
}
