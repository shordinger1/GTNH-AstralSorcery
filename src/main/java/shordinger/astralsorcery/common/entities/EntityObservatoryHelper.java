/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;

import shordinger.astralsorcery.common.container.ContainerObservatory;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileObservatory;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityObservatoryHelper
 * Created by HellFirePvP
 * Date: 26.05.2018 / 14:37
 */
public class EntityObservatoryHelper extends Entity {

    private static DataParameter<BlockPos> FIXED = EntityDataManager
        .createKey(EntityObservatoryHelper.class, DataSerializers.BLOCK_POS);

    public EntityObservatoryHelper(World worldIn) {
        super(worldIn);
        setSize(0, 0);
        this.isImmuneToFire = true;
    }

    public EntityObservatoryHelper(World world, BlockPos fixedPos) {
        super(world);
        setSize(0, 0);
        this.dataManager.set(FIXED, fixedPos);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(FIXED, BlockPos.ORIGIN);
    }

    public BlockPos getFixedObservatoryPos() {
        return this.dataManager.get(FIXED);
    }

    @Nullable
    public TileObservatory tryGetObservatory() {
        return MiscUtils.getTileAt(this.world, getFixedObservatoryPos(), TileObservatory.class, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.noClip = true;

        TileObservatory to;
        if ((to = isOnTelescope()) == null) {
            if (!world.isRemote) {
                setDead();
            }
            return;
        }
        List<Entity> passengers = getPassengers();
        if (!to.isUsable()) {
            passengers.forEach(Entity::dismountRidingEntity);
            return;
        }
        Entity riding = Iterables.getFirst(passengers, null);
        if (riding != null && riding instanceof EntityPlayer) {
            applyObservatoryRotationsFrom(to, (EntityPlayer) riding);
        }
    }

    public void applyObservatoryRotationsFrom(TileObservatory to, EntityPlayer riding) {
        if (riding.openContainer != null && riding.openContainer instanceof ContainerObservatory) {
            // Adjust observatory pitch and jaw to player head
            this.rotationYaw = riding.rotationYawHead;
            this.prevRotationYaw = riding.prevRotationYawHead;
            this.rotationPitch = riding.rotationPitch;
            this.prevRotationPitch = riding.prevRotationPitch;
        } else {
            // Adjust observatory to player-body
            this.rotationYaw = riding.renderYawOffset;
            this.prevRotationYaw = riding.prevRenderYawOffset;
        }

        to.updatePitchYaw(this.rotationPitch, this.prevRotationPitch, this.rotationYaw, this.prevRotationYaw);
    }

    @Nullable
    private TileObservatory isOnTelescope() {
        BlockPos fixed = getFixedObservatoryPos();
        TileObservatory to = MiscUtils.getTileAt(this.world, fixed, TileObservatory.class, true);
        if (to == null) {
            return null;
        }
        UUID helper = to.getEntityHelperRef();
        if (helper == null || !helper.equals(this.entityUniqueID)) {
            return null;
        }
        return to;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        if (!super.canBeRidden(entityIn)) return false;
        TileObservatory to = isOnTelescope();
        return to != null && to.isUsable();
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean isOverWater() {
        return true;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {}

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {}

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(BlocksAS.blockObservatory);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}
}
