/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import java.awt.*;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystal;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktShootEntity
 * Created by HellFirePvP
 * Date: 31.07.2017 / 10:33
 */
public class PktShootEntity implements IMessage, IMessageHandler<PktShootEntity, IMessage> {

    private static final Random rand = new Random();

    private int entityId;
    private Vector3 motionVector;

    private boolean hasEffect = false;
    private double effectLength = 0;

    public PktShootEntity() {
    }

    public PktShootEntity(int entityId, Vector3 motionVector) {
        this.entityId = entityId;
        this.motionVector = motionVector;
    }

    public PktShootEntity setEffectLength(double length) {
        this.hasEffect = true;
        this.effectLength = length;
        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.motionVector = new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.hasEffect = buf.readBoolean();
        if (this.hasEffect) {
            this.effectLength = buf.readDouble();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeDouble(this.motionVector.getX());
        buf.writeDouble(this.motionVector.getY());
        buf.writeDouble(this.motionVector.getZ());
        buf.writeBoolean(this.hasEffect);
        if (hasEffect) {
            buf.writeDouble(this.effectLength);
        }
    }

    @Override
    public IMessage onMessage(PktShootEntity message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> shootEntity(message));
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static void shootEntity(PktShootEntity pkt) {
        World world = Minecraft.getMinecraft().theWorld;
        Entity entity = world.getEntityByID(pkt.entityId);
        if (entity != null) {
            entity.motionX = pkt.motionVector.getX();
            entity.motionY = pkt.motionVector.getY();
            entity.motionZ = pkt.motionVector.getZ();
            if (pkt.hasEffect) {
                Vector3 origin = new Vector3(
                    entity.posX + entity.width / 2F,
                    entity.posY + entity.height,
                    entity.posZ + entity.width / 2F);
                Vector3 look = new Vector3(entity.getLookVec()).normalize()
                    .multiply(pkt.effectLength * 18);
                Vector3 motionReverse = look.clone()
                    .normalize()
                    .multiply(-0.4 * pkt.effectLength);

                Vector3 perp = look.clone()
                    .perpendicular();
                for (int i = 0; i < 500 + rand.nextInt(80); i++) {
                    Vector3 at = look.clone()
                        .multiply(0.2 + rand.nextFloat() * 2.5)
                        .add(
                            perp.clone()
                                .rotate(rand.nextFloat() * 360, look)
                                .multiply(rand.nextFloat() * 1.6))
                        .add(origin);

                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                    p.scale(0.35F + rand.nextFloat() * 0.2F)
                        .setMaxAge(10 + rand.nextInt(10));
                    p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                        .setAlphaMultiplier(1F);
                    p.gravity(0.004);
                    if (rand.nextBoolean()) {
                        p.setColor(Color.WHITE);
                        p.scale(0.1F + rand.nextFloat() * 0.05F);
                    } else {
                        p.setColor(BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor);
                    }
                    if (rand.nextInt(4) != 0) {
                        p.motion(motionReverse.getX(), motionReverse.getY(), motionReverse.getZ());
                    } else {
                        p.motion(0, 0, 0);
                    }
                }
            }
        }
    }

}
