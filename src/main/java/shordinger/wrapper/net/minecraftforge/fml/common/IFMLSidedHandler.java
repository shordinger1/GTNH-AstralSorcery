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

package shordinger.wrapper.net.minecraftforge.fml.common;

import java.io.File;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import shordinger.wrapper.net.minecraft.network.INetHandler;
import shordinger.wrapper.net.minecraft.network.NetworkManager;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.IThreadListener;
import shordinger.wrapper.net.minecraftforge.common.util.CompoundDataFixer;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventBus;

public interface IFMLSidedHandler {

    List<String> getAdditionalBrandingInformation();

    Side getSide();

    void haltGame(String message, Throwable exception);

    void showGuiScreen(Object clientGuiElement);

    void queryUser(StartupQuery query) throws InterruptedException;

    void beginServerLoading(MinecraftServer server);

    void finishServerLoading();

    File getSavesDirectory();

    MinecraftServer getServer();

    boolean isDisplayCloseRequested();

    boolean shouldServerShouldBeKilledQuietly();

    void addModAsResource(ModContainer container);

    String getCurrentLanguage();

    void serverStopped();

    NetworkManager getClientToServerNetworkManager();

    INetHandler getClientPlayHandler();

    void fireNetRegistrationEvent(EventBus bus, NetworkManager manager, Set<String> channelSet, String channel,
                                  Side side);

    boolean shouldAllowPlayerLogins();

    void allowLogins();

    IThreadListener getWorldThread(INetHandler net);

    void processWindowMessages();

    String stripSpecialChars(String message);

    void reloadRenderers();

    void fireSidedRegistryEvents();

    CompoundDataFixer getDataFixer();

    boolean isDisplayVSyncForced();

    default void resetClientRecipeBook() {
    }

    default void reloadSearchTrees() {
    }

    default void reloadCreativeSettings() {
    }
}
