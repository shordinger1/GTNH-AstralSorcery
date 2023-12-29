/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core.patch.helper;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import shordinger.astralsorcery.core.ClassPatch;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchAddPlayerAttribute
 * Created by HellFirePvP
 * Date: 19.11.2018 / 16:25
 */
public class PatchAddPlayerAttribute extends ClassPatch {

    public PatchAddPlayerAttribute() {
        super("net.minecraft.entity.ai.attributes.AbstractAttributeMap");
    }

    @Override
    public void patch(ClassNode cn) {
        FieldNode entityField = new FieldNode(
            Opcodes.ACC_PUBLIC,
            "as_entity",
            "Lnet/minecraft/entity/EntityLivingBase;",
            "",
            null);
        cn.fields.add(entityField);
    }

}
