/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.item.ItemBlock;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.world.WorldServer;
import shordinger.wrapper.net.minecraftforge.event.entity.EntityJoinWorldEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDropCaptureAssist
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:07
 */
public class BlockDropCaptureAssist {

    public static BlockDropCaptureAssist instance = new BlockDropCaptureAssist();

    private static Map<Integer, NonNullList<ItemStack>> capturedStacks = new HashMap<>();
    private static int stack = -1;

    private BlockDropCaptureAssist() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrop(EntityJoinWorldEvent event) {
        if (event.getWorld() instanceof WorldServer && event.getEntity() instanceof EntityItem) {
            ItemStack itemStack = ((EntityItem) event.getEntity()).getItem();
            if (stack > -1) {
                event.setCanceled(true);
                if (!itemStack.isEmpty()) {
                    if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock()
                        .equals(Blocks.STONE)) {
                        event.getEntity()
                            .setDead();
                        return;
                    }
                    // Apparently concurrency sometimes gets us here...
                    if (stack > -1) {
                        capturedStacks.computeIfAbsent(stack, st -> NonNullList.create())
                            .add(itemStack);
                    }
                }
                event.getEntity()
                    .setDead();
            }
        }
    }

    public static void startCapturing() {
        stack++;
        capturedStacks.put(stack, NonNullList.create());
    }

    public static NonNullList<ItemStack> getCapturedStacksAndStop() {
        NonNullList<ItemStack> pop = capturedStacks.get(stack);
        capturedStacks.remove(stack);
        stack = Math.max(-1, stack - 1);
        return pop == null ? NonNullList.create() : pop;
    }

}
