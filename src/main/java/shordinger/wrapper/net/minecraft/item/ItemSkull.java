package shordinger.wrapper.net.minecraft.item;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockSkull;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTUtil;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntitySkull;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemSkull extends Item {

    private static final String[] SKULL_TYPES = new String[]{"skeleton", "wither", "zombie", "char", "creeper",
        "dragon"};

    public ItemSkull() {
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing == EnumFacing.DOWN) {
            return EnumActionResult.FAIL;
        } else {
            if (worldIn.getBlockState(pos)
                .getBlock()
                .isReplaceable(worldIn, pos)) {
                facing = EnumFacing.UP;
                pos = pos.down();
            }
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            boolean flag = block.isReplaceable(worldIn, pos);

            if (!flag) {
                if (!worldIn.getBlockState(pos)
                    .getMaterial()
                    .isSolid() && !worldIn.isSideSolid(pos, facing, true)) {
                    return EnumActionResult.FAIL;
                }

                pos = pos.offset(facing);
            }

            ItemStack itemstack = player.getHeldItem(hand);

            if (player.canPlayerEdit(pos, facing, itemstack) && Blocks.SKULL.canPlaceBlockAt(worldIn, pos)) {
                if (worldIn.isRemote) {
                    return EnumActionResult.SUCCESS;
                } else {
                    worldIn.setBlockState(
                        pos,
                        Blocks.SKULL.getDefaultState()
                            .withProperty(BlockSkull.FACING, facing),
                        11);
                    int i = 0;

                    if (facing == EnumFacing.UP) {
                        i = MathHelper.floor((double) (player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
                    }

                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntitySkull) {
                        TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;

                        if (itemstack.getMetadata() == 3) {
                            GameProfile gameprofile = null;

                            if (itemstack.hasTagCompound()) {
                                NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                                if (nbttagcompound.hasKey("SkullOwner", 10)) {
                                    gameprofile = NBTUtil
                                        .readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                                } else if (nbttagcompound.hasKey("SkullOwner", 8)
                                    && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
                                    gameprofile = new GameProfile(
                                        (UUID) null,
                                        nbttagcompound.getString("SkullOwner"));
                                }
                            }

                            tileentityskull.setPlayerProfile(gameprofile);
                        } else {
                            tileentityskull.setType(itemstack.getMetadata());
                        }

                        tileentityskull.setSkullRotation(i);
                        Blocks.SKULL.checkWitherSpawn(worldIn, pos, tileentityskull);
                    }

                    if (player instanceof EntityPlayerMP) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
                    }

                    itemstack.shrink(1);
                    return EnumActionResult.SUCCESS;
                }
            } else {
                return EnumActionResult.FAIL;
            }
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < SKULL_TYPES.length; ++i) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage) {
        return damage;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getMetadata();

        if (i < 0 || i >= SKULL_TYPES.length) {
            i = 0;
        }

        return super.getUnlocalizedName() + "." + SKULL_TYPES[i];
    }

    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getMetadata() == 3 && stack.hasTagCompound()) {
            if (stack.getTagCompound()
                .hasKey("SkullOwner", 8)) {
                return I18n.translateToLocalFormatted(
                    "item.skull.player.name",
                    stack.getTagCompound()
                        .getString("SkullOwner"));
            }

            if (stack.getTagCompound()
                .hasKey("SkullOwner", 10)) {
                NBTTagCompound nbttagcompound = stack.getTagCompound()
                    .getCompoundTag("SkullOwner");

                if (nbttagcompound.hasKey("Name", 8)) {
                    return I18n.translateToLocalFormatted("item.skull.player.name", nbttagcompound.getString("Name"));
                }
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    /**
     * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
     */
    public boolean updateItemStackNBT(NBTTagCompound nbt) {
        super.updateItemStackNBT(nbt);

        if (nbt.hasKey("SkullOwner", 8) && !StringUtils.isBlank(nbt.getString("SkullOwner"))) {
            GameProfile gameprofile = new GameProfile((UUID) null, nbt.getString("SkullOwner"));
            gameprofile = TileEntitySkull.updateGameprofile(gameprofile);
            nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
            return true;
        } else {
            return false;
        }
    }
}
