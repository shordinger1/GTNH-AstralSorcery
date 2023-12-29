/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.block.EffectTranslucentFallingBlock;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.integrations.ModIntegrationOreStages;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktOreScan;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.struct.OreDiscoverer;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalPickaxe
 * Created by HellFirePvP
 * Date: 12.03.2017 / 23:25
 */
public class ItemChargedCrystalPickaxe extends ItemCrystalPickaxe implements ChargedCrystalToolBase {

    private static int idx = 0;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (hand ==  && !itemStackIn.isEmpty() && scanForOres(worldIn, playerIn)) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand ==  && scanForOres(worldIn, playerIn)) {
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private boolean scanForOres(World world, EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            Thread tr = new Thread(() -> {
                BlockArray foundOres = OreDiscoverer.startSearch(world, Vector3.atEntityCorner(player), 14);
                if (!foundOres.isEmpty()) {
                    List<BlockPos> positions = new LinkedList<>();
                    BlockPos plPos = player.getPosition();
                    for (BlockPos pos : foundOres.getPattern()
                        .keySet()) {
                        if (pos.distanceSq(plPos) < 350) {
                            positions.add(pos);
                        }
                    }
                    PktOreScan scan = new PktOreScan(positions, true);
                    PacketChannel.CHANNEL.sendTo(scan, (EntityPlayerMP) player);
                }
            });
            tr.setName("Ore Scan " + idx);
            idx++;
            tr.start();
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playClientEffects(Collection<BlockPos> positions, boolean tumble) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;
        List<IBlockState> changed = new LinkedList<>();

        for (BlockPos at : positions) {
            Vector3 atPos = new Vector3(at).add(0.5, 0.5, 0.5);
            atPos.add(
                itemRand.nextFloat() - itemRand.nextFloat(),
                itemRand.nextFloat() - itemRand.nextFloat(),
                itemRand.nextFloat() - itemRand.nextFloat());
            IBlockState state = Minecraft.getMinecraft().theWorld.getBlockState(at);
            if (Mods.ORESTAGES.isPresent()) {
                if (changed.contains(state) || !ModIntegrationOreStages.canSeeOreClient(state)) {
                    changed.add(state);
                    continue;
                }
            }

            EffectTranslucentFallingBlock bl = EffectHandler.getInstance()
                .translucentFallingBlock(atPos, state);
            bl.setDisableDepth(true)
                .setScaleFunction(new EntityComplexFX.ScaleFunction.Shrink<>());
            bl.setMotion(0, 0.03, 0)
                .setAlphaFunction(EntityComplexFX.AlphaFunction.PYRAMID);
            if (tumble) {
                bl.tumble();
            }
            bl.setMaxAge(35);
        }
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalPickaxe;
    }

}
