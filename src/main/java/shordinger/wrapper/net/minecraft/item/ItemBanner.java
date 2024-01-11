package shordinger.wrapper.net.minecraft.item;

import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.block.BlockStandingSign;
import shordinger.wrapper.net.minecraft.block.BlockWallSign;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.tileentity.BannerPattern;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityBanner;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemBanner extends ItemBlock {

    public ItemBanner() {
        super(Blocks.STANDING_BANNER);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        boolean flag = iblockstate.getBlock()
            .isReplaceable(worldIn, pos);

        if (facing != EnumFacing.DOWN && (iblockstate.getMaterial()
            .isSolid() || flag) && (!flag || facing == EnumFacing.UP)) {
            pos = pos.offset(facing);
            ItemStack itemstack = player.getHeldItem(hand);

            if (player.canPlayerEdit(pos, facing, itemstack) && Blocks.STANDING_BANNER.canPlaceBlockAt(worldIn, pos)) {
                if (worldIn.isRemote) {
                    return EnumActionResult.SUCCESS;
                } else {
                    pos = flag ? pos.down() : pos;

                    if (facing == EnumFacing.UP) {
                        int i = MathHelper.floor((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                        worldIn.setBlockState(
                            pos,
                            Blocks.STANDING_BANNER.getDefaultState()
                                .withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)),
                            3);
                    } else {
                        worldIn.setBlockState(
                            pos,
                            Blocks.WALL_BANNER.getDefaultState()
                                .withProperty(BlockWallSign.FACING, facing),
                            3);
                    }

                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntityBanner) {
                        ((TileEntityBanner) tileentity).setItemValues(itemstack, false);
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
        } else {
            return EnumActionResult.FAIL;
        }
    }

    public String getItemStackDisplayName(ItemStack stack) {
        String s = "item.banner.";
        EnumDyeColor enumdyecolor = getBaseColor(stack);
        s = s + enumdyecolor.getUnlocalizedName() + ".name";
        return I18n.translateToLocal(s);
    }

    @SideOnly(Side.CLIENT)
    public static void appendHoverTextFromTileEntityTag(ItemStack stack, List<String> p_185054_1_) {
        NBTTagCompound nbttagcompound = stack.getSubCompound("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKey("Patterns")) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Patterns", 10);

            for (int i = 0; i < nbttaglist.tagCount() && i < 6; ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(nbttagcompound1.getInteger("Color"));
                BannerPattern bannerpattern = BannerPattern.byHash(nbttagcompound1.getString("Pattern"));

                if (bannerpattern != null) {
                    p_185054_1_.add(
                        I18n.translateToLocal(
                            "item.banner." + bannerpattern.getFileName() + "." + enumdyecolor.getUnlocalizedName()));
                }
            }
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        appendHoverTextFromTileEntityTag(stack, tooltip);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
                items.add(makeBanner(enumdyecolor, (NBTTagList) null));
            }
        }
    }

    public static ItemStack makeBanner(EnumDyeColor color, @Nullable NBTTagList patterns) {
        ItemStack itemstack = new ItemStack(Items.BANNER, 1, color.getDyeDamage());

        if (patterns != null && !patterns.hasNoTags()) {
            itemstack.getOrCreateSubCompound("BlockEntityTag")
                .setTag("Patterns", patterns.copy());
        }

        return itemstack;
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab() {
        return CreativeTabs.DECORATIONS;
    }

    public static EnumDyeColor getBaseColor(ItemStack stack) {
        return EnumDyeColor.byDyeDamage(stack.getMetadata() & 15);
    }
}
