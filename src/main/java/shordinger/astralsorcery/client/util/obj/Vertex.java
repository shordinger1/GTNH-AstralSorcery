/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.obj;

/**
 * HellFirePvP@Admin
 * Date: 15.06.2015 / 00:07
 * on WingsExMod
 * Vertex
 */
public class Vertex {

    public float x, y, z;

    public Vertex(float x, float y) {
        this(x, y, 0F);
    }

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
