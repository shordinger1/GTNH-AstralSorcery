package shordinger.wrapper.net.minecraft.entity.ai;

import java.util.List;

import com.google.common.collect.Lists;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;

public class EntitySenses {

    EntityLiving entity;
    /**
     * Cache of entities which we can see
     */
    List<Entity> seenEntities = Lists.<Entity>newArrayList();
    /**
     * Cache of entities which we cannot see
     */
    List<Entity> unseenEntities = Lists.<Entity>newArrayList();

    public EntitySenses(EntityLiving entityIn) {
        this.entity = entityIn;
    }

    /**
     * Clears canSeeCachePositive and canSeeCacheNegative.
     */
    public void clearSensingCache() {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    /**
     * Checks, whether 'our' entity can see the entity given as argument (true) or not (false), caching the result.
     */
    public boolean canSee(Entity entityIn) {
        if (this.seenEntities.contains(entityIn)) {
            return true;
        } else if (this.unseenEntities.contains(entityIn)) {
            return false;
        } else {
            this.entity.world.profiler.startSection("canSee");
            boolean flag = this.entity.canEntityBeSeen(entityIn);
            this.entity.world.profiler.endSection();

            if (flag) {
                this.seenEntities.add(entityIn);
            } else {
                this.unseenEntities.add(entityIn);
            }

            return flag;
        }
    }
}
