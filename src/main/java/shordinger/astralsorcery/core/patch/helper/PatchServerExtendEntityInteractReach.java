/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core.patch.helper;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import shordinger.astralsorcery.core.ClassPatch;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchServerExtendEntityInteractReach
 * Created by HellFirePvP
 * Date: 23.08.2018 / 08:44
 */
public class PatchServerExtendEntityInteractReach extends ClassPatch {

    public PatchServerExtendEntityInteractReach() {
        super("net.minecraft.network.NetHandlerPlayServer");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethodLazy(cn, "processUseEntity", "func_147340_a");
        int overwrite = peekFirstInstructionAfter(
            mn,
            0,
            (a) -> a instanceof LdcInsnNode && ((LdcInsnNode) a).cst instanceof Number
                && Math.abs(((Number) ((LdcInsnNode) a).cst).doubleValue() - 36.0D) <= 0.01D);
        if (overwrite != -1) {
            AbstractInsnNode node = mn.instructions.get(overwrite);
            AbstractInsnNode prev = node.getPrevious();
            mn.instructions.remove(node);
            mn.instructions.insert(prev, new LdcInsnNode(new Double(Integer.MAX_VALUE))); // Sure....
        }
    }

}
