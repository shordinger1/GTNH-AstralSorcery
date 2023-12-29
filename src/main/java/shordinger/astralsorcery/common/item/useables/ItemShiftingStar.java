/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.useables;

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.base.render.INBTModel;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.SoundHelper;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemShiftingStar
 * Created by HellFirePvP
 * Date: 09.02.2017 / 23:05
 */
public class ItemShiftingStar extends Item implements INBTModel {

    public ItemShiftingStar() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation suggestedDefaultLocation) {
        IMajorConstellation cst = getAttunement(stack);
        if (cst != null) {
            return new ModelResourceLocation(
                new ResourceLocation(
                    suggestedDefaultLocation.getResourceDomain(),
                    suggestedDefaultLocation.getResourcePath() + "_" + cst.getSimpleName()),
                suggestedDefaultLocation.getVariant());
        }
        return suggestedDefaultLocation;
    }

    @Override
    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation) {
        List<ResourceLocation> all = Lists.newArrayList();
        all.add(defaultLocation);
        for (IMajorConstellation cst : ConstellationRegistry.getMajorConstellations()) {
            all.add(
                new ResourceLocation(
                    defaultLocation.getResourceDomain(),
                    defaultLocation.getResourcePath() + "_" + cst.getSimpleName()));
        }
        return all;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            for (IMajorConstellation cst : ConstellationRegistry.getMajorConstellations()) {
                ItemStack star = new ItemStack(this);
                setAttunement(star, cst);
                items.add(star);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IMajorConstellation cst;
        if ((cst = getAttunement(stack)) != null) {
            PlayerProgress prog = ResearchManager.clientProgress;
            if (prog != null) {
                if (prog.hasConstellationDiscovered(cst.getUnlocalizedName())) {
                    tooltip.add(TextFormatting.BLUE + I18n.format(cst.getUnlocalizedName()));
                } else {
                    tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                }
            }
        }
    }

    public static ItemStack createStack(@Nullable IMajorConstellation cst) {
        ItemStack stack = new ItemStack(ItemsAS.shiftingStar);
        if (cst != null) {
            setAttunement(stack, cst);
        }
        return stack;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String unloc = super.getUnlocalizedName(stack);
        if (getAttunement(stack) != null) {
            unloc += ".enhanced";
        }
        return unloc;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return getAttunement(stack) != null ? RegistryItems.rarityRelic : super.getRarity(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn) {
        playerIn.setActiveHand(hand);
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!worldIn.isRemote && entityLiving instanceof EntityPlayerMP) {
            EntityPlayer pl = (EntityPlayer) entityLiving;
            IMajorConstellation cst;
            if ((cst = getAttunement(stack)) != null) {
                PlayerProgress prog = ResearchManager.getProgress(pl, Side.SERVER);
                if (!prog.isValid() || !prog.wasOnceAttuned()
                    || !prog.hasConstellationDiscovered(cst.getUnlocalizedName())) {
                    return stack;
                }
                double exp = prog.getPerkExp();
                if (ResearchManager.setAttunedConstellation(pl, cst)) {
                    ResearchManager.setExp(pl, MathHelper.lfloor(exp));
                    pl.sendMessage(
                        new TextComponentTranslation("progress.switch.attunement")
                            .setStyle(new Style().setColor(TextFormatting.BLUE)));
                    SoundHelper
                        .playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, worldIn, entityLiving.getPosition(), 1F, 1F);
                } else {
                    return stack;
                }
            } else if (ResearchManager.setAttunedConstellation(pl, null)) {
                pl.sendMessage(
                    new TextComponentTranslation("progress.remove.attunement")
                        .setStyle(new Style().setColor(TextFormatting.BLUE)));
                SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, worldIn, entityLiving.getPosition(), 1F, 1F);
            } else {
                return stack;
            }
        }
        return null;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player.worldObj.isRemote) {
            playEffects(
                player,
                getAttunement(stack),
                getMaxItemUseDuration(stack) - count,
                getMaxItemUseDuration(stack));
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEffects(EntityLivingBase pl, @Nullable IMajorConstellation attunement, int tick, int total) {
        if (attunement != null) {
            float percCycle = (float) ((((float) (tick % total)) / ((float) total)) * 2 * Math.PI);
            int parts = 5;
            for (int i = 0; i < parts; i++) {
                // outer
                float angleSwirl = 75F;
                Vector3 center = Vector3.atEntityCorner(pl)
                    .setY(pl.posY);
                Vector3 v = new Vector3(1, 0, 0);
                float originalAngle = (((float) i) / ((float) parts)) * 360F;
                double angle = originalAngle + (MathHelper.sin(percCycle) * angleSwirl);
                v.rotate(-Math.toRadians(angle), Vector3.RotAxis.Y_AXIS)
                    .normalize()
                    .multiply(4);
                Vector3 pos = center.clone()
                    .add(v);

                Vector3 mot = center.clone()
                    .subtract(pos)
                    .normalize()
                    .multiply(0.1);

                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos);
                particle.gravity(0.004)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                    .scale(itemRand.nextFloat() * 0.4F + 0.27F);
                particle.setMaxAge(50);
                particle.scale(0.2F + itemRand.nextFloat());
                if (itemRand.nextInt(4) == 0) {
                    particle.setColor(Color.WHITE);
                } else if (itemRand.nextInt(3) == 0) {
                    particle.setColor(
                        attunement.getConstellationColor()
                            .brighter());
                } else {
                    particle.setColor(attunement.getConstellationColor());
                }
                particle.motion(mot.getX(), mot.getY(), mot.getZ());
            }
        } else {
            for (int i = 0; i < 3; i++) {
                EntityFXFacingParticle particle = EffectHelper
                    .genericFlareParticle(pl.posX, pl.posY + pl.getEyeHeight() / 2, pl.posZ);
                particle.motion(-0.1 + itemRand.nextFloat() * 0.2, 0.01, -0.1 + itemRand.nextFloat() * 0.2);
                if (itemRand.nextInt(3) == 0) {
                    particle.setColor(Color.WHITE);
                }
                particle.scale(0.2F + itemRand.nextFloat());
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return getAttunement(stack) == null ? 60 : 100;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    public static void setAttunement(ItemStack stack, IMajorConstellation cst) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemShiftingStar)) {
            return;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cmp.setString("starAttunement", cst.getUnlocalizedName());
    }

    @Nullable
    public static IMajorConstellation getAttunement(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemShiftingStar)) {
            return null;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("starAttunement")) {
            return null;
        }
        return ConstellationRegistry.getMajorConstellationByName(cmp.getString("starAttunement"));
    }

}
