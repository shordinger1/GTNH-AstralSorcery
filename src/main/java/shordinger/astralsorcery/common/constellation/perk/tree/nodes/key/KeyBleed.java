/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.registry.RegistryPotions;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyBleed
 * Created by HellFirePvP
 * Date: 28.07.2018 / 17:43
 */
public class KeyBleed extends KeyPerk {

    private int bleedDuration = 60;
    private float bleedChance = 0.25F;

    public KeyBleed(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                bleedDuration = cfg.getInt(
                    "BleedDuration",
                    getConfigurationSection(),
                    bleedDuration,
                    5,
                    400,
                    "Defines the duration of the bleeding effect when applied. Refreshes this duration when a it is applied again");
                bleedChance = cfg.getFloat(
                    "BleedChance",
                    getConfigurationSection(),
                    bleedChance,
                    0.01F,
                    1F,
                    "Defines the base chance a bleed can/is applied when an entity is being hit by this entity");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.bleedDuration = MathHelper.ceil(this.bleedDuration * multiplier);
        this.bleedChance *= multiplier;
    }

    @SubscribeEvent
    public void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                EntityLivingBase target = event.getEntityLiving();

                float chance = bleedChance;
                chance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_BLEED_CHANCE, chance);
                if (rand.nextFloat() < chance) {
                    int stackCap = 3; // So the "real" stackcap is 'amplifier = 3' that means we always have to be lower
                    // than this value.
                    stackCap = Math.round(
                        PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_BLEED_STACKS, stackCap));
                    int duration = bleedDuration;
                    duration = Math.round(
                        PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_BLEED_DURATION, duration));

                    int setAmplifier = 0;
                    if (target.isPotionActive(RegistryPotions.potionBleed)) {
                        PotionEffect pe = target.getActivePotionEffect(RegistryPotions.potionBleed);
                        if (pe != null) {
                            setAmplifier = Math.min(pe.getAmplifier() + 1, stackCap - 1);
                        }
                    }

                    target.addPotionEffect(
                        new PotionEffect(RegistryPotions.potionBleed, duration, setAmplifier, false, true));
                }
            }
        }
    }

}
