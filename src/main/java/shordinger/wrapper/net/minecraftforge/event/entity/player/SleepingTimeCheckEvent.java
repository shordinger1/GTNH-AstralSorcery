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

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * This event is fired when the game checks if players can sleep at this time.<br>
 * Failing this check will cause sleeping players to wake up and prevent awake players from sleeping.<br>
 * <p>
 * This event has a result. {@link HasResult}<br>
 * <p>
 * setResult(ALLOW) informs game that player can sleep at this time.<br>
 * setResult(DEFAULT) causes game to check !{@link World#isDaytime()} instead.
 */
@HasResult
public class SleepingTimeCheckEvent extends PlayerEvent {

    private final BlockPos sleepingLocation;

    public SleepingTimeCheckEvent(EntityPlayer player, BlockPos sleepingLocation) {
        super(player);
        this.sleepingLocation = sleepingLocation;
    }

    /**
     * Note that the sleeping location may be an approximated one.
     *
     * @return The player's sleeping location.
     */
    public BlockPos getSleepingLocation() {
        return sleepingLocation;
    }
}
