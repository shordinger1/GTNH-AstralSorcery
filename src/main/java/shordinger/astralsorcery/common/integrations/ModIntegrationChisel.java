/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations;

import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.block.BlockBlackMarble;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationChisel
 * Created by HellFirePvP
 * Date: 17.07.2017 / 18:02
 */
public class ModIntegrationChisel {

    public static void sendVariantIMC() {
        for (BlockMarble.MarbleBlockType type : BlockMarble.MarbleBlockType.values()) {
            if(type.obtainableInCreative()) {
                sendVariantMapping(type.asBlock(), type.asStack(), ChiselGroup.MARBLE);
            }
        }
        for (BlockBlackMarble.BlackMarbleBlockType type : BlockBlackMarble.BlackMarbleBlockType.values()) {
            if(type.obtainableInCreative()) {
                sendVariantMapping(type.asBlock(), type.asStack(), ChiselGroup.SOOTY_MARBLE);
            }
        }
    }

    private static void sendVariantMapping(IBlockState state, ItemStack stack, ChiselGroup group) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("group", group.group);
        tag.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
        tag.setString("block", state.getBlock().getRegistryName().toString());
        tag.setInteger("meta", state.getBlock().getMetaFromState(state));
        FMLInterModComms.sendMessage(Mods.CHISEL.modid, "add_variation", tag);
    }

    public static enum ChiselGroup {

        MARBLE("marble"),
        SOOTY_MARBLE("sooty_marble");

        private final String group;

        ChiselGroup(String group) {
            this.group = group;
        }
    }

}
