package shordinger.wrapper.net.minecraft.entity;

import java.util.Map;

import com.google.common.collect.Maps;

import shordinger.wrapper.net.minecraft.entity.boss.EntityDragon;
import shordinger.wrapper.net.minecraft.entity.boss.EntityWither;
import shordinger.wrapper.net.minecraft.entity.monster.EntityBlaze;
import shordinger.wrapper.net.minecraft.entity.monster.EntityCaveSpider;
import shordinger.wrapper.net.minecraft.entity.monster.EntityCreeper;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEnderman;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEndermite;
import shordinger.wrapper.net.minecraft.entity.monster.EntityGhast;
import shordinger.wrapper.net.minecraft.entity.monster.EntityGiantZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityGuardian;
import shordinger.wrapper.net.minecraft.entity.monster.EntityHusk;
import shordinger.wrapper.net.minecraft.entity.monster.EntityIronGolem;
import shordinger.wrapper.net.minecraft.entity.monster.EntityMagmaCube;
import shordinger.wrapper.net.minecraft.entity.monster.EntityPigZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySilverfish;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySkeleton;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySlime;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySnowman;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySpider;
import shordinger.wrapper.net.minecraft.entity.monster.EntityStray;
import shordinger.wrapper.net.minecraft.entity.monster.EntityWitch;
import shordinger.wrapper.net.minecraft.entity.monster.EntityWitherSkeleton;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombieVillager;
import shordinger.wrapper.net.minecraft.entity.passive.EntityBat;
import shordinger.wrapper.net.minecraft.entity.passive.EntityChicken;
import shordinger.wrapper.net.minecraft.entity.passive.EntityCow;
import shordinger.wrapper.net.minecraft.entity.passive.EntityDonkey;
import shordinger.wrapper.net.minecraft.entity.passive.EntityHorse;
import shordinger.wrapper.net.minecraft.entity.passive.EntityMooshroom;
import shordinger.wrapper.net.minecraft.entity.passive.EntityMule;
import shordinger.wrapper.net.minecraft.entity.passive.EntityOcelot;
import shordinger.wrapper.net.minecraft.entity.passive.EntityParrot;
import shordinger.wrapper.net.minecraft.entity.passive.EntityPig;
import shordinger.wrapper.net.minecraft.entity.passive.EntityRabbit;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySheep;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySkeletonHorse;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySquid;
import shordinger.wrapper.net.minecraft.entity.passive.EntityVillager;
import shordinger.wrapper.net.minecraft.entity.passive.EntityWolf;
import shordinger.wrapper.net.minecraft.entity.passive.EntityZombieHorse;

public class EntitySpawnPlacementRegistry {

    private static final Map<Class<?>, EntityLiving.SpawnPlacementType> ENTITY_PLACEMENTS = Maps.<Class<?>, EntityLiving.SpawnPlacementType>newHashMap();

    public static EntityLiving.SpawnPlacementType getPlacementForEntity(Class<?> entityClass) {
        return ENTITY_PLACEMENTS.getOrDefault(entityClass, EntityLiving.SpawnPlacementType.ON_GROUND);
    }

    public static void setPlacementType(Class<? extends Entity> entityClass,
                                        EntityLiving.SpawnPlacementType placementType) {
        ENTITY_PLACEMENTS.putIfAbsent(entityClass, placementType);
    }

    static {
        ENTITY_PLACEMENTS.put(EntityBat.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityChicken.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityCow.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityHorse.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySkeletonHorse.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityZombieHorse.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityDonkey.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityMule.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityMooshroom.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityOcelot.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityPig.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityRabbit.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityParrot.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySheep.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySnowman.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySquid.class, EntityLiving.SpawnPlacementType.IN_WATER);
        ENTITY_PLACEMENTS.put(EntityIronGolem.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityWolf.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityVillager.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityDragon.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityWither.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityBlaze.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityCaveSpider.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityCreeper.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityEnderman.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityEndermite.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityGhast.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityGiantZombie.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityGuardian.class, EntityLiving.SpawnPlacementType.IN_WATER);
        ENTITY_PLACEMENTS.put(EntityMagmaCube.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityPigZombie.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySilverfish.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySkeleton.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityWitherSkeleton.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityStray.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySlime.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySpider.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityWitch.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityZombie.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityZombieVillager.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityHusk.class, EntityLiving.SpawnPlacementType.ON_GROUND);
    }
}
