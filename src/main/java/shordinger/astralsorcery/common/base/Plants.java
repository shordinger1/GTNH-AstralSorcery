/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Plants
 * Created by HellFirePvP
 * Date: 10.10.2017 / 22:03
 */
public enum Plants {

    FLOWER_RED(Blocks.RED_FLOWER, true),
    FLOWER_YELLOW(Blocks.YELLOW_FLOWER, true),
    GRASS(Blocks.TALLGRASS, true),
    MELON(Blocks.MELON_BLOCK),
    PUMPKIN(Blocks.PUMPKIN, true),
    SAPLING(Blocks.SAPLING, true);

    private static final Random rand = new Random();
    private List<IBlockState> potentialBlockStates = new ArrayList<>();

    private Plants(List<IBlockState> states) {
        this.potentialBlockStates = Lists.newArrayList(states);
    }

    private Plants(IBlockState... states) {
        this.potentialBlockStates = Arrays.asList(states);
    }

    private Plants(IBlockState state) {
        this.potentialBlockStates = Arrays.asList(state);
    }

    private Plants(Block block, boolean unused_computeAll) {
        this(buildStates(block));
    }

    private Plants(Block block) {
        this(block.getDefaultState());
    }

    private Plants(ResourceLocation key, Mods owningMod, boolean computeAll) {
        if (owningMod.isPresent()) {
            Block b = ForgeRegistries.BLOCKS.getValue(key);
            if (b != null && b != Blocks.AIR) {
                if (computeAll) {
                    potentialBlockStates.addAll(buildStates(b));
                } else {
                    potentialBlockStates.add(b.getDefaultState());
                }
            }
        }
    }

    private static List<IBlockState> buildStates(Block block) {
        List<IBlockState> available = block.getBlockState()
            .getValidStates();
        if (available.isEmpty()) {
            available = new LinkedList<>();
            available.add(block.getDefaultState());
        }
        return available;
    }

    public IBlockState getRandomState() {
        return potentialBlockStates.get(rand.nextInt(potentialBlockStates.size()));
    }

    private IBlockState getRandomState_Rec() {
        if (potentialBlockStates.isEmpty()) {
            return getAnyRandomState(); // Unloaded mod. rec call.
        }
        return potentialBlockStates.get(rand.nextInt(potentialBlockStates.size()));
    }

    public static IBlockState getAnyRandomState() {
        return values()[rand.nextInt(values().length)].getRandomState_Rec();
    }

    public static boolean matchesAny(IBlockState test) {
        for (Plants plant : values()) {
            if (plant.potentialBlockStates.isEmpty()) continue;

            for (IBlockState state : plant.potentialBlockStates) {
                if (state.equals(test)) {
                    return true;
                }
            }
        }
        return false;
    }

}
