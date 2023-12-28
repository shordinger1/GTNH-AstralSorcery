/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.data.research.EnumGatedKnowledge;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.base.render.ISpecialStackDescriptor;
import shordinger.astralsorcery.common.item.block.ItemCollectorCrystal;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.CrystalPropertyItem;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.structure.BlockStructureObserver;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCollectorCrystalBase
 * Created by HellFirePvP
 * Date: 15.09.2016 / 19:03
 */
public abstract class BlockCollectorCrystalBase extends BlockStarlightNetwork
    implements ISpecialStackDescriptor, CrystalPropertyItem, BlockStructureObserver {

    private static AxisAlignedBB boxCrystal = new AxisAlignedBB(0.3, 0, 0.3, 0.7, 1, 0.7);

    public BlockCollectorCrystalBase(Material material, MapColor color) {
        super(material, color);
        setBlockUnbreakable();
        setResistance(200000F);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.GLASS);
        setLightLevel(0.7F);
        setCreativeTab(RegistryItems.creativeTabAstralSorceryTunedCrystals);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        Color c = null;
        if (state.getBlock() instanceof BlockCelestialCollectorCrystal) {
            c = CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
        }
        for (int i = 0; i < 1 + world.rand.nextInt(2); i++) {
            AstralSorcery.proxy.fireLightning(
                world,
                new Vector3(target.getBlockPos()).add(0.5, 0.5, 0.5),
                new Vector3(target.getBlockPos()).add(0.5, 0.5, 0.5)
                    .add(-0.5 + world.rand.nextFloat(), -2 + world.rand.nextFloat() * 4, -0.5 + world.rand.nextFloat()),
                c);
        }
        return true;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boxCrystal;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
        Optional<Boolean> missing = CrystalProperties.addPropertyTooltip(prop, tooltip, getMaxSize(stack));

        if (missing.isPresent()) {
            ProgressionTier tier = ResearchManager.clientProgress.getTierReached();
            IWeakConstellation c = ItemCollectorCrystal.getConstellation(stack);
            if (c != null) {
                if (EnumGatedKnowledge.COLLECTOR_TYPE.canSee(tier)
                    && ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                    tooltip.add(
                        TextFormatting.GRAY + I18n
                            .format("crystal.collect.type", TextFormatting.BLUE + I18n.format(c.getUnlocalizedName())));
                    IMinorConstellation tr = ItemCollectorCrystal.getTrait(stack);
                    if (tr != null) {
                        if (EnumGatedKnowledge.CRYSTAL_TRAIT.canSee(tier)
                            && ResearchManager.clientProgress.hasConstellationDiscovered(tr.getUnlocalizedName())) {
                            tooltip.add(
                                TextFormatting.GRAY + I18n.format(
                                    "crystal.trait",
                                    TextFormatting.BLUE + I18n.format(tr.getUnlocalizedName())));
                        } else {
                            tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                        }
                    }
                } else if (!missing.get()) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                }
            }
        }
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        BlockCollectorCrystalBase.CollectorCrystalType type = ItemCollectorCrystal.getType(stack);
        if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            return CrystalProperties.MAX_SIZE_CELESTIAL;
        }
        return CrystalProperties.MAX_SIZE_ROCK;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    /*
     * @Override
     * public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand
     * hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
     * if(!worldIn.isRemote) {
     * TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class);
     * if(te != null) {
     * playerIn.addChatMessage(new TextComponentString("PlayerMade: " + te.isPlayerMade()));
     * playerIn.addChatMessage(new TextComponentString("Constellation: " + te.getTransmittingType().getName()));
     * playerIn.addChatMessage(new TextComponentString("Can charge: " + te.canCharge()));
     * playerIn.addChatMessage(new TextComponentString("Charge: " + te.getCharge()));
     * }
     * }
     * return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
     * }
     */

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class, true);
        if (te != null) {
            if (te.isPlayerMade()) {
                return 4.0F;
            }
        }
        return super.getBlockHardness(blockState, worldIn, pos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        if (!(placer instanceof EntityPlayer)) return;
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class, true);
        if (te == null) return;

        IWeakConstellation c = ItemCollectorCrystal.getConstellation(stack);
        if (c != null) {
            te.onPlace(
                c,
                ItemCollectorCrystal.getTrait(stack),
                CrystalProperties.getCrystalProperties(stack),
                placer.getUniqueID(),
                ItemCollectorCrystal.getType(stack));
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
                         int fortune) {
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorCrystal();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        TileCollectorCrystal te = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, true);
        if (te != null) {
            if (te.getCrystalProperties() == null || te.getConstellation() == null || te.getType() == null) {
                return null;
            }
            ItemStack stack = new ItemStack(this);
            CrystalProperties.applyCrystalProperties(stack, te.getCrystalProperties());
            ItemCollectorCrystal.setConstellation(stack, te.getConstellation());
            ItemCollectorCrystal.setTraitConstellation(stack, te.getTrait());
            ItemCollectorCrystal.setType(stack, te.getType());
            return stack;
        }
        return null;
    }

    @Override
    public String getUnlocalizedName() {
        PlayerProgress client = ResearchManager.clientProgress;
        if (EnumGatedKnowledge.COLLECTOR_CRYSTAL.canSee(client.getTierReached())) {
            return super.getUnlocalizedName();
        }
        return "tile.blockcollectorcrystal.obf";
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class, true);
        if (te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(
                PktParticleEvent.ParticleEventType.COLLECTOR_BURST,
                pos.getX(),
                pos.getY(),
                pos.getZ());
            PacketChannel.CHANNEL.sendToAllAround(event, PacketChannel.pointFromPos(worldIn, pos, 32));
            TileCollectorCrystal.breakDamage(worldIn, pos);

            if (te.isPlayerMade() && !player.isCreative()) {
                ItemStack drop = new ItemStack(
                    te.getType() == CollectorCrystalType.CELESTIAL_CRYSTAL ? BlocksAS.celestialCollectorCrystal
                        : BlocksAS.collectorCrystal);
                if (te.getCrystalProperties() != null && te.getConstellation() != null) {
                    CrystalProperties.applyCrystalProperties(drop, te.getCrystalProperties());
                    ItemCollectorCrystal
                        .setType(drop, te.getType() != null ? te.getType() : CollectorCrystalType.ROCK_CRYSTAL);
                    ItemCollectorCrystal.setConstellation(drop, te.getConstellation());
                    ItemCollectorCrystal.setTraitConstellation(drop, te.getTrait());
                    ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                }
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public static enum CollectorCrystalType {

        ROCK_CRYSTAL(new Color(0xDD, 0xDD, 0xFF)),
        CELESTIAL_CRYSTAL(new Color(0x0, 0x88, 0xFF));

        public final Color displayColor;

        private CollectorCrystalType(Color c) {
            this.displayColor = c;
        }

    }

}
