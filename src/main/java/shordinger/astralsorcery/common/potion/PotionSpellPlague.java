/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.potion;

import java.util.Collections;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionSpellPlague
 * Created by HellFirePvP
 * Date: 07.07.2017 / 10:51
 */
public class PotionSpellPlague extends PotionCustomTexture {

    private static Object texBuffer = null;

    public PotionSpellPlague() {
        super(true, 0x680190);
        setPotionName("effect.as.spellplague");
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        // if(entityLivingBaseIn.hasCapability(SpellPlague.CAPABILITY_SPELL_PLAGUE, null)) {
        // SpellPlague plague = entityLivingBaseIn.getCapability(SpellPlague.CAPABILITY_SPELL_PLAGUE, null);
        // if(plague != null){
        // plague.onTick(entityLivingBaseIn);
        // PotionApplyEvent pe = entityLivingBaseIn.getActivePotionEffect(this);
        // if(pe != null) {
        // if(plague.onTick(entityLivingBaseIn)) {
        // pe.duration = 1;
        // } else {
        // pe.duration = 100000;
        // }
        // }
        // }
        // }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if (texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_spellplague");
        }
        return (BindableResource) texBuffer;
    }

}
