package shordinger.wrapper.net.minecraft.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.passive.EntityChicken;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.World;

public class EntityEgg extends EntityThrowable {

    public EntityEgg(World worldIn) {
        super(worldIn);
    }

    public EntityEgg(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityEgg(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public static void registerFixesEgg(DataFixer fixer) {
        EntityThrowable.registerFixesThrowable(fixer, "ThrownEgg");
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            double d0 = 0.08D;

            for (int i = 0; i < 8; ++i) {
                this.world.spawnParticle(
                    EnumParticleTypes.ITEM_CRACK,
                    this.posX,
                    this.posY,
                    this.posZ,
                    ((double) this.rand.nextFloat() - 0.5D) * 0.08D,
                    ((double) this.rand.nextFloat() - 0.5D) * 0.08D,
                    ((double) this.rand.nextFloat() - 0.5D) * 0.08D,
                    Item.getIdFromItem(Items.EGG));
            }
        }
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (!this.world.isRemote) {
            if (this.rand.nextInt(8) == 0) {
                int i = 1;

                if (this.rand.nextInt(32) == 0) {
                    i = 4;
                }

                for (int j = 0; j < i; ++j) {
                    EntityChicken entitychicken = new EntityChicken(this.world);
                    entitychicken.setGrowingAge(-24000);
                    entitychicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                    this.world.spawnEntity(entitychicken);
                }
            }

            this.world.setEntityState(this, (byte) 3);
            this.setDead();
        }
    }
}
