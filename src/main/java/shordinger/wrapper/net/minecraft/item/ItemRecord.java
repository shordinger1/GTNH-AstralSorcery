package shordinger.wrapper.net.minecraft.item;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.BlockJukebox;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemRecord extends Item {

    private static final Map<SoundEvent, ItemRecord> RECORDS = Maps.<SoundEvent, ItemRecord>newHashMap();
    private final SoundEvent sound;
    private final String displayName;

    protected ItemRecord(String recordName, SoundEvent soundIn) {
        this.displayName = "item.record." + recordName + ".desc";
        this.sound = soundIn;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.MISC);
        RECORDS.put(this.sound, this);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == Blocks.JUKEBOX
            && !((Boolean) iblockstate.getValue(BlockJukebox.HAS_RECORD)).booleanValue()) {
            if (!worldIn.isRemote) {
                ItemStack itemstack = player.getHeldItem(hand);
                ((BlockJukebox) Blocks.JUKEBOX).insertRecord(worldIn, pos, iblockstate, itemstack);
                worldIn.playEvent((EntityPlayer) null, 1010, pos, Item.getIdFromItem(this));
                itemstack.shrink(1);
                player.addStat(StatList.RECORD_PLAYED);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.PASS;
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(this.getRecordNameLocal());
    }

    @SideOnly(Side.CLIENT)
    public String getRecordNameLocal() {
        return I18n.translateToLocal(this.displayName);
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static ItemRecord getBySound(SoundEvent soundIn) {
        return RECORDS.get(soundIn);
    }

    @SideOnly(Side.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }
}
