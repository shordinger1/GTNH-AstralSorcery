/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Feb 5, 2014, 1:34:44 PM (GMT)]
 */
package vazkii.botania.api.wand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * Any block that implements this has a HUD rendered when being hovered
 * with a Wand of the Forest.
 */
public interface IWandHUD {

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos);

}
