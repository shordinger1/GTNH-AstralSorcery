package shordinger.wrapper.net.minecraft.item;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemMapBase extends Item {

    /**
     * false for all Items except sub-classes of ItemMapBase
     */
    public boolean isMap() {
        return true;
    }

    @Nullable
    public Packet<?> createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
        return null;
    }
}
