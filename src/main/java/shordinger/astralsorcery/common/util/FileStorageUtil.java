/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.io.File;

import javax.annotation.Nonnull;

import shordinger.astralsorcery.Tags;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FileStorageUtil
 * Created by HellFirePvP
 * Date: 23.01.2018 / 23:09
 */
public class FileStorageUtil {

    private FileStorageUtil() {
    }

    @Nonnull
    public static File getAstralSorceryDirectory() {
        File f = new File(System.getProperty("user.dir"), Tags.MODID);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    @Nonnull
    public static File getGeneralSubDirectory(String directoryName) {
        File f = new File(FileStorageUtil.getAstralSorceryDirectory(), directoryName);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

}
