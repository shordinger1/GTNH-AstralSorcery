package shordinger.wrapper.net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ParticleSmokeLarge extends ParticleSmokeNormal {

    protected ParticleSmokeLarge(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1201_8_,
                                 double p_i1201_10_, double p_i1201_12_) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, p_i1201_8_, p_i1201_10_, p_i1201_12_, 2.5F);
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {

        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new ParticleSmokeLarge(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
