package shordinger.wrapper.net.minecraft.world;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.init.Biomes;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;
import shordinger.wrapper.net.minecraft.world.biome.BiomeProviderSingle;
import shordinger.wrapper.net.minecraft.world.end.DragonFightManager;
import shordinger.wrapper.net.minecraft.world.gen.ChunkGeneratorEnd;
import shordinger.wrapper.net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderEnd extends WorldProvider {

    private DragonFightManager dragonFightManager;

    /**
     * Creates a new {@link BiomeProvider} for the WorldProvider, and also sets the values of {@link #hasSkylight} and
     * {@link #hasNoSky} appropriately.
     * <p>
     * Note that subclasses generally override this method without calling the parent version.
     */
    public void init() {
        this.biomeProvider = new BiomeProviderSingle(Biomes.SKY);
        NBTTagCompound nbttagcompound = this.world.getWorldInfo()
            .getDimensionData(this.world.provider.getDimension());
        this.dragonFightManager = this.world instanceof WorldServer
            ? new DragonFightManager((WorldServer) this.world, nbttagcompound.getCompoundTag("DragonFight"))
            : null;
    }

    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorEnd(
            this.world,
            this.world.getWorldInfo()
                .isMapFeaturesEnabled(),
            this.world.getSeed(),
            this.getSpawnCoordinate());
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.0F;
    }

    /**
     * Returns array with sunrise/sunset colors
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return null;
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        int i = 10518688;
        float f = MathHelper.cos(p_76562_1_ * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        float f1 = 0.627451F;
        float f2 = 0.5019608F;
        float f3 = 0.627451F;
        f1 = f1 * (f * 0.0F + 0.15F);
        f2 = f2 * (f * 0.0F + 0.15F);
        f3 = f3 * (f * 0.0F + 0.15F);
        return new Vec3d((double) f1, (double) f2, (double) f3);
    }

    @SideOnly(Side.CLIENT)
    public boolean isSkyColored() {
        return false;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere() {
        return false;
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    public boolean isSurfaceWorld() {
        return false;
    }

    /**
     * the y level at which clouds are rendered.
     */
    @SideOnly(Side.CLIENT)
    public float getCloudHeight() {
        return 8.0F;
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int x, int z) {
        return this.world.getGroundAboveSeaLevel(new BlockPos(x, 0, z))
            .getMaterial()
            .blocksMovement();
    }

    public BlockPos getSpawnCoordinate() {
        return new BlockPos(100, 50, 0);
    }

    public int getAverageGroundLevel() {
        return 50;
    }

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z) {
        return false;
    }

    public DimensionType getDimensionType() {
        return DimensionType.THE_END;
    }

    /**
     * Called when the world is performing a save. Only used to save the state of the Dragon Boss fight in
     * WorldProviderEnd in Vanilla.
     */
    public void onWorldSave() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.dragonFightManager != null) {
            nbttagcompound.setTag("DragonFight", this.dragonFightManager.getCompound());
        }

        this.world.getWorldInfo()
            .setDimensionData(this.world.provider.getDimension(), nbttagcompound);
    }

    /**
     * Called when the world is updating entities. Only used in WorldProviderEnd to update the DragonFightManager in
     * Vanilla.
     */
    public void onWorldUpdateEntities() {
        if (this.dragonFightManager != null) {
            this.dragonFightManager.tick();
        }
    }

    @Nullable
    public DragonFightManager getDragonFightManager() {
        return this.dragonFightManager;
    }

    /**
     * Called when a Player is added to the provider's world.
     */
    @Override
    public void onPlayerAdded(net.minecraft.entity.player.EntityPlayerMP player) {
        if (this.dragonFightManager != null) {
            this.dragonFightManager.addPlayer(player);
        }
    }

    /**
     * Called when a Player is removed from the provider's world.
     */
    @Override
    public void onPlayerRemoved(net.minecraft.entity.player.EntityPlayerMP player) {
        if (this.dragonFightManager != null) {
            this.dragonFightManager.removePlayer(player);
        }
    }
}
