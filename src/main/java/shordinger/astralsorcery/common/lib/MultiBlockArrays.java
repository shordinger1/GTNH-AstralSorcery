/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.array.StructureBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiBlockArrays
 * Created by HellFirePvP
 * Date: 16.05.2016 / 15:45
 */
public class MultiBlockArrays {

    public static StructureBlockArray ancientShrine;

    public static StructureBlockArray desertShrine;

    public static StructureBlockArray smallShrine;

    public static StructureBlockArray treasureShrine;

    public static StructureBlockArray smallRuin;
    public static PatternBlockArray patternSmallRuin;

    @PasteBlacklist
    public static PatternBlockArray patternRitualPedestal;

    @PasteBlacklist
    public static PatternBlockArray patternRitualPedestalWithLink;

    public static PatternBlockArray patternAltarAttunement;

    public static PatternBlockArray patternAltarConstellation;

    public static PatternBlockArray patternAltarTrait;

    public static PatternBlockArray patternAttunementFrame;

    public static PatternBlockArray patternStarlightInfuser;

    public static PatternBlockArray patternCollectorRelay;

    public static PatternBlockArray patternCelestialGateway;

    public static PatternBlockArray patternFountain;

    @PasteBlacklist
    public static PatternBlockArray patternCollectorEnhancement;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface PasteBlacklist {
    }

}
