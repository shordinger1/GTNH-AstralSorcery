/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.util.PositionedLoopSound;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SoundHelper
 * Created by HellFirePvP
 * Date: 06.12.2016 / 12:45
 */
public class SoundHelper {

    public static void playSoundAround(SoundEvent sound, World world, BlockPos position, float volume, float pitch) {
        playSoundAround(
            sound,
            SoundCategory.MASTER,
            world,
            position.getX(),
            position.getY(),
            position.getZ(),
            volume,
            pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundCategory category, World world, BlockPos position,
                                       float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, World world, Vector3 position, float volume, float pitch) {
        playSoundAround(
            sound,
            SoundCategory.MASTER,
            world,
            position.getX(),
            position.getY(),
            position.getZ(),
            volume,
            pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundCategory category, World world, Vector3 position,
                                       float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundCategory category, World world, double posX, double posY,
                                       double posZ, float volume, float pitch) {
        if (sound instanceof SoundUtils.CategorizedSoundEvent) {
            category = ((SoundUtils.CategorizedSoundEvent) sound).getCategory();
        }
        world.playSound(null, posX, posY, posZ, sound, category, volume, pitch);
    }

    @SideOnly(Side.CLIENT)
    public static PositionedLoopSound playSoundLoopClient(SoundEvent sound, Vector3 pos, float volume, float pitch,
                                                          PositionedLoopSound.ActivityFunction func) {
        SoundCategory cat = SoundCategory.MASTER;
        if (sound instanceof SoundUtils.CategorizedSoundEvent) {
            cat = ((SoundUtils.CategorizedSoundEvent) sound).getCategory();
        }
        PositionedLoopSound posSound = new PositionedLoopSound(sound, cat, volume, pitch, pos);
        posSound.setRefreshFunction(func);
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(posSound);
        return posSound;
    }

    @SideOnly(Side.CLIENT)
    public float getSoundVolume(SoundCategory cat) {
        return Minecraft.getMinecraft().gameSettings.getSoundLevel(cat);
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundClient(SoundEvent sound, float volume, float pitch) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            player.playSound(sound, volume, pitch);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundClientWorld(SoundUtils.CategorizedSoundEvent sound, BlockPos pos, float volume,
                                            float pitch) {
        playSoundClientWorld(sound, sound.getCategory(), pos, volume, pitch);
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundClientWorld(SoundEvent sound, SoundCategory cat, BlockPos pos, float volume,
                                            float pitch) {
        if (Minecraft.getMinecraft().theWorld != null) {
            Minecraft.getMinecraft().theWorld.playSound(
                Minecraft.getMinecraft().theWorld,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                sound,
                cat,
                volume,
                pitch);
        }
    }

}
