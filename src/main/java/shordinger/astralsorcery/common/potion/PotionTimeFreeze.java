/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.potion;

import java.awt.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionTimeFreeze
 * Created by HellFirePvP
 * Date: 12.02.2018 / 23:03
 */
public class PotionTimeFreeze extends PotionCustomTexture {

    private static Object texBuffer = null;

    public static final Color TIMEFREEZE_COLOR = new Color(0xB89AFF);

    public PotionTimeFreeze() {
        super(false, 0xB89AFF);
        setPotionName("effect.as.timefreeze");
        setBeneficial();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if (texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_timefreeze");
        }
        return (BindableResource) texBuffer;
    }
}
