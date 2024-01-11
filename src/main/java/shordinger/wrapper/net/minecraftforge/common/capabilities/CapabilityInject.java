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

package shordinger.wrapper.net.minecraftforge.common.capabilities;

import java.lang.annotation.*;

/**
 * When placed on a FIELD, the field will be set to an
 * instance of Capability once that capability is registered.
 * That field must be static and be able to hold a instance
 * of 'Capability'
 * <p>
 * Example:
 *
 * @CapabilityInject(IExampleCapability.class) private static final Capability<IExampleCapability> TEST_CAP = null;
 * <p>
 * When placed on a METHOD, the method will be invoked once the
 * capability is registered. This allows you to have a 'enable features'
 * callback. It MUST have one parameter of type 'Capability;
 * <p>
 * Example:
 * @CapabilityInject(IExampleCapability.class) private static void capRegistered(Capability<IExampleCapability> cap) {}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CapabilityInject {

    /**
     * The capability interface to listen for registration.
     * Note:
     * When reading annotations, DO NOT call this function as it will cause a hard dependency on the class.
     */
    Class<?> value();
}