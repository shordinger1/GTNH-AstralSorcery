package shordinger.wrapper.net.minecraft.entity.item;

import java.util.Random;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.inventory.InventoryHelper;
import shordinger.wrapper.net.minecraft.inventory.ItemStackHelper;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.datafix.FixTypes;
import shordinger.wrapper.net.minecraft.util.datafix.walkers.ItemStackDataLists;
import shordinger.wrapper.net.minecraft.world.ILockableContainer;
import shordinger.wrapper.net.minecraft.world.LockCode;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;
import shordinger.wrapper.net.minecraft.world.storage.loot.ILootContainer;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootContext;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTable;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer, ILootContainer {

    private NonNullList<ItemStack> minecartContainerItems = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
    /**
     * When set to true, the minecart will drop all items when setDead() is called. When false (such as when travelling
     * dimensions) it preserves its contents.
     */
    public boolean dropContentsWhenDead = true;
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public EntityMinecartContainer(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartContainer(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public void killMinecart(DamageSource source) {
        super.killMinecart(source);

        if (this.world.getGameRules()
            .getBoolean("doEntityDrops")) {
            InventoryHelper.dropInventoryItems(this.world, this, this);
        }
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.minecartContainerItems) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index) {
        this.addLoot((EntityPlayer) null);
        return this.minecartContainerItems.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count) {
        this.addLoot((EntityPlayer) null);
        return ItemStackHelper.getAndSplit(this.minecartContainerItems, index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index) {
        this.addLoot((EntityPlayer) null);
        ItemStack itemstack = this.minecartContainerItems.get(index);

        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.minecartContainerItems.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.addLoot((EntityPlayer) null);
        this.minecartContainerItems.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.isDead) {
            return false;
        } else {
            return player.getDistanceSq(this) <= 64.0D;
        }
    }

    public void openInventory(EntityPlayer player) {
    }

    public void closeInventory(EntityPlayer player) {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit() {
        return 64;
    }

    @Nullable
    public Entity changeDimension(int dimensionIn, net.minecraftforge.common.util.ITeleporter teleporter) {
        this.dropContentsWhenDead = false;
        return super.changeDimension(dimensionIn, teleporter);
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead() {
        if (this.dropContentsWhenDead) {
            InventoryHelper.dropInventoryItems(this.world, this, this);
        }

        super.setDead();
    }

    /**
     * Sets whether this entity should drop its items when setDead() is called. This applies to container minecarts.
     */
    public void setDropItemsWhenDead(boolean dropWhenDead) {
        this.dropContentsWhenDead = dropWhenDead;
    }

    public static void addDataFixers(DataFixer p_190574_0_, Class<?> p_190574_1_) {
        EntityMinecart.registerFixesMinecart(p_190574_0_, p_190574_1_);
        p_190574_0_.registerWalker(FixTypes.ENTITY, new ItemStackDataLists(p_190574_1_, new String[]{"Items"}));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (this.lootTable != null) {
            compound.setString("LootTable", this.lootTable.toString());

            if (this.lootTableSeed != 0L) {
                compound.setLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ItemStackHelper.saveAllItems(compound, this.minecartContainerItems);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.minecartContainerItems = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (compound.hasKey("LootTable", 8)) {
            this.lootTable = new ResourceLocation(compound.getString("LootTable"));
            this.lootTableSeed = compound.getLong("LootTableSeed");
        } else {
            ItemStackHelper.loadAllItems(compound, this.minecartContainerItems);
        }
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (super.processInitialInteract(player, hand)) return true;
        if (!this.world.isRemote) {
            player.displayGUIChest(this);
        }

        return true;
    }

    protected void applyDrag() {
        float f = 0.98F;

        if (this.lootTable == null) {
            int i = 15 - Container.calcRedstoneFromInventory(this);
            f += (float) i * 0.001F;
        }

        this.motionX *= (double) f;
        this.motionY *= 0.0D;
        this.motionZ *= (double) f;
    }

    public int getField(int id) {
        return 0;
    }

    public void setField(int id, int value) {
    }

    public int getFieldCount() {
        return 0;
    }

    public boolean isLocked() {
        return false;
    }

    public void setLockCode(LockCode code) {
    }

    public LockCode getLockCode() {
        return LockCode.EMPTY_CODE;
    }

    /**
     * Adds loot to the minecart's contents.
     */
    public void addLoot(@Nullable EntityPlayer player) {
        if (this.lootTable != null) {
            LootTable loottable = this.world.getLootTableManager()
                .getLootTableFromLocation(this.lootTable);
            this.lootTable = null;
            Random random;

            if (this.lootTableSeed == 0L) {
                random = new Random();
            } else {
                random = new Random(this.lootTableSeed);
            }

            LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.world)
                .withLootedEntity(this); // Forge: add looted entity to LootContext

            if (player != null) {
                lootcontext$builder.withLuck(player.getLuck())
                    .withPlayer(player); // Forge: add player to LootContext
            }

            loottable.fillInventory(this, random, lootcontext$builder.build());
        }
    }

    public net.minecraftforge.items.IItemHandler itemHandler = new net.minecraftforge.items.wrapper.InvWrapper(this);

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability,
                               @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) itemHandler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability,
                                 @Nullable net.minecraft.util.EnumFacing facing) {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            || super.hasCapability(capability, facing);
    }

    public void clear() {
        this.addLoot((EntityPlayer) null);
        this.minecartContainerItems.clear();
    }

    public void setLootTable(ResourceLocation lootTableIn, long lootTableSeedIn) {
        this.lootTable = lootTableIn;
        this.lootTableSeed = lootTableSeedIn;
    }

    public ResourceLocation getLootTable() {
        return this.lootTable;
    }
}
