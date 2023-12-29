/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDamageEffect
 * Created by HellFirePvP
 * Date: 24.11.2018 / 21:27
 */
public class KeyDamageEffect extends KeyPerk {

    private static final Random rand = new Random();
    private static final float baseApplyChance = 0.04F;

    public KeyDamageEffect(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamageResult(LivingDamageEvent event) {
        DamageSource source = event.source;
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                EntityLivingBase attacked = event.entityLiving;
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, baseApplyChance);
                if (rand.nextFloat() < chance) {
                    switch (rand.nextInt(3)) {
                        case 0:
                            attacked.setFire(100);
                            break;
                        case 1:
                            attacked.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 1, false, false));
                            break;
                        case 2:
                            attacked.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1, false, false));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

}
