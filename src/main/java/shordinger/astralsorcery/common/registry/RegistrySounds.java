/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.util.SoundUtils;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.SoundEvent;

import static hellfirepvp.astralsorcery.common.lib.Sounds.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistrySounds
 * Created by HellFirePvP
 * Date: 06.12.2016 / 12:54
 */
public class RegistrySounds {

    public static void init() {
        clipSwitch = registerSound("clipSwitch", SoundCategory.BLOCKS);
        attunement = registerSound("attunement", SoundCategory.MASTER);
        craftFinish = registerSound("craftFinish", SoundCategory.BLOCKS);
        bookClose = registerSound("bookClose", SoundCategory.MASTER);
        bookFlip = registerSound("bookFlip", SoundCategory.MASTER);
    }

    /*private static <T extends SoundEvent> T registerSound(String jsonName, SoundCategory predefinedCategory) {
        ResourceLocation res = new ResourceLocation(AstralSorcery.MODID, jsonName);
        SoundUtils.LoopableSoundEvent se = new SoundUtils.LoopableSoundEvent(res, predefinedCategory);
        se.setRegistryName(res);
        return registerSound((T) se);
    }*/

    private static <T extends SoundEvent> T registerSound(String jsonName, SoundCategory predefinedCategory) {
        ResourceLocation res = new ResourceLocation(AstralSorcery.MODID, jsonName);
        SoundUtils.CategorizedSoundEvent se = new SoundUtils.CategorizedSoundEvent(res, predefinedCategory);
        se.setRegistryName(res);
        return registerSound((T) se);
    }

    private static <T extends SoundEvent> T registerSound(String jsonName) {
        ResourceLocation res = new ResourceLocation(AstralSorcery.MODID, jsonName);
        SoundEvent se = new SoundEvent(res);
        se.setRegistryName(res);
        return registerSound((T) se);
    }

    private static <T extends SoundEvent> T registerSound(T soundEvent) {
        CommonProxy.registryPrimer.register(soundEvent);
        return soundEvent;
    }

}
