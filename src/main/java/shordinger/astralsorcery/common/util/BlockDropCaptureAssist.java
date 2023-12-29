/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.util.HashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDropCaptureAssist
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:07
 */
public class BlockDropCaptureAssist {

    public static BlockDropCaptureAssist instance = new BlockDropCaptureAssist();

    private static final Map<Integer, NonNullList<ItemStack>> capturedStacks = new HashMap<>();
    private static int stack = -1;

    private BlockDropCaptureAssist() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrop(EntityJoinWorldEvent event) {
        if (event.world instanceof WorldServer && event.entity instanceof EntityItem) {
            ItemStack itemStack = ((EntityItem) event.entity).getItem();
            if (stack > -1) {
                event.setCanceled(true);
                if (!itemStack.isEmpty()) {
                    if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock()
                        .equals(Blocks.stone)) {
                        event.entity.setDead();
                        return;
                    }
                    // Apparently concurrency sometimes gets us here...
                    if (stack > -1) {
                        capturedStacks.computeIfAbsent(stack, st -> NonNullList.create())
                            .add(itemStack);
                    }
                }
                event.entity.setDead();
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
