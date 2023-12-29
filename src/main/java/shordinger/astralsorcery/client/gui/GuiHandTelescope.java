/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import shordinger.astralsorcery.migration.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.gui.base.GuiSkyScreen;
import shordinger.astralsorcery.client.gui.base.GuiWHScreen;
import shordinger.astralsorcery.client.sky.RenderAstralSkybox;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.ClientUtils;
import shordinger.astralsorcery.client.util.RenderConstellation;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.constellation.star.StarConnection;
import shordinger.astralsorcery.common.constellation.star.StarLocation;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiHandTelescope
 * Created by HellFirePvP
 * Date: 28.12.2016 / 12:18
 */
public class GuiHandTelescope extends GuiWHScreen implements GuiSkyScreen {

    private static final Random random = new Random();

    private static final BindableResource textureGrid = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "gridhandtelescope");
    private static final BindableResource textureConnection = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");
    private static final Rectangle rectDrawing = new Rectangle(6, 6, 210, 210);

    private IMajorConstellation drawnConstellation = null;
    private Map<StarLocation, Rectangle> drawnStars = null;

    private static final int randomStars = 40;
    private List<StarPosition> usedStars = new ArrayList<>(randomStars);

    private IMajorConstellation topFound = null;
    private float selectedYaw = 0, selectedPitch = 0;

    private boolean grabCursor = false;

    private LinkedList<GuiTelescope.Line> drawnLines = new LinkedList<>();
    private Point start, end;

    public GuiHandTelescope() {
        super(216, 216);

        Optional<Long> currSeed = ConstellationSkyHandler.getInstance()
            .getSeedIfPresent(Minecraft.getMinecraft().theWorld);
        if (currSeed.isPresent()) {
            setupInitialStars(currSeed.get());
        }
    }

    private void setupInitialStars(long seed) {
        int offsetX = 6, offsetY = 6;
        int width = guiWidth - 6, height = guiHeight - 6;
        Random rand = new Random(seed);

        int day = (int) (Minecraft.getMinecraft().theWorld.getWorldTime() / Config.dayLength);
        for (int i = 0; i < Math.abs(day); i++) {
            rand.nextLong(); // Flush
        }

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(Minecraft.getMinecraft().theWorld);
        if (handle != null) {
            IMajorConstellation bestGuess = (IMajorConstellation) handle
                .getHighestDistributionConstellation(rand, (c) -> c instanceof IMajorConstellation);
            if (bestGuess != null && handle.getCurrentDistribution(bestGuess, (f) -> 1F) >= 0.8F
                && bestGuess.canDiscover(Minecraft.getMinecraft().thePlayer, ResearchManager.clientProgress)) {
                topFound = bestGuess;
                selectedYaw = (rand.nextFloat() * 360F) - 180F;
                selectedPitch = -90F + rand.nextFloat() * 25F;
            }
        }

        for (int i = 0; i < randomStars; i++) {
            usedStars.add(new StarPosition(offsetX + rand.nextFloat() * width, offsetY + rand.nextFloat() * height));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (!Minecraft.IS_RUNNING_ON_MAC) {
            KeyBinding.updateKeyBindState();
        }
        ClientUtils.grabMouseCursor();
        mc.inGameHasFocus = true;
    }

    @Override
    public void initGui() {
        super.initGui();

        if (!Minecraft.IS_RUNNING_ON_MAC) {
            KeyBinding.updateKeyBindState();
        }
        ClientUtils.grabMouseCursor();
        mc.inGameHasFocus = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        drawWHRect(textureGrid);
        TextureHelper.refreshTextureBindState();

        handleMouseMovement(partialTicks);

        World w = Minecraft.getMinecraft().theWorld;
        float pitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
        float transparency = 0F;
        if (pitch < -60F) {
            transparency = 1F;
        } else if (pitch < -10F) {
            transparency = (Math.abs(pitch) - 10F) / 50F;
            if (ConstellationSkyHandler.getInstance()
                .isNight(w)) {
                transparency *= transparency;
            }
        }
        boolean canSeeSky = canTelescopeSeeSky(w);

        if (usedStars.isEmpty()) {
            Optional<Long> currSeed = ConstellationSkyHandler.getInstance()
                .getSeedIfPresent(Minecraft.getMinecraft().theWorld);
            if (currSeed.isPresent()) {
                setupInitialStars(currSeed.get());

                zLevel -= 5;
                drawCellWithEffects(partialTicks, canSeeSky, transparency);
                zLevel += 5;
            }
        } else {
            zLevel -= 5;
            drawCellWithEffects(partialTicks, canSeeSky, transparency);
            zLevel += 5;
        }

        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void handleMouseMovement(float pticks) {
        boolean ctrl = isShiftKeyDown();

        if (grabCursor && !ctrl) {
            if (!Minecraft.IS_RUNNING_ON_MAC) {
                KeyBinding.updateKeyBindState();
            }
            ClientUtils.grabMouseCursor();
            Minecraft.getMinecraft().inGameHasFocus = true;
            grabCursor = false;
            clearLines();
        }
        if (!grabCursor && ctrl) {
            ClientUtils.ungrabMouseCursor();
            Minecraft.getMinecraft().inGameHasFocus = false;
            grabCursor = true;
        }

        if (!ctrl) {

            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = (float) this.mc.mouseHelper.deltaX * f1;
            float f3 = (float) this.mc.mouseHelper.deltaY * f1;
            int i = 1;

            if (this.mc.gameSettings.invertMouse) {
                i = -1;
            }

            float movementX;
            float movementY;
            EntityRenderer er = Minecraft.getMinecraft().entityRenderer;
            if (this.mc.gameSettings.smoothCamera) {
                er.smoothCamYaw += f2;
                er.smoothCamPitch += f3;
                float f4 = pticks - er.smoothCamPartialTicks;
                er.smoothCamPartialTicks = pticks;
                f2 = er.smoothCamFilterX * f4;
                f3 = er.smoothCamFilterY * f4;
                movementX = f2;
                movementY = f3 * i;
            } else {
                er.smoothCamYaw = 0.0F;
                er.smoothCamPitch = 0.0F;
                movementX = f2;
                movementY = f3 * i;
            }
            boolean nullify = this.mc.thePlayer.rotationPitch <= -89.99F && Math.abs(movementY) == movementY;
            this.mc.thePlayer.turn(movementX, movementY);
            if (nullify) movementY = 0;
            handleHandMovement(MathHelper.floor(movementX), MathHelper.floor(movementY));
        }
    }

    private void handleHandMovement(int changeX, int changeY) {
        int offsetX = 6, offsetY = 6;
        int width = guiWidth - 12, height = guiHeight - 12;

        for (StarPosition sl : usedStars) {
            sl.x -= changeX;
            sl.y += changeY;

            if (sl.x < offsetX) {
                sl.x += width;
            } else if (sl.x > (offsetX + width)) {
                sl.x -= width;
            }
            if (sl.y < offsetY) {
                sl.y += height;
            } else if (sl.y > (offsetY + height)) {
                sl.y -= height;
            }
        }
    }

    private void drawCellWithEffects(float partialTicks, boolean canSeeSky, float transparency) {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(Minecraft.getMinecraft().theWorld);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Optional<Long> seed = ConstellationSkyHandler.getInstance()
            .getSeedIfPresent(Minecraft.getMinecraft().theWorld);
        long s = 0;
        if (seed.isPresent()) {
            s = seed.get();
        }
        Random r = new Random(s * 31 + lastTracked * 31);

        drawnConstellation = null;
        drawnStars = null;

        if (handle != null) {
            IMajorConstellation bestGuess = (IMajorConstellation) handle
                .getHighestDistributionConstellation(r, (c) -> c instanceof IMajorConstellation);
            if ((topFound == null || !topFound.equals(bestGuess))
                && handle.getCurrentDistribution(bestGuess, (f) -> 1F) >= 0.8F) {
                topFound = bestGuess;
                selectedYaw = (r.nextFloat() * 360F) - 180F;
                selectedPitch = -90F + r.nextFloat() * 45F;
            }
        }

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        drawGridBackground(partialTicks, canSeeSky, transparency);

        if (canSeeSky) {
            int offsetX = guiLeft;
            int offsetZ = guiTop;
            zLevel += 1;
            Optional<Map<StarLocation, Rectangle>> stars = drawCellEffect(
                offsetX,
                offsetZ,
                getGuiWidth(),
                getGuiHeight(),
                partialTicks,
                transparency);
            zLevel -= 1;

            if (stars.isPresent()) {
                drawnConstellation = topFound;
                drawnStars = stars.get();
            }
        } else {
            abortDrawing();
            clearLines();
        }

        zLevel += 2;
        drawDrawnLines(r, partialTicks);
        zLevel -= 2;

    }

    private void drawDrawnLines(final Random r, final float pTicks) {
        if (!canStartDrawing()) {
            clearLines();
            abortDrawing();
            return;
        }

        float linebreadth = 2F;
        RenderConstellation.BrightnessFunction func = new RenderConstellation.BrightnessFunction() {

            @Override
            public float getBrightness() {
                return RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pTicks, 5 + r.nextInt(15));
            }
        };

        textureConnection.bind();

        for (int j = 0; j < 2; j++) {
            for (GuiTelescope.Line l : drawnLines) {
                drawLine(l.start, l.end, func, linebreadth, true);
            }

            if (start != null && end != null) {
                Point adjStart = new Point(start.x - guiLeft, start.y - guiTop);
                Point adjEnd = new Point(end.x - guiLeft, end.y - guiTop);
                drawLine(adjStart, adjEnd, func, linebreadth, false);
            }
        }
    }

    private void drawLine(Point start, Point end, RenderConstellation.BrightnessFunction func, float linebreadth,
                          boolean applyFunc) {
        Tessellator tes = Tessellator.instance;
        BufferBuilder vb = tes.getBuffer();

        float brightness;
        if (applyFunc) {
            brightness = func.getBrightness();
        } else {
            brightness = 1F;
        }
        float starBr = Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F);
        if (starBr <= 0.0F) {
            return;
        }
        brightness *= (starBr * 2);
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        GL11.glColor4f(brightness, brightness, brightness, brightness < 0 ? 0 : brightness);

        Vector3 fromStar = new Vector3(guiLeft + start.getX(), guiTop + start.getY(), zLevel);
        Vector3 toStar = new Vector3(guiLeft + end.getX(), guiTop + end.getY(), zLevel);

        Vector3 dir = toStar.clone()
            .subtract(fromStar);
        Vector3 degLot = dir.clone()
            .crossProduct(new Vector3(0, 0, 1))
            .normalize()
            .multiply(linebreadth);// .multiply(j == 0 ? 1 : -1);

        Vector3 vec00 = fromStar.clone()
            .add(degLot);
        Vector3 vecV = degLot.clone()
            .multiply(-2);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec00.clone()
                .add(
                    dir.clone()
                        .multiply(u))
                .add(
                    vecV.clone()
                        .multiply(v));
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                .tex(u, v)
                .endVertex();
        }

        tes.draw();
    }

    private Optional<Map<StarLocation, Rectangle>> drawCellEffect(int offsetX, int offsetY, int width, int height,
                                                                  float partialTicks, float transparency) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(Minecraft.getMinecraft().theWorld);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random();

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarPosition stars : usedStars) {
            r.setSeed(stars.seed);
            GL11.glPushMatrix();
            float brightness = 0.3F
                + (RenderConstellation.stdFlicker(ClientScheduler.getClientTick(), partialTicks, 10 + r.nextInt(20)))
                * 0.6F;
            brightness *= Minecraft.getMinecraft().theWorld.getStarBrightness(partialTicks) * 2 * transparency;
            brightness *= (1F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks));
            GL11.glColor4f(brightness, brightness, brightness, brightness);
            drawRect(MathHelper.floor(offsetX + stars.x), MathHelper.floor(offsetY + stars.y), 5, 5);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPopMatrix();
        }

        r.setSeed(lastTracked * 31);

        Map<StarLocation, Rectangle> rectangles = null;
        if (topFound != null) {
            zLevel += 1;

            float playerYaw = Minecraft.getMinecraft().thePlayer.rotationYaw % 360F;
            if (playerYaw < 0) {
                playerYaw += 360F;
            }
            if (playerYaw >= 180F) {
                playerYaw -= 360F;
            }
            float playerPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

            float diffYaw = playerYaw - selectedYaw;
            float diffPitch = playerPitch - selectedPitch;

            float sFactor = 35F;
            if ((Math.abs(diffYaw) <= sFactor || Math.abs(playerYaw + 360F) <= sFactor)
                && Math.abs(diffPitch) <= sFactor) {

                float rainBr = 1F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks);
                ScaledResolution res = new ScaledResolution(mc);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor(
                    (guiLeft + 5) * res.getScaleFactor(),
                    (guiTop + 5) * res.getScaleFactor(),
                    (guiWidth - 10) * res.getScaleFactor(),
                    (guiHeight - 10) * res.getScaleFactor());

                int wPart = ((int) (((float) width) * 0.1F));
                int hPart = ((int) (((float) height) * 0.1F));

                rectangles = RenderConstellation.renderConstellationIntoGUI(
                    topFound,
                    offsetX + wPart + MathHelper.floor((diffYaw / sFactor) * width),
                    offsetY + hPart + MathHelper.floor((diffPitch / sFactor) * height),
                    zLevel,
                    width - (((int) (wPart * 1.5F))),
                    height - (((int) (hPart * 1.5F))),
                    2,
                    new RenderConstellation.BrightnessFunction() {

                        @Override
                        public float getBrightness() {
                            return (0.3F + 0.7F * RenderConstellation
                                .conCFlicker(ClientScheduler.getClientTick(), partialTicks, 5 + r.nextInt(15)))
                                * transparency
                                * rainBr;
                        }
                    },
                    ResearchManager.clientProgress.hasConstellationDiscovered(topFound.getUnlocalizedName()),
                    true);

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            zLevel -= 1;
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
        return Optional.ofNullable(rectangles);
    }

    private void drawGridBackground(float partialTicks, boolean canSeeSky, float angleTransparency) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        Blending.PREALPHA.apply();
        Tuple<Color, Color> fromTo = GuiSkyScreen.getRBGFromTo(canSeeSky, angleTransparency, partialTicks);
        RenderingUtils.drawGradientRect(
            guiLeft + 4,
            guiTop + 4,
            zLevel,
            guiLeft + guiWidth - 4,
            guiTop + guiHeight - 4,
            fromTo.key,
            fromTo.value);
        Blending.DEFAULT.apply();
        GL11.glPopAttrib();
    }

    private boolean canTelescopeSeeSky(World renderWorld) {
        BlockPos pos = Minecraft.getMinecraft().thePlayer.getPosition();
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos other = pos.add(xx, 0, zz);
                if (!MiscUtils.canSeeSky(renderWorld, other, true, false)) {
                    return false;
                }
            }
        }
        return MiscUtils.canSeeSky(renderWorld, pos, true, false);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            tryStartDrawing(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) {
            informMovement(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            informRelease(mouseX, mouseY);
        }
    }

    private void tryStartDrawing(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        if (isInDrawingCell(mouseX, mouseY)) {
            start = new Point(mouseX, mouseY);
            end = new Point(mouseX, mouseY);
        } else {
            abortDrawing();
            clearLines();
        }
    }

    private boolean canStartDrawing() {
        return Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F) >= 0.35F
            && Minecraft.getMinecraft().theWorld.getRainStrength(1.0F) <= 0.1F
            && Minecraft.getMinecraft().thePlayer.rotationPitch <= -45F;
    }

    private void clearLines() {
        drawnLines.clear();
    }

    private boolean isInDrawingCell(int x, int y) {
        return rectDrawing.contains(x - guiLeft, y - guiTop);
    }

    private void informMovement(int mouseX, int mouseY) {
        if (!isInDrawingCell(mouseX, mouseY)) {
            abortDrawing();
            clearLines();
        } else {
            end = new Point(mouseX, mouseY);
        }
    }

    private void informRelease(int mouseX, int mouseY) {
        if (!isInDrawingCell(mouseX, mouseY)) {
            abortDrawing();
            clearLines();
        } else {
            if (start != null) {
                end = new Point(mouseX, mouseY);
                pushDrawnLine(start, end);
            } else {
                start = null;
                end = null;
            }
            abortDrawing();

            checkConstellation(drawnLines);
        }
    }

    private void checkConstellation(List<GuiTelescope.Line> drawnLines) {
        IConstellation c = drawnConstellation;
        if (c == null || ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) return;
        PlayerProgress client = ResearchManager.clientProgress;
        if (client == null) return;

        boolean has = false;
        for (String strConstellation : client.getSeenConstellations()) {
            IConstellation ce = ConstellationRegistry.getConstellationByName(strConstellation);
            if (ce != null && ce.equals(c)) {
                has = true;
                break;
            }
        }

        if (!has) return;

        List<StarConnection> sc = c.getStarConnections();
        if (sc.size() != drawnLines.size()) return; // Can't match otherwise anyway.
        if (!c.canDiscover(Minecraft.getMinecraft().thePlayer, ResearchManager.clientProgress)) return;

        for (StarConnection connection : sc) {
            Rectangle fromRect = drawnStars.get(connection.from);
            if (fromRect == null) {
                AstralSorcery.log.info("Could not check constellation of telescope drawing - starLocation is missing?");
                return;
            }
            Rectangle toRect = drawnStars.get(connection.to);
            if (toRect == null) {
                AstralSorcery.log.info("Could not check constellation of telescope drawing - starLocation is missing?");
                return;
            }
            if (!containsMatch(drawnLines, fromRect, toRect)) {
                return;
            }
        }

        // We found a match. horray.
        PacketChannel.CHANNEL.sendToServer(new PktDiscoverConstellation(c.getUnlocalizedName()));
        clearLines();
        abortDrawing();
    }

    private boolean containsMatch(List<GuiTelescope.Line> drawnLines, Rectangle r1, Rectangle r2) {
        for (GuiTelescope.Line l : drawnLines) {
            Point start = l.start;
            Point end = l.end;
            start = new Point(start.x + guiLeft, start.y + guiTop);
            end = new Point(end.x + guiLeft, end.y + guiTop);
            if ((r1.contains(start) && r2.contains(end)) || (r2.contains(start) && r1.contains(end))) {
                return true;
            }
        }
        return false;
    }

    private void pushDrawnLine(Point start, Point end) {
        if (Math.abs(start.getX() - end.getX()) <= 2 && Math.abs(start.getY() - end.getY()) <= 2) {
            return; // Rather a point than a line. probably not the users intention...
        }
        Point adjStart = new Point(start.x - guiLeft, start.y - guiTop);
        Point adjEnd = new Point(end.x - guiLeft, end.y - guiTop);
        GuiTelescope.Line l = new GuiTelescope.Line(adjStart, adjEnd);
        this.drawnLines.addLast(l);
    }

    private void abortDrawing() {
        start = null;
        end = null;
    }

    private static class StarPosition {

        private float x;
        private float y;
        private long seed = random.nextLong(); // Bad on performance i know i know.

        private StarPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}
