/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world;

import java.util.Random;

import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttribute
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:41
 */
public abstract class WorldGenAttribute {

    public final int attributeVersion;

    public WorldGenAttribute(int attributeVersion) {
        this.attributeVersion = attributeVersion;
    }

    public abstract void generate(Random random, int chunkX, int chunkZ, World world);

}
