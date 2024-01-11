package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.EnumCreatureAttribute;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIAttackMelee;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIHurtByTarget;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAISwimming;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWander;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWatchClosest;
import shordinger.wrapper.net.minecraft.entity.passive.EntityVillager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityVindicator extends AbstractIllager {

    private boolean johnny;
    private static final Predicate<Entity> JOHNNY_SELECTOR = new Predicate<Entity>() {

        public boolean apply(@Nullable Entity p_apply_1_) {
            return p_apply_1_ instanceof EntityLivingBase && ((EntityLivingBase) p_apply_1_).attackable();
        }
    };

    public EntityVindicator(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
    }

    public static void registerFixesVindicator(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityVindicator.class);
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityVindicator.class}));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
        this.targetTasks.addTask(4, new EntityVindicator.AIJohnnyAttack(this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.3499999940395355D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE)
            .setBaseValue(12.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(24.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
            .setBaseValue(5.0D);
    }

    protected void entityInit() {
        super.entityInit();
    }

    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_VINDICATION_ILLAGER;
    }

    @SideOnly(Side.CLIENT)
    public boolean isAggressive() {
        return this.isAggressive(1);
    }

    public void setAggressive(boolean p_190636_1_) {
        this.setAggressive(1, p_190636_1_);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (this.johnny) {
            compound.setBoolean("Johnny", true);
        }
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose() {
        return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.CROSSED;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("Johnny", 99)) {
            this.johnny = compound.getBoolean("Johnny");
        }
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
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        IEntityLivingData ientitylivingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        return ientitylivingdata;
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
    }

    protected void updateAITasks() {
        super.updateAITasks();
        this.setAggressive(this.getAttackTarget() != null);
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

    /**
     * Sets the custom name tag for this entity
     */
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);

        if (!this.johnny && "Johnny".equals(name)) {
            this.johnny = true;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATION_ILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATION_ILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VINDICATION_ILLAGER_HURT;
    }

    static class AIJohnnyAttack extends EntityAINearestAttackableTarget<EntityLivingBase> {

        public AIJohnnyAttack(EntityVindicator vindicator) {
            super(vindicator, EntityLivingBase.class, 0, true, true, EntityVindicator.JOHNNY_SELECTOR);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            return ((EntityVindicator) this.taskOwner).johnny && super.shouldExecute();
        }
    }
}
