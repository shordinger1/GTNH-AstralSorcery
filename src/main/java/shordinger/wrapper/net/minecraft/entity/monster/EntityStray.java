package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityTippedArrow;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityStray extends AbstractSkeleton {

    public EntityStray(World worldIn) {
        super(worldIn);
    }

    public static void registerFixesStray(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityStray.class);
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && this.world.canSeeSky(new BlockPos(this));
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_STRAY;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRAY_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_STRAY_STEP;
    }

    protected EntityArrow getArrow(float p_190726_1_) {
        EntityArrow entityarrow = super.getArrow(p_190726_1_);

        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).addEffect(new PotionEffect(MobEffects.SLOWNESS, 600));
        }

        return entityarrow;
    }
}
