/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.resource;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteQuery
 * Created by HellFirePvP
 * Date: 31.03.2017 / 14:29
 */
public class SpriteQuery extends TextureQuery {

    private final int rows, columns;

    private Object spriteResource;

    public SpriteQuery(AssetLoader.TextureLocation location, String name, int rows, int columns) {
        super(location, name);
        this.rows = rows;
        this.columns = columns;
    }

    private SpriteQuery(Object spriteResource, int rows, int columns) {
        super(null, "");
        this.spriteResource = spriteResource;
        this.rows = rows;
        this.columns = columns;
    }

    @SideOnly(Side.CLIENT)
    public static SpriteQuery of(SpriteSheetResource res) {
        return new SpriteQuery(res, res.getRows(), res.getColumns());
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @SideOnly(Side.CLIENT)
    public SpriteSheetResource resolveSprite() {
        if (spriteResource == null) {
            AbstractRenderableTexture res = resolve();
            spriteResource = new SpriteSheetResource(res, getRows(), getColumns());
        }
        return (SpriteSheetResource) spriteResource;
    }

}
