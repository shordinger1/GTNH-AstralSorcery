/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.thaumcraft.perks;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.wrapper.net.minecraft.client.resources.I18n;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyPerkThaumcraft
 * Created by HellFirePvP
 * Date: 18.11.2018 / 22:28
 */
public class KeyPerkThaumcraft extends KeyPerk {

    public KeyPerkThaumcraft(String name, int x, int y) {
        super(name, x, y);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Collection<String> getSource() {
        return Lists.newArrayList(I18n.format("perk.astralsorcery.compat.thaumcraft"));
    }

}
