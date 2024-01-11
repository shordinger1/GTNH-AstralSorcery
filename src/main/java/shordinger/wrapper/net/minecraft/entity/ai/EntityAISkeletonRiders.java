package shordinger.wrapper.net.minecraft.entity.ai;

import shordinger.wrapper.net.minecraft.enchantment.EnchantmentHelper;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.effect.EntityLightningBolt;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySkeleton;
import shordinger.wrapper.net.minecraft.entity.passive.AbstractHorse;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySkeletonHorse;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;

public class EntityAISkeletonRiders extends EntityAIBase {

    private final EntitySkeletonHorse horse;

    public EntityAISkeletonRiders(EntitySkeletonHorse horseIn) {
        this.horse = horseIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return this.horse.world.isAnyPlayerWithinRangeAt(this.horse.posX, this.horse.posY, this.horse.posZ, 10.0D);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask() {
        DifficultyInstance difficultyinstance = this.horse.world.getDifficultyForLocation(new BlockPos(this.horse));
        this.horse.setTrap(false);
        this.horse.setHorseTamed(true);
        this.horse.setGrowingAge(0);
        this.horse.world.addWeatherEffect(
            new EntityLightningBolt(this.horse.world, this.horse.posX, this.horse.posY, this.horse.posZ, true));
        EntitySkeleton entityskeleton = this.createSkeleton(difficultyinstance, this.horse);
        entityskeleton.startRiding(this.horse);

        for (int i = 0; i < 3; ++i) {
            AbstractHorse abstracthorse = this.createHorse(difficultyinstance);
            EntitySkeleton entityskeleton1 = this.createSkeleton(difficultyinstance, abstracthorse);
            entityskeleton1.startRiding(abstracthorse);
            abstracthorse.addVelocity(
                this.horse.getRNG()
                    .nextGaussian() * 0.5D,
                0.0D,
                this.horse.getRNG()
                    .nextGaussian() * 0.5D);
        }
    }

    private AbstractHorse createHorse(DifficultyInstance p_188515_1_) {
        EntitySkeletonHorse entityskeletonhorse = new EntitySkeletonHorse(this.horse.world);
        entityskeletonhorse.onInitialSpawn(p_188515_1_, (IEntityLivingData) null);
        entityskeletonhorse.setPosition(this.horse.posX, this.horse.posY, this.horse.posZ);
        entityskeletonhorse.hurtResistantTime = 60;
        entityskeletonhorse.enablePersistence();
        entityskeletonhorse.setHorseTamed(true);
        entityskeletonhorse.setGrowingAge(0);
        entityskeletonhorse.world.spawnEntity(entityskeletonhorse);
        return entityskeletonhorse;
    }

    private EntitySkeleton createSkeleton(DifficultyInstance p_188514_1_, AbstractHorse p_188514_2_) {
        EntitySkeleton entityskeleton = new EntitySkeleton(p_188514_2_.world);
        entityskeleton.onInitialSpawn(p_188514_1_, (IEntityLivingData) null);
        entityskeleton.setPosition(p_188514_2_.posX, p_188514_2_.posY, p_188514_2_.posZ);
        entityskeleton.hurtResistantTime = 60;
        entityskeleton.enablePersistence();

        if (entityskeleton.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
            .isEmpty()) {
            entityskeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }

        entityskeleton.setItemStackToSlot(
            EntityEquipmentSlot.MAINHAND,
            EnchantmentHelper.addRandomEnchantment(
                entityskeleton.getRNG(),
                entityskeleton.getHeldItemMainhand(),
                (int) (5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float) entityskeleton.getRNG()
                    .nextInt(18)),
                false));
        entityskeleton.setItemStackToSlot(
            EntityEquipmentSlot.HEAD,
            EnchantmentHelper.addRandomEnchantment(
                entityskeleton.getRNG(),
                entityskeleton.getItemStackFromSlot(EntityEquipmentSlot.HEAD),
                (int) (5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float) entityskeleton.getRNG()
                    .nextInt(18)),
                false));
        entityskeleton.world.spawnEntity(entityskeleton);
        return entityskeleton;
    }
}
