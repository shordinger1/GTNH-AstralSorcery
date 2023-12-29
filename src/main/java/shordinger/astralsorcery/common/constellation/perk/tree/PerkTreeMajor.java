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

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.gui.perk.BatchPerkContext;
import shordinger.astralsorcery.client.gui.perk.PerkRenderGroup;
import shordinger.astralsorcery.client.gui.perk.group.PerkPointHaloRenderGroup;
import shordinger.astralsorcery.client.util.BufferBatch;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.MajorPerk;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BufferBuilder;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeMajor
 * Created by HellFirePvP
 * Date: 08.07.2018 / 23:40
 */
public class PerkTreeMajor<T extends MajorPerk> extends PerkTreePoint<T> {

    public PerkTreeMajor(T perk, Point offset) {
        super(perk, offset);
        this.setRenderSize((int) (this.getRenderSize() * 1.4));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups) {
        super.addGroups(groups);
        groups.add(PerkPointHaloRenderGroup.INSTANCE);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Rectangle.Double renderPerkAtBatch(BatchPerkContext drawCtx, AllocationStatus status, long spriteOffsetTick,
                                              float pTicks, double x, double y, double scale) {
        SpriteSheetResource tex = getHaloSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointHaloRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Double();
        }
        BufferBatch buf = drawCtx.getContext(grp);
        BufferBuilder vb = buf.getBuffer();

        double haloSize = getRenderSize() * 0.8 * scale;
        if (status == AllocationStatus.ALLOCATED) {
            haloSize *= 1.5;
        }

        Vector3 starVec = new Vector3(x - haloSize, y - haloSize, 0);

        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone()
                .addX(haloSize * u * 2)
                .addY(haloSize * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                .tex(frameUV.key + uLength * u, frameUV.value + vLength * v)
                .color(1F, 1F, 1F, 0.85F)
                .endVertex();
        }

        super.renderPerkAtBatch(drawCtx, status, spriteOffsetTick, pTicks, x, y, scale);

        double actualSize = getRenderSize() * scale;
        return new Rectangle.Double(-actualSize, -actualSize, actualSize * 2, actualSize * 2);
    }

}
