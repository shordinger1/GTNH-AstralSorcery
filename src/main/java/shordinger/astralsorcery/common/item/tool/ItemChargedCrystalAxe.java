/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.awt.*;
import java.util.Map;

import javax.annotation.Nonnull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.network.packet.server.PktDualParticleEvent;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.tile.TileFakeTree;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.struct.TreeDiscoverer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalAxe
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:04
 */
public class ItemChargedCrystalAxe extends ItemCrystalAxe implements ChargedCrystalToolBase {

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        World world = player.getEntityWorld();
        if (!world.isRemote && !player.isSneaking()
            && !player.getCooldownTracker()
            .hasCooldown(ItemsAS.chargedCrystalAxe)) {
            BlockArray tree = TreeDiscoverer.tryCaptureTreeAt(world, pos, 9, true);
            if (tree != null) {
                Map<BlockPos, BlockArray.BlockInformation> pattern = tree.getPattern();
                for (Map.Entry<BlockPos, BlockArray.BlockInformation> blocks : pattern.entrySet()) {
                    if (world.setBlockState(blocks.getKey(), BlocksAS.blockFakeTree.getDefaultState())) {
                        TileFakeTree tt = MiscUtils.getTileAt(world, blocks.getKey(), TileFakeTree.class, true);
                        if (tt != null) {
                            tt.setupTile(player, itemstack, blocks.getValue().state);
                            itemstack.damageItem(1, player);
                        } else {
                            world.setBlockState(blocks.getKey(), blocks.getValue().state);
                        }
                    }
                }
                if (!ChargedCrystalToolBase.tryRevertMainHand(player, itemstack)) {
                    player.getCooldownTracker()
                        .setCooldown(ItemsAS.chargedCrystalAxe, 150);
                }
                return true;
            }
        }
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @SideOnly(Side.CLIENT)
    public static void playDrainParticles(PktDualParticleEvent pktDualParticleEvent) {
        Vector3 to = pktDualParticleEvent.getTargetVec();
        int colorHex = MathHelper.floor(pktDualParticleEvent.getAdditionalData());
        Color c = new Color(colorHex);
        for (int i = 0; i < 10; i++) {
            Vector3 from = pktDualParticleEvent.getOriginVec()
                .add(itemRand.nextFloat(), itemRand.nextFloat(), itemRand.nextFloat());
            Vector3 mov = to.clone()
                .subtract(from)
                .normalize()
                .multiply(0.1 + 0.1 * itemRand.nextFloat());
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
            p.motion(mov.getX(), mov.getY(), mov.getZ())
                .setMaxAge(30 + itemRand.nextInt(25));
            p.gravity(0.004)
                .scale(0.25F)
                .setColor(c);
        }
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalAxe;
    }

}
