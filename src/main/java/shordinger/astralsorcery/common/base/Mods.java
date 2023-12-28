/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Mods
 * Created by HellFirePvP
 * Date: 31.10.2016 / 11:30
 */
public enum Mods {

    TICONSTRUCT("tconstruct"),
    CRAFTTWEAKER("crafttweaker"),
    JEI("jei"),
    THAUMCRAFT("thaumcraft"),
    BLOODMAGIC("bloodmagic"),
    BOTANIA("botania"),
    CHISEL("chisel"),
    GALACTICRAFT_CORE("galacticraftcore"),
    GEOLOSYS("geolosys"),
    ORESTAGES("orestages"),
    GAMESTAGES("gamestages"),
    DRACONICEVOLUTION("draconicevolution"),
    UNIVERSALREMOTE("universalremote");

    public final String modid;
    private final boolean loaded;

    private static Class<?> gcPlayerClass, urPlayerClass;

    private Mods(String modName) {
        this.modid = modName;
        this.loaded = Loader.isModLoaded(this.modid);
    }

    public boolean isPresent() {
        return loaded;
    }

    public void sendIMC(String message, NBTTagCompound value) {
        FMLInterModComms.sendMessage(this.modid, message, value);
    }

    public void sendIMC(String message, String value) {
        FMLInterModComms.sendMessage(this.modid, message, value);
    }

    public void sendIMC(String message, Block value) {
        sendIMC(message, new ItemStack(value));
    }

    public void sendIMC(String message, Item value) {
        sendIMC(message, new ItemStack(value));
    }

    public void sendIMC(String message, ItemStack value) {
        FMLInterModComms.sendMessage(this.modid, message, value);
    }

    public void sendIMC(String message, ResourceLocation value) {
        FMLInterModComms.sendMessage(this.modid, message, value);
    }

    @Nullable
    public Class<?> getExtendedPlayerClass() {
        if (!isPresent()) return null;

        switch (this) {
            case GALACTICRAFT_CORE:
                if (gcPlayerClass == null) {
                    try {
                        gcPlayerClass = Class
                            .forName("micdoodle8.mods.galacticraft.core.entities.player.GCEntityPlayerMP");
                    } catch (Exception ignored) {
                    }
                }
                return gcPlayerClass;
            case UNIVERSALREMOTE:
                if (urPlayerClass == null) {
                    try {
                        urPlayerClass = Class.forName("clayborn.universalremote.hooks.entity.HookedEntityPlayerMP");
                    } catch (Exception ignored) {
                    }
                }
                return urPlayerClass;
            default:
                break;
        }
        return null;
    }

}
