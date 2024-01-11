package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityWitherSkeleton extends AbstractSkeleton {

    public EntityWitherSkeleton(World worldIn) {
        super(worldIn);
        this.setSize(0.7F, 2.4F);
        this.isImmuneToFire = true;
    }

    public static void registerFixesWitherSkeleton(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityWitherSkeleton.class);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_WITHER_SKELETON;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_STEP;
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        if (cause.getTrueSource() instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) cause.getTrueSource();

            if (entitycreeper.getPowered() && entitycreeper.ableToCauseSkullDrop()) {
                entitycreeper.incrementDroppedSkulls();
                this.entityDropItem(new ItemStack(Items.SKULL, 1, 1), 0.0F);
            }
        }
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
    }

    /**
     * Enchants Entity's current equipments based on given DifficultyInstance
     */
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
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
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
            .setBaseValue(4.0D);
        this.setCombatTask();
        return ientitylivingdata;
    }

    public float getEyeHeight() {
        return 2.1F;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (!super.attackEntityAsMob(entityIn)) {
            return false;
        } else {
            if (entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
            }

            return true;
        }
    }

    protected EntityArrow getArrow(float p_190726_1_) {
        EntityArrow entityarrow = super.getArrow(p_190726_1_);
        entityarrow.setFire(100);
        return entityarrow;
    }
}
