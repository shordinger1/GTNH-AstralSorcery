/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base.render;

import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelResourceLocation;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: INBTModel
 * Created by HellFirePvP
 * Date: 01.11.2017 / 01:06
 */
public interface INBTModel {

    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation suggestedDefaultLocation);

    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation);

    @Nullable
    @SideOnly(Side.CLIENT)
    default public EntityPlayer getCurrentClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

}
