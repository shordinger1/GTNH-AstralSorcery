package shordinger.wrapper.net.minecraft.entity.item;

import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityThrowable;
import shordinger.wrapper.net.minecraft.init.PotionTypes;
import shordinger.wrapper.net.minecraft.potion.PotionUtils;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {

    public EntityExpBottle(World worldIn) {
        super(worldIn);
    }

    public EntityExpBottle(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityExpBottle(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public static void registerFixesExpBottle(DataFixer fixer) {
        EntityThrowable.registerFixesThrowable(fixer, "ThrowableExpBottle");
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity() {
        return 0.07F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            this.world.playEvent(2002, new BlockPos(this), PotionUtils.getPotionColor(PotionTypes.WATER));
            int i = 3 + this.world.rand.nextInt(5) + this.world.rand.nextInt(5);

            while (i > 0) {
                int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
            }

            this.setDead();
        }
    }
}
