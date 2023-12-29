/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree;

import java.awt.*;
import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.gui.perk.BatchPerkContext;
import shordinger.astralsorcery.client.gui.perk.PerkRender;
import shordinger.astralsorcery.client.gui.perk.PerkRenderGroup;
import shordinger.astralsorcery.client.gui.perk.group.PerkPointRenderGroup;
import shordinger.astralsorcery.client.util.BufferBatch;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BufferBuilder;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreePoint
 * Created by HellFirePvP
 * Date: 17.06.2018 / 09:32
 */
public class PerkTreePoint<T extends AbstractPerk> implements PerkRender {

    private final Point offset;
    private final T perk;
    private int renderSize;

    private static final int spriteSize = 11;

    public PerkTreePoint(T perk, Point offset) {
        this.offset = offset;
        this.perk = perk;
        this.renderSize = spriteSize;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups) {
        groups.add(PerkPointRenderGroup.INSTANCE);
    }

    public void setRenderSize(int renderSize) {
        this.renderSize = renderSize;
    }

    public int getRenderSize() {
        return renderSize;
    }

    public T getPerk() {
        return perk;
    }

    public Point getOffset() {
        return offset;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Rectangle.Double renderPerkAtBatch(BatchPerkContext drawCtx, AllocationStatus status, long spriteOffsetTick,
                                              float pTicks, double x, double y, double scale) {
        SpriteSheetResource tex = getFlareSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Double();
        }
        BufferBatch buf = drawCtx.getContext(grp);
        BufferBuilder vb = buf.getBuffer();

        double size = renderSize * scale;
        Vector3 starVec = new Vector3(x - size, y - size, 0);

        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone()
                .addX(size * u * 2)
                .addY(size * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                .tex(frameUV.key + uLength * u, frameUV.value + vLength * v)
                .color(1F, 1F, 1F, 1F)
                .endVertex();
        }

        return new Rectangle.Double(-size, -size, size * 2, size * 2);
    }

    protected SpriteSheetResource getFlareSprite(AllocationStatus status) {
        SpriteSheetResource tex = switch (status) {
            case ALLOCATED -> SpriteLibrary.spritePerkActive;
            case UNLOCKABLE -> SpriteLibrary.spritePerkActivateable;
            default -> SpriteLibrary.spritePerkInactive;
        };
        return tex;
    }

    protected SpriteSheetResource getHaloSprite(AllocationStatus status) {
        SpriteSheetResource tex = switch (status) {
            case ALLOCATED -> SpriteLibrary.spriteHalo5;
            case UNLOCKABLE -> SpriteLibrary.spriteHalo6;
            default -> SpriteLibrary.spriteHalo4;
        };
        return tex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkTreePoint that = (PerkTreePoint) o;
        return Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset);
    }

    public static enum AllocationStatus {

        UNALLOCATED,
        ALLOCATED,
        UNLOCKABLE

    }

}
