/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.EntityData.EntityDataManager;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityNocturnalSpark
 * Created by HellFirePvP
 * Date: 03.07.2017 / 13:32
 */
public class EntityNocturnalSpark extends EntityThrowable implements EntityTechnicalAmbient {

    public EntityDataManager dataManager;
    private static final AxisAlignedBB NO_DUPE_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(15);

    private static final DataParameter<Boolean> SPAWNING = EntityDataManager
        .createKey(EntityNocturnalSpark.class, DataSerializers.BOOLEAN);
    private int ticksSpawning = 0;

    public EntityNocturnalSpark(World worldIn) {
        super(worldIn);
    }

    public EntityNocturnalSpark(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityNocturnalSpark(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        shoot(throwerIn, throwerIn.rotationPitch, throwerIn.rotationYaw, 0.0F, 0.7F, 0.9F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(SPAWNING, false);
    }

    public void setSpawning() {
        this.dataManager.set(SPAWNING, true);
    }

    public boolean isSpawning() {
        return this.dataManager.get(SPAWNING);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (isDead) return; // Uhh.......... mojang pls

        if (worldObj.isRemote) {
            playEffects();
        } else {
            if (isSpawning()) {
                ticksSpawning++;
                spawnCycle();
                if (ticksSpawning > 200) {
                    setDead();
                }
            }
        }
    }

    private void spawnCycle() {
        List sparks = worldObj.getEntitiesWithinAABB(EntityNocturnalSpark.class, NO_DUPE_BOX.offset(getPosition()));
        for (EntityNocturnalSpark spark : sparks) {
            if (this.equals(spark)) continue;
            if (spark.isDead || !spark.isSpawning()) continue;
            spark.setDead();
        }
        if (rand.nextInt(12) == 0 && worldObj instanceof WorldServer) {
            try {
                BlockPos pos = getPosition().up();
                pos.add(rand.nextInt(2) - rand.nextInt(2), 0, rand.nextInt(2) - rand.nextInt(2));
                List list = ((WorldServer) world).getChunkProvider()
                    .getPossibleCreatures(EnumCreatureType.MONSTER, pos);
                list = net.minecraftforge.event.ForgeEventFactory
                    .getPotentialSpawns((WorldServer) world, EnumCreatureType.MONSTER, pos, list);
                if (list == null || list.isEmpty()) return;
                Biome.SpawnListEntry entry = list.get(rand.nextInt(list.size())); // Intentionally non-weighted.
                if (worldObj.getGameRules()
                    .getBoolean("mobGriefing") && EntityCreeper.class.isAssignableFrom(entry.entityClass))
                    return; // No.

                Block down = worldObj.getBlockState(getPosition())
                    .getBlock();
                boolean canAtAll = down != Blocks.BARRIER && down != Blocks.BEDROCK;
                if (canAtAll
                    && WorldEntitySpawner.isValidEmptySpawnBlock(WorldHelper.getBlockState(world, getPosition()))
                    && WorldEntitySpawner.isValidEmptySpawnBlock(WorldHelper.getBlockState(world, pos))) {
                    EntityLiving entity = entry.newInstance(world);
                    entity.setPositionAndRotation(
                        pos.getX() + 0.5,
                        pos.getY(),
                        pos.getZ() + 0.5,
                        rand.nextFloat() * 360F,
                        0F);
                    if (!net.minecraftforge.event.ForgeEventFactory
                        .doSpecialSpawn(entity, world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F)) {
                        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
                    }
                    if (entity.isNotColliding()) {
                        worldObj.spawnEntity(entity);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        if (isSpawning()) {
            for (int i = 0; i < 15; i++) {
                Vector3 thisPos = Vector3.atEntityCorner(this)
                    .addY(1);
                MiscUtils.applyRandomOffset(thisPos, rand, 2 + rand.nextInt(4));
                EntityFXFacingParticle particle = EffectHelper
                    .genericFlareParticle(thisPos.getX(), thisPos.getY(), thisPos.getZ())
                    .scale(4F)
                    .setColor(Color.BLACK)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                    .gravity(0.004)
                    .setAlphaMultiplier(0.7F);
                if (rand.nextInt(5) == 0) {
                    randomizeColor(particle);
                }
                if (rand.nextInt(3) == 0) {
                    Vector3 target = Vector3.atEntityCorner(this);
                    MiscUtils.applyRandomOffset(target, rand, 4);
                    AstralSorcery.proxy.fireLightning(worldObj, Vector3.atEntityCorner(this), target, Color.BLACK);
                }
            }
        } else {
            EntityFXFacingParticle particle;
            for (int i = 0; i < 6; i++) {
                particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
                particle
                    .motion(
                        0.04F - rand.nextFloat() * 0.08F,
                        0.04F - rand.nextFloat() * 0.08F,
                        0.04F - rand.nextFloat() * 0.08F)
                    .scale(0.25F);
                randomizeColor(particle);
            }
            particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.scale(0.6F);
            randomizeColor(particle);
            particle = EffectHelper.genericFlareParticle(posX + motionX / 2F, posY + motionY / 2F, posZ + motionZ / 2F);
            particle.scale(0.6F);
            randomizeColor(particle);
        }
    }

    @SideOnly(Side.CLIENT)
    private void randomizeColor(EntityFXFacingParticle particle) {
        switch (rand.nextInt(3)) {
            case 0 -> particle.setColor(Color.BLACK);
            case 1 -> particle.setColor(new Color(0x4E016D));
            case 2 -> particle.setColor(new Color(0x0C1576));
            default -> {
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (RayTraceResult.Type.ENTITY.equals(result.typeOfHit)) {
            return;
        }
        BlockPos hit = result.hitVec;
        setSpawning();
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.posX = hit.x;
        this.posY = hit.y;
        this.posZ = hit.z;
    }

}
