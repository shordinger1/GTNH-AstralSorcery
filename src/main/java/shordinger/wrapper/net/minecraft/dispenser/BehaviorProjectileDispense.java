package shordinger.wrapper.net.minecraft.dispenser;

import shordinger.wrapper.net.minecraft.block.BlockDispenser;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.IProjectile;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        IPosition iposition = BlockDispenser.getDispensePosition(source);
        EnumFacing enumfacing = (EnumFacing) source.getBlockState()
            .getValue(BlockDispenser.FACING);
        IProjectile iprojectile = this.getProjectileEntity(world, iposition, stack);
        iprojectile.shoot(
            (double) enumfacing.getFrontOffsetX(),
            (double) ((float) enumfacing.getFrontOffsetY() + 0.1F),
            (double) enumfacing.getFrontOffsetZ(),
            this.getProjectileVelocity(),
            this.getProjectileInaccuracy());
        world.spawnEntity((Entity) iprojectile);
        stack.shrink(1);
        return stack;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(IBlockSource source) {
        source.getWorld()
            .playEvent(1002, source.getBlockPos(), 0);
    }

    /**
     * Return the projectile entity spawned by this dispense behavior.
     */
    protected abstract IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn);

    protected float getProjectileInaccuracy() {
        return 6.0F;
    }

    protected float getProjectileVelocity() {
        return 1.1F;
    }
}
