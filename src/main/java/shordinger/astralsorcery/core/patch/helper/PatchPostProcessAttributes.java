/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core.patch.helper;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import shordinger.astralsorcery.core.ClassPatch;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchPostProcessAttributes
 * Created by HellFirePvP
 * Date: 17.11.2018 / 10:12
 */
public class PatchPostProcessAttributes extends ClassPatch {

    public PatchPostProcessAttributes() {
        super("net.minecraft.entity.ai.attributes.ModifiableAttributeInstance");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethodLazy(cn, "computeValue", "func_111129_g");
        int instr = peekFirstInstructionAfter(mn, 0, Opcodes.DRETURN);
        while (instr != -1) {
            AbstractInsnNode ain = mn.instructions.get(instr)
                .getPrevious();
            mn.instructions.insert(
                ain,
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "shordinger/astralsorcery/common/event/AttributeEvent",
                    "postProcessVanilla",
                    "(DLnet/minecraft/entity/ai/attributes/ModifiableAttributeInstance;)D",
                    false));
            mn.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 0));

            instr = peekFirstInstructionAfter(mn, instr + 3, Opcodes.DRETURN);
        }
    }

}
