/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASMTransformationException
 * Created by HellFirePvP
 * Date: 11.08.2016 / 10:04
 */
public class ASMTransformationException extends RuntimeException {

    public ASMTransformationException() {
    }

    public ASMTransformationException(String message) {
        super(message);
    }

    public ASMTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ASMTransformationException(Throwable cause) {
        super(cause);
    }

    public ASMTransformationException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
