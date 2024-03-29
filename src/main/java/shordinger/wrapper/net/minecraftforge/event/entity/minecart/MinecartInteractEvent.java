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

package shordinger.wrapper.net.minecraftforge.event.entity.minecart;

import javax.annotation.Nonnull;

import shordinger.wrapper.net.minecraft.entity.item.EntityMinecart;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * MinecartInteractEvent is fired when a player interacts with a minecart. <br>
 * This event is fired whenever a player interacts with a minecart in
 * {@link EntityMinecart#processInitialInteract(EntityPlayer, EnumHand)}.
 * <br>
 * <br>
 * {@link #player} contains the EntityPlayer that is involved with this minecart interaction.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not interact with the minecart.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class MinecartInteractEvent extends MinecartEvent {

    private final EntityPlayer player;
    private final EnumHand hand;

    public MinecartInteractEvent(EntityMinecart minecart, EntityPlayer player, EnumHand hand) {
        super(minecart);
        this.player = player;
        this.hand = hand;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Nonnull
    public ItemStack getItem() {
        return player.getHeldItem(hand);
    }

    public EnumHand getHand() {
        return hand;
    }
}
