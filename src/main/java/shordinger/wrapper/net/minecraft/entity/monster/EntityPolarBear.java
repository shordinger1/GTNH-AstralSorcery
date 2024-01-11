package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityAgeable;
import shordinger.wrapper.net.minecraft.entity.EntityCreature;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIAttackMelee;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIFollowParent;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIHurtByTarget;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAILookIdle;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIPanic;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAISwimming;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWander;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWatchClosest;
import shordinger.wrapper.net.minecraft.entity.passive.EntityAnimal;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityPolarBear extends EntityAnimal {

    private static final DataParameter<Boolean> IS_STANDING = EntityDataManager
        .<Boolean>createKey(EntityPolarBear.class, DataSerializers.BOOLEAN);
    private float clientSideStandAnimation0;
    private float clientSideStandAnimation;
    private int warningSoundTicks;

    public EntityPolarBear(World worldIn) {
        super(worldIn);
        this.setSize(1.3F, 1.4F);
    }

    public EntityAgeable createChild(EntityAgeable ageable) {
        return new EntityPolarBear(this.world);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityPolarBear.AIMeleeAttack());
        this.tasks.addTask(1, new EntityPolarBear.AIPanic());
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityPolarBear.AIHurtByTarget());
        this.targetTasks.addTask(2, new EntityPolarBear.AIAttackPlayer());
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE)
            .setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.25D);
        this.getAttributeMap()
            .registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
            .setBaseValue(6.0D);
    }

    protected SoundEvent getAmbientSound() {
        return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_BABY_AMBIENT : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_POLAR_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_POLAR_BEAR;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(IS_STANDING, Boolean.valueOf(false));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            this.clientSideStandAnimation0 = this.clientSideStandAnimation;

            if (this.isStanding()) {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            } else {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(
            DamageSource.causeMobDamage(this),
            (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
                .getAttributeValue()));

        if (flag) {
            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    public boolean isStanding() {
        return ((Boolean) this.dataManager.get(IS_STANDING)).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(IS_STANDING, Boolean.valueOf(standing));
    }

    @SideOnly(Side.CLIENT)
    public float getStandingAnimationScale(float p_189795_1_) {
        return (this.clientSideStandAnimation0
            + (this.clientSideStandAnimation - this.clientSideStandAnimation0) * p_189795_1_) / 6.0F;
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory.
     * <p>
     * The livingdata parameter is used to pass data between all instances during a pack spawn. It will be null on the
     * first call. Subclasses may check if it's null, and then create a new one and return it if so, initializing all
     * entities in the pack with the contained data.
     *
     * @param difficulty The current local difficulty
     * @param livingdata Shared spawn data. Will usually be null. (See return value for more information)
     * @return The IEntityLivingData to pass to this method for other instances of this entity class within the same
     * pack
     */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        if (livingdata instanceof EntityPolarBear.GroupData) {
            if (((EntityPolarBear.GroupData) livingdata).madeParent) {
                this.setGrowingAge(-24000);
            }
        } else {
            EntityPolarBear.GroupData entitypolarbear$groupdata = new EntityPolarBear.GroupData();
            entitypolarbear$groupdata.madeParent = true;
            livingdata = entitypolarbear$groupdata;
        }

        return livingdata;
    }

    class AIAttackPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {

        public AIAttackPlayer() {
            super(EntityPolarBear.this, EntityPlayer.class, 20, true, true, (Predicate) null);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (EntityPolarBear.this.isChild()) {
                return false;
            } else {
                if (super.shouldExecute()) {
                    for (EntityPolarBear entitypolarbear : EntityPolarBear.this.world.getEntitiesWithinAABB(
                        EntityPolarBear.class,
                        EntityPolarBear.this.getEntityBoundingBox()
                            .grow(8.0D, 4.0D, 8.0D))) {
                        if (entitypolarbear.isChild()) {
                            return true;
                        }
                    }
                }

                EntityPolarBear.this.setAttackTarget((EntityLivingBase) null);
                return false;
            }
        }

        protected double getTargetDistance() {
            return super.getTargetDistance() * 0.5D;
        }
    }

    class AIHurtByTarget extends EntityAIHurtByTarget {

        public AIHurtByTarget() {
            super(EntityPolarBear.this, false);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            super.startExecuting();

            if (EntityPolarBear.this.isChild()) {
                this.alertOthers();
                this.resetTask();
            }
        }

        protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
            if (creatureIn instanceof EntityPolarBear && !creatureIn.isChild()) {
                super.setEntityAttackTarget(creatureIn, entityLivingBaseIn);
            }
        }
    }

    class AIMeleeAttack extends EntityAIAttackMelee {

        public AIMeleeAttack() {
            super(EntityPolarBear.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);

            if (distToEnemySqr <= d0 && this.attackTick <= 0) {
                this.attackTick = 20;
                this.attacker.attackEntityAsMob(enemy);
                EntityPolarBear.this.setStanding(false);
            } else if (distToEnemySqr <= d0 * 2.0D) {
                if (this.attackTick <= 0) {
                    EntityPolarBear.this.setStanding(false);
                    this.attackTick = 20;
                }

                if (this.attackTick <= 10) {
                    EntityPolarBear.this.setStanding(true);
                    EntityPolarBear.this.playWarningSound();
                }
            } else {
                this.attackTick = 20;
                EntityPolarBear.this.setStanding(false);
            }
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EntityPolarBear.this.setStanding(false);
            super.resetTask();
        }

        protected double getAttackReachSqr(EntityLivingBase attackTarget) {
            return (double) (4.0F + attackTarget.width);
        }
    }

    class AIPanic extends EntityAIPanic {

        public AIPanic() {
            super(EntityPolarBear.this, 2.0D);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            return !EntityPolarBear.this.isChild() && !EntityPolarBear.this.isBurning() ? false : super.shouldExecute();
        }
    }

    static class GroupData implements IEntityLivingData {

        public boolean madeParent;

        private GroupData() {
        }
    }
}
