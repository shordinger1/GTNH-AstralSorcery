/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.base.render.INBTModel;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktProgressionUpdate;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemKnowledgeShare
 * Created by HellFirePvP
 * Date: 05.07.2017 / 11:39
 */
public class ItemKnowledgeShare extends Item implements INBTModel {

    public ItemKnowledgeShare() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack creative = new ItemStack(this);
            setCreative(creative);
            items.add(creative);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (isCreative(stack)) {
            tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("misc.knowledge.inscribed.creative"));
            return;
        }
        if (getKnowledge(stack) == null) {
            tooltip.add(I18n.format("misc.knowledge.missing"));
        } else {
            String name = getKnowledgeOwnerName(stack);
            if (name != null) {
                tooltip.add(I18n.format("misc.knowledge.inscribed", (TextFormatting.BLUE + name)));
            }
        }
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation suggestedDefaultLocation) {
        if (isCreative(stack) || getKnowledgeOwnerName(stack) != null) {
            return new ModelResourceLocation(
                new ResourceLocation(
                    suggestedDefaultLocation.getResourceDomain(),
                    suggestedDefaultLocation.getResourcePath() + "_written"),
                suggestedDefaultLocation.getVariant());
        }
        return suggestedDefaultLocation;
    }

    @Override
    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation) {
        List<ResourceLocation> out = new LinkedList<>();
        out.add(defaultLocation);
        out.add(
            new ResourceLocation(defaultLocation.getResourceDomain(), defaultLocation.getResourcePath() + "_written"));
        return out;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerInIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (stack.stackSize==0 || worldIn.isRemote) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }
        if (!isCreative(stack) && (playerIn.isSneaking() || getKnowledge(stack) == null)) {
            tryInscribeKnowledge(stack, playerIn);
        } else {
            tryGiveKnowledge(stack, playerIn);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.stackSize==0 || worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        if (!isCreative(stack) && (player.isSneaking() || getKnowledge(stack) == null)) {
            tryInscribeKnowledge(stack, player);
        } else {
            tryGiveKnowledge(stack, player);
        }
        return EnumActionResult.SUCCESS;
    }

    private void tryGiveKnowledge(ItemStack stack, EntityPlayer player) {
        if (player instanceof EntityPlayerMP && MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            return;
        }

        if (isCreative(stack)) {
            ResearchManager.forceMaximizeAll(player);
            return;
        }
        if (canInscribeKnowledge(stack, player)) return; // Means it's either empty or the player that has incsribed the
        // knowledge is trying to use it.
        PlayerProgress progress = getKnowledge(stack);
        if (progress == null) return;
        ProgressionTier prev = progress.getTierReached();
        if (ResearchManager.mergeApplyPlayerprogress(progress, player) && progress.getTierReached()
            .isThisLater(prev)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(progress.getTierReached());
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }
    }

    private void tryInscribeKnowledge(ItemStack stack, EntityPlayer player) {
        if (canInscribeKnowledge(stack, player)) {
            setKnowledge(stack, player, ResearchManager.getProgress(player, Side.SERVER));
        }
    }

    @Nullable
    public EntityPlayer getKnowledgeOwner(ItemStack stack, MinecraftServer server) {
        if (isCreative(stack)) return null;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasUniqueId("knowledgeOwnerUUID")) {
            return null;
        }
        UUID owner = compound.getUniqueId("knowledgeOwnerUUID");
        return server.getPlayerList()
            .getPlayerByUUID(owner);
    }

    @Nullable
    public String getKnowledgeOwnerName(ItemStack stack) {
        if (isCreative(stack)) return null;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasKey("knowledgeOwnerName")) {
            return null;
        }
        return compound.getString("knowledgeOwnerName");
    }

    @Nullable
    public PlayerProgress getKnowledge(ItemStack stack) {
        if (isCreative(stack)) return null;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasKey("knowledgeTag")) {
            return null;
        }
        NBTTagCompound tag = compound.getCompoundTag("knowledgeTag");
        try {
            PlayerProgress progress = new PlayerProgress();
            progress.loadKnowledge(tag);
            return progress;
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean canInscribeKnowledge(ItemStack stack, EntityPlayer player) {
        if (isCreative(stack)) return false;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasUniqueId("knowledgeOwnerUUID")) {
            return true;
        }
        UUID owner = compound.getUniqueId("knowledgeOwnerUUID");
        return player.getUniqueID()
            .equals(owner);
    }

    public void setKnowledge(ItemStack stack, EntityPlayer player, PlayerProgress progress) {
        if (isCreative(stack) || !progress.isValid()) return;

        NBTTagCompound knowledge = new NBTTagCompound();
        progress.storeKnowledge(knowledge);
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        compound.setString("knowledgeOwnerName", player.getDisplayName());
        compound.setUniqueId("knowledgeOwnerUUID", player.getUniqueID());
        compound.setTag("knowledgeTag", knowledge);
    }

    public boolean isCreative(ItemStack stack) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("creativeKnowledge")) {
            return false;
        }
        return cmp.getBoolean("creativeKnowledge");
    }

    private void setCreative(ItemStack stack) {
        NBTHelper.getPersistentData(stack)
            .setBoolean("creativeKnowledge", true);
    }

}
