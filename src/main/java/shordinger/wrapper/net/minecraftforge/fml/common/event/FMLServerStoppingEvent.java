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

package shordinger.wrapper.net.minecraftforge.fml.common.event;

import shordinger.wrapper.net.minecraftforge.fml.common.LoaderState.ModState;

/**
 * Called when the server begins an orderly shutdown, before {@link FMLServerStoppedEvent}.
 *
 * @author cpw
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 */
public class FMLServerStoppingEvent extends FMLStateEvent {

    public FMLServerStoppingEvent(Object... data) {
        super(data);
    }

    @Override
    public ModState getModState() {
        return ModState.AVAILABLE;
    }

}
