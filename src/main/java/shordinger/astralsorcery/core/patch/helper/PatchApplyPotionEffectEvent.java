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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import shordinger.astralsorcery.core.ClassPatch;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchApplyPotionEffectEvent
 * Created by HellFirePvP
 * Date: 24.10.2018 / 21:25
 */
public class PatchApplyPotionEffectEvent extends ClassPatch {

    public PatchApplyPotionEffectEvent() {
        super("net.minecraft.entity.EntityLivingBase");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethodLazy(cn, "addPotionEffect", "func_70690_d");
        MethodInsnNode m = getFirstMethodCall(
            mn,
            "net/minecraft/entity/EntityLivingBase",
            "onNewPotionEffect",
            "func_70670_a",
            "(Lnet/minecraft/potion/PotionEffect;)V");
        mn.instructions.insert(
            m,
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "shordinger/astralsorcery/common/event/PotionApplyEvent",
                "fireNew",
                "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/PotionEffect;)V",
                false));
        mn.instructions.insert(m, new VarInsnNode(Opcodes.ALOAD, 1)); // passedPotionEffect
        mn.instructions.insert(m, new VarInsnNode(Opcodes.ALOAD, 0)); // thisEntity

        m = getFirstMethodCall(
            mn,
            "net/minecraft/entity/EntityLivingBase",
            "onChangedPotionEffect",
            "func_70695_b",
            "(Lnet/minecraft/potion/PotionEffect;Z)V");
        mn.instructions.insert(
            m,
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "shordinger/astralsorcery/common/event/PotionApplyEvent",
                "fireChanged",
                "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/PotionEffect;Lnet/minecraft/potion/PotionEffect;)V",
                false));
        mn.instructions.insert(m, new VarInsnNode(Opcodes.ALOAD, 2)); // combinedPotionEffect
        mn.instructions.insert(m, new VarInsnNode(Opcodes.ALOAD, 1)); // passedPotionEffect
        mn.instructions.insert(m, new VarInsnNode(Opcodes.ALOAD, 0)); // thisEntity

    }

}
