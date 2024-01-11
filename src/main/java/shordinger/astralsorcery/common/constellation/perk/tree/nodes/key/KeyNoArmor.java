/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;
import shordinger.wrapper.net.minecraftforge.event.entity.living.LivingHurtEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyNoArmor
 * Created by HellFirePvP
 * Date: 11.08.2018 / 20:26
 */
public class KeyNoArmor extends KeyPerk {

    private float dmgReductionMultiplier = 0.7F;

    public KeyNoArmor(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                dmgReductionMultiplier = cfg.getFloat("ReductionMultiplier", getConfigurationSection(), dmgReductionMultiplier, 0.05F, 1F,
                        "The multiplier that is applied to damage the player receives. The lower the more damage is negated.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.dmgReductionMultiplier *= multiplier;
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            int eq = 0;
            for (ItemStack stack : player.getArmorInventoryList()) {
                if(!stack.isEmpty()) {
                    eq++;
                }
            }
            if (eq < 2) {
                float effMulti = PerkAttributeHelper.getOrCreateMap(player, side)
                        .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);
                event.setAmount(event.getAmount() * (dmgReductionMultiplier * (1F / effMulti)));
            }
        }
    }

}
