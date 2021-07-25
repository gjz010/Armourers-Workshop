package moe.plushie.armourers_workshop.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;

public class CommandExecute extends ModCommand {

    private final ICommandExecute commandExecute;
    
    public CommandExecute(ModCommand parent, String name, ICommandExecute commandExecute) {
        super(parent, name);
        this.commandExecute = commandExecute;
    }
    
    @Override
    public int execute(CommandContext<CommandSource> ctx) throws CommandException, CommandSyntaxException {
        commandExecute.execute(ctx);
        return 0;
    }
    
    public static interface ICommandExecute {

        public void execute(CommandContext<CommandSource> ctx) throws CommandException, CommandSyntaxException ;
    }
}
