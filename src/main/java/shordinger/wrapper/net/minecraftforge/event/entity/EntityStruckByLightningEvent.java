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

package shordinger.wrapper.net.minecraftforge.event.entity;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.effect.EntityLightningBolt;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.event.ForgeEventFactory;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * EntityStruckByLightningEvent is fired when an Entity is about to be struck by lightening.<br>
 * This event is fired whenever an EntityLightningBolt is updated to strike an Entity in
 * {@link EntityLightningBolt#onUpdate()} via
 * {@link ForgeEventFactory#onEntityStruckByLightning(Entity, EntityLightningBolt)}.<br>
 * <br>
 * {@link #lightning} contains the instance of EntityLightningBolt attempting to strike an entity.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not struck by the lightening.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class EntityStruckByLightningEvent extends EntityEvent {

    private final EntityLightningBolt lightning;

    public EntityStruckByLightningEvent(Entity entity, EntityLightningBolt lightning) {
        super(entity);
        this.lightning = lightning;
    }

    public EntityLightningBolt getLightning() {
        return lightning;
    }
}
