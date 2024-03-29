/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraftforge.event.entity.living.LivingHurtEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDamageArmor
 * Created by HellFirePvP
 * Date: 23.11.2018 / 20:17
 */
public class KeyDamageArmor extends KeyPerk {

    private final float dmgPercentPerArmor;

    public KeyDamageArmor(String name, int x, int y, float dmgPercent) {
        super(name, x, y);
        this.dmgPercentPerArmor = dmgPercent;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDmg(LivingHurtEvent event) {
        EntityLivingBase attacked = event.getEntityLiving();
        if (attacked instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacked;
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                int armorPieces = 0;
                for (ItemStack armor : player.getArmorInventoryList()) {
                    if (!armor.isEmpty()) {
                        armorPieces++;
                    }
                }
                if (armorPieces == 0) {
                    return;
                }

                float dmg = event.getAmount();
                dmg *= ((dmgPercentPerArmor * armorPieces) * PerkAttributeHelper.getOrCreateMap(player, side)
                    .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT));
                event.setAmount(Math.max(event.getAmount() - dmg, 0));

                int armorDmg = MathHelper.ceil(dmg * 1.3F);
                for (ItemStack stack : player.getArmorInventoryList()) {
                    stack.damageItem(armorDmg, player);
                }
            }
        }
    }

}
