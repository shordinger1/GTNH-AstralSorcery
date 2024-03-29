/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.fml.client;

import java.util.List;

import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraftforge.fml.common.Loader;
import shordinger.wrapper.net.minecraftforge.fml.common.MissingModsException;
import shordinger.wrapper.net.minecraftforge.fml.common.ModContainer;
import shordinger.wrapper.net.minecraftforge.fml.common.versioning.ArtifactVersion;
import shordinger.wrapper.net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class GuiModsMissing extends GuiErrorBase {

    private MissingModsException modsMissing;

    public GuiModsMissing(MissingModsException modsMissing) {
        this.modsMissing = modsMissing;
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        List<MissingModsException.MissingModInfo> missingModsVersions = modsMissing.getMissingModInfos();
        int offset = Math.max(85 - missingModsVersions.size() * 10, 10);
        String modMissingDependenciesText = I18n.format(
            "fml.messages.mod.missing.dependencies.compatibility",
            TextFormatting.BOLD + modsMissing.getModName() + TextFormatting.RESET);
        this.drawCenteredString(this.fontRenderer, modMissingDependenciesText, this.width / 2, offset, 0xFFFFFF);
        offset += 5;
        for (MissingModsException.MissingModInfo versionInfo : missingModsVersions) {
            ArtifactVersion acceptedVersion = versionInfo.getAcceptedVersion();
            String acceptedModId = acceptedVersion.getLabel();
            ArtifactVersion currentVersion = versionInfo.getCurrentVersion();
            String missingReason;
            if (currentVersion == null) {
                missingReason = I18n.format("fml.messages.mod.missing.dependencies.missing");
            } else {
                missingReason = I18n
                    .format("fml.messages.mod.missing.dependencies.you.have", currentVersion.getVersionString());
            }
            String acceptedModVersionString = acceptedVersion.getRangeString();
            if (acceptedVersion instanceof DefaultArtifactVersion) {
                DefaultArtifactVersion dav = (DefaultArtifactVersion) acceptedVersion;
                if (dav.getRange() != null) {
                    acceptedModVersionString = dav.getRange()
                        .toStringFriendly();
                }
            }
            ModContainer acceptedMod = Loader.instance()
                .getIndexedModList()
                .get(acceptedModId);
            String acceptedModName = acceptedMod != null ? acceptedMod.getName() : acceptedModId;
            String versionInfoText = String.format(
                TextFormatting.BOLD + "%s " + TextFormatting.RESET + "%s (%s)",
                acceptedModName,
                acceptedModVersionString,
                missingReason);
            String message;
            if (versionInfo.isRequired()) {
                message = I18n.format("fml.messages.mod.missing.dependencies.requires", versionInfoText);
            } else {
                message = I18n.format("fml.messages.mod.missing.dependencies.compatible.with", versionInfoText);
            }
            offset += 10;
            this.drawCenteredString(this.fontRenderer, message, this.width / 2, offset, 0xEEEEEE);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
