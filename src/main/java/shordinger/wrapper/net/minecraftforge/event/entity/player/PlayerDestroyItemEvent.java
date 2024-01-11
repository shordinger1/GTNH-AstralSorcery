/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.client.entity.EntityPlayerSP;
import shordinger.wrapper.net.minecraft.client.multiplayer.PlayerControllerMP;
import shordinger.wrapper.net.minecraft.client.multiplayer.WorldClient;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.server.management.PlayerInteractionManager;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.ForgeHooks;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.event.ForgeEventFactory;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * PlayerDestroyItemEvent is fired when a player destroys an item.<br>
 * This event is fired whenever a player destroys an item in
 * {@link PlayerControllerMP#onPlayerDestroyBlock(BlockPos)},
 * {@link PlayerControllerMP#processRightClick(EntityPlayer, World, EnumHand)},
 * {@link PlayerControllerMP#processRightClickBlock(EntityPlayerSP, WorldClient, BlockPos, EnumFacing, Vec3d, EnumHand)},
 * {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity)},
 * {@link EntityPlayer#damageShield(float)},
 * {@link EntityPlayer#interactOn(Entity, EnumHand)},
 * {@link ForgeHooks#getContainerItem(ItemStack)},
 * {@link PlayerInteractionManager#processRightClick(EntityPlayer, World, ItemStack, EnumHand)},
 * {@link PlayerInteractionManager#processRightClickBlock(EntityPlayer, World, ItemStack, EnumHand, BlockPos, EnumFacing, float, float, float)}
 * and {@link PlayerInteractionManager#tryHarvestBlock(BlockPos)}.<br>
 * <br>
 * {@link #original} contains the original ItemStack before the item was destroyed. <br>
 * (@link #hand) contains the hand that the current item was held in.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired from {@link ForgeEventFactory#onPlayerDestroyItem(EntityPlayer, ItemStack, EnumHand)}.<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerDestroyItemEvent extends PlayerEvent {

    @Nonnull
    private final ItemStack original;
    @Nullable
    private final EnumHand hand; // May be null if this player destroys the item by any use besides holding it.

    public PlayerDestroyItemEvent(EntityPlayer player, @Nonnull ItemStack original, @Nullable EnumHand hand) {
        super(player);
        this.original = original;
        this.hand = hand;
    }

    @Nonnull
    public ItemStack getOriginal() {
        return this.original;
    }

    @Nullable
    public EnumHand getHand() {
        return this.hand;
    }

}
