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

import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is called when a player collides with a EntityItem on the ground.
 * The event can be canceled, and no further processing will be done.
 * <p>
 * You can set the result of this event to ALLOW which will trigger the
 * processing of achievements, FML's event, play the sound, and kill the
 * entity if all the items are picked up.
 * <p>
 * setResult(ALLOW) is the same as the old setHandled()
 */
@Cancelable
@Event.HasResult
public class EntityItemPickupEvent extends PlayerEvent {

    private final EntityItem item;

    public EntityItemPickupEvent(EntityPlayer player, EntityItem item) {
        super(player);
        this.item = item;
    }

    public EntityItem getItem() {
        return item;
    }
}
