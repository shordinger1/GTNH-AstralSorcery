/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core;

import org.objectweb.asm.tree.ClassNode;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SubClassTransformer
 * Created by HellFirePvP
 * Date: 05.12.2016 / 16:50
 */
public interface SubClassTransformer {

    void transformClassNode(ClassNode cn, String transformedClassName, String obfName);

    String getIdentifier();

    void addErrorInformation();

    boolean isTransformRequired(String transformedClassName);

}
