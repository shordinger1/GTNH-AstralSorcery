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

package shordinger.wrapper.net.minecraftforge.event.entity.living;

import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraftforge.common.ForgeHooks;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in
 * {@link EntityLivingBase#onDeath(DamageSource)},
 * {@link EntityPlayer#onDeath(DamageSource)}, and
 * {@link EntityPlayerMP#onDeath(DamageSource)}. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDeath(EntityLivingBase, DamageSource)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the entity to die. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not die.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingDeathEvent extends LivingEvent {

    private final DamageSource source;

    public LivingDeathEvent(EntityLivingBase entity, DamageSource source) {
        super(entity);
        this.source = source;
    }

    public DamageSource getSource() {
        return source;
    }
}
