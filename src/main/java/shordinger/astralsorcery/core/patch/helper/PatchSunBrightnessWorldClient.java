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

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.core.ClassPatch;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchSunBrightnessWorldClient
 * Created by HellFirePvP
 * Date: 31.05.2018 / 15:45
 */
public class PatchSunBrightnessWorldClient extends ClassPatch {

    public PatchSunBrightnessWorldClient() {
        super("net.minecraft.world.World");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "getSunBrightnessBody", "getSunBrightnessBody", "(F)F");
        int index = peekFirstInstructionAfter(mn, 0, Opcodes.FRETURN);
        AbstractInsnNode fRet;
        while (index > 0 && (fRet = mn.instructions.get(index)) != null) {
            mn.instructions.insertBefore(fRet, new VarInsnNode(Opcodes.ALOAD, 0));
            mn.instructions.insertBefore(
                fRet,
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "shordinger/astralsorcery/common/event/listener/EventHandlerRedirect",
                    "getSunBrightnessBodyInj",
                    "(FLnet/minecraft/world/World;)F",
                    false));
            index = peekFirstInstructionAfter(mn, mn.instructions.indexOf(fRet) + 1, Opcodes.FRETURN);
        }
    }

    @Override
    public boolean canExecuteForSide(Side side) {
        return side == Side.CLIENT;
    }
}
