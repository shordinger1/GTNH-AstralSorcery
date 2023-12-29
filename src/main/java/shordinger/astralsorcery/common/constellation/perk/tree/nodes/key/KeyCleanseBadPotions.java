/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyCleanseBadPotions
 * Created by HellFirePvP
 * Date: 24.11.2018 / 15:39
 */
public class KeyCleanseBadPotions extends KeyPerk {

    private static final Random rand = new Random();

    public KeyCleanseBadPotions(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHeal(LivingHealEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
            EntityPlayer player = (EntityPlayer) entity;
            List<PotionEffect> badEffects = player.getActivePotionEffects()
                .stream()
                .filter(
                    p -> p.getPotion()
                        .isBadEffect())
                .collect(Collectors.toList());
            if (badEffects.isEmpty()) {
                return;
            }
            PotionEffect effect = badEffects.get(rand.nextInt(badEffects.size()));
            PlayerProgress prog = ResearchManager.getProgress(player, Side.SERVER);
            if (prog.hasPerkEffect(this)) {
                float inclChance = 0.1F;
                inclChance = PerkAttributeHelper.getOrCreateMap(player, Side.SERVER)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, inclChance);
                float chance = getChance(event.ammount) * inclChance;
                if (rand.nextFloat() < chance) {
                    player.removePotionEffect(effect.getPotion());
                }
            }
        }
    }

    private float getChance(float healed) {
        if (healed <= 0) {
            return 0;
        }
        float chance = ((3F / (healed * -0.66666667F)) + 5F) / 5F;
        return MathHelper.clamp(chance, 0F, 1F);
    }

}
