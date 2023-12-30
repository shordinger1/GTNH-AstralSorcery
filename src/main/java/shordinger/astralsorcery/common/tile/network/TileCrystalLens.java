/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.network;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.block.network.BlockLens;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.item.ItemColoredLens;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.base.crystal.CrystalPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.base.crystal.CrystalTransmissionNode;
import shordinger.astralsorcery.common.tile.base.TileTransmissionBase;
import shordinger.astralsorcery.common.util.RaytraceAssist;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCrystalLens
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:11
 */
public class TileCrystalLens extends TileTransmissionBase {

    private CrystalProperties properties = null;
    private ItemColoredLens.ColorType lensColor;

    private int lensEffectTimeout = 0;

    // So we can tell the client to render beams eventhough the actual connection doesn't exist.
    private List<BlockPos> occupiedConnections = new LinkedList<>();

    @Nullable
    public CrystalProperties getCrystalProperties() {
        return properties;
    }

    public void onPlace(@Nullable CrystalProperties properties) {
        this.properties = properties;
        markForUpdate();
    }

    public void onTransmissionTick() {
        this.lensEffectTimeout = 5;
    }

    // Returns the old one.
    @Nullable
    public ItemColoredLens.ColorType setLensColor(ItemColoredLens.ColorType type) {
        ItemColoredLens.ColorType old = this.lensColor;
        this.lensColor = type;
        markForUpdate();
        IPrismTransmissionNode node = getNode();
        if (node != null) {
            boolean shouldIgnore = type == ItemColoredLens.ColorType.SPECTRAL;
            if (node instanceof CrystalTransmissionNode) {
                if (shouldIgnore != ((CrystalTransmissionNode) node).ignoresBlockCollision()) {
                    ((CrystalTransmissionNode) node).updateIgnoreBlockCollisionState(world, shouldIgnore);
                }
                if (lensColor != null) {
                    ((CrystalTransmissionNode) node).updateAdditionalLoss(1F - lensColor.getFlowReduction());
                } else {
                    ((CrystalTransmissionNode) node).updateAdditionalLoss(1F);
                }
            } else if (node instanceof CrystalPrismTransmissionNode) {
                if (shouldIgnore != ((CrystalPrismTransmissionNode) node).ignoresBlockCollision()) {
                    ((CrystalPrismTransmissionNode) node).updateIgnoreBlockCollisionState(world, shouldIgnore);
                }
                if (lensColor != null) {
                    ((CrystalPrismTransmissionNode) node).updateAdditionalLoss(1F - lensColor.getFlowReduction());
                } else {
                    ((CrystalPrismTransmissionNode) node).updateAdditionalLoss(1F);
                }
            }
        }
        return old;
    }

    public List<BlockPos> getOccupiedConnections() {
        return occupiedConnections;
    }

    @Nullable
    public ItemColoredLens.ColorType getLensColor() {
        return lensColor;
    }

    public ForgeDirection getPlacedAgainst() {
        IBlockState state = WorldHelper.getBlockState(world, getPos());
        if (!(state.getBlock() instanceof BlockLens)) {
            return ForgeDirection.DOWN;
        }
        return state.getValue(BlockLens.PLACED_AGAINST);
    }

    @Override
    public void update() {
        super.update();

        if (lensColor != null) {
            if (world.isRemote) {
                playColorEffects();
            } else {
                doLensColorEffects(lensColor);
            }
        }
    }

    private void doLensColorEffects(ItemColoredLens.ColorType lensColor) {
        if (getTicksExisted() % 4 != 0) return;

        this.occupiedConnections.clear();
        markForUpdate();

        if (lensEffectTimeout > 0) {
            lensEffectTimeout--;
        } else {
            return;
        }

        Vector3 thisVec = new Vector3(this).add(0.5, 0.5, 0.5);
        List<BlockPos> linked = getLinkedPositions();
        float str = 1F / ((float) linked.size());
        str *= 0.7F;
        for (BlockPos linkedTo : linked) {
            Vector3 to = new Vector3(linkedTo).add(0.5, 0.5, 0.5);
            RaytraceAssist rta = new RaytraceAssist(thisVec, to).includeEndPoint();
            if (lensColor.getType() == ItemColoredLens.TargetType.BLOCK
                || lensColor.getType() == ItemColoredLens.TargetType.ANY) {
                boolean clear = rta.isClear(world);
                if (!clear && rta.blockHit() != null) {
                    BlockPos hit = rta.blockHit();
                    IBlockState hitState = WorldHelper.getBlockState(world, hit);
                    if (!hit.equals(to.toBlockPos()) || (!hitState.getBlock()
                        .equals(BlocksAS.lens)
                        && !hitState.getBlock()
                        .equals(BlocksAS.lensPrism))) {
                        lensColor.onBlockOccupyingBeam(world, hit, hitState, str);
                    }
                    this.occupiedConnections.add(hit);
                }
            }
            if (lensColor.getType() == ItemColoredLens.TargetType.ENTITY
                || lensColor.getType() == ItemColoredLens.TargetType.ANY) {
                rta.setCollectEntities(0.5);
                rta.isClear(world);
                List<Entity> found = rta.collectedEntities(world);
                float pStr = lensColor == ItemColoredLens.ColorType.FIRE ? str / 2F : str;
                for (Entity entity : found) {
                    lensColor.onEntityInBeam(thisVec, to, entity, pStr);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playColorEffects() {
        Entity rView = Minecraft.getMinecraft()
            .getRenderViewEntity();
        if (rView == null) rView = Minecraft.getMinecraft().thePlayer;
        if (rView.getDistanceSq(getPos()) > Config.maxEffectRenderDistanceSq) return;
        Vector3 pos = new Vector3(this).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        particle.setColor(lensColor.wrappedColor);
        particle.motion(
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1));
        particle.scale(0.2F);

        Vector3 source = new Vector3(this).add(0.5, 0.5, 0.5);
        Color overlay = lensColor.wrappedColor;
        if (getTicksExisted() % 40 == 0) {
            for (BlockPos dst : occupiedConnections) {
                Vector3 to = new Vector3(dst).add(0.5, 0.5, 0.5);
                EffectLightbeam beam = EffectHandler.getInstance()
                    .lightbeam(to, source, 0.6);
                beam.setColorOverlay(overlay.getRed() / 255F, overlay.getGreen() / 255F, overlay.getBlue() / 255F, 1F);
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("size") && compound.hasKey("purity") && compound.hasKey("collect")) {
            this.properties = CrystalProperties.readFromNBT(compound);
        } else {
            this.properties = null;
        }
        int col = compound.getInteger("color");
        if (col >= 0 && col < ItemColoredLens.ColorType.values().length) {
            this.lensColor = ItemColoredLens.ColorType.values()[col];
        } else {
            this.lensColor = null;
        }

        this.occupiedConnections.clear();
        NBTTagList list = compound.getTagList("listOccupied", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos bp = NBTHelper.readBlockPosFromNBT(tag);
            this.occupiedConnections.add(bp);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (this.properties != null) {
            this.properties.writeToNBT(compound);
        }
        compound.setInteger("color", lensColor != null ? lensColor.ordinal() : -1);

        NBTTagList list = new NBTTagList();
        for (BlockPos to : occupiedConnections) {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(to, cmp);
            list.appendTag(cmp);
        }
        compound.setTag("listOccupied", list);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blocklens.name";
    }

    @Override
    @Nonnull
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new CrystalTransmissionNode(at, getCrystalProperties());
    }

}
