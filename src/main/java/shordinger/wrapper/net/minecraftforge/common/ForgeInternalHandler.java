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

package shordinger.wrapper.net.minecraftforge.common;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.world.WorldServer;
import shordinger.wrapper.net.minecraftforge.common.util.FakePlayerFactory;
import shordinger.wrapper.net.minecraftforge.event.entity.EntityJoinWorldEvent;
import shordinger.wrapper.net.minecraftforge.event.world.ChunkEvent;
import shordinger.wrapper.net.minecraftforge.event.world.WorldEvent;
import shordinger.wrapper.net.minecraftforge.fml.client.FMLClientHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.TickEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ForgeInternalHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote) {
            ForgeChunkManager.loadEntity(event.getEntity());
        }

        Entity entity = event.getEntity();
        if (entity.getClass()
            .equals(EntityItem.class)) {
            ItemStack stack = ((EntityItem) entity).getItem();
            Item item = stack.getItem();
            if (item.hasCustomEntity(stack)) {
                Entity newEntity = item.createEntity(event.getWorld(), entity, stack);
                if (newEntity != null) {
                    entity.setDead();
                    event.setCanceled(true);
                    event.getWorld()
                        .spawnEntity(newEntity);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionLoad(WorldEvent.Load event) {
        ForgeChunkManager.loadWorld(event.getWorld());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionSave(WorldEvent.Save event) {
        ForgeChunkManager.saveWorld(event.getWorld());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionUnload(WorldEvent.Unload event) {
        ForgeChunkManager.unloadWorld(event.getWorld());
        if (event.getWorld() instanceof WorldServer) FakePlayerFactory.unloadWorld((WorldServer) event.getWorld());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        WorldWorkerManager.tick(event.phase == TickEvent.Phase.START);
    }

    @SubscribeEvent
    public void checkSettings(ClientTickEvent event) {
        if (event.phase == Phase.END) FMLClientHandler.instance()
            .updateCloudSettings();
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (!event.getWorld().isRemote) FarmlandWaterManager.removeTickets(event.getChunk());
    }
}
