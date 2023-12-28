/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.controller;

import java.awt.*;

import net.minecraft.client.Minecraft;

import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.IComplexEffect;
import shordinger.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.common.tile.IStructureAreaOfInfluence;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfluenceSizePreview
 * Created by HellFirePvP
 * Date: 26.08.2018 / 10:09
 */
public class InfluenceSizePreview implements IComplexEffect {

    private static final int MAX_LIFE = 40;
    private static final float alphaPerFade = 1F / MAX_LIFE;

    private boolean removed = false;
    private int fadeOutTicks = 1;
    private final IStructureAreaOfInfluence tile;

    private EntityFXFloatingCube cube1 = null, cube2 = null;

    public InfluenceSizePreview(IStructureAreaOfInfluence tile) {
        this.tile = tile;
    }

    public IStructureAreaOfInfluence getTile() {
        return tile;
    }

    @Override
    public boolean canRemove() {
        return fadeOutTicks <= 0 || Minecraft.getMinecraft().world == null
            || Minecraft.getMinecraft().world.provider.dimensionId != tile.getDimensionId();
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public void flagAsRemoved() {
        removed = true;
    }

    @Override
    public void clearRemoveFlag() {
        removed = false;
    }

    @Override
    public void tick() {
        if (needsFadeOut()) {
            this.fadeOutTicks = Math.max(0, this.fadeOutTicks - 1);
        } else {
            this.fadeOutTicks = Math.min(MAX_LIFE, this.fadeOutTicks + 1);
        }

        if (this.fadeOutTicks > 0 && this.tile != null) {
            BlockPos spawnAt = this.tile.getActualRenderOffsetPos();
            float radius = (float) this.tile.getRadius();
            radius *= 1.25F;
            if (spawnAt != null) {
                if (this.cube1 == null || this.cube1.isRemoved()) {
                    this.cube1 = setupCube(spawnAt, radius);
                }
                if (this.cube2 == null || this.cube2.isRemoved()) {
                    this.cube2 = setupCube(spawnAt, radius);
                }

                handleEffects(cube1, radius * 1.3F, this.fadeOutTicks * alphaPerFade * 0.2F);
                handleEffects(cube2, radius * 0.85F, this.fadeOutTicks * alphaPerFade * 0.2F);
            }
        }
    }

    protected boolean needsFadeOut() {
        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) {
            return true;
        }
        IStructureAreaOfInfluence aoe = EffectHandler.getInstance()
            .getCurrentActiveAOEView();
        if (aoe == null || !aoe.equals(this.tile)) {
            return true;
        }
        if (MiscUtils
            .getTileAt(Minecraft.getMinecraft().world, aoe.getLocationPos(), IStructureAreaOfInfluence.class, false)
            == null) {
            return true;
        }
        double rad;
        if (!this.tile.providesEffect() || (rad = this.tile.getRadius()) <= 0) {
            return true;
        }
        BlockPos offset = this.tile.getActualRenderOffsetPos();
        double dst = Minecraft.getMinecraft().player.getDistance(offset.getX(), offset.getY(), offset.getZ());
        if (dst <= 4) {
            return false;
        }
        return dst > 45 || dst > rad * 0.75;
    }

    private void handleEffects(EntityFXFloatingCube cube, float radius, float alpha) {
        cube.setTumbleIntensityMultiplier(0.06F);
        cube.setScale(radius);
        cube.setAlphaMultiplier(alpha);
        Color col = this.tile.getEffectRenderColor();
        if (col != null) {
            cube.setColorHandler((c) -> col.brighter());
        }
    }

    private EntityFXFloatingCube setupCube(BlockPos at, float radius) {
        EntityFXFloatingCube cube = new EntityFXFloatingCube(
            AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "core_edge"));
        cube.setRefreshFunction(() -> !isRemoved());

        cube.setAlphaMultiplier(0F);
        cube.setPosition(new Vector3(at).add(0.5, 0.5, 0.5));
        cube.setScale(radius);
        cube.setTumbleIntensityMultiplier(0.06F);
        cube.setBlendMode(Blending.DEFAULT);
        cube.tumble();
        EffectHandler.getInstance()
            .registerFX(cube);
        return cube;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void render(float pTicks) {
    }

}
