/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.block.BlockCustomOre;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.util.ItemComparator;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightOreTransmutations
 * Created by HellFirePvP
 * Date: 30.01.2017 / 12:30
 */
public class LightOreTransmutations {

    public static Collection<Transmutation> mtTransmutations = new ArrayList<>(); // Minetweaker cache
    private static Collection<Transmutation> registeredTransmutations = new ArrayList<>();

    private static Collection<Transmutation> localFallback = new ArrayList<>();

    public static void init() {
        registerTransmutation(
            new Transmutation(Blocks.MAGMA.getDefaultState(), Blocks.OBSIDIAN.getDefaultState(), 400.0D));
        registerTransmutation(new Transmutation(Blocks.SAND.getDefaultState(), Blocks.CLAY.getDefaultState(), 400.0D));
        registerTransmutation(
            new Transmutation(Blocks.DIAMOND_ORE.getDefaultState(), Blocks.EMERALD_ORE.getDefaultState(), 1000.0D));
        registerTransmutation(
            new Transmutation(Blocks.NETHER_WART_BLOCK.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), 200.0D));
        registerTransmutation(
            new Transmutation(Blocks.SEA_LANTERN.getDefaultState(), Blocks.LAPIS_BLOCK.getDefaultState(), 200.0D));
        registerTransmutation(
            new Transmutation(Blocks.SANDSTONE.getDefaultState(), Blocks.END_STONE.getDefaultState(), 200.0D));
        registerTransmutation(
            new Transmutation(Blocks.NETHERRACK.getDefaultState(), Blocks.NETHER_BRICK.getDefaultState(), 200.0D));

        registerTransmutation(
            new Transmutation(
                Blocks.IRON_ORE.getDefaultState(),
                BlocksAS.customOre.getDefaultState()
                    .withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.STARMETAL),
                100));
        registerTransmutation(
            new Transmutation(
                Blocks.PUMPKIN,
                Blocks.CAKE.getDefaultState(),
                new ItemStack(Blocks.PUMPKIN),
                new ItemStack(Items.CAKE),
                600.0D));

        cacheLocalFallback();
    }

    private static void cacheLocalFallback() {
        if (localFallback.isEmpty()) {
            localFallback.addAll(registeredTransmutations);
        }
    }

    public static void loadFromFallback() {
        registeredTransmutations.clear();
        registeredTransmutations.addAll(localFallback);
    }

    public static Transmutation tryRemoveTransmutation(ItemStack outRemove, boolean matchMeta) {
        Block b = Block.getBlockFromItem(outRemove.getItem());
        if (b != Blocks.AIR) {
            for (Transmutation tr : registeredTransmutations) {
                if (tr.output.getBlock()
                    .equals(b)) {
                    if (!matchMeta || tr.output.getBlock()
                        .getMetaFromState(tr.output) == outRemove.getMetadata()) {
                        registeredTransmutations.remove(tr);
                        return tr;
                    }
                }
            }
        }
        for (Transmutation tr : registeredTransmutations) {
            if (!tr.outStack.isEmpty() && ItemComparator
                .compare(tr.outStack, outRemove, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                registeredTransmutations.remove(tr);
                return tr;
            }
        }
        return null;
    }

    // Will return itself if successful.
    @Nullable
    public static Transmutation registerTransmutation(Transmutation tr) {
        for (Transmutation t : registeredTransmutations) {
            if (t.matchesInput(tr)) {
                AstralSorcery.log
                    .warn("Tried to register Transmutation that has the same input as an already existing one.");
                return null;
            }
        }
        if (!tr.hasValidInput()) {
            AstralSorcery.log.warn("Tried to register Transmutation with null input - Skipping!");
            return null;
        }
        if (tr.getInputAsBlock()
            .equals(Blocks.CRAFTING_TABLE)) {
            AstralSorcery.log.warn(
                "Cannot register Transmutation of workbench -> something. By default occupied by general crafting which is handled differently.");
            return null;
        }
        if (tr.output == null) {
            AstralSorcery.log.warn("Tried to register Transmutation with null output - Skipping!");
            return null;
        }
        registeredTransmutations.add(tr);
        return tr;
    }

    public static Collection<Transmutation> getRegisteredTransmutations() {
        return Collections.unmodifiableCollection(registeredTransmutations);
    }

    @Nullable
    public static Transmutation searchForTransmutation(IBlockState tryStateIn) {
        for (Transmutation tr : registeredTransmutations) {
            if (tr.matchesInput(tryStateIn)) return tr;
        }
        for (Transmutation tr : mtTransmutations) {
            if (tr.matchesInput(tryStateIn)) return tr;
        }
        return null;
    }

    public static class Transmutation {

        private final MatchingType type;

        private final Block inBlock;

        private final IBlockState input;
        private final IBlockState output;
        private final double cost;

        @Nonnull
        private final ItemStack outStack;
        @Nonnull
        private final ItemStack inStack;

        private IWeakConstellation requiredType = null;

        public Transmutation(Block input, IBlockState output, double cost) {
            this(input, output, null, null, cost);
        }

        public Transmutation(Block input, IBlockState output, @Nonnull ItemStack inputDisplay,
                             @Nonnull ItemStack outputDisplay, double cost) {
            this.type = MatchingType.BLOCK;
            this.input = null;

            this.inBlock = input;
            this.output = output;
            this.cost = cost;
            this.outStack = outputDisplay;
            this.inStack = inputDisplay;
        }

        public Transmutation(IBlockState input, IBlockState output, double cost) {
            this(input, output, null, null, cost);
        }

        public Transmutation(IBlockState input, IBlockState output, @Nonnull ItemStack inputDisplay,
                             @Nonnull ItemStack outputDisplay, double cost) {
            this.type = MatchingType.STATE;
            this.inBlock = null;

            this.input = input;
            this.output = output;
            this.cost = cost;
            this.outStack = outputDisplay;
            this.inStack = inputDisplay;
        }

        public Transmutation setRequiredType(IWeakConstellation requiredType) {
            this.requiredType = requiredType;
            return this;
        }

        public IWeakConstellation getRequiredType() {
            return requiredType;
        }

        public Block getInputAsBlock() {
            switch (type) {
                case STATE:
                    return this.input.getBlock();
                case BLOCK:
                    return this.inBlock;
                default:
                    break;
            }
            return Blocks.AIR;
        }

        public boolean hasValidInput() {
            switch (type) {
                case STATE:
                    return input != null && !input.getBlock()
                        .equals(Blocks.AIR);
                case BLOCK:
                    return inBlock != null && !inBlock.equals(Blocks.AIR);
                default:
                    break;
            }
            return false;
        }

        public boolean matchesInput(IBlockState state) {
            switch (type) {
                case STATE:
                    return input.equals(state);
                case BLOCK:
                    return inBlock.equals(state.getBlock());
                default:
                    break;
            }
            return false;
        }

        public boolean matchesInput(Transmutation other) {
            switch (type) {
                case STATE:
                    switch (other.type) {
                        case STATE:
                            return input.equals(other.input);
                        case BLOCK:
                            return input.getBlock()
                                .equals(other.inBlock);
                        default:
                            break;
                    }
                case BLOCK:
                    switch (other.type) {
                        case STATE:
                            return inBlock.equals(other.input.getBlock());
                        case BLOCK:
                            return inBlock.equals(other.inBlock);
                        default:
                            break;
                    }
                default:
                    break;
            }
            return false;
        }

        public boolean matchesOutput(IBlockState state) {
            return output.equals(state);
        }

        public IBlockState getOutput() {
            return output;
        }

        public double getCost() {
            return cost;
        }

        @Nonnull
        public ItemStack getInputDisplayStack() {
            if (!inStack.isEmpty()) {
                return inStack.copy();
            }
            return ItemUtils.createBlockStack(input);
        }

        @Nonnull
        public ItemStack getOutputDisplayStack() {
            if (!outStack.isEmpty()) {
                return outStack.copy();
            }
            return ItemUtils.createBlockStack(output);
        }

    }

    private static enum MatchingType {

        STATE,
        BLOCK;

    }

}
