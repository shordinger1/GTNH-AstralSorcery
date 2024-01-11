package shordinger.wrapper.net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.EntityList;
import shordinger.wrapper.net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.tileentity.MobSpawnerBaseLogic;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityMobSpawner;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.datafix.FixTypes;
import shordinger.wrapper.net.minecraft.util.datafix.IDataFixer;
import shordinger.wrapper.net.minecraft.util.datafix.IDataWalker;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {

    /**
     * Mob spawner logic for this spawner minecart.
     */
    private final MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {

        public void broadcastEvent(int id) {
            EntityMinecartMobSpawner.this.world.setEntityState(EntityMinecartMobSpawner.this, (byte) id);
        }

        public World getSpawnerWorld() {
            return EntityMinecartMobSpawner.this.world;
        }

        public BlockPos getSpawnerPosition() {
            return new BlockPos(EntityMinecartMobSpawner.this);
        }

        public net.minecraft.entity.Entity getSpawnerEntity() {
            return EntityMinecartMobSpawner.this;
        }
    };

    public EntityMinecartMobSpawner(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartMobSpawner(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public static void registerFixesMinecartMobSpawner(DataFixer fixer) {
        registerFixesMinecart(fixer, EntityMinecartMobSpawner.class);
        fixer.registerWalker(FixTypes.ENTITY, new IDataWalker() {

            public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn) {
                String s = compound.getString("id");

                if (EntityList.getKey(EntityMinecartMobSpawner.class)
                    .equals(new ResourceLocation(s))) {
                    compound.setString(
                        "id",
                        TileEntity.getKey(TileEntityMobSpawner.class)
                            .toString());
                    fixer.process(FixTypes.BLOCK_ENTITY, compound, versionIn);
                    compound.setString("id", s);
                }

                return compound;
            }
        });
    }

    public EntityMinecart.Type getType() {
        return EntityMinecart.Type.SPAWNER;
    }

    public IBlockState getDefaultDisplayTile() {
        return Blocks.MOB_SPAWNER.getDefaultState();
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.mobSpawnerLogic.readFromNBT(compound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        this.mobSpawnerLogic.writeToNBT(compound);
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        this.mobSpawnerLogic.setDelayToMin(id);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();
        this.mobSpawnerLogic.updateSpawner();
    }
}
