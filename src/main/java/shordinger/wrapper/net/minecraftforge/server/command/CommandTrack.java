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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.command.WrongUsageException;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.world.DimensionType;
import shordinger.wrapper.net.minecraftforge.common.DimensionManager;
import shordinger.wrapper.net.minecraftforge.server.timings.ForgeTimings;
import shordinger.wrapper.net.minecraftforge.server.timings.TimeTracker;

class CommandTrack extends CommandTreeBase {

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

    public CommandTrack() {
        addSubcommand(new StartTrackingCommand());
        addSubcommand(new ResetTrackingCommand());
        addSubcommand(new TrackResultsTileEntity());
        addSubcommand(new TrackResultsEntity());
        addSubcommand(new CommandTreeHelp(this));
    }

    /**
     * Gets the name of the command
     */
    @Override
    public String getName() {
        return "track";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.forge.tracking.usage";
    }

    private static class StartTrackingCommand extends CommandBase {

        /**
         * Callback for when the command is executed
         */
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 2) {
                throw new WrongUsageException(getUsage(sender));
            }
            String type = args[0];
            int duration = parseInt(args[1], 1, 60);
            if ("te".equals(type)) {
                TimeTracker.TILE_ENTITY_UPDATE.reset();
                TimeTracker.TILE_ENTITY_UPDATE.enable(duration);
                sender.sendMessage(
                    TextComponentHelper
                        .createComponentTranslation(sender, "commands.forge.tracking.te.enabled", duration));
            } else if ("entity".equals(type)) {
                TimeTracker.ENTITY_UPDATE.reset();
                TimeTracker.ENTITY_UPDATE.enable(duration);
                sender.sendMessage(
                    TextComponentHelper
                        .createComponentTranslation(sender, "commands.forge.tracking.entity.enabled", duration));
            } else {
                throw new WrongUsageException(getUsage(sender));
            }
        }

        /**
         * Get a list of options for when the user presses the TAB key
         */
        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                              @Nullable BlockPos targetPos) {
            return Arrays.asList("te", "entity");
        }

        /**
         * Gets the name of the command
         */
        @Override
        public String getName() {
            return "start";
        }

        /**
         * Return the required permission level for this command.
         */
        @Override
        public int getRequiredPermissionLevel() {
            return 2;
        }

        /**
         * Gets the usage string for the command.
         */
        @Override
        public String getUsage(ICommandSender sender) {
            return "commands.forge.tracking.start.usage";
        }
    }

    private static class ResetTrackingCommand extends CommandBase {

        /**
         * Gets the name of the command
         */
        @Override
        public String getName() {
            return "reset";
        }

        /**
         * Gets the usage string for the command.
         */
        @Override
        public String getUsage(ICommandSender sender) {
            return "commands.forge.tracking.reset.usage";
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
            if (args.length != 1) {
                throw new WrongUsageException(getUsage(sender));
            }
            String type = args[0];
            if ("te".equals(type)) {
                TimeTracker.TILE_ENTITY_UPDATE.reset();
                sender.sendMessage(
                    TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.reset"));
            } else if ("entity".equals(type)) {
                TimeTracker.ENTITY_UPDATE.reset();
                sender.sendMessage(
                    TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.reset"));
            } else {
                throw new WrongUsageException(getUsage(sender));
            }
        }

        /**
         * Get a list of options for when the user presses the TAB key
         */
        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                              @Nullable BlockPos targetPos) {
            return Arrays.asList("te", "entity");
        }
    }

    /**
     * A base command for all the tracking results commands
     *
     * @param <T>
     */
    private static abstract class TrackResultsBaseCommand<T> extends CommandBase {

        private TimeTracker<T> tracker;

        protected TrackResultsBaseCommand(TimeTracker<T> tracker) {
            this.tracker = tracker;
        }

        /**
         * Returns the time objects recorded by the time tracker sorted by average time
         *
         * @return A list of time objects
         */
        protected List<ForgeTimings<T>> getSortedTimings() {
            ArrayList<ForgeTimings<T>> list = new ArrayList<>();

            list.addAll(tracker.getTimingData());
            list.sort(Comparator.comparingDouble(ForgeTimings::getAverageTimings));
            Collections.reverse(list);

            return list;
        }

        /**
         * Callback for when the command is executed
         */
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            List<ForgeTimings<T>> timingsList = getSortedTimings();
            if (timingsList.isEmpty()) {
                sender.sendMessage(
                    TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.noData"));
            } else {
                timingsList.stream()
                    .filter(
                        timings -> timings.getObject()
                            .get() != null)
                    .limit(10)
                    .forEach(timings -> sender.sendMessage(buildTrackString(sender, timings)));
            }
        }

        protected abstract ITextComponent buildTrackString(ICommandSender sender, ForgeTimings<T> data);

        /**
         * Gets the time suffix for the provided time in nanoseconds
         *
         * @param time The time in nanoseconds
         * @return The time suffix
         */
        protected String getTimeSuffix(double time) {
            if (time < 1000) {
                return "?s";
            } else {
                return "ms";
            }
        }

        /**
         * Translates a world dimension ID into a name
         *
         * @param dimId The dimension ID
         * @return The name of the dimension
         */
        protected String getWorldName(int dimId) {
            DimensionType type = DimensionManager.getProviderType(dimId);
            if (type == null) {
                return "Dim " + dimId;
            } else {
                return type.getName();
            }
        }
    }

    private static class TrackResultsEntity extends TrackResultsBaseCommand<Entity> {

        public TrackResultsEntity() {
            super(TimeTracker.ENTITY_UPDATE);
        }

        /**
         * Gets the name of the command
         */
        @Override
        public String getName() {
            return "entity";
        }

        /**
         * Gets the usage string for the command.
         */
        @Override
        public String getUsage(ICommandSender sender) {
            return "commands.forge.tracking.entity.usage";
        }

        @Override
        protected ITextComponent buildTrackString(ICommandSender sender, ForgeTimings<Entity> data) {
            Entity entity = data.getObject()
                .get();
            if (entity == null)
                return TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.invalid");

            BlockPos currentPos = entity.getPosition();
            String world = getWorldName(entity.world.provider.dimensionId);
            double averageTimings = data.getAverageTimings();
            String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000)
                : TIME_FORMAT.format(averageTimings)) + getTimeSuffix(averageTimings);

            return TextComponentHelper.createComponentTranslation(
                sender,
                "commands.forge.tracking.timingEntry",
                entity.getName(),
                world,
                currentPos.getX(),
                currentPos.getY(),
                currentPos.getZ(),
                tickTime);
        }
    }

    private static class TrackResultsTileEntity extends TrackResultsBaseCommand<TileEntity> {

        public TrackResultsTileEntity() {
            super(TimeTracker.TILE_ENTITY_UPDATE);
        }

        /**
         * Gets the name of the command
         */
        @Override
        public String getName() {
            return "te";
        }

        /**
         * Gets the usage string for the command.
         */
        @Override
        public String getUsage(ICommandSender sender) {
            return "commands.forge.tracking.te.usage";
        }

        @Override
        protected ITextComponent buildTrackString(ICommandSender sender, ForgeTimings<TileEntity> data) {
            TileEntity te = data.getObject()
                .get();
            if (te == null)
                return TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.invalid");

            String name = getTileEntityName(te);
            BlockPos pos = te.getPos();

            double averageTimings = data.getAverageTimings();
            String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000)
                : TIME_FORMAT.format(averageTimings)) + getTimeSuffix(averageTimings);
            return TextComponentHelper.createComponentTranslation(
                sender,
                "commands.forge.tracking.timingEntry",
                name,
                getWorldName(te.getWorld().provider.dimensionId),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                tickTime);
        }

        private String getTileEntityName(TileEntity tileEntity) {
            ResourceLocation registryId = TileEntity.getKey(tileEntity.getClass());
            if (registryId == null) return tileEntity.getClass()
                .getSimpleName();
            else {
                return registryId.toString();
            }
        }
    }
}
