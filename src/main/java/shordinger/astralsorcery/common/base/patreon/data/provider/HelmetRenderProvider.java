/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.data.provider;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.base.PtEffectHelmetRender;
import shordinger.astralsorcery.common.base.patreon.data.EffectProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: HelmetRenderProvider
 * Created by HellFirePvP
 * Date: 25.02.2019 / 20:36
 */
public class HelmetRenderProvider implements EffectProvider<PtEffectHelmetRender> {

    @Override
    public PtEffectHelmetRender buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID effectUniqueId = UUID.fromString(effectParameters.get(0));
        String[] itemInfo = effectParameters.get(1)
            .split(";");
        Item item = Item.getByNameOrId(itemInfo[0]);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item: " + itemInfo[0]);
        }
        int data = Integer.parseInt(itemInfo[1]);
        ItemStack stack = new ItemStack(item, 1, data);
        PatreonEffectHelper.FlareColor flColor = effectParameters.size() > 2
            ? PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(2))
            : null;
        return new PtEffectHelmetRender(effectUniqueId, flColor, uuid, stack);
    }

}
