/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import com.google.common.base.Predicate;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityList;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTable;
import shordinger.wrapper.net.minecraftforge.event.ForgeEventFactory;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityUtils
 * Created by HellFirePvP
 * Date: 14.09.2016 / 20:10
 */
public class EntityUtils {

    private static final Method getLootTableMethod;

    public static boolean canEntitySpawnHere(World world, BlockPos at, ResourceLocation entityKey, boolean respectConditions, @Nullable Function<Entity, Void> preCheckEntity) {
        Entity entity = EntityList.createEntityByIDFromName(entityKey, world);
        if(entity == null) {
            return false;
        }
        entity.setLocationAndAngles(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
        if (preCheckEntity != null) {
            preCheckEntity.apply(entity);
        }
        if(respectConditions) {
            if(entity instanceof EntityLiving) {
                Event.Result canSpawn = ForgeEventFactory.canEntitySpawn((EntityLiving) entity, world, at.getX() + 0.5F, at.getY() + 0.5F, at.getZ() + 0.5F, null);
                if (canSpawn != Event.Result.ALLOW && (canSpawn != Event.Result.DEFAULT || (!((EntityLiving) entity).getCanSpawnHere() || !((EntityLiving) entity).isNotColliding()))) {
                    return false;
                }
            }
        }
        return doesEntityHaveSpace(world, entity);
    }

    public static boolean doesEntityHaveSpace(World world, Entity entity) {
        return !world.containsAnyLiquid(entity.getEntityBoundingBox())
                && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()
                && world.checkNoEntityCollision(entity.getEntityBoundingBox(), entity);
    }

    public static void applyVortexMotion(Function<Void, Vector3> getPositionFunction, Function<Vector3, Object> addMotionFunction, Vector3 to, double vortexRange, double multiplier) {
        Vector3 pos = getPositionFunction.apply(null);
        double diffX = (to.getX() - pos.getX()) / vortexRange;
        double diffY = (to.getY() - pos.getY()) / vortexRange;
        double diffZ = (to.getZ() - pos.getZ()) / vortexRange;
        double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (1.0D - dist > 0.0D) {
            double dstFactorSq = (1.0D - dist) * (1.0D - dist);
            Vector3 toAdd = new Vector3();
            toAdd.setX(diffX / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setY(diffY / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setZ(diffZ / dist * dstFactorSq * 0.15D * multiplier);
            addMotionFunction.apply(toAdd);
        }
    }

    public static Predicate<? super Entity> selectEntities(Class<? extends Entity>... entities) {
        return (Predicate<Entity>) entity -> {
            if(entity == null || entity.isDead) return false;
            Class<? extends Entity> clazz = entity.getClass();
            for (Class<? extends Entity> test : entities) {
                if(test.isAssignableFrom(clazz)) return true;
            }
            return false;
        };
    }

    public static Predicate<? super Entity> selectItemClassInstaceof(Class<?> itemClass) {
        return (Predicate<Entity>) entity -> {
            if(entity == null || entity.isDead) return false;
            if(!(entity instanceof EntityItem)) return false;
            ItemStack i = ((EntityItem) entity).getItem();
            if(i.isEmpty()) return false;
            return itemClass.isAssignableFrom(i.getItem().getClass());
        };
    }

    public static Predicate<? super Entity> selectItem(Item item) {
        return (Predicate<Entity>) entity -> {
            if(entity == null || entity.isDead) return false;
            if(!(entity instanceof EntityItem)) return false;
            ItemStack i = ((EntityItem) entity).getItem();
            if(i.isEmpty()) return false;
            return i.getItem().equals(item);
        };
    }

    public static Predicate<? super Entity> selectItemStack(Function<ItemStack, Boolean> acceptor) {
        return entity -> {
            if(entity == null || entity.isDead) return false;
            if(!(entity instanceof EntityItem)) return false;
            ItemStack i = ((EntityItem) entity).getItem();
            if(i.isEmpty()) return false;
            return acceptor.apply(i);
        };
    }

    @Nullable
    public static <T> T selectClosest(Collection<T> elements, Function<T, Double> dstFunc) {
        if(elements.isEmpty()) return null;

        double dstClosest = Double.MAX_VALUE;
        T closestElement = null;
        for (T element : elements) {
            double dst = dstFunc.apply(element);
            if(dst < dstClosest) {
                closestElement = element;
                dstClosest = dst;
            }
        }
        return closestElement;
    }

    @Nullable
    public static LootTable getLootTable(EntityLiving entity) {
        ResourceLocation table = entity.deathLootTable;
        if(getLootTableMethod == null) return null;
        try {
            if (table == null) table = (ResourceLocation) getLootTableMethod.invoke(entity);
            return entity.world.getLootTableManager().getLootTableFromLocation(table);
        } catch (Exception e) {
            return null;
        }
    }

    static {
        Method m = null;
        try {
            m = ReflectionHelper.findMethod(EntityLiving.class, "getLootTable", "func_184647_J");
        } catch (Exception exc) {}
        getLootTableMethod = m;
    }

}
