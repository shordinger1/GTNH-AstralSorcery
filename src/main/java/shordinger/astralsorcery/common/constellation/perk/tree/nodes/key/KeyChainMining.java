/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.util.BlockDropCaptureAssist;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.struct.BlockDiscoverer;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyChainMining
 * Created by HellFirePvP
 * Date: 30.07.2018 / 01:09
 */
public class KeyChainMining extends KeyPerk {

    private float chainChance = 0.2F;
    private int chainLength = 4;

    private static boolean chainOngoing = false;

    public KeyChainMining(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                chainLength = cfg.getInt(
                    "ChainLength",
                    getConfigurationSection(),
                    chainLength,
                    2,
                    20,
                    "Defines the maximum length of a single break-chain.");
                chainChance = cfg.getFloat(
                    "ChainChance",
                    getConfigurationSection(),
                    chainChance,
                    0.01F,
                    1F,
                    "Defines the base chance a chain is tried to be built.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.chainChance *= multiplier;
        this.chainLength = MathHelper.ceil(this.chainLength * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (side == Side.SERVER && player instanceof EntityPlayerMP
            && prog.hasPerkEffect(this)
            && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)
            && !player.isSneaking()
            && event.world instanceof WorldServer
            && !player.isCreative()) {
            if (chainOngoing) return;
            chainOngoing = true;
            try {
                WorldServer world = (WorldServer) event.world;
                if (doMiningChain(world, event.getPos(), event.getState(), player, side)) {
                    float doubleChance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN);
                    if (rand.nextFloat() < doubleChance) {
                        while (doMiningChain(world, event.getPos(), event.getState(), player, side)) {
                        }
                    }
                }
            } finally {
                chainOngoing = false;
            }
        }
    }

    private boolean doMiningChain(WorldServer world, BlockPos pos, IBlockState state, EntityPlayer player, Side side) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        float ch = chainChance;
        ch = PerkAttributeHelper.getOrCreateMap(player, side)
            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_MINING_CHAIN_CHANCE, ch);
        if (rand.nextFloat() < ch) {
            float fLength = chainLength;
            fLength = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_MINING_CHAIN_LENGTH, fLength);
            BlockArray chain = BlockDiscoverer.discoverBlocksWithSameStateAroundChain(
                world,
                pos,
                state,
                Math.round(fLength),
                null,
                ((world1, pos1, state1) -> pos1.getY() >= player.getPosition()
                    .getY() && state1.getBlockHardness(world1, pos1) >= 0
                    && world1.getTileEntity(pos1) == null
                    && !world1.isAirBlock(pos1)
                    && state1.getBlock()
                    .canHarvestBlock(world1, pos1, player)));
            if (!chain.isEmpty()) {
                int broken = 0;
                FakePlayer fp = AstralSorcery.proxy.getASFakePlayerServer(world);
                for (BlockPos at : chain.getPattern()
                    .keySet()) {
                    IBlockState atState = WorldHelper.getBlockState(world, at);
                    int exp;
                    try {
                        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, atState, fp);
                        MinecraftForge.EVENT_BUS.post(event);
                        exp = event.getExpToDrop();
                        if (event.isCanceled()) continue;
                    } catch (Exception exc) {
                        continue;
                    }
                    boolean capturing = false;
                    try {
                        BlockDropCaptureAssist.startCapturing();
                        capturing = true;

                        TileEntity te = world.getTileEntity(at);
                        Block block = atState.getBlock();
                        if (block.removedByPlayer(atState, world, at, player, true)) {
                            block.onBlockDestroyedByPlayer(world, at, atState);
                            block.harvestBlock(world, player, at, atState, te, player.getHeldItemMainhand());
                            if (exp > 0) {
                                block.dropXpOnBlockBreak(world, at, exp);
                            }
                            PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.ARCHITECT_PLACE,
                                at);
                            ev.setAdditionalDataLong(Block.getStateId(atState));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 16));
                            broken++;
                        }
                        List<ItemStack> drops = BlockDropCaptureAssist.getCapturedStacksAndStop();
                        capturing = false;
                        Vector3 plPos = Vector3.atEntityCenter(player);
                        for (ItemStack stack : drops) {
                            if (!player.addItemStackToInventory(stack)) {
                                ItemUtils.dropItemNaturally(
                                    player.getEntityWorld(),
                                    plPos.getX() + rand.nextFloat() - rand.nextFloat(),
                                    player.posY,
                                    plPos.getZ() + rand.nextFloat() - rand.nextFloat(),
                                    stack);
                            }
                        }
                    } catch (Exception ignored) {
                    } finally {
                        if (capturing) {
                            BlockDropCaptureAssist.getCapturedStacksAndStop(); // Discard.
                        }
                    }
                }
                return broken >= chain.getPattern()
                    .size() / 2;
            }
        }
        return false;
    }

}
