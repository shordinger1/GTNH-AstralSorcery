/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.wand;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.gtnewhorizons.modularui.api.GlStateManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.lwjgl.opengl.GL11;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.event.ClientRenderEventHandler;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.integrations.ModIntegrationBotania;
import shordinger.astralsorcery.common.item.ItemBlockStorage;
import shordinger.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import shordinger.astralsorcery.common.item.base.render.ItemHandRender;
import shordinger.astralsorcery.common.item.base.render.ItemHudRender;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.BufferBuilder;
import shordinger.astralsorcery.migration.IBlockState;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemArchitectWand
 * Created by HellFirePvP
 * Date: 06.02.2017 / 22:49
 */
public class ItemArchitectWand extends ItemBlockStorage
    implements ItemHandRender, ItemHudRender, ItemAlignmentChargeConsumer {

    private static final double architectRange = 60.0D;

    public ItemArchitectWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return ct == ChargeType.TEMP;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderInHandHUD(ItemStack lastCacheInstance, float fadeAlpha, float pTicks) {
        Collection<ItemStack> stored = getMappedStoredStates(lastCacheInstance).values();
        if (stored.isEmpty()) return;

        Map<ItemStack, Integer> amountMap = new LinkedHashMap<>();
        for (ItemStack stack : stored) {
            int found = 0;
            if (Mods.BOTANIA.isPresent()) {
                found = ModIntegrationBotania.getItemCount(
                    Minecraft.getMinecraft().thePlayer,
                    lastCacheInstance,
                    ItemUtils.createBlockState(stack));
            } else {
                Collection<ItemStack> stacks = ItemUtils
                    .scanInventoryForMatching(new InvWrapper(Minecraft.getMinecraft().thePlayer.inventory), stack, false);
                for (ItemStack foundStack : stacks) {
                    found += foundStack.stackSize;
                }
            }
            amountMap.put(stack, found);
        }

        int heightNormal = 26;
        int heightSplit = 13;
        int width = 26;
        int offsetX = 30;
        int offsetY = 15;

        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        GlStateManager.color(1F, 1F, 1F, fadeAlpha * 0.9F);
        GL11.glColor4f(1F, 1F, 1F, fadeAlpha * 0.9F);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        int tempOffsetY = offsetY;
        for (int i = 0; i < amountMap.size(); i++) {
            boolean first = i == 0;
            boolean last = (i + 1 == amountMap.size());
            if (first) {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                ClientRenderEventHandler.texHUDItemFrame.bind();
                vb.pos(offsetX, tempOffsetY + heightSplit, 10)
                    .tex(0, 0.5)
                    .endVertex();
                vb.pos(offsetX + width, tempOffsetY + heightSplit, 10)
                    .tex(1, 0.5)
                    .endVertex();
                vb.pos(offsetX + width, tempOffsetY, 10)
                    .tex(1, 0)
                    .endVertex();
                vb.pos(offsetX, tempOffsetY, 10)
                    .tex(0, 0)
                    .endVertex();
                tempOffsetY += heightSplit;
                tes.draw();
            } else {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                ClientRenderEventHandler.texHUDItemFrameEx.bind();
                vb.pos(offsetX, tempOffsetY + heightNormal, 10)
                    .tex(0, 1)
                    .endVertex();
                vb.pos(offsetX + width, tempOffsetY + heightNormal, 10)
                    .tex(1, 1)
                    .endVertex();
                vb.pos(offsetX + width, tempOffsetY, 10)
                    .tex(1, 0)
                    .endVertex();
                vb.pos(offsetX, tempOffsetY, 10)
                    .tex(0, 0)
                    .endVertex();
                tempOffsetY += heightNormal;
                tes.draw();
            }
            if (last) {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                ClientRenderEventHandler.texHUDItemFrame.bind();
                vb.pos(offsetX, tempOffsetY + heightSplit, 10)
                    .tex(0, 1)
                    .endVertex();
                vb.pos(offsetX + width, tempOffsetY + heightSplit, 10)
                    .tex(1, 1)
                    .endVertex();
                vb.pos(offsetX + width, tempOffsetY, 10)
                    .tex(1, 0.5)
                    .endVertex();
                vb.pos(offsetX, tempOffsetY, 10)
                    .tex(0, 0.5)
                    .endVertex();
                tempOffsetY += heightSplit;
                tes.draw();
            }
        }

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem ri = Minecraft.getMinecraft()
            .getRenderItem();

        tempOffsetY = offsetY;
        for (Map.Entry<ItemStack, Integer> entry : amountMap.entrySet()) {
            ri.renderItemAndEffectIntoGUI(
                Minecraft.getMinecraft().thePlayer,
                entry.getKey(),
                offsetX + 5,
                tempOffsetY + 5);
            tempOffsetY += heightNormal;
            GlStateManager.enableAlpha(); // Because Mc item rendering..
        }

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX + 14, offsetY + 16, 0);
        int c = 0x00DDDDDD;
        for (Map.Entry<ItemStack, Integer> entry : amountMap.entrySet()) {
            String amountStr = String.valueOf(entry.getValue());
            if (entry.getValue() == -1) {
                amountStr = "\u221E";
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(-Minecraft.getMinecraft().fontRenderer.getStringWidth(amountStr) / 3, 0, 0);
            GlStateManager.scale(0.7, 0.7, 0.7);
            if (amountStr.length() > 3) {
                GlStateManager.scale(0.9, 0.9, 0.9);
            }
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(amountStr, 0, 0, c);
            GlStateManager.popMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);

            GlStateManager.translate(0, heightNormal, 0);
        }
        TextureHelper.refreshTextureBindState();

        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderWhileInHand(ItemStack stack, EnumHand hand, float pTicks) {
        List<IBlockState> storedStates = Lists.newArrayList(getMappedStoredStates(stack).keySet());
        if (storedStates.isEmpty()) return;
        Random r = getPreviewRandomFromWorld(Minecraft.getMinecraft().world);

        Deque<BlockPos> placeable = filterBlocksToPlace(
            Minecraft.getMinecraft().thePlayer,
            Minecraft.getMinecraft().world,
            architectRange);
        if (!placeable.isEmpty()) {
            RayTraceResult rtr = getLookBlock(Minecraft.getMinecraft().thePlayer, false, true, architectRange);
            if (rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK) {
                return;
            }
            BlockPos hitVec = rtr.hitVec;
            EnumFacing sideHit = rtr.sideHit;

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            Blending.ADDITIVEDARK.applyStateManager();
            GlStateManager.disableAlpha();
            RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
            World w = Minecraft.getMinecraft().world;

            TextureHelper.setActiveTextureToAtlasSprite();
            Tessellator tes = Tessellator.getInstance();
            BufferBuilder vb = tes.getBuffer();
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            for (BlockPos pos : placeable) {
                Collections.shuffle(storedStates, r);
                IBlockState potentialState = Iterables.getFirst(storedStates, Blocks.AIR.getDefaultState());
                try {
                    potentialState = potentialState.getBlock()
                        .getStateForPlacement(
                            Minecraft.getMinecraft().world,
                            pos,
                            sideHit,
                            (float) hitVec.x,
                            (float) hitVec.y,
                            (float) hitVec.z,
                            potentialState.getBlock()
                                .getMetaFromState(potentialState),
                            Minecraft.getMinecraft().thePlayer,
                            hand);
                } catch (Exception exc) {
                }
                RenderingUtils.renderBlockSafely(w, pos, potentialState, vb);
            }
            vb.sortVertexData(
                (float) TileEntityRendererDispatcher.staticPlayerX,
                (float) TileEntityRendererDispatcher.staticPlayerY,
                (float) TileEntityRendererDispatcher.staticPlayerZ);
            tes.draw();
            Blending.DEFAULT.applyStateManager();
            Blending.DEFAULT.apply();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.isEmpty()) return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(hand));
        if (world.isRemote) return ActionResult.newResult(EnumActionResult.SUCCESS, stack);

        Map<IBlockState, ItemStack> storedStates = getMappedStoredStates(stack);
        if (storedStates.isEmpty()) return ActionResult.newResult(EnumActionResult.SUCCESS, stack);

        RayTraceResult rtr = getLookBlock(playerIn, false, true, architectRange);
        if (rtr == null || rtr.sideHit == null || rtr.hitVec == null)
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        EnumFacing sideHit = rtr.sideHit;
        List<Tuple<IBlockState, ItemStack>> shuffleable = MiscUtils.flatten(storedStates, Tuple::new);

        Random r = getPreviewRandomFromWorld(world);
        Deque<BlockPos> placeable = filterBlocksToPlace(playerIn, world, architectRange);
        if (!placeable.isEmpty()) {
            for (BlockPos placePos : placeable) {
                Collections.shuffle(shuffleable, r);
                Tuple<IBlockState, ItemStack> applicable = playerIn.isCreative() ? Iterables.getFirst(shuffleable, null)
                    : null;
                if (!playerIn.isCreative()) {
                    for (Tuple<IBlockState, ItemStack> it : shuffleable) {
                        ItemStack test = ItemUtils.copyStackWithSize(it.value, 1);
                        if (ItemUtils.consumeFromPlayerInventory(playerIn, stack, test, true)) {
                            applicable = it;
                            break;
                        }
                    }
                }
                if (applicable == null) break; // No more blocks. LUL

                if (drainTempCharge(playerIn, Config.architectWandUseCost, true)) {
                    IBlockState place = applicable.key;
                    try {
                        place = applicable.key.getBlock()
                            .getStateForPlacement(
                                world,
                                placePos,
                                sideHit,
                                (float) rtr.hitVec.x,
                                (float) rtr.hitVec.y,
                                (float) rtr.hitVec.z,
                                applicable.value.getMetadata(),
                                playerIn,
                                hand);
                    } catch (Exception exc) {
                    }
                    if (MiscUtils.canPlayerPlaceBlockPos(playerIn, hand, place, placePos, sideHit)) {
                        if (world.setBlockState(placePos, place)) {
                            drainTempCharge(playerIn, Config.architectWandUseCost, false);
                            if (!playerIn.isCreative()) {
                                ItemUtils.consumeFromPlayerInventory(
                                    playerIn,
                                    stack,
                                    ItemUtils.copyStackWithSize(applicable.value, 1),
                                    false);
                            }
                            PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.ARCHITECT_PLACE,
                                placePos);
                            ev.setAdditionalDataLong(Block.getStateId(applicable.key));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                        }
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.isEmpty()) return EnumActionResult.SUCCESS;

        if (playerIn.isSneaking()) {
            tryStoreBlock(stack, world, pos);
            return EnumActionResult.SUCCESS;
        } else {
            if (!world.isRemote) {
                Map<IBlockState, ItemStack> storedStates = getMappedStoredStates(stack);
                if (storedStates.isEmpty()) return EnumActionResult.SUCCESS;

                List<Tuple<IBlockState, ItemStack>> shuffleable = MiscUtils.flatten(storedStates, Tuple::new);
                Random r = getPreviewRandomFromWorld(world);

                Deque<BlockPos> placeable = filterBlocksToPlace(playerIn, world, architectRange);
                if (!placeable.isEmpty()) {
                    for (BlockPos placePos : placeable) {
                        Collections.shuffle(shuffleable, r);
                        Tuple<IBlockState, ItemStack> applicable = playerIn.isCreative()
                            ? Iterables.getFirst(shuffleable, null)
                            : null;
                        if (!playerIn.isCreative()) {
                            for (Tuple<IBlockState, ItemStack> it : shuffleable) {
                                ItemStack test = ItemUtils.copyStackWithSize(it.value, 1);
                                if (ItemUtils.consumeFromPlayerInventory(playerIn, stack, test, true)) {
                                    applicable = it;
                                    break;
                                }
                            }
                        }
                        if (applicable == null) break; // No more blocks. LUL

                        if (drainTempCharge(playerIn, Config.architectWandUseCost, true)) {
                            IBlockState place = applicable.key;
                            try {
                                place = applicable.key.getBlock()
                                    .getStateForPlacement(
                                        world,
                                        placePos,
                                        facing,
                                        hitX,
                                        hitY,
                                        hitZ,
                                        applicable.value.getMetadata(),
                                        playerIn,
                                        hand);
                            } catch (Exception exc) {
                            }
                            if (MiscUtils.canPlayerPlaceBlockPos(playerIn, hand, place, placePos, facing)) {
                                if (world.setBlockState(placePos, place)) {
                                    drainTempCharge(playerIn, Config.architectWandUseCost, false);
                                    if (!playerIn.isCreative()) {
                                        ItemUtils.consumeFromPlayerInventory(
                                            playerIn,
                                            stack,
                                            ItemUtils.copyStackWithSize(applicable.value, 1),
                                            false);
                                    }
                                    PktParticleEvent ev = new PktParticleEvent(
                                        PktParticleEvent.ParticleEventType.ARCHITECT_PLACE,
                                        placePos);
                                    ev.setAdditionalDataLong(Block.getStateId(applicable.key));
                                    PacketChannel.CHANNEL
                                        .sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                                }
                            }
                        }
                    }
                }
            }
        }

        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
    public static void playArchitectPlaceEvent(PktParticleEvent event) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            Vector3 at = event.getVec();
            IBlockState state = Block.getStateById((int) event.getAdditionalDataLong());
            RenderingUtils.playBlockBreakParticles(at.toBlockPos(), state);
            for (int i = 0; i < 9; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    at.getX()
                        + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)),
                    at.getY()
                        + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)),
                    at.getZ()
                        + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)));
                p.motion(
                    (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1),
                    (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1),
                    (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1));
                p.scale(0.35F)
                    .setColor(Color.WHITE.brighter());
            }
        }, 1);
    }

    private Deque<BlockPos> filterBlocksToPlace(Entity entity, World world, double range) {
        Deque<BlockPos> placeable = getBlocksToPlaceAt(entity, range);
        boolean discard = false;
        Iterator<BlockPos> iterator = placeable.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (discard) {
                iterator.remove();
                continue;
            }
            if (!world.isAirBlock(pos) && !WorldHelper.getBlockState(world, pos)
                .getBlock()
                .isReplaceable(world, pos)) {
                discard = true;
                iterator.remove();
            }
        }
        return placeable;
    }

    private Deque<BlockPos> getBlocksToPlaceAt(Entity entity, double range) {
        RayTraceResult rtr = getLookBlock(entity, false, true, range);
        if (rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK) {
            return Lists.newLinkedList();
        }
        LinkedList<BlockPos> blocks = Lists.newLinkedList();
        EnumFacing sideHit = rtr.sideHit;
        BlockPos hitPos = rtr.getBlockPos();
        int length;
        int cmpFrom;
        double cmpTo;
        switch (sideHit.getAxis()) {
            case X:
                cmpFrom = hitPos.getX();
                cmpTo = entity.posX;
                break;
            case Y:
                cmpFrom = hitPos.getY();
                cmpTo = entity.posY;
                break;
            case Z:
                cmpFrom = hitPos.getZ();
                cmpTo = entity.posZ;
                break;
            default:
                return Lists.newLinkedList();
        }
        length = (int) Math.min(20, Math.abs(cmpFrom + 0.5 - cmpTo));
        for (int i = 1; i < length; i++) {
            blocks.add(hitPos.offset(sideHit, i));
        }
        return blocks;
    }

}
