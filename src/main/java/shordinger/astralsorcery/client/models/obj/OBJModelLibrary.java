/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.models.obj;

import shordinger.astralsorcery.client.util.obj.WavefrontObject;
import shordinger.astralsorcery.client.util.resource.AssetLoader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OBJModelLibrary
 * Created by HellFirePvP
 * Date: 16.09.2016 / 15:36
 */
public class OBJModelLibrary {

    public static final WavefrontObject bigCrystal = load("crystal_big");

    public static final WavefrontObject crystalsStage1 = load("c_crystal1");

    private static WavefrontObject load(String name) {
        return AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, name);
    }

    public static void init() {
    } // To invoke static initializer for fields

}
