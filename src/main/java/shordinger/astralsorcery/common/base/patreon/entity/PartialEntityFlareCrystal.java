/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.entity;

import java.awt.*;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.RowSpriteSheetResource;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;
import shordinger.astralsorcery.client.util.resource.TextureQuery;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PartialEntityFlareCrystal
 * Created by HellFirePvP
 * Date: 27.12.2018 / 01:27
 */
public class PartialEntityFlareCrystal extends PatreonPartialEntity {

    private TextureQuery queryTexture = new TextureQuery(AssetLoader.TextureLocation.MODELS, "crystal_big_blue");
    private Color colorTheme = Color.WHITE;
    private final PatreonEffectHelper.FlareColor flareColor;

    private Object crystalEffect = null;
    private Object flareEffect = null;

    public PartialEntityFlareCrystal(PatreonEffectHelper.FlareColor flareColor, UUID ownerUUID) {
        super(ownerUUID);
        this.flareColor = flareColor;
    }

    public PartialEntityFlareCrystal setQueryTexture(TextureQuery queryTexture) {
        this.queryTexture = queryTexture;
        return this;
    }

    public PartialEntityFlareCrystal setColorTheme(Color colorTheme) {
        this.colorTheme = colorTheme;
        return this;
    }

    @SideOnly(Side.CLIENT)
    protected void spawnEffects() {
        super.spawnEffects();

        if (rand.nextBoolean() || !Config.enablePatreonEffects) return;

        int age = 30 + rand.nextInt(15);
        float scale = 0.1F + rand.nextFloat() * 0.1F;
        Vector3 at = new Vector3(this.pos);
        at.add(
            rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1));
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(at);
        particle.scale(scale)
            .gravity(0.004)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        particle.setColor(rand.nextInt(3) == 0 ? this.flareColor.color2 : this.flareColor.color1);
        particle.setMaxAge(age);

        if (rand.nextBoolean()) {
            particle = EffectHelper.genericFlareParticle(at);
            particle.setColor(Color.WHITE)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            particle.scale(scale * 0.3F)
                .gravity(0.004);
            particle.setMaxAge(age - 10);
        }
    }

    @SideOnly(Side.CLIENT)
    private SpriteSheetResource getSprite() {
        return RowSpriteSheetResource.crop(this.flareColor.getTexture(), this.flareColor.spriteRowIndex());
    }

    @SideOnly(Side.CLIENT)
    public void tickInRenderDistance() {
        super.tickInRenderDistance();

        if (crystalEffect != null) {
            EffectFloatingCrystal crystal = (EffectFloatingCrystal) crystalEffect;
            if (crystal.isRemoved() && Config.enablePatreonEffects) {
                EffectHandler.getInstance()
                    .registerFX(crystal);
            }
        } else {
            EffectFloatingCrystal crystal = new EffectFloatingCrystal();
            crystal.setPositionUpdateFunction((fx, v, m) -> this.getPos());
            crystal.setRefreshFunc(() -> !removed && Config.enablePatreonEffects);
            crystal.setTexture(queryTexture);
            crystal.setColorTheme(colorTheme);
            EffectHandler.getInstance()
                .registerFX(crystal);
            crystalEffect = crystal;
        }

        if (flareEffect != null) {
            EntityFXFacingSprite p = (EntityFXFacingSprite) flareEffect;
            if (p.isRemoved() && Config.enablePatreonEffects) {
                EffectHandler.getInstance()
                    .registerFX(p);
            }
        } else {
            EntityFXFacingSprite p = EntityFXFacingSprite
                .fromSpriteSheet(getSprite(), pos.getX(), pos.getY(), pos.getZ(), 0.75F, 0);
            p.setPositionUpdateFunction(
                (fx, v, m) -> this.getPos()
                    .addY(0.2));
            p.setRefreshFunc(() -> !removed && Config.enablePatreonEffects);
            EffectHandler.getInstance()
                .registerFX(p);
            this.flareEffect = p;
        }
    }

}
