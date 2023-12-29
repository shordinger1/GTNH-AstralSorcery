/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.perk;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.util.resource.AbstractRenderableTexture;
import shordinger.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkRenderGroup
 * Created by HellFirePvP
 * Date: 24.11.2018 / 11:44
 */
@SideOnly(Side.CLIENT)
public class PerkRenderGroup {

    private static int counter = 0;
    private final int id;

    private List<BatchPerkContext.TextureObjectGroup> addedGroups = Lists.newArrayList();
    private Map<AbstractRenderableTexture, Integer> underlyingTextures = Maps.newHashMap();

    public PerkRenderGroup() {
        this.id = counter++;
    }

    public void add(AbstractRenderableTexture texture, Integer priority) {
        this.underlyingTextures.put(texture, priority);
    }

    public void batchRegister(BatchPerkContext ctx) {
        for (AbstractRenderableTexture tex : underlyingTextures.keySet()) {
            addedGroups.add(ctx.addContext(tex, underlyingTextures.get(tex)));
        }
    }

    @Nullable
    public BatchPerkContext.TextureObjectGroup getGroup(AbstractRenderableTexture texture) {
        return MiscUtils.iterativeSearch(
            addedGroups,
            grp -> grp.getResource()
                .equals(texture));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkRenderGroup that = (PerkRenderGroup) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
