/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.orestages.api.OreTiersAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.Optional;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.migration.block.IBlockState;

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
