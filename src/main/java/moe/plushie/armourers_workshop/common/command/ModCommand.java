package moe.plushie.armourers_workshop.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

public abstract class ModCommand{

    private final ModCommand parent;
    private final String name;

    public ModCommand(ModCommand parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public int getParentCount() {
        if (parent != null) {
            return parent.getParentCount() + 1;
        }
        return 0;
    }

    
    public String getFullName() {
        if (parent != null) {
            return parent.getFullName() + "." + name;
        }
        return name;
    }

    public String getName() {
        return name;
    }


    protected static int parseInt(String s, int min, int max) throws CommandException{
        try{
            int i = Integer.parseInt(s);
            if(i<min || i>max){
                throw new CommandException(new StringTextComponent("out of range number "+s));
            }
            return i;
        }catch(NumberFormatException ex){
            throw new CommandException(new StringTextComponent("bad number "+s));
        }

    }
    protected String[] getPlayers(MinecraftServer server) {
        return server.getPlayerNames();
    }

    public LiteralArgumentBuilder<CommandSource> buildCommand(){
        LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(this.getName());
        builder.requires((x)->x.hasPermission(this.getRequiredPermissionLevel()));
        builder.executes((x)->this.execute(x));
        return builder;
    }
    public int getRequiredPermissionLevel(){
        return 2;
    }
    protected int execute(CommandContext<CommandSource> source) throws CommandException, CommandSyntaxException {
        return 0;
    }
}
