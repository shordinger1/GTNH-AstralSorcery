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

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecart;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * MinecartCollisionEvent is fired when a minecart collides with an Entity.
 * This event is fired whenever a minecraft collides in
 * {@link EntityMinecart#applyEntityCollision(Entity)}.
 * <p>
 * {@link #collider} contains the Entity the Minecart collided with.
 * <p>
 * This event is not {@link Cancelable}.
 * <p>
 * This event does not have a result. {@link HasResult}
 * <p>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class MinecartCollisionEvent extends MinecartEvent {

    private final Entity collider;

    public MinecartCollisionEvent(EntityMinecart minecart, Entity collider) {
        super(minecart);
        this.collider = collider;
    }

    public Entity getCollider() {
        return collider;
    }
}
