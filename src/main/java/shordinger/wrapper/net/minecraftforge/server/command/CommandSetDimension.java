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

package shordinger.wrapper.net.minecraftforge.server.command;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.command.WrongUsageException;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.DimensionManager;
import shordinger.wrapper.net.minecraftforge.common.util.ITeleporter;

public class CommandSetDimension extends CommandBase {

    /**
     * Gets the name of the command
     */
    @Override
    public String getName() {
        return "setdimension";
    }

    /**
     * Get a list of aliases for this command. <b>Never return null!</b>
     */
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setdim");
    }

    /**
     * Gets the usage string for the command.
     */
    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.forge.setdim.usage";
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        if (args.length > 2 && args.length <= 5) {
            return getTabCompletionCoordinate(args, 2, targetPos);
        }
        return Collections.emptyList();
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    /**
     * Callback for when the command is executed
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // args: <entity> <dim> [<x> <y> <z>]
        if (args.length != 2 && args.length != 5) {
            throw new WrongUsageException("commands.forge.setdim.usage");
        }
        Entity entity = getEntity(server, sender, args[0]);
        if (!checkEntity(entity)) {
            throw new CommandException("commands.forge.setdim.invalid.entity", entity.getName());
        }
        int dimension = parseInt(args[1]);
        if (!DimensionManager.isDimensionRegistered(dimension)) {
            throw new CommandException("commands.forge.setdim.invalid.dim", dimension);
        }
        if (dimension == entity.dimension) {
            throw new CommandException("commands.forge.setdim.invalid.nochange", entity.getName(), dimension);
        }
        BlockPos pos = args.length == 5 ? parseBlockPos(sender, args, 2, false) : sender.getPosition();
        entity.changeDimension(dimension, new CommandTeleporter(pos));
    }

    private static boolean checkEntity(Entity entity) {
        // use vanilla portal logic, try to avoid doing anything too silly
        return !entity.isRiding() && !entity.isBeingRidden() && entity.isNonBoss();
    }

    private static class CommandTeleporter implements ITeleporter {

        private final BlockPos targetPos;

        private CommandTeleporter(BlockPos targetPos) {
            this.targetPos = targetPos;
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw) {
            entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
        }
    }
}
