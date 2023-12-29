/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei.base;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;

import com.google.common.collect.Lists;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.AstralSorcery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIBaseCategory
 * Created by HellFirePvP
 * Date: 15.02.2017 / 17:00
 */
public abstract class JEIBaseCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

    private final String locTitle, uid;

    public JEIBaseCategory(String unlocTitle, String uid) {
        this.locTitle = I18n.format(unlocTitle);
        this.uid = uid;
    }

    @Override
    public String getModName() {
        return AstralSorcery.NAME;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getTitle() {
        return locTitle;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

}
