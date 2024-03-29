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
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;
import shordinger.wrapper.net.minecraftforge.event.entity.living.LivingHurtEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDisarm
 * Created by HellFirePvP
 * Date: 20.07.2018 / 18:08
 */
public class KeyDisarm extends KeyPerk {

    private float dropChance = 0.05F;

    public KeyDisarm(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                dropChance = cfg.getFloat(
                    "DropChance",
                    getConfigurationSection(),
                    dropChance,
                    0F,
                    1F,
                    "Defines the chance (in percent) per hit to make the attacked entity drop its armor.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.dropChance *= multiplier;
    }

    @SubscribeEvent
    public void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, dropChance);
                float currentChance = MathHelper.clamp(chance, 0F, 1F);
                for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                    if (rand.nextFloat() >= currentChance) {
                        continue;
                    }
                    EntityLivingBase attacked = event.getEntityLiving();
                    ItemStack stack = attacked.getItemStackFromSlot(slot);
                    if (!stack.isEmpty()) {
                        attacked.setItemStackToSlot(slot, ItemStack.EMPTY);
                        ItemUtils.dropItemNaturally(attacked.world, attacked.posX, attacked.posY, attacked.posZ, stack);
                        break;
                    }
                }
            }
        }
    }

}
