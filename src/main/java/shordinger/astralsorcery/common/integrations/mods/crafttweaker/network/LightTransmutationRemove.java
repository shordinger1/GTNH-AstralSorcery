/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import net.minecraft.item.ItemStack;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.util.ByteBufUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightTransmutationRemove
 * Created by HellFirePvP
 * Date: 27.02.2017 / 12:18
 */
public class LightTransmutationRemove implements SerializeableRecipe {

    private ItemStack matchStack;
    private boolean matchMeta;

    LightTransmutationRemove() {
    }

    public LightTransmutationRemove(ItemStack matchStack, boolean matchMeta) {
        this.matchStack = matchStack;
        this.matchMeta = matchMeta;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.TRANSMUTE_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.matchStack = ByteBufUtils.readItemStack(buf);
        this.matchMeta = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.matchStack);
        buf.writeBoolean(this.matchMeta);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.removeMTTransmutation(this.matchStack, this.matchMeta);
    }

}
