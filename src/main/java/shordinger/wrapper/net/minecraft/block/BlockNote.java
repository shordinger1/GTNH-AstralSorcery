package shordinger.wrapper.net.minecraft.block;

import java.util.List;

import com.google.common.collect.Lists;

import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityNote;
import shordinger.wrapper.net.minecraft.util.EnumBlockRenderType;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class BlockNote extends BlockContainer {

    private static final List<SoundEvent> INSTRUMENTS = Lists.newArrayList(
        SoundEvents.BLOCK_NOTE_HARP,
        SoundEvents.BLOCK_NOTE_BASEDRUM,
        SoundEvents.BLOCK_NOTE_SNARE,
        SoundEvents.BLOCK_NOTE_HAT,
        SoundEvents.BLOCK_NOTE_BASS,
        SoundEvents.BLOCK_NOTE_FLUTE,
        SoundEvents.BLOCK_NOTE_BELL,
        SoundEvents.BLOCK_NOTE_GUITAR,
        SoundEvents.BLOCK_NOTE_CHIME,
        SoundEvents.BLOCK_NOTE_XYLOPHONE);

    public BlockNote() {
        super(Material.WOOD);
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean flag = worldIn.isBlockPowered(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityNote) {
            TileEntityNote tileentitynote = (TileEntityNote) tileentity;

            if (tileentitynote.previousRedstoneState != flag) {
                if (flag) {
                    tileentitynote.triggerNote(worldIn, pos);
                }

                tileentitynote.previousRedstoneState = flag;
            }
        }
    }

    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityNote) {
                TileEntityNote tileentitynote = (TileEntityNote) tileentity;
                int old = tileentitynote.note;
                tileentitynote.changePitch();
                if (old == tileentitynote.note) return false;
                tileentitynote.triggerNote(worldIn, pos);
                playerIn.addStat(StatList.NOTEBLOCK_TUNED);
            }

            return true;
        }
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityNote) {
                ((TileEntityNote) tileentity).triggerNote(worldIn, pos);
                playerIn.addStat(StatList.NOTEBLOCK_PLAYED);
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityNote();
    }

    private SoundEvent getInstrument(int eventId) {
        if (eventId < 0 || eventId >= INSTRUMENTS.size()) {
            eventId = 0;
        }

        return INSTRUMENTS.get(eventId);
    }

    /**
     * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
     * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
     * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
     */
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        net.minecraftforge.event.world.NoteBlockEvent.Play e = new net.minecraftforge.event.world.NoteBlockEvent.Play(
            worldIn,
            pos,
            state,
            param,
            id);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e)) return false;
        id = e.getInstrument()
            .ordinal();
        param = e.getVanillaNoteId();
        float f = (float) Math.pow(2.0D, (double) (param - 12) / 12.0D);
        worldIn.playSound((EntityPlayer) null, pos, this.getInstrument(id), SoundCategory.RECORDS, 3.0F, f);
        worldIn.spawnParticle(
            EnumParticleTypes.NOTE,
            (double) pos.getX() + 0.5D,
            (double) pos.getY() + 1.2D,
            (double) pos.getZ() + 0.5D,
            (double) param / 24.0D,
            0.0D,
            0.0D);
        return true;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
