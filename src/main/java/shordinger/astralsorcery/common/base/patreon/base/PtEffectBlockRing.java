/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.base;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import com.gtnewhorizons.modularui.api.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectBlockRing
 * Created by HellFirePvP
 * Date: 05.04.2019 / 21:35
 */
public class PtEffectBlockRing extends PatreonEffectHelper.PatreonEffect {

    private final UUID playerUUID;

    private final float distance;
    private final float rotationAngle;
    private final int repetition;
    private final int rotationSpeed;
    private final float rotationPart;
    private final Map<BlockPos, IBlockState> pattern;

    /*
     * Based on X = 2
     * variable in Z and Y direction, X towards and from player
     */
    public PtEffectBlockRing(UUID sessionEffectId, PatreonEffectHelper.FlareColor chosenColor, UUID playerUUID,
                             float distance, float rotationAngle, int repeats, int tickRotationSpeed, Map<BlockPos, IBlockState> pattern) {
        super(sessionEffectId, chosenColor);

        this.playerUUID = playerUUID;
        this.distance = distance;
        this.rotationAngle = rotationAngle;
        this.repetition = repeats;
        this.rotationSpeed = tickRotationSpeed;
        this.rotationPart = 360F / rotationSpeed;
        this.pattern = pattern;
    }

    @Override
    public void initialize() {
        super.initialize();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLast(RenderWorldLastEvent event) {
        EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && // First person
            pl != null
            && pl.getUniqueID()
            .equals(playerUUID)) {

            float alpha = 1F;
            if (pl.rotationPitch >= 35F) {
                alpha = Math.max(0, (55F - pl.rotationPitch) / 20F);
            }
            renderRingAt(new Vector3(0, 0.2, 0), alpha, event.getPartialTicks());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderPost(RenderPlayerEvent.Post ev) {
        EntityPlayer player = ev.getEntityPlayer();
        if (!player.getUniqueID()
            .equals(playerUUID)) {
            return;
        }

        renderRingAt(new Vector3(ev.getX(), ev.getY(), ev.getZ()), 1F, ev.getPartialRenderTick());
    }

    @SideOnly(Side.CLIENT)
    private void renderRingAt(Vector3 vec, float alphaMultiplier, float pTicks) {
        float addedRotationAngle = 0;
        TextureHelper.setActiveTextureToAtlasSprite();

        if (rotationSpeed > 1) {
            float rot = ClientScheduler.getIndependentClientTick() % rotationSpeed;
            addedRotationAngle = (rot / ((float) (rotationSpeed))) * 360F + this.rotationPart * pTicks;
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        Blending.ADDITIVE_ALPHA.applyStateManager();

        for (int rotation = 0; rotation < 360; rotation += (360 / repetition)) {
            for (BlockPos offset : pattern.keySet()) {
                IBlockState state = pattern.get(offset);

                TextureAtlasSprite tas = RenderingUtils.tryGetTextureOfBlockState(state);
                if (tas == null) {
                    continue;
                }

                float angle = offset.getZ() * rotationAngle + rotation + addedRotationAngle;

                Vector3 dir = new Vector3(offset.getX() - distance, offset.getY(), 0);
                dir.rotate(Math.toRadians(angle), Vector3.RotAxis.Y_AXIS);
                dir.multiply(new Vector3(0.2F, 0.1F, 0.2F));
                dir.add(vec);

                GlStateManager.pushMatrix();
                GlStateManager.translate(dir.getX(), dir.getY(), dir.getZ());
                GlStateManager.scale(0.09, 0.09, 0.09);
                GlStateManager.color(1F, 1F, 1F, alphaMultiplier);

                RenderingUtils.renderTexturedCubeCentral(
                    new Vector3(),
                    1F,
                    tas.getMinU(),
                    tas.getMinV(),
                    tas.getMaxU() - tas.getMinU(),
                    tas.getMaxV() - tas.getMinV());

                GlStateManager.popMatrix();
            }
        }

        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
    }
}
