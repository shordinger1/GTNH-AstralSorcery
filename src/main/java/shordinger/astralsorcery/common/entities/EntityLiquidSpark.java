/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import com.google.common.base.Predicates;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.base.LiquidInteraction;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktLiquidInteractionBurst;
import shordinger.astralsorcery.common.tile.ILiquidStarlightPowered;
import shordinger.astralsorcery.common.tile.base.TileEntitySynchronized;
import shordinger.astralsorcery.common.util.ASDataSerializers;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureAtlasSprite;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityFlying;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.EntityFlyHelper;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EntitySelectors;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import shordinger.wrapper.net.minecraftforge.fluids.capability.IFluidHandler;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityLiquidSpark
 * Created by HellFirePvP
 * Date: 28.10.2017 / 14:39
 */
public class EntityLiquidSpark extends EntityFlying implements EntityTechnicalAmbient {

    private static final DataParameter<Integer> ENTITY_TARGET = EntityDataManager.createKey(EntityLiquidSpark.class, DataSerializers.VARINT);
    private static final DataParameter<FluidStack> FLUID_REPRESENTED = EntityDataManager.createKey(EntityLiquidSpark.class, ASDataSerializers.FLUID);

    private LiquidInteraction purpose;
    private TileEntity tileTarget;
    private BlockPos resolvableTilePos = null;

    public EntityLiquidSpark(World worldIn) {
        super(worldIn);
        setSize(0.4F, 0.4F);
        this.noClip = true;
        this.moveHelper = new EntityFlyHelper(this);
        this.purpose = null;
    }

    public EntityLiquidSpark(World world, BlockPos spawnPos, LiquidInteraction purposeOfLiving) {
        super(world);
        setSize(0.4F, 0.4F);
        setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        this.noClip = true;
        this.moveHelper = new EntityFlyHelper(this);
        this.purpose = purposeOfLiving;
    }

    public EntityLiquidSpark(World world, BlockPos spawnPos, TileEntity target) {
        super(world);
        setSize(0.4F, 0.4F);
        setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        this.noClip = true;
        this.moveHelper = new EntityFlyHelper(this);
        this.tileTarget = target;
    }

    public void setTarget(EntityLiquidSpark other) {
        this.dataManager.set(ENTITY_TARGET, other.getEntityId());
    }

    public void setFluidRepresented(FluidStack fs) {
        this.dataManager.set(FLUID_REPRESENTED, fs);
    }

    public FluidStack getRepresentitiveFluid() {
        return this.dataManager.get(FLUID_REPRESENTED);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(ENTITY_TARGET, -1);
        this.dataManager.register(FLUID_REPRESENTED, null);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.35);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(isDead) return;

        this.noClip = getEntityWorld().getBlockState(this.getPosition()).getBlock().equals(BlocksAS.blockChalice);

        if(this.resolvableTilePos != null) {
            this.tileTarget = MiscUtils.getTileAt(world, resolvableTilePos, TileEntity.class, true);
            this.resolvableTilePos = null;
        }

        if(!world.isRemote) {
            if(ticksExisted > 800) {
                setDead();
                return;
            }

            List<Entity> nearby = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(1),
                    Predicates.and(EntitySelectors.IS_ALIVE, EntitySelectors.NOT_SPECTATING));
            if (nearby.size() > 2) {
                setDead();
                return;
            }

            if(purpose != null) {
                int target = this.dataManager.get(ENTITY_TARGET);
                if(target == -1) {
                    setDead();
                    return;
                }
                Entity e = world.getEntityByID(target);
                if(e == null || e.isDead || !(e instanceof EntityLiquidSpark)) {
                    setDead();
                    return;
                }

                if(getDistance(e) < 0.7F) {
                    setDead();
                    e.setDead();
                    Vector3 at = Vector3.atEntityCenter(e)
                            .subtract(Vector3.atEntityCenter(this))
                            .divide(2)
                            .add(Vector3.atEntityCenter(this));
                    purpose.triggerInteraction(this.world, at);
                    PktLiquidInteractionBurst ev = new PktLiquidInteractionBurst(
                            this.purpose.getComponent1(),
                            this.purpose.getComponent2(),
                            at);
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(this.world, at.toBlockPos(), 32));
                } else {
                    this.moveHelper.setMoveTo(e.posX, e.posY, e.posZ, 2.4F);
                }
            } else if(tileTarget != null) {
                if(tileTarget.isInvalid() ||
                        MiscUtils.getTileAt(world, tileTarget.getPos(), tileTarget.getClass(), true) == null) {
                    setDead();
                    return;
                }
                Vector3 target = new Vector3(tileTarget.getPos()).add(0.5, 0.5, 0.5);

                if(getDistance(target.getX(), target.getY(), target.getZ()) < 1.1F) {
                    setDead();
                    FluidStack contained = getRepresentitiveFluid();
                    if (contained == null) {
                        return;
                    }

                    if(contained.getFluid() == BlocksAS.fluidLiquidStarlight && tileTarget instanceof ILiquidStarlightPowered) {
                        ((ILiquidStarlightPowered) tileTarget).acceptStarlight(contained.amount);
                    } else if(tileTarget.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                        IFluidHandler handler = tileTarget.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        if(handler != null) {
                            handler.fill(contained, true);
                        }
                    }
                    if(tileTarget instanceof TileEntitySynchronized) {
                        ((TileEntitySynchronized) tileTarget).markForUpdate();
                    } else {
                        tileTarget.markDirty();
                    }
                    Vector3 at = Vector3.atEntityCenter(this);

                    PktLiquidInteractionBurst ev = new PktLiquidInteractionBurst(
                            contained,
                            contained,
                            at);
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(this.world, at.toBlockPos(), 32));
                } else {
                    this.moveHelper.setMoveTo(target.getX(), target.getY(), target.getZ(), 2.4F);
                }
            } else {
                setDead();
            }
        } else {
            playAmbientParticles();
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if(compound.hasKey("tileTarget")) {
            this.resolvableTilePos = NBTHelper.readBlockPosFromNBT(compound.getCompoundTag("tileTarget"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if(this.tileTarget != null) {
            NBTHelper.setAsSubTag(compound, "tileTarget", tag -> NBTHelper.writeBlockPosToNBT(this.tileTarget.getPos(), tag));
        }
    }

    @SideOnly(Side.CLIENT)
    private void playAmbientParticles() {
        FluidStack stack = getRepresentitiveFluid();
        if(stack == null) return;
        TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(stack);

        Vector3 at = Vector3.atEntityCenter(this);
        EntityFXFloatingCube cube = RenderingUtils.spawnFloatingBlockCubeParticle(at, tas);
        cube.setTextureSubSizePercentage(1F / 16F).setMaxAge(20 + rand.nextInt(20));
        cube.setWorldLightCoord(Minecraft.getMinecraft().world, at.toBlockPos());
        cube.setColorHandler(cb -> new Color(stack.getFluid().getColor(stack)));
        cube.setScale(0.14F).tumble().setMotion(
                rand.nextFloat() * 0.02F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.02F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.02F * (rand.nextBoolean() ? 1 : -1));

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at);
        p.setColor(Color.WHITE).scale(0.3F + rand.nextFloat() * 0.1F).setMaxAge(20 + rand.nextInt(10));
    }

}
