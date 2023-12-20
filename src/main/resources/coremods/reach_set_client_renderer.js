function initializeCoreMod() {
    return {
        'reach_set_client_renderer': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.GameRenderer',
                'methodName': 'func_78473_a',
                'methodDesc': '(F)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'reach_set_client_renderer\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');

                var extendedReachName = ASMAPI.mapMethod('func_78749_i');
                var extendedReach = ASMAPI.findFirstMethodCall(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/client/multiplayer/PlayerController',
                        extendedReachName,
                        '()Z');


                var setExtendedReach = extendedReach;
                do {
                    setExtendedReach = ASMAPI.findFirstInstructionAfter(method,
                        Opcodes.LDC,
                        method.instructions.indexOf(setExtendedReach));
                } while (setExtendedReach.cst.doubleValue() != 6.0);

                var prevSetExtendedReach = setExtendedReach.getPrevious();
                //method.instructions.remove(setExtendedReach);

                method.instructions.insert(setExtendedReach, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'getOverriddenCreativeEntityReach',
                    '(D)D',
                    ASMAPI.MethodType.STATIC));
                //method.instructions.insert(prevSetExtendedReach, new VarInsnNode(Opcodes.DLOAD, 3));

                ASMAPI.log('INFO', 'Added \'reach_set_client_renderer\' ASM patch!');
                return method;
            }
        }
    }
}