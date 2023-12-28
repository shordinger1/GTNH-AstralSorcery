/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.cape;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.cape.impl.*;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectAevitas;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectArmara;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectBootes;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectDiscidia;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectEvorsio;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectFornax;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectHorologium;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectLucerna;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectMineralis;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectOctans;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectVicio;
import shordinger.astralsorcery.common.data.config.Config;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectRegistry
 * Created by HellFirePvP
 * Date: 10.10.2017 / 00:46
 */
public class CapeEffectRegistry {

    private static Map<IConstellation, CapeEffectFactory<? extends CapeArmorEffect>> armorEffectMap = new HashMap<>();

    @Nullable
    public static CapeEffectFactory<? extends CapeArmorEffect> getArmorEffect(IConstellation cst) {
        return armorEffectMap.get(cst);
    }

    public static void registerCapeArmorEffect(IConstellation cst, Class<? extends CapeArmorEffect> armorEffectClass) {
        if (getArmorEffect(cst) != null) return;
        armorEffectMap.put(cst, new CapeEffectFactory<>(armorEffectClass));
    }

    public static void addDynamicConfigEntries() {
        Config.addDynamicEntry(new CapeEffectAevitas(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectDiscidia(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectEvorsio(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectArmara(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectVicio(new NBTTagCompound()));

        Config.addDynamicEntry(new CapeEffectLucerna(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectFornax(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectPelotrio(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectMineralis(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectOctans(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectHorologium(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectBootes(new NBTTagCompound()));
    }

}
