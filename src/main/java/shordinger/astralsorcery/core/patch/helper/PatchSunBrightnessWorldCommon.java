/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core.patch.helper;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import shordinger.astralsorcery.core.ClassPatch;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchSunBrightnessWorldCommon
 * Created by HellFirePvP
 * Date: 31.05.2018 / 15:28
 */
public class PatchSunBrightnessWorldCommon extends ClassPatch {

    public PatchSunBrightnessWorldCommon() {
        super("net.minecraft.world.World");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "getSunBrightnessFactor", "getSunBrightnessFactor", "(F)F");
        int index = peekFirstInstructionAfter(mn, 0, Opcodes.FRETURN);
        AbstractInsnNode fRet;
        while (index > 0 && (fRet = mn.instructions.get(index)) != null) {
            mn.instructions.insertBefore(fRet, new VarInsnNode(Opcodes.ALOAD, 0));
            mn.instructions.insertBefore(
                fRet,
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "shordinger/astralsorcery/common/event/listener/EventHandlerRedirect",
                    "getSunBrightnessFactorInj",
                    "(FLnet/minecraft/world/World;)F",
                    false));
            index = peekFirstInstructionAfter(mn, mn.instructions.indexOf(fRet) + 1, Opcodes.FRETURN);
        }
    }

}
