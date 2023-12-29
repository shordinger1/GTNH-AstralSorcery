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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.util.ASDataSerializers;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.LootTableUtil;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.effect.ShootingStarExplosion;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.EntityData.DataParameter;
import shordinger.astralsorcery.migration.EntityData.EntityDataManager;
import shordinger.astralsorcery.migration.IBlockState;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the BeeBetterAtBees Mod
 * Class: EntityShootingStar
 * Created by HellFirePvP
 * Date: 13.10.2018 / 12:54
 */
public class EntityShootingStar extends EntityThrowable implements EntityTechnicalAmbient {

    public EntityDataManager dataManager;
    private static final DataParameter<Vector3> SHOOT_CONSTANT = EntityDataManager
        .createKey(EntityShootingStar.class, ASDataSerializers.VECTOR);
    private static final DataParameter<Long> EFFECT_SEED = EntityDataManager
        .createKey(EntityShootingStar.class, ASDataSerializers.LONG);
    private static final DataParameter<Long> LAST_UPDATE = EntityDataManager
        .createKey(EntityShootingStar.class, ASDataSerializers.LONG);

    // Not saved or synced value to deny 'capturing' one.
    private boolean removalPending = true;

    public EntityShootingStar(World worldIn) {
        super(worldIn);
    }

    public EntityShootingStar(World worldIn, double x, double y, double z, Vector3 shot) {
        super(worldIn, x, y, z);
        this.setSize(0.1F, 0.1F);
        this.removalPending = false;
        this.dataManager.set(SHOOT_CONSTANT, shot);
        this.dataManager.set(EFFECT_SEED, rand.nextLong());
        this.dataManager.set(LAST_UPDATE, worldIn.getTotalWorldTime());
        correctMovement();
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(SHOOT_CONSTANT, new Vector3());
        this.dataManager.register(EFFECT_SEED, 0L);
        this.dataManager.register(LAST_UPDATE, 0L);
    }

    private void correctMovement() {
        Vector3 shot = this.dataManager.get(SHOOT_CONSTANT);
        this.motionX = shot.getX();
        this.motionZ = shot.getZ();
        if (this.posY >= 500) {
            this.motionY = -0.09;
        } else {
            this.motionY = -0.7F * (1F - (((float) this.posY) / 1000F));
        }
    }

    public long getEffectSeed() {
        return this.dataManager.get(EFFECT_SEED);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        long lastTrackedTick = this.dataManager.get(LAST_UPDATE);

        if (!worldObj.isRemote) {
            if (removalPending || !ConstellationSkyHandler.getInstance()
                .isNight(worldObj) || worldObj.getTotalWorldTime() - lastTrackedTick >= 20) {
                setDead();
                return;
            }

            this.dataManager.set(LAST_UPDATE, worldObj.getTotalWorldTime());

            if (isInWater() || isInLava()) {
                RayTraceResult rtr = new RayTraceResult(new BlockPos(0, 0, 0), EnumFacing.UP, this.getPosition());
                if (!ForgeEventFactory.onProjectileImpact(this, rtr)) {
                    this.onImpact(rtr);
                }
            }
        }

        correctMovement();

        if (worldObj.isRemote) {
            if (!ConstellationSkyHandler.getInstance()
                .isNight(worldObj) || worldObj.getTotalWorldTime() - lastTrackedTick >= 20) {
                setDead();
                return;
            }

            spawnEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnEffects() {
        Vector3 shot = this.dataManager.get(SHOOT_CONSTANT);
        float positionDist = 96F;

        EntityComplexFX.RenderOffsetController renderCtrl = (fx, currentRenderPos, currentMotion, pTicks) -> {
            EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
            if (pl == null) {
                return currentRenderPos;
            }
            EntityFXFacingParticle pt = (EntityFXFacingParticle) fx;
            Vector3 v = pt.getPosition()
                .clone()
                .subtract(Vector3.atEntityCenter(pl));
            if (v.length() <= positionDist) {
                return currentRenderPos;
            }
            return Vector3.atEntityCenter(pl)
                .add(
                    v.normalize()
                        .multiply(positionDist));
        };
        EntityComplexFX.ScaleFunction scaleFct = (fx, pos, pTicks, scaleIn) -> {
            EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
            if (pl == null) {
                return scaleIn;
            }
            scaleIn = new EntityComplexFX.ScaleFunction.Shrink<>().getScale((EntityComplexFX) fx, pos, pTicks, scaleIn);
            EntityFXFacingParticle pt = (EntityFXFacingParticle) fx;
            Vector3 v = pt.getPosition()
                .clone()
                .subtract(Vector3.atEntityCenter(pl));
            float mul = v.length() <= positionDist ? 1 : (float) (positionDist / (v.length()));
            return (scaleIn * 0.25F) + ((mul * scaleIn) - (scaleIn * 0.25F));
        };

        for (int i = 0; i < 4; i++) {
            if (rand.nextFloat() > 0.75F) continue;
            Vector3 dir = shot.clone()
                .multiply(rand.nextFloat() * -0.6F);
            dir.setX(dir.getX() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));
            dir.setZ(dir.getZ() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));
            // dir.rotate(Math.toRadians((30 + rand.nextInt(15)) * (rand.nextBoolean() ? 1 : -1)), dir.perpendicular());
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
            p.setColor(Color.WHITE)
                .setDistanceRemovable(false)
                .scale(1.2F + rand.nextFloat() * 0.5F)
                .motion(dir.getX(), dir.getY(), dir.getZ())
                .setAlphaMultiplier(0.85F)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .setMaxAge(90 + rand.nextInt(40));
            // Position within view distance
            p.setRenderOffsetController(renderCtrl);
            // Make smaller if further away, not too linearly though.
            p.setScaleFunction(scaleFct);
        }

        float scale = 4F + rand.nextFloat() * 3F;
        int age = 5 + rand.nextInt(2);

        Random seeded = new Random(getEffectSeed());
        EntityFXFacingParticle star = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        star.setColor(Color.getHSBColor(seeded.nextFloat() * 360F, 1F, 1F))
            .setDistanceRemovable(false)
            .scale(scale)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
            .setMaxAge(age);
        star.setRenderOffsetController(renderCtrl);
        star.setScaleFunction(scaleFct);
        EntityFXFacingParticle st2 = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        st2.setColor(Color.WHITE)
            .setDistanceRemovable(false)
            .scale(scale * 0.6F)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
            .setMaxAge(Math.round(age * 1.5F));
        st2.setRenderOffsetController(renderCtrl);
        st2.setScaleFunction(scaleFct);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (removalPending || result.typeOfHit == RayTraceResult.Type.MISS) {
            return;
        }

        setDead();

        BlockPos hit = result.getBlockPos();
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            hit = result.entityHit.getPosition();
        }
        if (!worldObj.isRemote && MiscUtils.isChunkLoaded(worldObj, hit)) {

            IBlockState state = worldObj.getBlockState(hit);
            boolean eligableForExplosion = true;
            if (MiscUtils.isFluidBlock(state)) {
                Fluid f = MiscUtils.tryGetFuild(state);
                if (f != null) {
                    if (f.getTemperature(worldObj, hit) <= 300) { // About room temp; incl. water
                        eligableForExplosion = false;
                    }
                }
            }

            Vector3 v = Vector3.atEntityCenter(this);
            ShootingStarExplosion.play(worldObj, v, !eligableForExplosion, getEffectSeed());

            EntityItem generated = new EntityItem(
                worldObj,
                v.getX(),
                v.getY(),
                v.getZ(),
                new ItemStack(ItemsAS.fragmentCapsule));
            Vector3 m = new Vector3();
            MiscUtils.applyRandomOffset(m, rand, 0.25F);
            generated.motionX = m.getX();
            generated.motionY = Math.abs(m.getY());
            generated.motionZ = m.getZ();
            generated.setPickupDelay(20);
            worldObj.spawnEntity(generated);

            LootTable table = worldObj.getLootTableManager()
                .getLootTableFromLocation(LootTableUtil.LOOT_TABLE_SHOOTING_STAR);
            if (table != null && worldObj instanceof WorldServer) {
                LootContext context = new LootContext.Builder((WorldServer) worldObj).build();
                List<ItemStack> stacks = table.generateLootForPools(rand, context);
                for (ItemStack stack : stacks) {
                    ItemUtils.dropItemNaturally(worldObj, v.getX(), v.getY(), v.getZ(), stack);
                }
            }
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }
}
