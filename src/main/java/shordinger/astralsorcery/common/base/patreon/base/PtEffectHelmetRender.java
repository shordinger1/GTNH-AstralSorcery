/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.base;

import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraftforge.client.event.RenderPlayerEvent;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectHelmetRender
 * Created by HellFirePvP
 * Date: 25.02.2019 / 20:24
 */
public class PtEffectHelmetRender extends PatreonEffectHelper.PatreonEffect {

    private final UUID playerUUID;
    private final ItemStack dummyHeadItem;
    private boolean addedHelmet = false;

    public PtEffectHelmetRender(UUID uniqueId, PatreonEffectHelper.FlareColor chosenColor, UUID plUUID,
                                ItemStack headStack) {
        super(uniqueId, chosenColor);
        this.playerUUID = plUUID;
        this.dummyHeadItem = headStack;
    }

    @Override
    public void initialize() {
        super.initialize();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderPre(RenderPlayerEvent.Pre ev) {
        EntityPlayer player = ev.getEntityPlayer();
        if (player.getUniqueID()
            .equals(playerUUID)
            && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
            .isEmpty()) {
            player.inventory.armorInventory
                .set(EntityEquipmentSlot.HEAD.getIndex(), ItemUtils.copyStackWithSize(dummyHeadItem, 1));
            addedHelmet = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderPost(RenderPlayerEvent.Post ev) {
        EntityPlayer player = ev.getEntityPlayer();
        if (player.getUniqueID()
            .equals(playerUUID) && addedHelmet) {
            player.inventory.armorInventory.set(EntityEquipmentSlot.HEAD.getIndex(), ItemStack.EMPTY);
            addedHelmet = false;
        }
    }

}
