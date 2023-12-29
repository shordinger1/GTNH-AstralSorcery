/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.common.auxiliary.link.ILinkableTile;
import shordinger.astralsorcery.common.starlight.IStarlightTransmission;
import shordinger.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import shordinger.astralsorcery.common.tile.network.TileCrystalLens;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityTransmissionBase
 * Created by HellFirePvP
 * Date: 03.08.2016 / 10:35
 */
public abstract class TileTransmissionBase extends TileNetwork implements IStarlightTransmission, ILinkableTile {

    protected boolean singleLink = getClass() == TileCrystalLens.class;

    private List<BlockPos> positions = new LinkedList<>();

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        positions.clear();

        if (compound.hasKey("linked")) {
            NBTTagList list = compound.getTagList("linked", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                positions.add(NBTHelper.readBlockPosFromNBT(tag));
            }
        }
    }

    @Override
    public boolean onSelect(EntityPlayer player) {
        if (player.isSneaking()) {
            for (BlockPos linkTo : Lists.newArrayList(getLinkedPositions())) {
                tryUnlink(player, linkTo);
            }
            player.sendMessage(
                new TextComponentTranslation("misc.link.unlink.all")
                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
            return false;
        }
        return true;
    }

    @Override
    public World getLinkWorld() {
        return getTrWorld();
    }

    @Override
    public BlockPos getLinkPos() {
        return getTrPos();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        NBTTagList list = new NBTTagList();
        for (BlockPos pos : positions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(pos, tag);
            list.appendTag(tag);
        }
        compound.setTag("linked", list);
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {
        if (other.equals(getPos())) return;

        if (TransmissionNetworkHelper.createTransmissionLink(this, other)) {
            if (singleLink) this.positions.clear();

            if (singleLink || !this.positions.contains(other)) {
                this.positions.add(other);
                markForUpdate();
            }
        }
    }

    @Override
    @Nonnull
    public BlockPos getTrPos() {
        return getPos();
    }

    @Override
    @Nonnull
    public World getTrWorld() {
        return getWorld();
    }

    @Override
    public boolean tryLink(EntityPlayer player, BlockPos other) {
        return !other.equals(getPos()) && TransmissionNetworkHelper.canCreateTransmissionLink(this, other);
    }

    @Override
    public boolean tryUnlink(EntityPlayer player, BlockPos other) {
        if (other.equals(getPos())) return false;

        if (TransmissionNetworkHelper.hasTransmissionLink(this, other)) {
            TransmissionNetworkHelper.removeTransmissionLink(this, other);
            this.positions.remove(other);
            markForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return positions;
    }
}
