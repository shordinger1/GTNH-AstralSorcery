/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.base;

import java.util.UUID;
import java.util.function.Function;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.texture.TextureSpritePlane;
import shordinger.astralsorcery.client.util.resource.SpriteQuery;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectFixedSprite
 * Created by HellFirePvP
 * Date: 06.11.2018 / 18:44
 */
public class PtEffectFixedSprite extends PatreonEffectHelper.PatreonEffect {

    private Object activeSprite;

    private SpriteQuery spriteQuery;
    private Function<EntityPlayer, Vector3> positionFunction = Vector3::atEntityCenter;

    public PtEffectFixedSprite(UUID uniqueId, PatreonEffectHelper.FlareColor chosenColor, SpriteQuery spriteQuery) {
        super(uniqueId, chosenColor);
        this.spriteQuery = spriteQuery;
    }

    private Vector3 getPosition(EntityPlayer player) {
        return positionFunction.apply(player);
    }

    public PtEffectFixedSprite setPositionFunction(Function<EntityPlayer, Vector3> positionFunction) {
        this.positionFunction = positionFunction;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public void doEffect(EntityPlayer player) {
        SpriteSheetResource res = spriteQuery.resolveSprite();
        if (res != null) {
            makeSprite(res, player);
        }
    }

    @SideOnly(Side.CLIENT)
    private TextureSpritePlane makeSprite(SpriteSheetResource resource, EntityPlayer owningPlayer) {
        TextureSpritePlane spr = (TextureSpritePlane) activeSprite;
        if (spr == null || spr.canRemove() || spr.isRemoved()) {
            spr = EffectHandler.getInstance()
                .textureSpritePlane(resource, Vector3.RotAxis.Y_AXIS.clone());
            spr.setPosFunc((fx, position, motionToBeMoved) -> this.getPosition(owningPlayer));
            spr.setNoRotation(45)
                .setAlphaMultiplier(1F);
            spr.setRefreshFunc(
                () -> !owningPlayer.isDead && Minecraft.getMinecraft().thePlayer != null
                    && Minecraft.getMinecraft().theWorld != null
                    && Minecraft.getMinecraft().theWorld.provider != null);
            spr.setScale(10F);
            activeSprite = spr;
        }
        return spr;
    }

}
