/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkEffectHelper;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.constellation.perk.types.ICooldownPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.registry.RegistryPotions;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyCheatDeath
 * Created by HellFirePvP
 * Date: 08.08.2018 / 22:55
 */
public class KeyCheatDeath extends KeyPerk implements ICooldownPerk {

    public float thresholdApplyPerkHealth = 4F;
    public float thresholdApplyPerkDamage = 6F;
    public int cooldownPotionApplication = 1000;

    public int potionDuration = 600;
    public int potionAmplifier = 0;

    public KeyCheatDeath(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                thresholdApplyPerkHealth = cfg.getFloat(
                    "ThresholdHealth",
                    getConfigurationSection(),
                    thresholdApplyPerkHealth,
                    0F,
                    20F,
                    "If the player drops below this value of health, the potion effect will apply in case it isn't on cooldown.");
                thresholdApplyPerkDamage = cfg.getFloat(
                    "ThresholdDamage",
                    getConfigurationSection(),
                    thresholdApplyPerkDamage,
                    1F,
                    100F,
                    "If the player takes damage equals/higher to the amount of damage configured here, the potion effect will apply in case it isn't on cooldown.");
                cooldownPotionApplication = cfg.getInt(
                    "CooldownPotion",
                    getConfigurationSection(),
                    cooldownPotionApplication,
                    1,
                    Integer.MAX_VALUE,
                    "Once the potion effect gets applied, it'll take at least this amount of ticks or a server restart until it can be re-applied by this perk");

                potionDuration = cfg.getInt(
                    "PotionDuration",
                    getConfigurationSection(),
                    potionDuration,
                    1,
                    Integer.MAX_VALUE,
                    "Once the potion effect gets applied by any of the triggers, this will be used as tick-duration of the potion effect.");
                potionAmplifier = cfg.getInt(
                    "PotionAmplifier",
                    getConfigurationSection(),
                    potionAmplifier,
                    0,
                    32,
                    "Once the potion effect gets applied by any of the triggers, this will be used as amplifier of the potion effect.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.potionDuration = MathHelper.ceil(this.potionDuration * multiplier);
        this.potionAmplifier = MathHelper.ceil(this.potionAmplifier * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this) && side == Side.SERVER) {
                if (player.getHealth() <= thresholdApplyPerkHealth || event.ammount >= thresholdApplyPerkDamage) {
                    if (!PerkEffectHelper.EVENT_INSTANCE.isCooldownActiveForPlayer(player, this)) {
                        PerkEffectHelper.EVENT_INSTANCE
                            .setCooldownActiveForPlayer(player, this, cooldownPotionApplication);
                        player.addPotionEffect(
                            new PotionEffect(
                                RegistryPotions.potionCheatDeath,
                                potionDuration,
                                potionAmplifier,
                                true,
                                false));
                    }
                }
            }
        }
    }

    @Override
    public void handleCooldownTimeout(EntityPlayer player) {
    }

}
