/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.advancements.instances;

import com.google.gson.JsonObject;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.wrapper.net.minecraft.advancements.critereon.AbstractCriterionInstance;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.util.JsonUtils;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelInstance
 * Created by HellFirePvP
 * Date: 27.10.2018 / 13:14
 */
public class PerkLevelInstance extends AbstractCriterionInstance {

    private int levelNeeded = 0;

    private PerkLevelInstance(ResourceLocation id) {
        super(id);
    }

    public static PerkLevelInstance deserialize(ResourceLocation id, JsonObject json) {
        PerkLevelInstance i = new PerkLevelInstance(id);
        i.levelNeeded = JsonUtils.getInt(json, "level");
        return i;
    }

    public boolean test(EntityPlayerMP player) {
        return ResearchManager.getProgress(player, Side.SERVER).getPerkLevel(player) >= this.levelNeeded;
    }

}
