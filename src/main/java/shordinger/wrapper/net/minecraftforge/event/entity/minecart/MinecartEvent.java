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

import shordinger.wrapper.net.minecraft.entity.item.EntityMinecart;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.event.entity.EntityEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;

/**
 * MinecartEvent is fired whenever an event involving minecart entities occurs. <br>
 * If a method utilizes this {@link Event} as its parameter, the method will <br>
 * receive every child event of this class.<br>
 * <br>
 * {@link #minecart} contains the minecart entity involved with this event.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class MinecartEvent extends EntityEvent {

    private final EntityMinecart minecart;

    public MinecartEvent(EntityMinecart minecart) {
        super(minecart);
        this.minecart = minecart;
    }

    public EntityMinecart getMinecart() {
        return minecart;
    }
}
