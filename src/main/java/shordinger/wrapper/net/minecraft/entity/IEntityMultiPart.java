package shordinger.wrapper.net.minecraft.entity;

import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.world.World;

public interface IEntityMultiPart {

    World getWorld();

    boolean attackEntityFromPart(MultiPartEntityPart dragonPart, DamageSource source, float damage);
}
