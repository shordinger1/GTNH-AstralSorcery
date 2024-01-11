package shordinger.wrapper.net.minecraft.client.particle;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.EnumBlockRenderType;
import shordinger.wrapper.net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ParticleBlockDust extends ParticleDigging {

    protected ParticleBlockDust(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
                                double ySpeedIn, double zSpeedIn, IBlockState state) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {

        @Nullable
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            IBlockState iblockstate = Block.getStateById(p_178902_15_[0]);
            return iblockstate.getRenderType() == EnumBlockRenderType.INVISIBLE ? null
                : (new ParticleBlockDust(
                worldIn,
                xCoordIn,
                yCoordIn,
                zCoordIn,
                xSpeedIn,
                ySpeedIn,
                zSpeedIn,
                iblockstate)).init();
        }
    }
}
