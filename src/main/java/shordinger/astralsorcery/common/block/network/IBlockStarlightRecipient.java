/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import java.util.Random;

import net.minecraft.world.World;

import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IBlockStarlightRecipient
 * Created by HellFirePvP
 * Date: 04.08.2016 / 22:27
 */
public interface IBlockStarlightRecipient {

    /**
     * Called when this block receives starlight from the network broadcast
     * This is only called if the chunk it is in is also loaded!
     * <p>
     * For a Chunk independent implementation check the tile entities and ITransmissionReceiver
     * as well as its implementations
     * <p>
     * Note that this is only fired if this block is a block linked to an endpoint of a network
     * and if this block is not a transmission node.
     *
     * @param world         the world this block instance is in
     * @param rand          a world-independent random for convenience
     * @param pos           the position
     * @param starlightType the constellation type of the starlight received
     * @param amount        the amount received
     */
    public void receiveStarlight(World world, Random rand, BlockPos pos, IWeakConstellation starlightType,
                                 double amount);

}
