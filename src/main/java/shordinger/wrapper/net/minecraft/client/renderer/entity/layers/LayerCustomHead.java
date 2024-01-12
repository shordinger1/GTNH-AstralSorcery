package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.model.ModelRenderer;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombieVillager;
import shordinger.wrapper.net.minecraft.entity.passive.EntityVillager;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemArmor;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTUtil;
import shordinger.wrapper.net.minecraft.tileentity.TileEntitySkull;
import shordinger.wrapper.net.minecraft.util.EnumFacing;

@SideOnly(Side.CLIENT)
public class LayerCustomHead implements LayerRenderer<EntityLivingBase> {

    private final ModelRenderer modelRenderer;

    public LayerCustomHead(ModelRenderer p_i46120_1_) {
        this.modelRenderer = p_i46120_1_;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        if (!itemstack.isEmpty()) {
            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();

            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            boolean flag = entitylivingbaseIn instanceof EntityVillager
                || entitylivingbaseIn instanceof EntityZombieVillager;

            if (entitylivingbaseIn.isChild() && !(entitylivingbaseIn instanceof EntityVillager)) {
                float f = 2.0F;
                float f1 = 1.4F;
                GlStateManager.translate(0.0F, 0.5F * scale, 0.0F);
                GlStateManager.scale(0.7F, 0.7F, 0.7F);
                GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            this.modelRenderer.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (item == Items.SKULL) {
                float f2 = 1.1875F;
                GlStateManager.scale(1.1875F, -1.1875F, -1.1875F);

                if (flag) {
                    GlStateManager.translate(0.0F, 0.0625F, 0.0F);
                }

                GameProfile gameprofile = null;

                if (itemstack.hasTagCompound()) {
                    NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                    if (nbttagcompound.hasKey("SkullOwner", 10)) {
                        gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                    } else if (nbttagcompound.hasKey("SkullOwner", 8)) {
                        String s = nbttagcompound.getString("SkullOwner");

                        if (!StringUtils.isBlank(s)) {
                            gameprofile = TileEntitySkull.updateGameprofile(new GameProfile((UUID) null, s));
                            nbttagcompound
                                .setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                        }
                    }
                }

                TileEntitySkullRenderer.instance.renderSkull(
                    -0.5F,
                    0.0F,
                    -0.5F,
                    EnumFacing.UP,
                    180.0F,
                    itemstack.getMetadata(),
                    gameprofile,
                    -1,
                    limbSwing);
            } else if (!(item instanceof ItemArmor) || ((ItemArmor) item).getEquipmentSlot() != EntityEquipmentSlot.HEAD) {
                float f3 = 0.625F;
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(0.625F, -0.625F, -0.625F);

                if (flag) {
                    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                }

                minecraft.getItemRenderer()
                    .renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD);
            }

            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
