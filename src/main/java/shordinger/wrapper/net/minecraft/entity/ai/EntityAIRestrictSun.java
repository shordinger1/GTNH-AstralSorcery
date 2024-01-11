package shordinger.wrapper.net.minecraft.entity.ai;

import shordinger.wrapper.net.minecraft.entity.EntityCreature;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.pathfinding.PathNavigateGround;

public class EntityAIRestrictSun extends EntityAIBase {

    private final EntityCreature entity;

    public EntityAIRestrictSun(EntityCreature creature) {
        this.entity = creature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return this.entity.world.isDaytime() && this.entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
            .isEmpty();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        ((PathNavigateGround) this.entity.getNavigator()).setAvoidSun(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        ((PathNavigateGround) this.entity.getNavigator()).setAvoidSun(false);
    }
}
