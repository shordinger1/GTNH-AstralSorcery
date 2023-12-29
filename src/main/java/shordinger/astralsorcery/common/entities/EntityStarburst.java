/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.util.EntityUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.effect.CelestialStrike;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityStarburst
 * Created by HellFirePvP
 * Date: 12.03.2017 / 10:46
 */
public class EntityStarburst extends EntityThrowable {

    private static final AxisAlignedBB searchBox = new AxisAlignedBB(-1, -1, -1, 1, 1, 1).grow(17);
    private static final double descendingDst = 17.0D;

    private int targetId = -1;

    public EntityStarburst(World worldIn) {
        super(worldIn);
    }

    public EntityStarburst(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityStarburst(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        shoot(throwerIn, throwerIn.rotationPitch, throwerIn.rotationYaw, 0.0F, 0.7F, 1.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            playEffects();
        } else {
            if (targetId == -1) {
                AxisAlignedBB box = searchBox.offset(posX, posY, posZ);
                List entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box, EntitySelectors.IS_ALIVE);
                if (getThrower() != null) {
                    entities.remove(getThrower());
                }
                entities.removeIf(e -> !MiscUtils.canPlayerAttackServer(getThrower(), e));

                EntityLivingBase closest = EntityUtils
                    .selectClosest(entities, entityLivingBase -> entityLivingBase.getDistanceSq(this));
                if (closest != null) {
                    targetId = closest.getEntityId();
                }
            }
            if (targetId != -1) {
                Entity e = worldObj.getEntityByID(targetId);
                if (e == null || e.isDead || !(e instanceof EntityLivingBase entity)) {
                    targetId = -1;
                } else {

                    Vector3 thisPos = Vector3.atEntityCorner(this);
                    Vector3 targetEntity = Vector3.atEntityCorner(entity);
                    Vector3 dirMotion = targetEntity.clone()
                        .subtract(thisPos);
                    Vector3 currentMotion = new Vector3(this.motionX, this.motionY, this.motionZ);
                    double dst = thisPos.distance(targetEntity);
                    if (dst < descendingDst) {
                        double originalPart = dst / descendingDst;
                        double length = currentMotion.length();
                        currentMotion = dirMotion.multiply(1 - originalPart)
                            .add(
                                currentMotion.clone()
                                    .multiply(originalPart));
                        currentMotion.normalize()
                            .multiply(length);
                    }

                    this.motionX = currentMotion.getX();
                    this.motionY = currentMotion.getY();
                    this.motionZ = currentMotion.getZ();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        EntityFXFacingParticle particle;
        for (int i = 0; i < 2; i++) {
            particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle
                .motion(
                    rand.nextFloat() * 0.03F - rand.nextFloat() * 0.06F,
                    rand.nextFloat() * 0.03F - rand.nextFloat() * 0.06F,
                    rand.nextFloat() * 0.03F - rand.nextFloat() * 0.06F)
                .scale(0.3F);
            switch (rand.nextInt(4)) {
                case 0 -> particle.setColor(Color.WHITE);
                case 1 -> particle.setColor(new Color(0x69B5FF));
                case 2 -> particle.setColor(new Color(0x0078FF));
                default -> {
                }
            }
        }
        if (ticksExisted % 12 == 0) {
            for (Vector3 pos : MiscUtils.getCirclePositions(
                Vector3.atEntityCenter(this),
                new Vector3(motionX, motionY, motionZ),
                1F,
                25 + rand.nextInt(14))) {
                particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ())
                    .gravity(0.004);
                particle.scale(0.4F)
                    .setAlphaMultiplier(0.5F);
                particle.motion(
                    rand.nextFloat() * 0.02F - rand.nextFloat() * 0.04F,
                    rand.nextFloat() * 0.02F - rand.nextFloat() * 0.04F,
                    rand.nextFloat() * 0.02F - rand.nextFloat() * 0.04F);
                switch (rand.nextInt(3)) {
                    case 0 -> particle.setColor(Color.WHITE);
                    case 1 -> particle.setColor(new Color(0x61A2FF));
                    case 2 -> particle.setColor(new Color(0x3A4ABD));
                    default -> {
                    }
                }
            }
        }
        particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
        particle.scale(0.6F);
        switch (rand.nextInt(4)) {
            case 0 -> particle.setColor(Color.WHITE);
            case 1 -> particle.setColor(new Color(0x69B5FF));
            case 2 -> particle.setColor(new Color(0x0078FF));
            default -> {
            }
        }
        particle = EffectHelper.genericFlareParticle(posX + motionX / 2F, posY + motionY / 2F, posZ + motionZ / 2F);
        particle.scale(0.6F);
        switch (rand.nextInt(4)) {
            case 0 -> particle.setColor(Color.WHITE);
            case 1 -> particle.setColor(new Color(0x69B5FF));
            case 2 -> particle.setColor(new Color(0x0078FF));
            default -> {
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
                if (result.entityHit.equals(getThrower())) {
                    return;
                }
                CelestialStrike.play(
                    getThrower(),
                    world,
                    Vector3.atEntityCenter(result.entityHit),
                    Vector3.atEntityCenter(result.entityHit));
            }
            setDead();
        }
    }

}
