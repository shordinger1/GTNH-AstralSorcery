/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.entities.EntityItemHighlighted;
import shordinger.astralsorcery.common.item.base.ItemHighlighted;
import shordinger.astralsorcery.common.item.base.render.ItemDynamicColor;
import shordinger.astralsorcery.common.lib.Sounds;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.SoundHelper;
import shordinger.astralsorcery.common.util.WRItemObject;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemConstellationPaper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:16
 */
public class ItemConstellationPaper extends Item implements ItemHighlighted, ItemDynamicColor {

    public ItemConstellationPaper() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorceryPapers);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1));

            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                ItemStack cPaper = new ItemStack(this, 1);
                setConstellation(cPaper, c);
                items.add(cPaper);
            }
        }
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFFFF;
        IConstellation c = getConstellation(stack);
        if (c != null) {
            if (ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                return 0xFF000000 | c.getConstellationColor()
                    .getRGB();
            }
        }
        return 0xFF333333;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        EntityItemHighlighted ei = new EntityItemHighlighted(world, entity.posX, entity.posY, entity.posZ, itemstack);
        ei.setDefaultPickupDelay();
        ei.motionX = entity.motionX;
        ei.motionY = entity.motionY;
        ei.motionZ = entity.motionZ;
        if (entity instanceof EntityItem) {
            ei.setThrower(((EntityItem) entity).getThrower());
            ei.setOwner(((EntityItem) entity).getOwner());
        }
        return ei;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IConstellation c = getConstellation(stack);
        if (c != null && c.canDiscover(Minecraft.getMinecraft().thePlayer, ResearchManager.clientProgress)) {
            tooltip.add(TextFormatting.BLUE + I18n.format(c.getUnlocalizedName()));
        } else {
            tooltip.add(TextFormatting.GRAY + I18n.format("constellation.noInformation"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (itemStackIn.isEmpty()) return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
        if (worldIn.isRemote && getConstellation(itemStackIn) != null) {
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            AstralSorcery.proxy.openGui(
                CommonProxy.EnumGuiId.CONSTELLATION_PAPER,
                playerIn,
                worldIn,
                ConstellationRegistry.getConstellationId(getConstellation(itemStackIn)),
                0,
                0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || entityIn == null || !(entityIn instanceof EntityPlayer)) return;

        IConstellation cst = getConstellation(stack);

        if (cst == null) {
            PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn, Side.SERVER);

            List<IConstellation> constellations = new ArrayList<>();
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                if (c.canDiscover((EntityPlayer) entityIn, progress)) {
                    constellations.add(c);
                }
            }

            for (String strConstellation : progress.getKnownConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellationByName(strConstellation);
                if (c != null) {
                    constellations.remove(c);
                }
            }
            for (String strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellationByName(strConstellation);
                if (c != null) {
                    constellations.remove(c);
                }
            }

            if (!constellations.isEmpty()) {
                List<WRItemObject<IConstellation>> wrp = buildWeightedRandomList(constellations);
                WRItemObject<IConstellation> result = WeightedRandom.getRandomItem(worldIn.rand, wrp);
                setConstellation(stack, result.getValue());
            }
        }

        cst = getConstellation(stack);
        if (cst != null) {
            PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn, Side.SERVER);

            boolean has = false;
            for (String strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellationByName(strConstellation);
                if (c != null && c.equals(cst)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                if (ResearchManager.memorizeConstellation(cst, (EntityPlayer) entityIn)) {
                    entityIn.sendMessage(
                        new TextComponentTranslation(
                            "progress.seen.constellation.chat",
                            new TextComponentTranslation(cst.getUnlocalizedName())
                                .setStyle(new Style().setColor(TextFormatting.GRAY)))
                            .setStyle(new Style().setColor(TextFormatting.BLUE)));
                    if (ResearchManager.clientProgress.getSeenConstellations()
                        .size() == 1) {
                        entityIn.sendMessage(
                            new TextComponentTranslation("progress.seen.constellation.first.chat")
                                .setStyle(new Style().setColor(TextFormatting.BLUE)));
                    }
                }
            }
        }
    }

    private List<WRItemObject<IConstellation>> buildWeightedRandomList(List<IConstellation> constellations) {
        List<WRItemObject<IConstellation>> wrc = new ArrayList<>();
        for (IConstellation c : constellations) {
            WRItemObject<IConstellation> i = new WRItemObject<>(1, c);// (int) (tier.getShowupChance() * 100), c);
            wrc.add(i);
        }
        return wrc;
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        IConstellation c = getConstellation(stack);
        if (c != null) {
            if (ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                return c.getConstellationColor();
            }
            return c.getTierRenderColor();
        }
        return Color.GRAY;
    }

    public static IConstellation getConstellation(ItemStack stack) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) return null;
        return IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public static void setConstellation(ItemStack stack, IConstellation constellation) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) return;
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        constellation.writeToNBT(tag);
    }

}
