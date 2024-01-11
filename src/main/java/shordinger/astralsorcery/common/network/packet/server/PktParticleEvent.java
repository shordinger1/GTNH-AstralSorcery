/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.block.BlockCustomOre;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectEvorsio;
import shordinger.astralsorcery.common.constellation.effect.aoe.*;
import shordinger.astralsorcery.common.entities.EntityFlare;
import shordinger.astralsorcery.common.entities.EntityItemStardust;
import shordinger.astralsorcery.common.event.listener.EventHandlerEntity;
import shordinger.astralsorcery.common.item.tool.wand.ItemWand;
import shordinger.astralsorcery.common.item.wand.ItemArchitectWand;
import shordinger.astralsorcery.common.potion.PotionCheatDeath;
import shordinger.astralsorcery.common.starlight.network.handlers.BlockTransmutationHandler;
import shordinger.astralsorcery.common.tile.*;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.common.util.RaytraceAssist;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.effect.CelestialStrike;
import shordinger.astralsorcery.common.util.effect.ShootingStarExplosion;
import shordinger.astralsorcery.common.util.effect.time.TimeStopEffectHelper;
import io.netty.buffer.ByteBuf;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.util.math.Vec3i;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktParticleEvent
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:15
 */
public class PktParticleEvent implements IMessage, IMessageHandler<PktParticleEvent, IMessage> {

    private int typeOrdinal;
    private double xCoord, yCoord, zCoord;
    private double additionalDataDouble = 0.0D;
    private long additionalDataLong = 0L;

    public PktParticleEvent() {}

    public PktParticleEvent(ParticleEventType type, Vec3i vec) {
        this(type, vec.getX(), vec.getY(), vec.getZ());
    }

    public PktParticleEvent(ParticleEventType type, Vector3 vec) {
        this(type, vec.getX(), vec.getY(), vec.getZ());
    }

    public PktParticleEvent(ParticleEventType type, double x, double y, double z) {
        this.typeOrdinal = type.ordinal();
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public void setAdditionalData(double additionalData) {
        this.additionalDataDouble = additionalData;
    }

    public double getAdditionalData() {
        return additionalDataDouble;
    }

    public void setAdditionalDataLong(long additionalDataLong) {
        this.additionalDataLong = additionalDataLong;
    }

    public long getAdditionalDataLong() {
        return additionalDataLong;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOrdinal = buf.readInt();
        this.xCoord = buf.readDouble();
        this.yCoord = buf.readDouble();
        this.zCoord = buf.readDouble();
        this.additionalDataDouble = buf.readDouble();
        this.additionalDataLong = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.typeOrdinal);
        buf.writeDouble(this.xCoord);
        buf.writeDouble(this.yCoord);
        buf.writeDouble(this.zCoord);
        buf.writeDouble(this.additionalDataDouble);
        buf.writeLong(this.additionalDataLong);
    }

    @Override
    public IMessage onMessage(PktParticleEvent message, MessageContext ctx) {
        try {
            ParticleEventType type = ParticleEventType.values()[message.typeOrdinal];
            EventAction trigger = type.getTrigger(ctx.side);
            if(trigger != null) {
                AstralSorcery.proxy.scheduleClientside(() -> triggerClientside(trigger, message));
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn("Error executing ParticleEventType " + message.typeOrdinal + " at " + xCoord + ", " + yCoord + ", " + zCoord);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void triggerClientside(EventAction trigger, PktParticleEvent message) {
        if(Minecraft.getMinecraft().world == null) return;
        AstralSorcery.proxy.scheduleClientside(() -> trigger.trigger(message));
    }

    public Vector3 getVec() {
        return new Vector3(xCoord, yCoord, zCoord);
    }

    public static enum ParticleEventType {

        //DEFINE EVENT TRIGGER IN THE FCKING HUGE SWITCH STATEMENT DOWN TEHRE.
        COLLECTOR_BURST,
        GEM_CRYSTAL_BURST,
        CELESTIAL_CRYSTAL_BURST,
        CELESTIAL_CRYSTAL_FORM,
        CRAFT_FINISH_BURST,
        STARMETAL_ORE_CHARGE,
        TRANSMUTATION_CHARGE,
        WELL_CATALYST_BREAK,
        WAND_CRYSTAL_HIGHLIGHT,
        PHOENIX_PROC,
        TREE_VORTEX,
        ARCHITECT_PLACE,
        CEL_STRIKE,
        SH_STAR,
        SH_STAR_EX,
        BURN_PARCHMENT,
        ENGRAVE_LENS,
        GEN_STRUCTURE,
        DISCIDIA_ATTACK_STACK,
        TIME_FREEZE_EFFECT,

        CE_CROP_INTERACT,
        CE_MELT_BLOCK,
        CE_ACCEL_TILE,
        CE_DMG_ENTITY,
        CE_WATER_FISH,
        CE_BREAK_BLOCK,
        CE_SPAWN_PREPARE_EFFECTS,

        CAPE_EVORSIO_BREAK,
        CAPE_EVORSIO_AOE,

        FLARE_PROC,
        RT_DEBUG;

        //GOD I HATE THIS PART
        //But i can't do this in the ctor because server-client stuffs.
        @SideOnly(Side.CLIENT)
        private static EventAction getClientTrigger(ParticleEventType type) {
            switch (type) {
                case COLLECTOR_BURST:
                    return TileCollectorCrystal::breakParticles;
                case CELESTIAL_CRYSTAL_BURST:
                    return TileCelestialCrystals::breakParticles;
                case CELESTIAL_CRYSTAL_FORM:
                    return EntityItemStardust::spawnFormationParticles;
                case CRAFT_FINISH_BURST:
                    return TileAltar::finishBurst;
                case STARMETAL_ORE_CHARGE:
                    return BlockCustomOre::playStarmetalOreEffects;
                case TRANSMUTATION_CHARGE:
                    return BlockTransmutationHandler::playTransmutationEffects;
                case WELL_CATALYST_BREAK:
                    return TileWell::catalystBurst;
                case WAND_CRYSTAL_HIGHLIGHT:
                    return ItemWand::highlightEffects;
                case PHOENIX_PROC:
                    return PotionCheatDeath::playEntityDeathEffect;
                case CE_CROP_INTERACT:
                    return CEffectAevitas::playParticles;
                case ARCHITECT_PLACE:
                    return ItemArchitectWand::playArchitectPlaceEvent;
                case CE_MELT_BLOCK:
                    return CEffectFornax::playParticles;
                case FLARE_PROC:
                    return EntityFlare::playParticles;
                case CE_ACCEL_TILE:
                    return CEffectHorologium::playParticles;
                case CE_DMG_ENTITY:
                    return CEffectDiscidia::playParticles;
                case CE_WATER_FISH:
                    return CEffectOctans::playParticles;
                case TREE_VORTEX:
                    return TileTreeBeacon::playParticles;
                case RT_DEBUG:
                    return RaytraceAssist::playDebug;
                case CEL_STRIKE:
                    return CelestialStrike::playEffects;
                case SH_STAR:
                    return ShootingStarExplosion::playEffects;
                case SH_STAR_EX:
                    return ShootingStarExplosion::playExEffects;
                case BURN_PARCHMENT:
                    return TileMapDrawingTable::burnParchmentEffects;
                case ENGRAVE_LENS:
                    return TileMapDrawingTable::engraveLensEffects;
                case GEN_STRUCTURE:
                    return TileOreGenerator::playGenerateStructureEffect;
                case CE_BREAK_BLOCK:
                    return CEffectEvorsio::playBreakEffects;
                case CE_SPAWN_PREPARE_EFFECTS:
                    return CEffectPelotrio::playSpawnPrepareEffects;
                case DISCIDIA_ATTACK_STACK:
                    return EventHandlerEntity::playDiscidiaStackAttackEffects;
                case CAPE_EVORSIO_BREAK:
                    return CapeEffectEvorsio::playBlockBreakParticles;
                case CAPE_EVORSIO_AOE:
                    return CapeEffectEvorsio::playAreaDamageParticles;
                case TIME_FREEZE_EFFECT:
                    return TimeStopEffectHelper::playEntityParticles;
                case GEM_CRYSTAL_BURST:
                    return TileGemCrystals::playBreakParticles;
                default:
                    break;
            }
            return null;
        }

        public EventAction getTrigger(Side side) {
            if(!side.isClient()) return null;
            return getClientTrigger(this);
        }

    }

    private static interface EventAction {

        public void trigger(PktParticleEvent event);

    }

}
