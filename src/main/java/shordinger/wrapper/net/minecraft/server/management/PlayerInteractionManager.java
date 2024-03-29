package shordinger.wrapper.net.minecraft.server.management;

import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockChest;
import shordinger.wrapper.net.minecraft.block.BlockCommandBlock;
import shordinger.wrapper.net.minecraft.block.BlockStructure;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.inventory.IInventory;
import shordinger.wrapper.net.minecraft.item.ItemBlock;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.network.play.server.SPacketBlockChange;
import shordinger.wrapper.net.minecraft.network.play.server.SPacketPlayerListItem;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityChest;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.GameType;
import shordinger.wrapper.net.minecraft.world.ILockableContainer;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;

public class PlayerInteractionManager {

    /**
     * The world object that this object is connected to.
     */
    public World world;
    /**
     * The EntityPlayerMP object that this object is connected to.
     */
    public EntityPlayerMP player;
    private GameType gameType = GameType.NOT_SET;
    /**
     * True if the player is destroying a block
     */
    private boolean isDestroyingBlock;
    private int initialDamage;
    private BlockPos destroyPos = BlockPos.ORIGIN;
    private int curblockDamage;
    /**
     * Set to true when the "finished destroying block" packet is received but the block wasn't fully damaged yet. The
     * block will not be destroyed while this is false.
     */
    private boolean receivedFinishDiggingPacket;
    private BlockPos delayedDestroyPos = BlockPos.ORIGIN;
    private int initialBlockDamage;
    private int durabilityRemainingOnBlock = -1;

    public PlayerInteractionManager(World worldIn) {
        this.world = worldIn;
    }

    public void setGameType(GameType type) {
        this.gameType = type;
        type.configurePlayerCapabilities(this.player.capabilities);
        this.player.sendPlayerAbilities();
        this.player.mcServer.getPlayerList()
            .sendPacketToAllPlayers(
                new SPacketPlayerListItem(
                    SPacketPlayerListItem.Action.UPDATE_GAME_MODE,
                    new EntityPlayerMP[]{this.player}));
        this.world.updateAllPlayersSleepingFlag();
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public boolean survivalOrAdventure() {
        return this.gameType.isSurvivalOrAdventure();
    }

    /**
     * Get if we are in creative game mode.
     */
    public boolean isCreative() {
        return this.gameType.isCreative();
    }

    /**
     * if the gameType is currently NOT_SET then change it to par1
     */
    public void initializeGameType(GameType type) {
        if (this.gameType == GameType.NOT_SET) {
            this.gameType = type;
        }

        this.setGameType(this.gameType);
    }

    public void updateBlockRemoving() {
        ++this.curblockDamage;

        if (this.receivedFinishDiggingPacket) {
            int i = this.curblockDamage - this.initialBlockDamage;
            IBlockState iblockstate = this.world.getBlockState(this.delayedDestroyPos);

            if (iblockstate.getBlock()
                .isAir(iblockstate, world, delayedDestroyPos)) {
                this.receivedFinishDiggingPacket = false;
            } else {
                float f = iblockstate
                    .getPlayerRelativeBlockHardness(this.player, this.player.world, this.delayedDestroyPos)
                    * (float) (i + 1);
                int j = (int) (f * 10.0F);

                if (j != this.durabilityRemainingOnBlock) {
                    this.world.sendBlockBreakProgress(this.player.getEntityId(), this.delayedDestroyPos, j);
                    this.durabilityRemainingOnBlock = j;
                }

                if (f >= 1.0F) {
                    this.receivedFinishDiggingPacket = false;
                    this.tryHarvestBlock(this.delayedDestroyPos);
                }
            }
        } else if (this.isDestroyingBlock) {
            IBlockState iblockstate1 = this.world.getBlockState(this.destroyPos);

            if (iblockstate1.getBlock()
                .isAir(iblockstate1, world, destroyPos)) {
                this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
                this.durabilityRemainingOnBlock = -1;
                this.isDestroyingBlock = false;
            } else {
                int k = this.curblockDamage - this.initialDamage;
                float f1 = iblockstate1.getPlayerRelativeBlockHardness(this.player, this.player.world, this.destroyPos)
                    * (float) (k + 1); // Forge: Fix network break progress using wrong position
                int l = (int) (f1 * 10.0F);

                if (l != this.durabilityRemainingOnBlock) {
                    this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, l);
                    this.durabilityRemainingOnBlock = l;
                }
            }
        }
    }

    /**
     * If not creative, it calls sendBlockBreakProgress until the block is broken first. tryHarvestBlock can also be the
     * result of this call.
     */
    public void onBlockClicked(BlockPos pos, EnumFacing side) {
        double reachDist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
            .getAttributeValue();
        net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks
            .onLeftClickBlock(
                player,
                pos,
                side,
                net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, reachDist + 1));
        if (event.isCanceled()) {
            // Restore block and te data
            player.connection.sendPacket(new SPacketBlockChange(world, pos));
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            return;
        }

        if (this.isCreative()) {
            if (!this.world.extinguishFire((EntityPlayer) null, pos, side)) {
                this.tryHarvestBlock(pos);
            }
        } else {
            IBlockState iblockstate = this.world.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (this.gameType.hasLimitedInteractions()) {
                if (this.gameType == GameType.SPECTATOR) {
                    return;
                }

                if (!this.player.isAllowEdit()) {
                    ItemStack itemstack = this.player.getHeldItemMainhand();

                    if (itemstack.isEmpty()) {
                        return;
                    }

                    if (!itemstack.canDestroy(block)) {
                        return;
                    }
                }
            }

            this.initialDamage = this.curblockDamage;
            float f = 1.0F;

            if (!iblockstate.getBlock()
                .isAir(iblockstate, world, pos)) {
                if (event.getUseBlock() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                    block.onBlockClicked(this.world, pos, this.player);
                    this.world.extinguishFire((EntityPlayer) null, pos, side);
                } else {
                    // Restore block and te data
                    player.connection.sendPacket(new SPacketBlockChange(world, pos));
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                }
                f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, pos);
            }
            if (event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                if (f >= 1.0F) {
                    // Restore block and te data
                    player.connection.sendPacket(new SPacketBlockChange(world, pos));
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                }
                return;
            }

            if (!iblockstate.getBlock()
                .isAir(iblockstate, world, pos) && f >= 1.0F) {
                this.tryHarvestBlock(pos);
            } else {
                this.isDestroyingBlock = true;
                this.destroyPos = pos;
                int i = (int) (f * 10.0F);
                this.world.sendBlockBreakProgress(this.player.getEntityId(), pos, i);
                this.durabilityRemainingOnBlock = i;
            }
        }
    }

    public void blockRemoving(BlockPos pos) {
        if (pos.equals(this.destroyPos)) {
            int i = this.curblockDamage - this.initialDamage;
            IBlockState iblockstate = this.world.getBlockState(pos);

            if (!iblockstate.getBlock()
                .isAir(iblockstate, world, pos)) {
                float f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, pos)
                    * (float) (i + 1);

                if (f >= 0.7F) {
                    this.isDestroyingBlock = false;
                    this.world.sendBlockBreakProgress(this.player.getEntityId(), pos, -1);
                    this.tryHarvestBlock(pos);
                } else if (!this.receivedFinishDiggingPacket) {
                    this.isDestroyingBlock = false;
                    this.receivedFinishDiggingPacket = true;
                    this.delayedDestroyPos = pos;
                    this.initialBlockDamage = this.initialDamage;
                }
            }
        }
    }

    /**
     * Stops the block breaking process
     */
    public void cancelDestroyingBlock() {
        this.isDestroyingBlock = false;
        this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    private boolean removeBlock(BlockPos pos) {
        return removeBlock(pos, false);
    }

    private boolean removeBlock(BlockPos pos, boolean canHarvest) {
        IBlockState iblockstate = this.world.getBlockState(pos);
        boolean flag = iblockstate.getBlock()
            .removedByPlayer(iblockstate, world, pos, player, canHarvest);

        if (flag) {
            iblockstate.getBlock()
                .onBlockDestroyedByPlayer(this.world, pos, iblockstate);
        }

        return flag;
    }

    /**
     * Attempts to harvest a block
     */
    public boolean tryHarvestBlock(BlockPos pos) {
        int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, gameType, player, pos);
        if (exp == -1) {
            return false;
        } else {
            IBlockState iblockstate = this.world.getBlockState(pos);
            TileEntity tileentity = this.world.getTileEntity(pos);
            Block block = iblockstate.getBlock();

            if ((block instanceof BlockCommandBlock || block instanceof BlockStructure)
                && !this.player.canUseCommandBlock()) {
                this.world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
                return false;
            } else {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem()
                    .onBlockStartBreak(stack, pos, player)) return false;

                this.world.playEvent(this.player, 2001, pos, Block.getStateId(iblockstate));
                boolean flag1 = false;

                if (this.isCreative()) {
                    flag1 = this.removeBlock(pos);
                    this.player.connection.sendPacket(new SPacketBlockChange(this.world, pos));
                } else {
                    ItemStack itemstack1 = this.player.getHeldItemMainhand();
                    ItemStack itemstack2 = itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy();
                    boolean flag = iblockstate.getBlock()
                        .canHarvestBlock(world, pos, player);

                    if (!itemstack1.isEmpty()) {
                        itemstack1.onBlockDestroyed(this.world, iblockstate, pos, this.player);
                        if (itemstack1.isEmpty()) net.minecraftforge.event.ForgeEventFactory
                            .onPlayerDestroyItem(this.player, itemstack2, EnumHand.MAIN_HAND);
                    }

                    flag1 = this.removeBlock(pos, flag);
                    if (flag1 && flag) {
                        iblockstate.getBlock()
                            .harvestBlock(this.world, this.player, pos, iblockstate, tileentity, itemstack2);
                    }
                }

                // Drop experience
                if (!this.isCreative() && flag1 && exp > 0) {
                    iblockstate.getBlock()
                        .dropXpOnBlockBreak(world, pos, exp);
                }
                return flag1;
            }
        }
    }

    public EnumActionResult processRightClick(EntityPlayer player, World worldIn, ItemStack stack, EnumHand hand) {
        if (this.gameType == GameType.SPECTATOR) {
            return EnumActionResult.PASS;
        } else if (player.getCooldownTracker()
            .hasCooldown(stack.getItem())) {
            return EnumActionResult.PASS;
        } else {
            EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(player, hand);
            if (cancelResult != null) return cancelResult;
            int i = stack.getCount();
            int j = stack.getMetadata();
            ItemStack copyBeforeUse = stack.copy();
            ActionResult<ItemStack> actionresult = stack.useItemRightClick(worldIn, player, hand);
            ItemStack itemstack = actionresult.getResult();

            if (itemstack == stack && itemstack.getCount() == i
                && itemstack.getMaxItemUseDuration() <= 0
                && itemstack.getMetadata() == j) {
                return actionresult.getType();
            } else if (actionresult.getType() == EnumActionResult.FAIL && itemstack.getMaxItemUseDuration() > 0
                && !player.isHandActive()) {
                return actionresult.getType();
            } else {
                player.setHeldItem(hand, itemstack);

                if (this.isCreative()) {
                    itemstack.setCount(i);

                    if (itemstack.isItemStackDamageable()) {
                        itemstack.setItemDamage(j);
                    }
                }

                if (itemstack.isEmpty()) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }

                if (!player.isHandActive()) {
                    ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                }

                return actionresult.getType();
            }
        }
    }

    public EnumActionResult processRightClickBlock(EntityPlayer player, World worldIn, ItemStack stack, EnumHand hand,
                                                   BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (this.gameType == GameType.SPECTATOR) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof ILockableContainer) {
                Block block1 = worldIn.getBlockState(pos)
                    .getBlock();
                ILockableContainer ilockablecontainer = (ILockableContainer) tileentity;

                if (ilockablecontainer instanceof TileEntityChest && block1 instanceof BlockChest) {
                    ilockablecontainer = ((BlockChest) block1).getLockableContainer(worldIn, pos);
                }

                if (ilockablecontainer != null) {
                    player.displayGUIChest(ilockablecontainer);
                    return EnumActionResult.SUCCESS;
                }
            } else if (tileentity instanceof IInventory) {
                player.displayGUIChest((IInventory) tileentity);
                return EnumActionResult.SUCCESS;
            }

            return EnumActionResult.PASS;
        } else {
            double reachDist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
                .getAttributeValue();
            net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks
                .onRightClickBlock(
                    player,
                    hand,
                    pos,
                    facing,
                    net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, reachDist + 1));
            if (event.isCanceled()) return event.getCancellationResult();

            EnumActionResult result = EnumActionResult.PASS;
            if (event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                result = stack.onItemUseFirst(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                if (result != EnumActionResult.PASS) return result;
            }

            boolean bypass = player.getHeldItemMainhand()
                .doesSneakBypassUse(worldIn, pos, player)
                && player.getHeldItemOffhand()
                .doesSneakBypassUse(worldIn, pos, player);

            if (!player.isSneaking() || bypass
                || event.getUseBlock() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                if (event.getUseBlock() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                    if (iblockstate.getBlock()
                        .onBlockActivated(worldIn, pos, iblockstate, player, hand, facing, hitX, hitY, hitZ)) {
                        result = EnumActionResult.SUCCESS;
                    }
            }

            if (stack.isEmpty()) {
                return EnumActionResult.PASS;
            } else if (player.getCooldownTracker()
                .hasCooldown(stack.getItem())) {
                return EnumActionResult.PASS;
            } else {
                if (stack.getItem() instanceof ItemBlock && !player.canUseCommandBlock()) {
                    Block block = ((ItemBlock) stack.getItem()).getBlock();

                    if (block instanceof BlockCommandBlock || block instanceof BlockStructure) {
                        return EnumActionResult.FAIL;
                    }
                }

                if (this.isCreative()) {
                    int j = stack.getMetadata();
                    int i = stack.getCount();
                    if (result != EnumActionResult.SUCCESS
                        && event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY
                        || result == EnumActionResult.SUCCESS && event.getUseItem()
                        == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
                        EnumActionResult enumactionresult = stack
                            .onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                        stack.setItemDamage(j);
                        stack.setCount(i);
                        return enumactionresult;
                    } else return result;
                } else {
                    if (result != EnumActionResult.SUCCESS
                        && event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY
                        || result == EnumActionResult.SUCCESS && event.getUseItem()
                        == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
                        ItemStack copyBeforeUse = stack.copy();
                        result = stack.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                        if (stack.isEmpty()) net.minecraftforge.event.ForgeEventFactory
                            .onPlayerDestroyItem(player, copyBeforeUse, hand);
                    }
                    return result;
                }
            }
        }
    }

    /**
     * Sets the world instance.
     */
    public void setWorld(WorldServer serverWorld) {
        this.world = serverWorld;
    }

    @Deprecated // use the attribute directly
    public double getBlockReachDistance() {
        return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
            .getAttributeValue();
    }

    @Deprecated // use an attribute modifier
    public void setBlockReachDistance(double distance) {
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
            .setBaseValue(distance);
    }
}
