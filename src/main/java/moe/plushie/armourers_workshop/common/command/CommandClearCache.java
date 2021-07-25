package moe.plushie.armourers_workshop.common.command;

import java.util.List;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class CommandClearCache extends ModCommand {

    private static final String[] CACHE_SIDE = { "client", "server" };

    public CommandClearCache(ModCommand parent) {
        super(parent, "clear_cache");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> buildCommand() {
        return Commands.literal(this.getName())
                .then(Commands.literal("client").then(
                        Commands.argument("cache_size", IntegerArgumentType.integer())
                                .then(Commands.argument("player", EntityArgument.player()).executes((x)->this.executeClient(x)))

                ))
                .then(Commands.literal("server")).then(
                        Commands.argument("cache_size", IntegerArgumentType.integer())
                                .executes((x)->this.executeServer(x)));
    }


    protected int executeServer(CommandContext<CommandSource> source) throws CommandException, CommandSyntaxException {
        CommonSkinCache.INSTANCE.clearAll();
        return 0;
    }
    protected int executeClient(CommandContext<CommandSource> source) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = EntityArgument.getPlayer(source, "player");
        PacketHandler.networkWrapper.sendTo(new MessageServerClientCommand(CommandType.CLEAR_MODEL_CACHE), player);
        return 0;
    }



}
