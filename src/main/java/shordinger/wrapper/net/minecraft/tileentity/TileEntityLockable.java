package shordinger.wrapper.net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.world.ILockableContainer;
import shordinger.wrapper.net.minecraft.world.LockCode;

public abstract class TileEntityLockable extends TileEntity implements ILockableContainer {

    private LockCode code = LockCode.EMPTY_CODE;

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.code = LockCode.fromNBT(compound);
    }

    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (this.code != null) {
            this.code.toNBT(compound);
        }

        return compound;
    }

    public boolean isLocked() {
        return this.code != null && !this.code.isEmpty();
    }

    public LockCode getLockCode() {
        return this.code;
    }

    public void setLockCode(LockCode code) {
        this.code = code;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public ITextComponent getDisplayName() {
        return (ITextComponent) (this.hasCustomName() ? new TextComponentString(this.getName())
            : new TextComponentTranslation(this.getName(), new Object[0]));
    }

    private net.minecraftforge.items.IItemHandler itemHandler;

    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    @javax.annotation.Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability,
                               @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) (itemHandler == null ? (itemHandler = createUnSidedHandler()) : itemHandler);
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability,
                                 @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            || super.hasCapability(capability, facing);
    }
}
