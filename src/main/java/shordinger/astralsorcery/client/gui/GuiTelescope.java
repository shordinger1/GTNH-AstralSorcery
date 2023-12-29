/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.data.KnowledgeFragmentData;
import shordinger.astralsorcery.client.data.PersistentDataManager;
import shordinger.astralsorcery.client.gui.base.GuiSkyScreen;
import shordinger.astralsorcery.client.gui.base.GuiTileBase;
import shordinger.astralsorcery.client.sky.RenderAstralSkybox;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.ClientConstellationGenerator;
import shordinger.astralsorcery.client.util.RenderConstellation;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.constellation.MoonPhase;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.constellation.star.StarConnection;
import shordinger.astralsorcery.common.constellation.star.StarLocation;
import shordinger.astralsorcery.common.data.fragment.KnowledgeFragment;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.knowledge.ItemKnowledgeFragment;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import shordinger.astralsorcery.common.network.packet.client.PktRotateTelescope;
import shordinger.astralsorcery.common.tile.TileTelescope;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.BufferBuilder;
import shordinger.astralsorcery.migration.MathHelper;
import shordinger.astralsorcery.migration.TextComponentString;
import shordinger.astralsorcery.migration.TextFormatting;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiTelescope
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:51
 */
public class GuiTelescope extends GuiTileBase<TileTelescope> implements GuiSkyScreen {

    private static final BindableResource texArrow = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");
    private static final BindableResource textureGrid = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "gridtelescope");
    private static final BindableResource textureConnection = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");

    private final EntityPlayer owningPlayer;
    private final TileTelescope guiOwner;
    private TileTelescope.TelescopeRotation rotation;

    private Rectangle rectArrowCW = null, rectArrowCCW = null;

    private SkyConstellationDistribution currentInformation = null;

    private LinkedList<Line> drawnLines = new LinkedList<>();
    private Point start, end;

    public GuiTelescope(EntityPlayer player, TileTelescope e) {
        super(e, 280, 280);
        this.owningPlayer = player;
        this.guiOwner = e;
        this.rotation = e.getRotation();

        setupConstellations();
    }

    private void setupConstellations() {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(guiOwner.getWorld());
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random(
            guiOwner.getWorld()
                .getSeed() * 31 + lastTracked * 31);

        currentInformation = new SkyConstellationDistribution();

        for (TileTelescope.TelescopeRotation rot : TileTelescope.TelescopeRotation.values()) {
            currentInformation.informationMap.put(rot, new RotationConstellationInformation());
        }
        if (handle != null) {
            List<IWeakConstellation> weakConstellations = new LinkedList<>();
            for (IConstellation c : handle.getActiveConstellations()) {
                if (c instanceof IWeakConstellation
                    && c.canDiscover(Minecraft.getMinecraft().thePlayer, ResearchManager.clientProgress)) {
                    weakConstellations.add((IWeakConstellation) c);
                }
            }
            weakConstellations = weakConstellations.subList(0, Math.min(8, weakConstellations.size()));
            for (IWeakConstellation cst : weakConstellations) {
                Tuple<Point, TileTelescope.TelescopeRotation> foundPoint;
                do {
                    foundPoint = findEmptyPlace(r);
                } while (foundPoint == null);
                currentInformation.informationMap.get(foundPoint.value).constellations.put(foundPoint.key, cst);
            }

            List<ItemStack> fragmentStacks = ItemKnowledgeFragment.gatherFragments(owningPlayer);
            List<KnowledgeFragment> fragList = new LinkedList<>();
            for (ItemStack item : fragmentStacks) {
                KnowledgeFragment frag = ItemKnowledgeFragment.resolveFragment(item);
                Optional<Long> seedOpt = ItemKnowledgeFragment.getSeed(item);
                if (seedOpt.isPresent() && frag != null && !fragList.contains(frag)) {
                    fragList.add(frag);

                    IConstellation cst = frag.getDiscoverConstellation(seedOpt.get());
                    List<MoonPhase> phases = frag.getShowupPhases(seedOpt.get());
                    if (cst != null && phases.contains(handle.getCurrentMoonPhase())) {
                        int attempts = 100;
                        Tuple<Point, TileTelescope.TelescopeRotation> foundPoint;
                        do {
                            foundPoint = findEmptyPlace(r);
                            attempts--;
                        } while (foundPoint == null && attempts > 0);
                        if (foundPoint != null) {
                            currentInformation.informationMap.get(foundPoint.value).constellations
                                .put(foundPoint.key, cst);
                        }
                    }
                }
            }
        }
    }

    private Tuple<Point, TileTelescope.TelescopeRotation> findEmptyPlace(Random rand) {
        TileTelescope.TelescopeRotation rot = TileTelescope.TelescopeRotation.values()[rand
            .nextInt(TileTelescope.TelescopeRotation.values().length)];
        RotationConstellationInformation info = currentInformation.informationMap.get(rot);
        int wh = ((int) SkyConstellationDistribution.constellationWH);
        int wdh = guiWidth - 6 - wh;
        int hgt = guiHeight - 6 - wh;
        int rX = 6 + rand.nextInt(wdh);
        int rY = 6 + rand.nextInt(hgt);
        Rectangle constellationRect = new Rectangle(rX, rY, wh, wh);
        for (Point p : info.constellations.keySet()) {
            Rectangle otherRect = new Rectangle(p.x, p.y, wh, wh);
            if (otherRect.intersects(constellationRect)) {
                return null;
            }
        }
        return new Tuple<>(new Point(rX, rY), rot);
    }

    public void handleRotationChange(boolean isClockwise) {
        rotation = isClockwise ? rotation.nextClockWise() : rotation.nextCounterClockWise();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        drawWHRect(textureGrid);
        TextureHelper.refreshTextureBindState();

        zLevel -= 5;
        drawCellsWithEffects(partialTicks);
        zLevel += 5;

        Point mouse = new Point(mouseX, mouseY);
        drawRotationArrows(partialTicks, mouse);

        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawRotationArrows(float partialTicks, Point mouse) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        rectArrowCW = null;
        rectArrowCCW = null;

        int width = 30;
        int height = 15;
        rectArrowCCW = new Rectangle(guiLeft - 40, guiTop + (guiHeight / 2), width, height);
        GL11.glPushMatrix();
        GL11.glTranslated(rectArrowCCW.getX() + (width / 2), rectArrowCCW.getY() + (height / 2), 0);
        float uFrom = 0F, vFrom = 0.5F;
        if (rectArrowCCW.contains(mouse)) {
            uFrom = 0.5F;
            GL11.glScaled(1.1, 1.1, 1.1);
        } else {
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GL11.glScaled(sin, sin, sin);
        }
        GL11.glColor4f(1F, 1F, 1F, 0.8F);
        GL11.glTranslated(-(width / 2), -(height / 2), 0);
        texArrow.bind();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GL11.glPopMatrix();

        rectArrowCW = new Rectangle(guiLeft + guiWidth + 10, guiTop + (guiHeight / 2), width, height);
        GL11.glPushMatrix();
        GL11.glTranslated(rectArrowCW.getX() + (width / 2), rectArrowCW.getY() + (height / 2), 0);
        uFrom = 0F;
        vFrom = 0F;
        if (rectArrowCW.contains(mouse)) {
            uFrom = 0.5F;
            GL11.glScaled(1.1, 1.1, 1.1);
        } else {
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GL11.glScaled(sin, sin, sin);
        }
        GL11.glColor4f(1F, 1F, 1F, 0.8F);
        GL11.glTranslated(-(width / 2), -(height / 2), 0);
        texArrow.bind();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawCellsWithEffects(float partialTicks) {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(guiOwner.getWorld());
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random(
            guiOwner.getWorldObj()
                .getSeed() * 31 + lastTracked * 31L
                + rotation.ordinal());
        World world = Minecraft.getMinecraft().theWorld;
        boolean canSeeSky = canTelescopeSeeSky(world);

        /*
         * if(handle != null) {
         * LinkedList<IConstellation> active = handle.getSortedActiveConstellations();
         * PlayerProgress prog = ResearchManager.clientProgress;
         * Iterator<IConstellation> iterator = active.iterator();
         * while (iterator.hasNext()) {
         * IConstellation c = iterator.next();
         * if(!(c instanceof IWeakConstellation)) {
         * iterator.remove();
         * continue;
         * }
         * if(!c.canDiscover(prog)) {
         * iterator.remove();
         * continue;
         * }
         * if(handle.getCurrentDistribution((IWeakConstellation) c, (f) -> f) <= 0.5F) {
         * iterator.remove();
         * }
         * }
         * if(active.size() <= 8) {
         * active.toArray(constellations);
         * } else {
         * active.subList(0, 8).toArray(constellations);
         * }
         * }
         */

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        drawGridBackground(partialTicks, canSeeSky);

        if (handle != null && canSeeSky) {
            zLevel += 1;
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();

            RenderAstralSkybox.TEX_STAR_1.bind();
            float starSize = 2.5F;
            for (int i = 0; i < 72 + r.nextInt(144); i++) {
                float innerOffsetX = starSize + r.nextInt(MathHelper.floor(guiWidth - starSize));
                float innerOffsetY = starSize + r.nextInt(MathHelper.floor(guiHeight - starSize));
                float brightness = 0.3F + (RenderConstellation
                    .stdFlicker(ClientScheduler.getClientTick(), partialTicks, 10 + r.nextInt(20))) * 0.6F;
                brightness *= Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F) * 2;
                brightness *= (1F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks));
                GL11.glColor4f(brightness, brightness, brightness, brightness);
                drawRectDetailed(
                    guiLeft + innerOffsetX - starSize,
                    guiTop + innerOffsetY - starSize,
                    starSize * 2,
                    starSize * 2);
                GL11.glColor4f(1, 1, 1, 1);
            }
            zLevel -= 1;

            RotationConstellationInformation info = currentInformation.informationMap.get(rotation);
            if (info != null) {
                currentInformation.informationMap.get(rotation).informations.clear();
                for (Map.Entry<Point, IConstellation> entry : info.constellations.entrySet()) {

                    float rainBr = 1F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks);
                    float widthHeight = SkyConstellationDistribution.constellationWH;

                    Point offset = entry.getKey();

                    Map<StarLocation, Rectangle> rectangles = RenderConstellation.renderConstellationIntoGUI(
                        entry.getValue(),
                        offset.x + guiLeft,
                        offset.y + guiTop,
                        zLevel,
                        ((int) widthHeight),
                        ((int) widthHeight),
                        2.5,
                        new RenderConstellation.BrightnessFunction() {

                            @Override
                            public float getBrightness() {
                                return RenderConstellation
                                    .conCFlicker(ClientScheduler.getClientTick(), partialTicks, 5 + r.nextInt(15))
                                    * rainBr;
                            }
                        },
                        ResearchManager.clientProgress.hasConstellationDiscovered(
                            entry.getValue()
                                .getUnlocalizedName()),
                        true);

                    currentInformation.informationMap.get(rotation).informations
                        .add(new ConstellationInformation(rectangles, entry.getValue()));
                }
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        zLevel += 2;
        drawDrawnLines(r, partialTicks);
        zLevel -= 2;

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
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
            for (Line l : drawnLines) {
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

    private void drawGridBackground(float partialTicks, boolean canSeeSky) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        Blending.PREALPHA.apply();
        Tuple<Color, Color> fromTo = GuiSkyScreen.getRBGFromTo(canSeeSky, 1F, partialTicks);
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
        BlockPos pos = guiOwner.getPos();
        /*
         * int height = 1;
         * IBlockState up = renderWorld.getBlockState(pos.up());
         * if(up.getBlock().equals(BlocksAS.blockStructural) &&
         * up.getValue(BlockStructural.BLOCK_TYPE).equals(BlockStructural.BlockType.TELESCOPE_STRUCT)) {
         * height += 1;
         * }
         */

        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos other = pos.add(xx, 0, zz);
                if (!MiscUtils.canSeeSky(renderWorld, other, true, false)) {
                    return false;
                }
            }
        }
        return MiscUtils.canSeeSky(renderWorld, pos.up(), true, false);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            tryStartDrawing(mouseX, mouseY);
        }

        if (mouseX <= guiLeft || mouseX >= guiLeft + guiWidth || mouseY <= guiTop || mouseY >= guiTop + guiHeight) {
            clearLines();
            abortDrawing();
        }

        Point p = new Point(mouseX, mouseY);
        if (rectArrowCW != null && rectArrowCW.contains(p)) {
            PktRotateTelescope pkt = new PktRotateTelescope(
                true,
                guiOwner.getWorld().provider.dimensionId,
                guiOwner.getPos());
            PacketChannel.CHANNEL.sendToServer(pkt);
            return;
        }
        if (rectArrowCCW != null && rectArrowCCW.contains(p)) {
            PktRotateTelescope pkt = new PktRotateTelescope(
                false,
                guiOwner.getWorld().provider.dimensionId,
                guiOwner.getPos());
            PacketChannel.CHANNEL.sendToServer(pkt);
        }

    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) {
            informMovement(mouseX, mouseY);
        }

        if (mouseX <= guiLeft || mouseX >= guiLeft + guiWidth || mouseY <= guiTop || mouseY >= guiTop + guiHeight) {
            clearLines();
            abortDrawing();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            informRelease(mouseX, mouseY);
        }

        if (mouseX <= guiLeft || mouseX >= guiLeft + guiWidth || mouseY <= guiTop || mouseY >= guiTop + guiHeight) {
            clearLines();
            abortDrawing();
        }
    }

    private void tryStartDrawing(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        start = new Point(mouseX, mouseY);
        end = new Point(mouseX, mouseY);
        /*
         * int cell = findCurrentCell(mouseX, mouseY);
         * if (currentLinesCell == -1) {
         * if (cell != -1) {
         * currentDrawCell = cell;
         * currentLinesCell = cell;
         * start = new Point(mouseX, mouseY);
         * end = new Point(mouseX, mouseY); //We want 2 different objects here though.
         * }
         * } else {
         * if (cell != currentLinesCell) {
         * abortDrawing();
         * clearLines();
         * start = new Point(mouseX, mouseY);
         * end = new Point(mouseX, mouseY); //We want 2 different objects here though.
         * } else {
         * start = new Point(mouseX, mouseY);
         * end = new Point(mouseX, mouseY); //We want 2 different objects here though.
         * }
         * }
         */
    }

    private boolean canStartDrawing() {
        return Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F) >= 0.35F;
    }

    private void clearLines() {
        drawnLines.clear();
    }

    /*
     * private int findCurrentCell(int x, int y) {
     * CellRenderInformation current = currentInformation;
     * for (Rectangle r : current.cells.keySet()) {
     * if (r.contains(x, y)) {
     * return current.cells.get(r);
     * }
     * }
     * return -1;
     * }
     */

    private void informMovement(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        end = new Point(mouseX, mouseY);
    }

    private void informRelease(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        if (start == null) return;

        end = new Point(mouseX, mouseY);
        pushDrawnLine(start, end);
        abortDrawing();

        checkConstellation(drawnLines);
    }

    private void checkConstellation(List<Line> drawnLines) {
        RotationConstellationInformation infos = currentInformation.informationMap.get(rotation);
        if (infos == null) return;
        List<ConstellationInformation> renderInfos = infos.informations;
        if (renderInfos.isEmpty()) return;

        lblInfos:
        for (ConstellationInformation info : renderInfos) {
            IConstellation c = info.constellation;
            if (c == null || ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName()))
                continue;
            PlayerProgress client = ResearchManager.clientProgress;
            if (client == null) return;

            boolean has = c instanceof ClientConstellationGenerator.ClientConstellation;
            for (String strConstellation : client.getSeenConstellations()) {
                IConstellation ce = ConstellationRegistry.getConstellationByName(strConstellation);
                if (ce != null && ce.equals(c)) {
                    has = true;
                    break;
                }
            }

            if (!has) continue;

            List<StarConnection> sc = c.getStarConnections();
            if (sc.size() != drawnLines.size()) continue; // Can't match otherwise anyway.
            if (!c.canDiscover(Minecraft.getMinecraft().thePlayer, ResearchManager.clientProgress)) continue;

            Map<StarLocation, Rectangle> stars = info.starRectangles;

            for (StarConnection connection : sc) {
                Rectangle fromRect = stars.get(connection.from);
                if (fromRect == null) {
                    AstralSorcery.log
                        .info("Could not check constellation of telescope drawing - starLocation is missing?");
                    continue lblInfos;
                }
                Rectangle toRect = stars.get(connection.to);
                if (toRect == null) {
                    AstralSorcery.log
                        .info("Could not check constellation of telescope drawing - starLocation is missing?");
                    continue lblInfos;
                }
                if (!containsMatch(drawnLines, fromRect, toRect)) {
                    continue lblInfos;
                }
            }

            // Don't sync mock constellations to server.
            if (c instanceof ClientConstellationGenerator.ClientConstellation) {
                KnowledgeFragment frag = ((ClientConstellationGenerator.ClientConstellation) c).getFragment();
                if (frag != null) {
                    ItemKnowledgeFragment.clearFragment(owningPlayer, frag);
                    KnowledgeFragmentData dat = PersistentDataManager.INSTANCE
                        .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
                    if (dat.addFragment(frag)) {
                        String cName = c.getUnlocalizedName();
                        cName = cName.isEmpty() ? "" : Character.toUpperCase(cName.charAt(0)) + cName.substring(1);
                        owningPlayer.sendMessage(
                            new TextComponentString(
                                TextFormatting.GREEN + I18n.format("misc.fragment.added.cst", cName)));
                        owningPlayer.sendMessage(
                            new TextComponentString(
                                TextFormatting.GREEN
                                    + I18n.format("misc.fragment.added", frag.getLocalizedIndexName())));
                    }

                    for (Map.Entry<Point, IConstellation> cstInfos : infos.constellations.entrySet()) {
                        if (cstInfos.getValue()
                            .equals(c)) {
                            infos.constellations.remove(cstInfos.getKey());
                        }
                    }
                }
            } else {
                // We found a match. horray.
                PacketChannel.CHANNEL.sendToServer(new PktDiscoverConstellation(c.getUnlocalizedName()));
            }
            clearLines();
            abortDrawing();
            return;
        }

    }

    private boolean containsMatch(List<Line> drawnLines, Rectangle r1, Rectangle r2) {
        for (Line l : drawnLines) {
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
        Line l = new Line(adjStart, adjEnd);
        this.drawnLines.addLast(l);
    }

    private void abortDrawing() {
        start = null;
        end = null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static class SkyConstellationDistribution {

        private static final float constellationWH = 150;
        private final Map<TileTelescope.TelescopeRotation, RotationConstellationInformation> informationMap = new HashMap<>();

    }

    public static class RotationConstellationInformation {

        private final List<ConstellationInformation> informations = new LinkedList<>();
        private final Map<Point, IConstellation> constellations = new HashMap<>();

    }

    public static class ConstellationInformation {

        private final Map<StarLocation, Rectangle> starRectangles;
        private final IConstellation constellation;

        public ConstellationInformation(Map<StarLocation, Rectangle> starRectangles, IConstellation c) {
            this.starRectangles = starRectangles;
            this.constellation = c;
        }
    }

    public static class Line {

        public final Point start, end;

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

}
