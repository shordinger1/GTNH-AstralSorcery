/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network;

import net.minecraft.world.World;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.client.ClientProxy;
import shordinger.astralsorcery.common.network.packet.ClientReplyPacket;
import shordinger.astralsorcery.common.network.packet.client.PktAttuneConstellation;
import shordinger.astralsorcery.common.network.packet.client.PktBurnParchment;
import shordinger.astralsorcery.common.network.packet.client.PktClearBlockStorageStack;
import shordinger.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import shordinger.astralsorcery.common.network.packet.client.PktElytraCapeState;
import shordinger.astralsorcery.common.network.packet.client.PktEngraveGlass;
import shordinger.astralsorcery.common.network.packet.client.PktPerkGemModification;
import shordinger.astralsorcery.common.network.packet.client.PktPlayerStatus;
import shordinger.astralsorcery.common.network.packet.client.PktRemoveKnowledgeFragment;
import shordinger.astralsorcery.common.network.packet.client.PktRequestPerkSealAction;
import shordinger.astralsorcery.common.network.packet.client.PktRequestSeed;
import shordinger.astralsorcery.common.network.packet.client.PktRequestSextantTarget;
import shordinger.astralsorcery.common.network.packet.client.PktRequestTeleport;
import shordinger.astralsorcery.common.network.packet.client.PktRotateTelescope;
import shordinger.astralsorcery.common.network.packet.client.PktSetSextantTarget;
import shordinger.astralsorcery.common.network.packet.client.PktUnlockPerk;
import shordinger.astralsorcery.common.network.packet.server.PktAttunementAltarState;
import shordinger.astralsorcery.common.network.packet.server.PktCraftingTableFix;
import shordinger.astralsorcery.common.network.packet.server.PktDualParticleEvent;
import shordinger.astralsorcery.common.network.packet.server.PktFinalizeLogin;
import shordinger.astralsorcery.common.network.packet.server.PktLightningEffect;
import shordinger.astralsorcery.common.network.packet.server.PktLiquidInteractionBurst;
import shordinger.astralsorcery.common.network.packet.server.PktOreScan;
import shordinger.astralsorcery.common.network.packet.server.PktParticleDataEvent;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.network.packet.server.PktPlayEffect;
import shordinger.astralsorcery.common.network.packet.server.PktPlayLiquidSpring;
import shordinger.astralsorcery.common.network.packet.server.PktProgressionUpdate;
import shordinger.astralsorcery.common.network.packet.server.PktShootEntity;
import shordinger.astralsorcery.common.network.packet.server.PktSyncCharge;
import shordinger.astralsorcery.common.network.packet.server.PktSyncData;
import shordinger.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import shordinger.astralsorcery.common.network.packet.server.PktSyncPerkActivity;
import shordinger.astralsorcery.common.network.packet.server.PktSyncStepAssist;
import shordinger.astralsorcery.common.network.packet.server.PktUpdateGateways;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PacketChannel
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:11
 */
public class PacketChannel {

    public static final SimpleNetworkWrapper CHANNEL = new SimpleNetworkWrapper(Tags.MODNAME) {

        @Override
        public void sendToServer(IMessage message) {
            if (message instanceof ClientReplyPacket && !PacketChannel.canBeSent()) {
                return;
            }
            super.sendToServer(message);
        }
    };

    @SideOnly(Side.CLIENT)
    private static boolean canBeSent() {
        return ClientProxy.connected;
    }

    public static void init() {
        int id = 0;

        // (server -> client)
        CHANNEL.registerMessage(PktSyncKnowledge.class, PktSyncKnowledge.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktSyncData.class, PktSyncData.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktParticleEvent.class, PktParticleEvent.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktCraftingTableFix.class, PktCraftingTableFix.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktProgressionUpdate.class, PktProgressionUpdate.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktPlayEffect.class, PktPlayEffect.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktRequestSeed.class, PktRequestSeed.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktUnlockPerk.class, PktUnlockPerk.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktAttunementAltarState.class, PktAttunementAltarState.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktRotateTelescope.class, PktRotateTelescope.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktLightningEffect.class, PktLightningEffect.class, id++, Side.CLIENT);
        // CHANNEL.registerMessage(PktSyncMinetweakerChanges.class, PktSyncMinetweakerChanges.class, id++, Side.CLIENT);
        // CHANNEL.registerMessage(PktSyncMinetweakerChanges.Compound.class, PktSyncMinetweakerChanges.Compound.class,
        // id++, Side.CLIENT);
        CHANNEL.registerMessage(PktDualParticleEvent.class, PktDualParticleEvent.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktOreScan.class, PktOreScan.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktSyncCharge.class, PktSyncCharge.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktSyncStepAssist.class, PktSyncStepAssist.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktUpdateGateways.class, PktUpdateGateways.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktBurnParchment.class, PktBurnParchment.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktParticleDataEvent.class, PktParticleDataEvent.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktShootEntity.class, PktShootEntity.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktLiquidInteractionBurst.class, PktLiquidInteractionBurst.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktPlayLiquidSpring.class, PktPlayLiquidSpring.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktFinalizeLogin.class, PktFinalizeLogin.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktRequestSextantTarget.class, PktRequestSextantTarget.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktSyncPerkActivity.class, PktSyncPerkActivity.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktRequestPerkSealAction.class, PktRequestPerkSealAction.class, id++, Side.CLIENT);

        // (client -> server)
        CHANNEL.registerMessage(PktDiscoverConstellation.class, PktDiscoverConstellation.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktRequestSeed.class, PktRequestSeed.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktUnlockPerk.class, PktUnlockPerk.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktAttunementAltarState.class, PktAttunementAltarState.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktAttuneConstellation.class, PktAttuneConstellation.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktRotateTelescope.class, PktRotateTelescope.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktRequestTeleport.class, PktRequestTeleport.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktBurnParchment.class, PktBurnParchment.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktEngraveGlass.class, PktEngraveGlass.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktElytraCapeState.class, PktElytraCapeState.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktClearBlockStorageStack.class, PktClearBlockStorageStack.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktSetSextantTarget.class, PktSetSextantTarget.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktRequestSextantTarget.class, PktRequestSextantTarget.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktRequestPerkSealAction.class, PktRequestPerkSealAction.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktRemoveKnowledgeFragment.class, PktRemoveKnowledgeFragment.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktPerkGemModification.class, PktPerkGemModification.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktPlayerStatus.class, PktPlayerStatus.class, id++, Side.SERVER);

        /*
         * Method registerPacket = ReflectionHelper.findMethod(
         * EnumConnectionState.class,
         * EnumConnectionState.PLAY,
         * new String[] { "registerPacket", "func_179245_a", "a" },
         * EnumPacketDirection.class, Class.class);
         * registerPacket.setAccessible(true);
         * try {
         * registerPacket.invoke(EnumConnectionState.HANDSHAKING, EnumPacketDirection.CLIENTBOUND,
         * PktWorldHandlerSyncEarly.class);
         * } catch (Exception e) {}
         */
    }

    public static NetworkRegistry.TargetPoint pointFromPos(World world, BlockPos pos, double range) {
        return new NetworkRegistry.TargetPoint(world.provider.dimensionId, pos.getX(), pos.getY(), pos.getZ(), range);
    }

}
