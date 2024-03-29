/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.orestages.api.OreTiersAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.Tuple;
import shordinger.wrapper.net.minecraftforge.fml.common.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationOreStages
 * Created by HellFirePvP
 * Date: 19.05.2018 / 18:56
 */
public class ModIntegrationOreStages {

    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "orestages")
    public static boolean canSeeOreClient(IBlockState test) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return false;
        Tuple<String, IBlockState> replacement;
        if ((replacement = OreTiersAPI.getStageInfo(test)) != null) {
            return GameStageHelper.clientHasStage(player, replacement.getFirst());
        }
        return true;
    }

}
