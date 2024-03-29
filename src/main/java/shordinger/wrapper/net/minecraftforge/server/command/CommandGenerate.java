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
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraftforge.common.WorldWorkerManager;

class CommandGenerate extends CommandBase {

    /**
     * Gets the name of the command
     */
    @Override
    public String getName() {
        return "generate";
    }

    /**
     * Get a list of aliases for this command. <b>Never return null!</b>
     */
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("gen");
    }

    /**
     * Gets the usage string for the command.
     */
    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.forge.gen.usage";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    /**
     * Callback for when the command is executed
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // x y z chunkCount [dim] [interval]
        if (args.length < 4) {
            throw new WrongUsageException("commands.forge.gen.usage");
        }

        BlockPos blockpos = parseBlockPos(sender, args, 0, false);
        int count = parseInt(args[3], 10);
        int dim = args.length >= 5 ? parseInt(args[4]) : sender.getEntityWorld().provider.getDimension();
        int interval = args.length >= 6 ? parseInt(args[5]) : -1;
        BlockPos chunkpos = new BlockPos(blockpos.getX() >> 4, 0, blockpos.getZ() >> 4);

        ChunkGenWorker worker = new ChunkGenWorker(sender, chunkpos, count, dim, interval);
        sender.sendMessage(worker.getStartMessage(sender));
        WorldWorkerManager.addWorker(worker);
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        if (args.length < 4) {
            return getTabCompletionCoordinate(args, 0, targetPos);
        }
        // Chunk Count? No completion
        // Dimension, Add support for names? Get list of ids? Meh
        return Collections.emptyList();
    }
}
