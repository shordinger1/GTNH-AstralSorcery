/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagList;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.common.base.FluidRarityRegistry;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.base.ISpecialInteractItem;
import shordinger.astralsorcery.common.item.base.render.INBTModel;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktPlayLiquidSpring;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.IStructureAreaOfInfluence;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.SkyCollectionHelper;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelResourceLocation;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.*;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;
import shordinger.wrapper.net.minecraftforge.fluids.FluidRegistry;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemSkyResonator
 * Created by HellFirePvP
 * Date: 17.01.2017 / 00:53
 */
public class ItemSkyResonator extends Item implements INBTModel, ISpecialInteractItem {

    private static Random rand = new Random();

    public ItemSkyResonator() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack enhanced;

            enhanced = new ItemStack(this);
            setEnhanced(enhanced);
            for (ResonatorUpgrade upgrade : ResonatorUpgrade.values()) {
                if (upgrade.obtainable()) {
                    setUpgradeUnlocked(enhanced, upgrade);
                }
            }
            items.add(enhanced);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!isEnhanced(stack)) return;

        ResonatorUpgrade current = getCurrentUpgrade(null, stack);
        for (ResonatorUpgrade upgrade : getUpgrades(stack)) {
            tooltip.add(
                (upgrade.equals(current) ? TextFormatting.AQUA : TextFormatting.BLUE)
                    + I18n.format(upgrade.getUnlocalizedUpgradeName()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (!isEnhanced(stack)) {
            return super.getUnlocalizedName(stack);
        }
        return getCurrentUpgrade(null, stack).getUnlocalizedName();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        if (!worldIn.isRemote && player.isSneaking()) {
            if (cycleUpgrade(player, player.getHeldItem(hand))) {
                return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        ResonatorUpgrade upgr = getCurrentUpgrade(player, stack);
        return upgr == ResonatorUpgrade.AREA_SIZE
            && MiscUtils.getTileAt(world, at, IStructureAreaOfInfluence.class, false) != null;
    }

    @Override
    public boolean onRightClick(World world, BlockPos pos, EntityPlayer player, EnumFacing side, EnumHand hand,
                                ItemStack stack) {
        ResonatorUpgrade upgr = getCurrentUpgrade(player, stack);
        if (upgr == ResonatorUpgrade.AREA_SIZE) {
            if (world.isRemote) {
                IStructureAreaOfInfluence aoe = MiscUtils.getTileAt(world, pos, IStructureAreaOfInfluence.class, false);
                if (aoe != null) {
                    playAoEDisplayEffect(aoe);
                }
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private void playAoEDisplayEffect(IStructureAreaOfInfluence aoe) {
        EffectHandler.getInstance()
            .requestSizePreviewFor(aoe);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!isSelected)
            isSelected = (entityIn instanceof EntityPlayer) && ((EntityPlayer) entityIn).getHeldItemOffhand() == stack;

        if (!worldIn.isRemote) {
            if (isSelected && entityIn instanceof EntityPlayerMP
                && getCurrentUpgrade((EntityPlayerMP) entityIn, stack) == ResonatorUpgrade.FLUID_FIELDS
                && getCurrentUpgrade((EntityPlayer) entityIn, stack).obtainable()) {
                double dstr = ConstellationSkyHandler.getInstance()
                    .getCurrentDaytimeDistribution(worldIn);
                if (dstr <= 1E-4) return;
                if (rand.nextFloat() < dstr && rand.nextInt(15) == 0) {

                    int oX = rand.nextInt(30) * (rand.nextBoolean() ? 1 : -1);
                    int oZ = rand.nextInt(30) * (rand.nextBoolean() ? 1 : -1);

                    BlockPos pos = new BlockPos(entityIn.getPosition()).add(oX, 0, oZ);
                    pos = worldIn.getTopSolidOrLiquidBlock(pos);
                    if (pos.getDistance(
                        MathHelper.floor(entityIn.posX),
                        MathHelper.floor(entityIn.posY),
                        MathHelper.floor(entityIn.posZ)) > 75) {
                        return;
                    }

                    FluidRarityRegistry.ChunkFluidEntry at = FluidRarityRegistry
                        .getChunkEntry(worldIn.getChunkFromBlockCoords(pos));
                    FluidStack display = at == null ? new FluidStack(FluidRegistry.WATER, 1) : at.tryDrain(1, false);
                    if (display == null || display.getFluid() == null) display = new FluidStack(FluidRegistry.WATER, 1);
                    PktPlayLiquidSpring pkt = new PktPlayLiquidSpring(
                        display,
                        new Vector3(pos).add(rand.nextFloat(), 0, rand.nextFloat()));
                    PacketChannel.CHANNEL
                        .sendToAllAround(pkt, PacketChannel.pointFromPos(worldIn, entityIn.getPosition(), 32));
                }
            }
        }
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation defaultModelPath) {
        if (!isEnhanced(stack)) {
            return defaultModelPath;
        }
        String path = defaultModelPath.getResourcePath() + "_upgraded";
        ResonatorUpgrade upgrade = getCurrentUpgrade(getCurrentClientPlayer(), stack);
        path += "_" + upgrade.appendixUpgrade;
        return new ModelResourceLocation(
            new ResourceLocation(defaultModelPath.getResourceDomain(), path),
            defaultModelPath.getVariant());
    }

    @Override
    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation) {
        List<ResourceLocation> out = new LinkedList<>();
        out.add(defaultLocation);
        for (ResonatorUpgrade upgrade : ResonatorUpgrade.values()) {
            if (!upgrade.obtainable()) continue;
            out.add(
                new ResourceLocation(
                    defaultLocation.getResourceDomain(),
                    defaultLocation.getResourcePath() + "_upgraded_" + upgrade.appendixUpgrade));
        }
        return out;
    }

    public static boolean isEnhanced(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        return NBTHelper.getPersistentData(stack)
            .getBoolean("enhanced");
    }

    public static ItemStack setEnhanced(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return stack;
        NBTHelper.getPersistentData(stack)
            .setBoolean("enhanced", true);
        setUpgradeUnlocked(stack, ResonatorUpgrade.STARLIGHT);
        return stack;
    }

    public static boolean cycleUpgrade(@Nonnull EntityPlayer player, ItemStack stack) {
        if (!isEnhanced(stack)) return false;
        ResonatorUpgrade current = getCurrentUpgrade(player, stack);
        ResonatorUpgrade next = getNextSelectableUpgrade(player, stack);
        return next != null && !next.equals(current) && setCurrentUpgrade(player, stack, next);
    }

    @Nullable
    public static ResonatorUpgrade getNextSelectableUpgrade(@Nonnull EntityPlayer viewing, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return null;
        if (!isEnhanced(stack)) return null;
        ResonatorUpgrade current = getCurrentUpgrade(viewing, stack);
        int currentOrd = current.ordinal();
        int test = currentOrd;
        do {
            test++;
            test %= ResonatorUpgrade.values().length;
            ResonatorUpgrade testUpgrade = ResonatorUpgrade.values()[test];
            if (testUpgrade.obtainable() && testUpgrade.canSwitchTo(viewing, stack) && !testUpgrade.equals(current)) {
                return testUpgrade;
            }
        } while (test != currentOrd);
        return null;
    }

    public static boolean setCurrentUpgrade(EntityPlayer setting, ItemStack stack, ResonatorUpgrade upgrade) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        if (upgrade.obtainable() && upgrade.canSwitchTo(setting, stack)) {
            NBTHelper.getPersistentData(stack)
                .setInteger("selected_upgrade", upgrade.ordinal());
            return true;
        }
        return false;
    }

    public static ItemStack setCurrentUpgradeUnsafe(ItemStack stack, ResonatorUpgrade upgrade) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator) || !upgrade.obtainable()) return stack;
        NBTHelper.getPersistentData(stack)
            .setInteger("selected_upgrade", upgrade.ordinal());
        return stack;
    }

    @Nonnull
    public static ResonatorUpgrade getCurrentUpgrade(@Nullable EntityPlayer viewing, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator))
            return ResonatorUpgrade.STARLIGHT; // Fallback
        if (!isEnhanced(stack)) return ResonatorUpgrade.STARLIGHT;
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        int current = cmp.getInteger("selected_upgrade");
        ResonatorUpgrade upgrade = ResonatorUpgrade.values()[MathHelper
            .clamp(current, 0, ResonatorUpgrade.values().length - 1)];
        if (!upgrade.obtainable()) {
            return ResonatorUpgrade.STARLIGHT;
        }
        if (viewing != null) {
            if (!upgrade.canSwitchTo(viewing, stack)) {
                return ResonatorUpgrade.STARLIGHT;
            }
        }
        return upgrade;
    }

    public static ItemStack setUpgradeUnlocked(ItemStack stack, ResonatorUpgrade upgrade) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return stack;
        if (!isEnhanced(stack)) return stack;
        if (upgrade.obtainable()) {
            upgrade.applyUpgrade(stack);
        }
        return stack;
    }

    public static boolean hasUpgrade(ItemStack stack, ResonatorUpgrade upgrade) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        if (!isEnhanced(stack)) return false;
        if (!upgrade.obtainable()) return false;
        return upgrade.hasUpgrade(stack);
    }

    public static List<ResonatorUpgrade> getUpgrades(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return Lists.newArrayList();
        if (!isEnhanced(stack)) return Lists.newArrayList(ResonatorUpgrade.STARLIGHT);
        List<ResonatorUpgrade> upgrades = Lists.newLinkedList();
        for (ResonatorUpgrade ru : ResonatorUpgrade.values()) {
            if (ru.hasUpgrade(stack) && ru.obtainable()) {
                upgrades.add(ru);
            }
        }
        return upgrades;
    }

    public static enum ResonatorUpgrade {

        STARLIGHT("starlight", (p, s) -> true),
        FLUID_FIELDS("liquid", (p, s) -> ResearchManager.getProgress(p)
            .getTierReached()
            .isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)),
        AREA_SIZE("structure", (p, s) -> ResearchManager.getProgress(p)
            .getTierReached()
            .isThisLaterOrEqual(ProgressionTier.ATTUNEMENT));

        private final ResonatorUpgradeCheck check;
        private final String appendixUpgrade;

        private ResonatorUpgrade(String appendixUpgrade, ResonatorUpgradeCheck check) {
            this.check = check;
            this.appendixUpgrade = appendixUpgrade;
        }

        public String getUnlocalizedName() {
            return "item.itemskyresonator." + appendixUpgrade;
        }

        public String getUnlocalizedUpgradeName() {
            return "item.itemskyresonator.upgrade." + appendixUpgrade + ".name";
        }

        public boolean obtainable() {
            return true;
        }

        public boolean hasUpgrade(ItemStack stack) {
            int id = ordinal();
            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            if (cmp.hasKey("upgrades", Constants.NBT.TAG_LIST)) {
                NBTTagList list = cmp.getTagList("upgrades", Constants.NBT.TAG_INT);
                for (int i = 0; i < list.tagCount(); i++) {
                    if (list.func_150306_c(i)[i] == id) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean canSwitchTo(@Nonnull EntityPlayer player, ItemStack stack) {
            return hasUpgrade(stack) && check.hasAccessToUpgrade(player, stack);
        }

        public void applyUpgrade(ItemStack stack) {
            if (hasUpgrade(stack)) return;

            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            if (!cmp.hasKey("upgrades", Constants.NBT.TAG_LIST)) {
                cmp.setTag("upgrades", new NBTTagList());
            }
            NBTTagList list = cmp.getTagList("upgrades", Constants.NBT.TAG_INT);
            list.appendTag(new NBTTagInt(ordinal()));
        }

        @SideOnly(Side.CLIENT)
        public void playResonatorEffects() {
            switch (this) {
                case STARLIGHT:
                    playStarlightFieldEffect();
                    break;
                default:
                    break;
            }
        }

        @SideOnly(Side.CLIENT)
        private void playStarlightFieldEffect() {
            if (!ConstellationSkyHandler.getInstance()
                .getSeedIfPresent(Minecraft.getMinecraft().world)
                .isPresent()) return;

            float nightPerc = ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
            if (nightPerc >= 0.05) {
                Color c = new Color(0, 6, 58);
                BlockPos center = Minecraft.getMinecraft().player.getPosition();
                int offsetX = center.getX();
                int offsetZ = center.getZ();
                BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain(center);

                for (int xx = -30; xx <= 30; xx++) {
                    for (int zz = -30; zz <= 30; zz++) {

                        BlockPos top = Minecraft.getMinecraft().world
                            .getTopSolidOrLiquidBlock(pos.setPos(offsetX + xx, 0, offsetZ + zz));
                        // Can be force unwrapped since statement 2nd Line prevents non-present values.
                        Float opF = SkyCollectionHelper
                            .getSkyNoiseDistributionClient(Minecraft.getMinecraft().world, top)
                            .get();

                        float fPerc = (float) Math.pow((opF - 0.4F) * 1.65F, 2);
                        if (opF >= 0.4F && rand.nextFloat() <= fPerc) {
                            if (rand.nextFloat() <= fPerc && rand.nextInt(6) == 0) {
                                EffectHelper
                                    .genericFlareParticle(
                                        top.getX() + rand.nextFloat(),
                                        top.getY() + 0.15,
                                        top.getZ() + rand.nextFloat())
                                    .scale(4F)
                                    .setColor(c)
                                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                                    .gravity(0.004)
                                    .setAlphaMultiplier(nightPerc * fPerc);
                                if (opF >= 0.8F && rand.nextInt(3) == 0) {
                                    EffectHelper
                                        .genericFlareParticle(
                                            top.getX() + rand.nextFloat(),
                                            top.getY() + 0.15,
                                            top.getZ() + rand.nextFloat())
                                        .scale(0.3F)
                                        .setColor(Color.WHITE)
                                        .gravity(0.01)
                                        .setAlphaMultiplier(nightPerc);
                                }
                            }
                        }
                    }
                }

                pos.release();
            }
        }
    }

    public static interface ResonatorUpgradeCheck {

        public boolean hasAccessToUpgrade(@Nonnull EntityPlayer player, ItemStack stack);

    }

}
