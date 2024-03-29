/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.cape.impl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.cape.CapeArmorEffect;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectDiscidia
 * Created by HellFirePvP
 * Date: 10.10.2017 / 20:03
 */
public class CapeEffectDiscidia extends CapeArmorEffect {

    private static float multiplierGained = 1F;

    public CapeEffectDiscidia(NBTTagCompound cmp) {
        super(cmp, "discidia");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.discidia;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        float chance = 0.3F;
        if (getLastAttackDamage() <= 0) {
            chance = 0.1F;
        }
        playConstellationCapeSparkles(pl, chance);
    }

    public void writeLastAttackDamage(float dmgIn) {
        getData().setFloat("lastAttack", dmgIn);
    }

    public float getLastAttackDamage() {
        return NBTHelper.getFloat(getData(), "lastAttack", 0) * multiplierGained;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        multiplierGained = cfg.getFloat(
            getKey() + "Multiplier",
            getConfigurationSection(),
            1F,
            0F,
            100F,
            "Sets the multiplier for how much of the received damage is converted into additional damage");
    }

}
