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

package shordinger.wrapper.net.minecraftforge.fml.client.registry;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import shordinger.wrapper.net.minecraft.client.settings.KeyBinding;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientRegistry {

    private static Map<Class<? extends Entity>, ResourceLocation> entityShaderMap = Maps.newHashMap();

    /**
     * Utility method for registering a tile entity and it's renderer at once - generally you should register them
     * separately
     *
     * @param tileEntityClass
     * @param id
     * @param specialRenderer
     */
    public static <T extends TileEntity> void registerTileEntity(Class<T> tileEntityClass, String id,
                                                                 TileEntitySpecialRenderer<? super T> specialRenderer) {
        GameRegistry.registerTileEntity(tileEntityClass, id);
        bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer);
    }

    public static <T extends TileEntity> void bindTileEntitySpecialRenderer(Class<T> tileEntityClass,
                                                                            TileEntitySpecialRenderer<? super T> specialRenderer) {
        TileEntityRendererDispatcher.instance.renderers.put(tileEntityClass, specialRenderer);
        specialRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    public static void registerKeyBinding(KeyBinding key) {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils
            .add(Minecraft.getMinecraft().gameSettings.keyBindings, key);
    }

    /**
     * Register a shader for an entity. This shader gets activated when a spectator begins spectating an entity.
     * Vanilla examples of this are the green effect for creepers and the invert effect for endermen.
     *
     * @param entityClass
     * @param shader
     */
    public static void registerEntityShader(Class<? extends Entity> entityClass, ResourceLocation shader) {
        entityShaderMap.put(entityClass, shader);
    }

    public static ResourceLocation getEntityShader(Class<? extends Entity> entityClass) {
        return entityShaderMap.get(entityClass);
    }
}
