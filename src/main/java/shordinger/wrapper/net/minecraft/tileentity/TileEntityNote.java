package shordinger.wrapper.net.minecraft.tileentity;

import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

public class TileEntityNote extends TileEntity {

    /**
     * Note to play
     */
    public byte note;
    /**
     * stores the latest redstone state
     */
    public boolean previousRedstoneState;

    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setByte("note", this.note);
        compound.setBoolean("powered", this.previousRedstoneState);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.note = compound.getByte("note");
        this.note = (byte) MathHelper.clamp(this.note, 0, 24);
        this.previousRedstoneState = compound.getBoolean("powered");
    }

    /**
     * change pitch by -> (currentPitch + 1) % 25
     */
    public void changePitch() {
        byte old = note;
        this.note = (byte) ((this.note + 1) % 25);
        if (!net.minecraftforge.common.ForgeHooks.onNoteChange(this, old)) return;
        this.markDirty();
    }

    public void triggerNote(World worldIn, BlockPos posIn) {
        if (worldIn.getBlockState(posIn.up())
            .getMaterial() == Material.AIR) {
            IBlockState iblockstate = worldIn.getBlockState(posIn.down());
            Material material = iblockstate.getMaterial();
            int i = 0;

            if (material == Material.ROCK) {
                i = 1;
            }

            if (material == Material.SAND) {
                i = 2;
            }

            if (material == Material.GLASS) {
                i = 3;
            }

            if (material == Material.WOOD) {
                i = 4;
            }

            Block block = iblockstate.getBlock();

            if (block == Blocks.CLAY) {
                i = 5;
            }

            if (block == Blocks.GOLD_BLOCK) {
                i = 6;
            }

            if (block == Blocks.WOOL) {
                i = 7;
            }

            if (block == Blocks.PACKED_ICE) {
                i = 8;
            }

            if (block == Blocks.BONE_BLOCK) {
                i = 9;
            }

            worldIn.addBlockEvent(posIn, Blocks.NOTEBLOCK, i, this.note);
        }
    }
}
