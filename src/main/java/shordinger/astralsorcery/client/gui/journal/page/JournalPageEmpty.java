/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.journal.page;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageEmpty
 * Created by HellFirePvP
 * Date: 21.10.2016 / 17:57
 */
public class JournalPageEmpty implements IJournalPage {

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render();
    }

    public static class Render implements IGuiRenderablePage {

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        }

    }

}
