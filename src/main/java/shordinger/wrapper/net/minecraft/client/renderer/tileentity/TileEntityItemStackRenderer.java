package shordinger.wrapper.net.minecraft.client.renderer.tileentity;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockChest;
import shordinger.wrapper.net.minecraft.block.BlockShulkerBox;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.model.ModelShield;
import shordinger.wrapper.net.minecraft.client.renderer.BannerTextures;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.EnumDyeColor;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTUtil;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityBanner;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityBed;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityChest;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityEnderChest;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityShulkerBox;
import shordinger.wrapper.net.minecraft.tileentity.TileEntitySkull;
import shordinger.wrapper.net.minecraft.util.EnumFacing;

@SideOnly(Side.CLIENT)
public class TileEntityItemStackRenderer {

    private static final TileEntityShulkerBox[] SHULKER_BOXES = new TileEntityShulkerBox[16];
    public static TileEntityItemStackRenderer instance;
    private final TileEntityChest chestBasic = new TileEntityChest(BlockChest.Type.BASIC);
    private final TileEntityChest chestTrap = new TileEntityChest(BlockChest.Type.TRAP);
    private final TileEntityEnderChest enderChest = new TileEntityEnderChest();
    private final TileEntityBanner banner = new TileEntityBanner();
    private final TileEntityBed bed = new TileEntityBed();
    private final TileEntitySkull skull = new TileEntitySkull();
    private final ModelShield modelShield = new ModelShield();

    public void renderByItem(ItemStack itemStackIn) {
        this.renderByItem(itemStackIn, 1.0F);
    }

    public void renderByItem(ItemStack p_192838_1_, float partialTicks) {
        Item item = p_192838_1_.getItem();

        if (item == Items.BANNER) {
            this.banner.setItemValues(p_192838_1_, false);
            TileEntityRendererDispatcher.instance.render(this.banner, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        } else if (item == Items.BED) {
            this.bed.setItemValues(p_192838_1_);
            TileEntityRendererDispatcher.instance.render(this.bed, 0.0D, 0.0D, 0.0D, 0.0F);
        } else if (item == Items.SHIELD) {
            if (p_192838_1_.getSubCompound("BlockEntityTag") != null) {
                this.banner.setItemValues(p_192838_1_, true);
                Minecraft.getMinecraft()
                    .getTextureManager()
                    .bindTexture(
                        BannerTextures.SHIELD_DESIGNS.getResourceLocation(
                            this.banner.getPatternResourceLocation(),
                            this.banner.getPatternList(),
                            this.banner.getColorList()));
            } else {
                Minecraft.getMinecraft()
                    .getTextureManager()
                    .bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            this.modelShield.render();
            GlStateManager.popMatrix();
        } else if (item == Items.SKULL) {
            GameProfile gameprofile = null;

            if (p_192838_1_.hasTagCompound()) {
                NBTTagCompound nbttagcompound = p_192838_1_.getTagCompound();

                if (nbttagcompound.hasKey("SkullOwner", 10)) {
                    gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                } else if (nbttagcompound.hasKey("SkullOwner", 8)
                    && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
                    GameProfile gameprofile1 = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));
                    gameprofile = TileEntitySkull.updateGameprofile(gameprofile1);
                    nbttagcompound.removeTag("SkullOwner");
                    nbttagcompound
                        .setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                }
            }

            if (TileEntitySkullRenderer.instance != null) {
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                TileEntitySkullRenderer.instance.renderSkull(
                    0.0F,
                    0.0F,
                    0.0F,
                    EnumFacing.UP,
                    180.0F,
                    p_192838_1_.getMetadata(),
                    gameprofile,
                    -1,
                    0.0F);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        } else if (item == Item.getItemFromBlock(Blocks.ENDER_CHEST)) {
            TileEntityRendererDispatcher.instance.render(this.enderChest, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        } else if (item == Item.getItemFromBlock(Blocks.TRAPPED_CHEST)) {
            TileEntityRendererDispatcher.instance.render(this.chestTrap, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        } else if (Block.getBlockFromItem(item) instanceof BlockShulkerBox) {
            TileEntityRendererDispatcher.instance.render(
                SHULKER_BOXES[BlockShulkerBox.getColorFromItem(item)
                    .getMetadata()],
                0.0D,
                0.0D,
                0.0D,
                0.0F,
                partialTicks);
        } else if (Block.getBlockFromItem(item) != Blocks.CHEST)
            net.minecraftforge.client.ForgeHooksClient.renderTileItem(p_192838_1_.getItem(), p_192838_1_.getMetadata());
        else {
            TileEntityRendererDispatcher.instance.render(this.chestBasic, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        }
    }

    static {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
            SHULKER_BOXES[enumdyecolor.getMetadata()] = new TileEntityShulkerBox(enumdyecolor);
        }

        instance = new TileEntityItemStackRenderer();
    }
}
