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
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraftforge.common.ForgeHooks;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * LivingHurtEvent is fired when an Entity is set to be hurt. <br>
 * This event is fired whenever an Entity is hurt in
 * {@link EntityLivingBase#damageEntity(DamageSource, float)} and
 * {@link EntityPlayer#damageEntity(DamageSource, float)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingHurt(EntityLivingBase, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the amount of damage dealt to the Entity that was hurt. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not hurt.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 *
 * @see LivingDamageEvent
 **/
@Cancelable
public class LivingHurtEvent extends LivingEvent {

    private final DamageSource source;
    private float amount;

    public LivingHurtEvent(EntityLivingBase entity, DamageSource source, float amount) {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
