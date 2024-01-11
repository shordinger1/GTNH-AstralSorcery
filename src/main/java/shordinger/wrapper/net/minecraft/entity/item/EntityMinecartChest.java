package shordinger.wrapper.net.minecraft.entity.item;

import shordinger.wrapper.net.minecraft.block.BlockChest;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.InventoryPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.inventory.ContainerChest;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {

    public EntityMinecartChest(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartChest(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public static void registerFixesMinecartChest(DataFixer fixer) {
        EntityMinecartContainer.addDataFixers(fixer, EntityMinecartChest.class);
    }

    public void killMinecart(DamageSource source) {
        super.killMinecart(source);

        if (this.world.getGameRules()
            .getBoolean("doEntityDrops")) {
            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.CHEST), 1, 0.0F);
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return 27;
    }

    public EntityMinecart.Type getType() {
        return EntityMinecart.Type.CHEST;
    }

    public IBlockState getDefaultDisplayTile() {
        return Blocks.CHEST.getDefaultState()
            .withProperty(BlockChest.FACING, EnumFacing.NORTH);
    }

    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    public String getGuiID() {
        return "minecraft:chest";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        this.addLoot(playerIn);
        return new ContainerChest(playerInventory, this, playerIn);
    }
}
