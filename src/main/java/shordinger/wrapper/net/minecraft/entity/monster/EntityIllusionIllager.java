package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.EnumCreatureAttribute;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.IRangedAttackMob;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIAttackRangedBow;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIHurtByTarget;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAISwimming;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWander;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWatchClosest;
import shordinger.wrapper.net.minecraft.entity.passive.EntityVillager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityTippedArrow;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;
import shordinger.wrapper.net.minecraft.world.EnumDifficulty;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityIllusionIllager extends EntitySpellcasterIllager implements IRangedAttackMob {

    private int ghostTime;
    private final Vec3d[][] renderLocations;

    public EntityIllusionIllager(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
        this.experienceValue = 5;
        this.renderLocations = new Vec3d[2][4];

        for (int i = 0; i < 4; ++i) {
            this.renderLocations[0][i] = new Vec3d(0.0D, 0.0D, 0.0D);
            this.renderLocations[1][i] = new Vec3d(0.0D, 0.0D, 0.0D);
        }
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntitySpellcasterIllager.AICastingApell());
        this.tasks.addTask(4, new EntityIllusionIllager.AIMirriorSpell());
        this.tasks.addTask(5, new EntityIllusionIllager.AIBlindnessSpell());
        this.tasks.addTask(6, new EntityAIAttackRangedBow(this, 0.5D, 20, 15.0F));
        this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityIllusionIllager.class}));
        this.targetTasks.addTask(
            2,
            (new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(
            3,
            (new EntityAINearestAttackableTarget(this, EntityVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(
            3,
            (new EntityAINearestAttackableTarget(this, EntityIronGolem.class, false)).setUnseenMemoryTicks(300));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE)
            .setBaseValue(18.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(32.0D);
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
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.onInitialSpawn(difficulty, livingdata);
    }

    protected void entityInit() {
        super.entityInit();
    }

    protected ResourceLocation getLootTable() {
        return LootTableList.EMPTY;
    }

    /**
     * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained
     * by a minecart, such as a command block).
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox()
            .grow(3.0D, 0.0D, 3.0D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.world.isRemote && this.isInvisible()) {
            --this.ghostTime;

            if (this.ghostTime < 0) {
                this.ghostTime = 0;
            }

            if (this.hurtTime != 1 && this.ticksExisted % 1200 != 0) {
                if (this.hurtTime == this.maxHurtTime - 1) {
                    this.ghostTime = 3;

                    for (int k = 0; k < 4; ++k) {
                        this.renderLocations[0][k] = this.renderLocations[1][k];
                        this.renderLocations[1][k] = new Vec3d(0.0D, 0.0D, 0.0D);
                    }
                }
            } else {
                this.ghostTime = 3;
                float f = -6.0F;
                int i = 13;

                for (int j = 0; j < 4; ++j) {
                    this.renderLocations[0][j] = this.renderLocations[1][j];
                    this.renderLocations[1][j] = new Vec3d(
                        (double) (-6.0F + (float) this.rand.nextInt(13)) * 0.5D,
                        (double) Math.max(0, this.rand.nextInt(6) - 4),
                        (double) (-6.0F + (float) this.rand.nextInt(13)) * 0.5D);
                }

                for (int l = 0; l < 16; ++l) {
                    this.world.spawnParticle(
                        EnumParticleTypes.CLOUD,
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        this.posY + this.rand.nextDouble() * (double) this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        0.0D,
                        0.0D,
                        0.0D);
                }

                this.world.playSound(
                    this.posX,
                    this.posY,
                    this.posZ,
                    SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE,
                    this.getSoundCategory(),
                    1.0F,
                    1.0F,
                    false);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public Vec3d[] getRenderLocations(float p_193098_1_) {
        if (this.ghostTime <= 0) {
            return this.renderLocations[1];
        } else {
            double d0 = (double) (((float) this.ghostTime - p_193098_1_) / 3.0F);
            d0 = Math.pow(d0, 0.25D);
            Vec3d[] avec3d = new Vec3d[4];

            for (int i = 0; i < 4; ++i) {
                avec3d[i] = this.renderLocations[1][i].scale(1.0D - d0)
                    .add(this.renderLocations[0][i].scale(d0));
            }

            return avec3d;
        }
    }

    /**
     * Returns whether this Entity is on the same team as the given Entity.
     */
    public boolean isOnSameTeam(Entity entityIn) {
        if (super.isOnSameTeam(entityIn)) {
            return true;
        } else if (entityIn instanceof EntityLivingBase
            && ((EntityLivingBase) entityIn).getCreatureAttribute() == EnumCreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return false;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ILLUSION_ILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ILLUSION_ILLAGER_HURT;
    }

    protected SoundEvent getSpellSound() {
        return SoundEvents.ENTITY_ILLAGER_CAST_SPELL;
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        EntityArrow entityarrow = this.createArrowEntity(distanceFactor);
        if (this.getHeldItemMainhand()
            .getItem() instanceof net.minecraft.item.ItemBow)
            entityarrow = ((net.minecraft.item.ItemBow) this.getHeldItemMainhand()
                .getItem()).customizeArrow(entityarrow);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - entityarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(
            d0,
            d1 + d3 * 0.20000000298023224D,
            d2,
            1.6F,
            (float) (14 - this.world.getDifficulty()
                .getDifficultyId() * 4));
        this.playSound(
            SoundEvents.ENTITY_SKELETON_SHOOT,
            1.0F,
            1.0F / (this.getRNG()
                .nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);
    }

    protected EntityArrow createArrowEntity(float p_193097_1_) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
        entitytippedarrow.setEnchantmentEffectsFromEntity(this, p_193097_1_);
        return entitytippedarrow;
    }

    @SideOnly(Side.CLIENT)
    public boolean isAggressive() {
        return this.isAggressive(1);
    }

    public void setSwingingArms(boolean swingingArms) {
        this.setAggressive(1, swingingArms);
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isSpellcasting()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        } else {
            return this.isAggressive() ? AbstractIllager.IllagerArmPose.BOW_AND_ARROW
                : AbstractIllager.IllagerArmPose.CROSSED;
        }
    }

    class AIBlindnessSpell extends EntitySpellcasterIllager.AIUseSpell {

        private int lastTargetId;

        private AIBlindnessSpell() {
            super();
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (!super.shouldExecute()) {
                return false;
            } else if (EntityIllusionIllager.this.getAttackTarget() == null) {
                return false;
            } else if (EntityIllusionIllager.this.getAttackTarget()
                .getEntityId() == this.lastTargetId) {
                return false;
            } else {
                return EntityIllusionIllager.this.world
                    .getDifficultyForLocation(new BlockPos(EntityIllusionIllager.this))
                    .isHarderThan((float) EnumDifficulty.NORMAL.ordinal());
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            super.startExecuting();
            this.lastTargetId = EntityIllusionIllager.this.getAttackTarget()
                .getEntityId();
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 180;
        }

        protected void castSpell() {
            EntityIllusionIllager.this.getAttackTarget()
                .addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 400));
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_ILLAGER_PREPARE_BLINDNESS;
        }

        protected EntitySpellcasterIllager.SpellType getSpellType() {
            return EntitySpellcasterIllager.SpellType.BLINDNESS;
        }
    }

    class AIMirriorSpell extends EntitySpellcasterIllager.AIUseSpell {

        private AIMirriorSpell() {
            super();
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (!super.shouldExecute()) {
                return false;
            } else {
                return !EntityIllusionIllager.this.isPotionActive(MobEffects.INVISIBILITY);
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void castSpell() {
            EntityIllusionIllager.this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 1200));
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_ILLAGER_PREPARE_MIRROR;
        }

        protected EntitySpellcasterIllager.SpellType getSpellType() {
            return EntitySpellcasterIllager.SpellType.DISAPPEAR;
        }
    }
}
