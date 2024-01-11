package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.EntityCreature;
import shordinger.wrapper.net.minecraft.entity.passive.IAnimals;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimals {

    public EntityGolem(World worldIn) {
        super(worldIn);
    }

    public void fall(float distance, float damageMultiplier) {
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval() {
        return 120;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn() {
        return false;
    }
}
