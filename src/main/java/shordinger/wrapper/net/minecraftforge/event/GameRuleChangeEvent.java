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

package shordinger.wrapper.net.minecraftforge.event;

import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.world.GameRules;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a game rule is changed,
 * via {@link net.minecraft.command.CommandGameRule#notifyGameRuleChange(GameRules, String, MinecraftServer)}.
 * <p>
 * This allows updating clients with the effects of server rule changes.
 */
public class GameRuleChangeEvent extends Event {

    private final GameRules rules;
    private final String ruleName;
    private final MinecraftServer server;

    public GameRuleChangeEvent(GameRules rules, String ruleName, MinecraftServer server) {
        this.rules = rules;
        this.ruleName = ruleName;
        this.server = server;
    }

    public GameRules getRules() {
        return rules;
    }

    public String getRuleName() {
        return ruleName;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
