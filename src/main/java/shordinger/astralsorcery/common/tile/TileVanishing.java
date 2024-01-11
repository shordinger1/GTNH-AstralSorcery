/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import shordinger.astralsorcery.common.item.tool.wand.ItemWand;
import shordinger.astralsorcery.common.item.tool.wand.WandAugment;
import shordinger.astralsorcery.common.tile.base.TileEntityTick;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileVanishing
 * Created by HellFirePvP
 * Date: 30.07.2017 / 17:35
 */
public class TileVanishing extends TileEntityTick {

    private static final AxisAlignedBB topBox = new AxisAlignedBB(-0.9,0, -0.9, 0.9, 0.9, 0.9);

    @Override
    public void update() {
        super.update();

        if(!world.isRemote && ticksExisted % 10 == 0) {
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, topBox.offset(pos));
            for (EntityPlayer player : players) {
                ItemStack held = player.getHeldItemMainhand();
                if(!held.isEmpty() && held.getItem() instanceof ItemWand && WandAugment.AEVITAS == ItemWand.getAugment(held)) {
                    return;
                }
                held = player.getHeldItemOffhand();
                if(!held.isEmpty() && held.getItem() instanceof ItemWand && WandAugment.AEVITAS == ItemWand.getAugment(held)) {
                    return;
                }
            }
            getWorld().setBlockToAir(getPos());
        }
    }

    @Override
    protected void onFirstTick() {}

}
