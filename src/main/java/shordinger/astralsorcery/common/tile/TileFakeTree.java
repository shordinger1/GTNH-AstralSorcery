/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.awt.*;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import shordinger.astralsorcery.common.item.tool.ItemChargedCrystalAxe;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktDualParticleEvent;
import shordinger.astralsorcery.common.tile.base.TileEntityTick;
import shordinger.astralsorcery.common.util.BlockDropCaptureAssist;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;
import shordinger.astralsorcery.migration.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 20:34
 */
public class TileFakeTree extends TileEntityTick {

    private TickAction ta;
    private IBlockState fakedState;

    private UUID playerEffectRef = null;

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {
            if (ticksExisted > 5 && ticksExisted % 4 == 0) {
                if (ta != null) {
                    ta.update(this);
                }
                if (fakedState == null || fakedState.getBlock()
                    .equals(Blocks.AIR)) {
                    cleanUp();
                }
            }
        }
    }

    private void cleanUp() {
        if (fakedState != null) {
            world.setBlockState(getPos(), fakedState);
        } else {
            world.setBlockToAir(getPos());
        }
    }

    @Override
    protected void onFirstTick() {
    }

    public void setupTile(BlockPos treeBeaconRef, IBlockState fakedState) {
        this.ta = new TreeBeaconRef(treeBeaconRef);
        this.fakedState = fakedState;
        markForUpdate();
    }

    public void setupTile(EntityPlayer breakingPlayer, ItemStack usedAxe, IBlockState fakedState) {
        this.ta = new PlayerHarvestRef(breakingPlayer, usedAxe);
        this.fakedState = fakedState;
        markForUpdate();
    }

    public void setPlayerEffectRef(UUID playerEffectRef) {
        this.playerEffectRef = playerEffectRef;
    }

    public UUID getPlayerEffectRef() {
        return playerEffectRef;
    }

    public IBlockState getFakedState() {
        return fakedState;
    }

    @Nullable
    public BlockPos getReference() {
        return ta instanceof TreeBeaconRef ? ((TreeBeaconRef) ta).ref : null;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        int index = compound.getInteger("type");
        if (index == 0) {
            this.ta = new TreeBeaconRef(null);
            ta.read(compound);
        } else {
            this.ta = new ClearAction();
        }

        if (compound.hasUniqueId("playerEffectRef")) {
            this.playerEffectRef = compound.getUniqueId("playerEffectRef");
        } else {
            this.playerEffectRef = null;
        }

        if (compound.hasKey("Block") && compound.hasKey("Data")) {
            int data = compound.getInteger("Data");
            Block b = Block.getBlockFromName(compound.getString("Block"));
            if (b != null) {
                fakedState = b.getStateFromMeta(data);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (ta instanceof TreeBeaconRef) {
            compound.setInteger("type", 0);
        } else if (ta instanceof PlayerHarvestRef) {
            compound.setInteger("type", 1);
        }
        if (ta != null) {
            ta.write(compound);
        }
        if (fakedState != null) {
            compound.setString(
                "Block",
                Block.REGISTRY.getNameForObject(fakedState.getBlock())
                    .toString());
            compound.setInteger(
                "Data",
                fakedState.getBlock()
                    .getMetaFromState(fakedState));
        }

        if (this.playerEffectRef != null) {
            compound.setUniqueId("playerEffectRef", this.playerEffectRef);
        }
    }

    @Override
    public void writeNetNBT(NBTTagCompound compound) {
        super.writeNetNBT(compound);

        compound.setInteger("type", 0);
    }

    private static interface TickAction {

        public void update(TileFakeTree tft);

        public void write(NBTTagCompound cmp);

        public void read(NBTTagCompound cmp);

    }

    private static class ClearAction implements TickAction {

        @Override
        public void update(TileFakeTree tft) {
            tft.world.setBlockToAir(tft.getPos());
        }

        @Override
        public void write(NBTTagCompound cmp) {
        }

        @Override
        public void read(NBTTagCompound cmp) {
        }

    }

    private static class PlayerHarvestRef implements TickAction {

        private EntityPlayer player;
        private ItemStack usedTool;

        private PlayerHarvestRef(EntityPlayer player, ItemStack usedAxe) {
            this.player = player;
            if (usedAxe != null && !usedAxe.isEmpty()) {
                this.usedTool = usedAxe.copy();
                Map<Enchantment, Integer> levels = EnchantmentHelper.getEnchantments(this.usedTool);
                if (levels.containsKey(Enchantments.FORTUNE)) {
                    levels.put(Enchantments.FORTUNE, levels.get(Enchantments.FORTUNE) + 2);
                } else {
                    levels.put(Enchantments.FORTUNE, 2);
                }
                EnchantmentHelper.setEnchantments(levels, this.usedTool);
            } else {
                this.usedTool = null;
            }
        }

        @Override
        public void update(TileFakeTree tft) {
            if (tft.ticksExisted <= 10) return;
            if (player != null && player instanceof EntityPlayerMP
                && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)
                && tft.fakedState != null) {
                NonNullList<ItemStack> out = NonNullList.create();
                harvestAndAppend(tft, out);
                Vector3 plPos = Vector3.atEntityCenter(player);
                for (ItemStack stack : out) {
                    if (!player.addItemStackToInventory(stack)) {
                        ItemUtils.dropItemNaturally(
                            player.getEntityWorld(),
                            plPos.getX() + rand.nextFloat() - rand.nextFloat(),
                            plPos.getY() + rand.nextFloat(),
                            plPos.getZ() + rand.nextFloat() - rand.nextFloat(),
                            stack);
                    }
                }
                PktDualParticleEvent ev = new PktDualParticleEvent(
                    PktDualParticleEvent.DualParticleEventType.CHARGE_HARVEST,
                    new Vector3(tft),
                    Vector3.atEntityCenter(player));
                if (usedTool != null
                    && (usedTool.isEmpty() || !(usedTool.getItem() instanceof ItemChargedCrystalAxe))) {
                    ev.setAdditionalData(
                        Color.GRAY.brighter()
                            .getRGB());
                } else {
                    ev.setAdditionalData(Color.GREEN.getRGB());
                }
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(tft.world, tft.getPos(), 24));
            }
            tft.getWorld()
                .setBlockToAir(tft.getPos());
        }

        private void harvestAndAppend(TileFakeTree tft, NonNullList<ItemStack> out) {
            BlockDropCaptureAssist.startCapturing();
            try {
                tft.getFakedState()
                    .getBlock()
                    .harvestBlock(player.getEntityWorld(), player, tft.getPos(), tft.getFakedState(), null, usedTool);
            } finally {
                BlockDropCaptureAssist.getCapturedStacksAndStop()
                    .forEach(stack -> {
                        if (stack != null && !stack.isEmpty()) {
                            out.add(stack);
                        }
                    });
            }
        }

        @Override
        public void write(NBTTagCompound cmp) {
        }

        @Override
        public void read(NBTTagCompound cmp) {
        }

    }

    private static class TreeBeaconRef implements TickAction {

        private BlockPos ref;

        private TreeBeaconRef(BlockPos ref) {
            this.ref = ref;
        }

        @Override
        public void update(TileFakeTree tft) {
            if (MiscUtils.isChunkLoaded(tft.world, new ChunkPos(ref))) {
                TileTreeBeacon beacon = MiscUtils.getTileAt(tft.world, ref, TileTreeBeacon.class, true);
                if (beacon == null || beacon.isInvalid()) {
                    tft.cleanUp();
                }
            }
        }

        @Override
        public void write(NBTTagCompound cmp) {
            if (ref != null) {
                NBTHelper.writeBlockPosToNBT(ref, cmp);
            }
        }

        @Override
        public void read(NBTTagCompound cmp) {
            ref = NBTHelper.readBlockPosFromNBT(cmp);
        }
    }

}
