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
 * Class: PatchEntityLivingBaseWaterSlowDown
 * Created by HellFirePvP
 * Date: 22.09.2018 / 10:17
 */
public class PatchEntityLivingBaseWaterSlowDown extends ClassPatch {

    public PatchEntityLivingBaseWaterSlowDown() {
        super("net.minecraft.entity.EntityLivingBase");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "getWaterSlowDown", "func_189749_co", "()F");
        int index = 0;
        while ((index = peekFirstInstructionAfter(mn, index, Opcodes.FRETURN)) != -1) {
            AbstractInsnNode fRet = mn.instructions.get(index);
            mn.instructions.insertBefore(fRet, new VarInsnNode(Opcodes.ALOAD, 0)); // thisEntity
            mn.instructions.insertBefore(
                fRet,
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "shordinger/astralsorcery/common/event/listener/EventHandlerCapeEffects",
                    "getWaterSlowDown",
                    "(FLnet/minecraft/entity/EntityLivingBase;)F",
                    false));
            index = mn.instructions.indexOf(fRet) + 1;
        }
    }
}
