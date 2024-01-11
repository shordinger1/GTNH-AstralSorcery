/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect;

import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.tile.TileRitualLink;
import shordinger.astralsorcery.common.tile.TileRitualPedestal;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffect
 * Created by HellFirePvP
 * Date: 01.10.2016 / 15:47
 */
public abstract class ConstellationEffect extends ConfigEntry {

    protected static final Random rand = new Random();

    private final IWeakConstellation constellation;

    //Always null on client.
    protected final ILocatable origin;

    public ConstellationEffect(@Nullable ILocatable origin, IWeakConstellation constellation, String cfgName) {
        super(Section.RITUAL_EFFECTS, cfgName);
        this.origin = origin;
        this.constellation = constellation;
    }

    public IWeakConstellation getConstellation() {
        return constellation;
    }

    //Once per TE client tick
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal,  float percEffectVisibility, boolean extendedEffects) {}

    //May be executed multiple times per tick
    //Even if this effect can handle multiple effects per tick, it is still possible that this method is called.
    public abstract boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified, @Nullable IMinorConstellation possibleTraitEffect);

    @Nullable
    public TileRitualPedestal getPedestal(World world, BlockPos pos) {
        TileEntity te = MiscUtils.getTileAt(world, pos, TileEntity.class, false);
        if(te == null) return null;
        if(te instanceof TileRitualLink) {
            TileRitualLink link = (TileRitualLink) te;
            pos = link.getLinkedTo();
            return MiscUtils.getTileAt(world, pos, TileRitualPedestal.class, false);
        }
        return te instanceof TileRitualPedestal ? (TileRitualPedestal) te : null;
    }

    public abstract ConstellationEffectProperties provideProperties(int mirrorCount);

    public void clearCache() {}

    public void readFromNBT(NBTTagCompound cmp) {}

    public void writeToNBT(NBTTagCompound cmp) {}

    @Nullable
    public EntityPlayer getOwningPlayerInWorld(World world, BlockPos pos) {
        TileRitualPedestal pedestal = getPedestal(world, pos);
        if(pedestal != null) {
            return pedestal.getOwningPlayerInWorld(world);
        }
        return null;
    }

}
