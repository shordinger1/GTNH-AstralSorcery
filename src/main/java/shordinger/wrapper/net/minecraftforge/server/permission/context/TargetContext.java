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

package shordinger.wrapper.net.minecraftforge.server.permission.context;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;

public class TargetContext extends PlayerContext {

    private final Entity target;

    public TargetContext(EntityPlayer ep, @Nullable Entity entity) {
        super(ep);
        target = entity;
    }

    @Override
    @Nullable
    public <T> T get(ContextKey<T> key) {
        return key.equals(ContextKeys.TARGET) ? (T) target : super.get(key);
    }

    @Override
    protected boolean covers(ContextKey<?> key) {
        return target != null && key.equals(ContextKeys.TARGET);
    }
}
