/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.resource;

import shordinger.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RowSpriteSheetResource
 * Created by HellFirePvP
 * Date: 23.06.2018 / 14:23
 */
public class RowSpriteSheetResource extends SpriteSheetResource {

    protected double vOffset;

    public RowSpriteSheetResource(AbstractRenderableTexture resource, int rowsTotal, int posRow, int columns) {
        super(resource, 1, columns);

        this.vPart = 1D / rowsTotal;
        this.vOffset = this.vPart * posRow;
    }

    public static RowSpriteSheetResource crop(SpriteSheetResource resource, int posRow) {
        return new RowSpriteSheetResource(resource.getResource(), resource.rows, posRow, resource.columns);
    }

    @Override
    public Tuple<Double, Double> getUVOffset(long frameTimer) {
        int frame = (int) (frameTimer % frameCount);
        return new Tuple<>((frame % columns) * uPart, this.vOffset);
    }
}
