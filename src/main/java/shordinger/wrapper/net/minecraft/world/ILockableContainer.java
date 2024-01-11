package shordinger.wrapper.net.minecraft.world;

import shordinger.wrapper.net.minecraft.inventory.IInventory;

public interface ILockableContainer extends IInventory, IInteractionObject {

    boolean isLocked();

    void setLockCode(LockCode code);

    LockCode getLockCode();
}
