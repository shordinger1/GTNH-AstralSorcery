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
import shordinger.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyMending
 * Created by HellFirePvP
 * Date: 17.07.2018 / 20:30
 */
public class KeyMending extends KeyPerk implements IPlayerTickPerk {

    private int chanceToRepair = 800;

    public KeyMending(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                chanceToRepair = cfg.getInt(
                    "Repair_Chance",
                    getConfigurationSection(),
                    chanceToRepair,
                    3,
                    6_000_000,
                    "Sets the chance (Random.nextInt(chance) == 0) to try to see if a piece of armor on the player that is damageable and damaged can be repaired; the lower the more likely");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.chanceToRepair = MathHelper.ceil(this.chanceToRepair * multiplier);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            float fChance = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(
                    player,
                    ResearchManager.getProgress(player, side),
                    AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT,
                    chanceToRepair);
            int chance = Math.max(MathHelper.floor(fChance), 1);
            for (ItemStack armor : player.getArmorInventoryList()) {
                if (rand.nextInt(chance) != 0) continue;
                if (!armor.isEmpty() && armor.isItemStackDamageable() && armor.isItemDamaged()) {
                    armor.setItemDamage(armor.getItemDamage() - 1);
                }
            }
        }
    }
}
