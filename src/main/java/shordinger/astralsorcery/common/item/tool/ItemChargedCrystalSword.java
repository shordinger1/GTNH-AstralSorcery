/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.effect.CelestialStrike;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalSword
 * Created by HellFirePvP
 * Date: 12.03.2017 / 10:45
 */
public class ItemChargedCrystalSword extends ItemCrystalSword implements ChargedCrystalToolBase {

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.getEntityWorld().isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMp = (EntityPlayerMP) player;
            if (!MiscUtils.isPlayerFakeMP(playerMp) && !player.isSneaking()
                && !playerMp.getCooldownTracker()
                .hasCooldown(ItemsAS.chargedCrystalSword)) {
                CelestialStrike.play(
                    player,
                    player.getEntityWorld(),
                    Vector3.atEntityCorner(entity),
                    Vector3.atEntityCenter(entity));
                stack.damageItem(1, player);
                if (!ChargedCrystalToolBase.tryRevertMainHand(playerMp, stack)) {
                    playerMp.getCooldownTracker()
                        .setCooldown(ItemsAS.chargedCrystalSword, 80);
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalSword;
    }

}
