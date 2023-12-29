/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.util.ClientScreenshotCache;
import shordinger.astralsorcery.client.util.UIGateway;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.client.PktRequestTeleport;
import shordinger.astralsorcery.common.tile.TileCelestialGateway;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.data.WorldBlockPos;
import shordinger.astralsorcery.migration.MathHelper;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientGatewayHandler
 * Created by HellFirePvP
 * Date: 19.04.2017 / 10:39
 */
public class ClientGatewayHandler {

    public static UIGateway.GatewayEntry focusingEntry = null;
    public static int focusTicks = 0;

    private static int screenshotCooldown = 0;
    private static WorldBlockPos lastScreenshotPos = null;

    private float fovPre = 70F;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (screenshotCooldown > 0) {
            screenshotCooldown--;
            if (screenshotCooldown <= 0) {
                lastScreenshotPos = null;
                screenshotCooldown = 0;
            }
        }
        if (Minecraft.getMinecraft().thePlayer == null) return;

        UIGateway ui = EffectHandler.getInstance()
            .getUiGateway();
        if (ui != null && Minecraft.getMinecraft().thePlayer.world != null) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            TileCelestialGateway gate = MiscUtils.getTileAt(
                player.world,
                Vector3.atEntityCorner(player)
                    .toBlockPos(),
                TileCelestialGateway.class,
                true);
            if (gate != null && gate.hasMultiblock() && gate.doesSeeSky()) {
                if (lastScreenshotPos != null) {
                    WorldBlockPos currentPos = WorldBlockPos.wrap(gate);
                    if (!lastScreenshotPos.equals(currentPos)) {
                        lastScreenshotPos = null;
                        screenshotCooldown = 0;
                    }
                } else {
                    captureScreenshot(gate);
                }

                UIGateway.GatewayEntry entry = ui.findMatchingEntry(
                    MathHelper.wrapDegrees(player.rotationYaw),
                    MathHelper.wrapDegrees(player.rotationPitch));
                if (entry == null) {
                    focusingEntry = null;
                    focusTicks = 0;
                } else {
                    if (!Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown()
                        && !Minecraft.getMinecraft().thePlayer.isSneaking()) {
                        focusTicks = 0;
                        focusingEntry = null;
                    } else {
                        if (focusingEntry != null) {
                            if (!entry.equals(focusingEntry)) {
                                focusingEntry = null;
                                focusTicks = 0;
                            } else {
                                focusTicks++;
                            }
                        } else {
                            focusingEntry = entry;
                            focusTicks = 0;
                        }
                    }
                }
            } else {
                focusingEntry = null;
                focusTicks = 0;
            }
        } else {
            focusingEntry = null;
            focusTicks = 0;
        }

        if (focusingEntry != null) {
            Vector3 dir = focusingEntry.relativePos.clone()
                .add(ui.getPos())
                .subtract(
                    Vector3.atEntityCorner(Minecraft.getMinecraft().thePlayer)
                        .addY(1.62));
            Vector3 mov = dir.clone()
                .normalize()
                .multiply(0.25F)
                .negate();
            Vector3 pos = focusingEntry.relativePos.clone()
                .add(ui.getPos());
            if (focusTicks > 40) {
                for (Vector3 v : MiscUtils.getCirclePositions(
                    pos,
                    dir,
                    EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.3 + 0.2,
                    EffectHandler.STATIC_EFFECT_RAND.nextInt(20) + 30)) {
                    EntityFXFacingParticle p = EffectHelper.genericGatewayFlareParticle(v.getX(), v.getY(), v.getZ());
                    Vector3 m = mov.clone()
                        .multiply(0.5 + EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.5);
                    p.gravity(0.004)
                        .scale(0.1F)
                        .motion(m.getX(), m.getY(), m.getZ());
                    switch (EffectHandler.STATIC_EFFECT_RAND.nextInt(4)) {
                        case 0 -> p.setColor(Color.WHITE);
                        case 1 -> p.setColor(new Color(0x69B5FF));
                        case 2 -> p.setColor(new Color(0x0078FF));
                        default -> {
                        }
                    }
                }
            } else {
                pos = focusingEntry.relativePos.clone()
                    .multiply(0.8)
                    .add(ui.getPos());
                float perc = ((float) focusTicks) / 40;
                List<Vector3> positions = MiscUtils.getCirclePositions(
                    pos,
                    dir.clone()
                        .negate(),
                    EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.2 + 0.4,
                    EffectHandler.STATIC_EFFECT_RAND.nextInt(6) + 25);
                for (int i = 0; i < positions.size(); i++) {
                    float pc = ((float) i) / ((float) positions.size());
                    if (pc >= perc) continue;

                    Vector3 v = positions.get(i);
                    EntityFXFacingParticle p = EffectHelper.genericGatewayFlareParticle(v.getX(), v.getY(), v.getZ());
                    p.gravity(0.004)
                        .scale(0.08F);
                    if (EffectHandler.STATIC_EFFECT_RAND.nextInt(3) == 0) {
                        Vector3 to = pos.clone()
                            .subtract(v);
                        to.normalize()
                            .multiply(0.02);
                        p.motion(to.getX(), to.getY(), to.getZ())
                            .setAlphaMultiplier(0.1F);
                    }
                    switch (EffectHandler.STATIC_EFFECT_RAND.nextInt(4)) {
                        case 0 -> p.setColor(Color.WHITE);
                        case 1 -> p.setColor(new Color(0x69B5FF));
                        case 2 -> p.setColor(new Color(0x0078FF));
                        default -> {
                        }
                    }
                }
                positions = MiscUtils.getCirclePositions(
                    pos,
                    dir,
                    EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.2 + 0.4,
                    EffectHandler.STATIC_EFFECT_RAND.nextInt(6) + 25);
                Collections.reverse(positions);
                for (int i = 0; i < positions.size(); i++) {
                    float pc = ((float) i) / ((float) positions.size());
                    if (pc >= perc) continue;

                    Vector3 v = positions.get(i);
                    EntityFXFacingParticle p = EffectHelper.genericGatewayFlareParticle(v.getX(), v.getY(), v.getZ());
                    p.gravity(0.004)
                        .scale(0.08F);
                    if (EffectHandler.STATIC_EFFECT_RAND.nextInt(3) == 0) {
                        Vector3 to = pos.clone()
                            .subtract(v);
                        to.normalize()
                            .multiply(0.02);
                        p.motion(to.getX(), to.getY(), to.getZ())
                            .setAlphaMultiplier(0.1F);
                    }
                    switch (EffectHandler.STATIC_EFFECT_RAND.nextInt(4)) {
                        case 0 -> p.setColor(Color.WHITE);
                        case 1 -> p.setColor(new Color(0x69B5FF));
                        case 2 -> p.setColor(new Color(0x0078FF));
                        default -> {
                        }
                    }
                }
            }

            if (focusTicks > 95) { // Time explained below
                Minecraft.getMinecraft().thePlayer.setSneaking(false);
                PacketChannel.CHANNEL
                    .sendToServer(new PktRequestTeleport(focusingEntry.originalDimId, focusingEntry.originalBlockPos));
                focusTicks = 0;
                focusingEntry = null;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void captureScreenshot(TileCelestialGateway gate) {
        ResourceLocation gatewayScreenshot = ClientScreenshotCache
            .tryQueryTextureFor(gate.getWorld().provider.dimensionId, gate.getPos());
        if (gatewayScreenshot == null && Minecraft.getMinecraft().thePlayer != null
            && Minecraft.getMinecraft().thePlayer.rotationPitch <= 0
            && Minecraft.getMinecraft().currentScreen == null
            && Minecraft.getMinecraft().renderGlobal.getRenderedChunks() > 200) {
            screenshotCooldown = 10;
            lastScreenshotPos = WorldBlockPos.wrap(gate);

            ClientScreenshotCache.takeViewScreenshotFor(gate.getWorld().provider.dimensionId, gate.getPos());
        }
    }

    // 40 circle, 40 portal, 15 drag

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onRenderTransform(TickEvent.RenderTickEvent event) {
        UIGateway ui = EffectHandler.getInstance()
            .getUiGateway();
        if (ui != null) {
            if (event.phase == TickEvent.Phase.START) {
                fovPre = Minecraft.getMinecraft().gameSettings.fovSetting;
                if (focusTicks < 80) {
                    return;
                }
                float percDone = 1F - ((focusTicks - 80F + event.renderTickTime) / 15F);
                float targetFov = 10F;
                float diff = fovPre - targetFov;
                Minecraft.getMinecraft().gameSettings.fovSetting = Math.max(targetFov, targetFov + diff * percDone);
            } else {
                Minecraft.getMinecraft().gameSettings.fovSetting = fovPre;
            }
        }
    }

}
