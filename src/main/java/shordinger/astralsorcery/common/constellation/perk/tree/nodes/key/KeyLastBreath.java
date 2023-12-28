/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyLastBreath
 * Created by HellFirePvP
 * Date: 20.07.2018 / 17:06
 */
public class KeyLastBreath extends KeyPerk {

    private float digSpeedIncrease = 1.5F;
    private float damageIncrease = 3F;

    public KeyLastBreath(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                digSpeedIncrease = cfg.getFloat(
                    "HarvestSpeed_Increase",
                    getConfigurationSection(),
                    digSpeedIncrease,
                    1F,
                    32F,
                    "Defines the dig speed multiplier you get additionally to your normal dig speed when being low on health (25% health = 75% of this additional multiplier)");
                damageIncrease = cfg.getFloat(
                    "Damage_Increase",
                    getConfigurationSection(),
                    damageIncrease,
                    1F,
                    32F,
                    "Defines the damage multiplier you get additionally to your normal damage when being low on health (25% health = 75% of this additional multiplier)");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.digSpeedIncrease *= multiplier;
        this.damageIncrease *= multiplier;
    }

    @SubscribeEvent
    public void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                float actIncrease = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, damageIncrease);
                float healthPerc = 1F - (Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth());
                event.setAmount(event.getAmount() * (1F + (healthPerc * actIncrease)));
            }
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            float actIncrease = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, digSpeedIncrease);
            float healthPerc = 1F - (Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth());
            event.setNewSpeed(event.getNewSpeed() * (1F + (healthPerc * actIncrease)));
        }
    }

}
