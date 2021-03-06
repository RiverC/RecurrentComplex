/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.reccomplex.commands;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.utils.ServerTranslations;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 03.08.14.
 */
public class CommandSelecting extends CommandBase
{
    @Override
    public String getName()
    {
        return RCConfig.commandPrefix + "selecting";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender commandSender)
    {
        return ServerTranslations.usage("commands.rcselecting.usage");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException
    {
        if (args.length < 7)
            throw ServerTranslations.wrongUsageException("commands.rcselecting.usage");

        BlockPos p1 = parseBlockPos(commandSender, args, 0, false);
        BlockPos p2 = parseBlockPos(commandSender, args, 3, false);
        String command = buildString(args, 6);

        server.commandManager.executeCommand(new SelectingSender(commandSender, p1, p2), command);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if (args.length >= 1 && args.length <= 3)
            return getTabCompletionCoordinate(args, 0, pos);
        if (args.length >= 4 && args.length <= 6)
            return getTabCompletionCoordinate(args, 3, pos);

        return Collections.emptyList();
    }

    public static class SelectingSender extends DelegatingSender implements SelectionOwner
    {
        private BlockPos point1;
        private BlockPos point2;

        public SelectingSender(ICommandSender sender, BlockPos point1, BlockPos point2)
        {
            super(sender);
            this.point1 = point1;
            this.point2 = point2;
        }

        @Nullable
        @Override
        public BlockPos getSelectedPoint1()
        {
            return point1;
        }

        @Override
        public void setSelectedPoint1(@Nullable BlockPos pos)
        {
            point1 = pos;
        }

        @Nullable
        @Override
        public BlockPos getSelectedPoint2()
        {
            return point2;
        }

        @Override
        public void setSelectedPoint2(@Nullable BlockPos pos)
        {
            point2 = pos;
        }
    }

}
