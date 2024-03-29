package shordinger.wrapper.net.minecraft.entity.item;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEndermite;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityThrowable;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityEndGateway;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable {

    private EntityLivingBase perlThrower;

    public EntityEnderPearl(World worldIn) {
        super(worldIn);
    }

    public EntityEnderPearl(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.perlThrower = throwerIn;
    }

    @SideOnly(Side.CLIENT)
    public EntityEnderPearl(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public static void registerFixesEnderPearl(DataFixer fixer) {
        EntityThrowable.registerFixesThrowable(fixer, "ThrownEnderpearl");
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        EntityLivingBase entitylivingbase = this.getThrower();

        if (result.entityHit != null) {
            if (result.entityHit == this.perlThrower) {
                return;
            }

            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, entitylivingbase), 0.0F);
        }

        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = result.getBlockPos();
            TileEntity tileentity = this.world.getTileEntity(blockpos);

            if (tileentity instanceof TileEntityEndGateway) {
                TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                if (entitylivingbase != null) {
                    if (entitylivingbase instanceof EntityPlayerMP) {
                        CriteriaTriggers.ENTER_BLOCK
                            .trigger((EntityPlayerMP) entitylivingbase, this.world.getBlockState(blockpos));
                    }

                    tileentityendgateway.teleportEntity(entitylivingbase);
                    this.setDead();
                    return;
                }

                tileentityendgateway.teleportEntity(this);
                return;
            }
        }

        for (int i = 0; i < 32; ++i) {
            this.world.spawnParticle(
                EnumParticleTypes.PORTAL,
                this.posX,
                this.posY + this.rand.nextDouble() * 2.0D,
                this.posZ,
                this.rand.nextGaussian(),
                0.0D,
                this.rand.nextGaussian());
        }

        if (!this.world.isRemote) {
            if (entitylivingbase instanceof EntityPlayerMP) {
                EntityPlayerMP entityplayermp = (EntityPlayerMP) entitylivingbase;

                if (entityplayermp.connection.getNetworkManager()
                    .isChannelOpen() && entityplayermp.world == this.world && !entityplayermp.isPlayerSleeping()) {
                    net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(
                        entityplayermp,
                        this.posX,
                        this.posY,
                        this.posZ,
                        5.0F);
                    if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower
                        // patch size
                        if (this.rand.nextFloat() < 0.05F && this.world.getGameRules()
                            .getBoolean("doMobSpawning")) {
                            EntityEndermite entityendermite = new EntityEndermite(this.world);
                            entityendermite.setSpawnedByPlayer(true);
                            entityendermite.setLocationAndAngles(
                                entitylivingbase.posX,
                                entitylivingbase.posY,
                                entitylivingbase.posZ,
                                entitylivingbase.rotationYaw,
                                entitylivingbase.rotationPitch);
                            this.world.spawnEntity(entityendermite);
                        }

                        if (entitylivingbase.isRiding()) {
                            entitylivingbase.dismountRidingEntity();
                        }

                        entitylivingbase
                            .setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                        entitylivingbase.fallDistance = 0.0F;
                        entitylivingbase.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
                    }
                }
            } else if (entitylivingbase != null) {
                entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                entitylivingbase.fallDistance = 0.0F;
            }

            this.setDead();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        EntityLivingBase entitylivingbase = this.getThrower();

        if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive()) {
            this.setDead();
        } else {
            super.onUpdate();
        }
    }

    @Nullable
    public Entity changeDimension(int dimensionIn, net.minecraftforge.common.util.ITeleporter teleporter) {
        if (this.thrower.dimension != dimensionIn) {
            this.thrower = null;
        }

        return super.changeDimension(dimensionIn, teleporter);
    }
}
