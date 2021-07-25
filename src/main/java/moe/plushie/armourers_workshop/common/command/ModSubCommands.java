package moe.plushie.armourers_workshop.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.command.Commands.*;

public abstract class ModSubCommands extends ModCommand {
    
    protected final ArrayList<ModCommand> subCommands;

    public ModSubCommands(ModCommand parent, String name) {
        super(parent, name);
        subCommands = new ArrayList<ModCommand>();
    }
    
    protected void addSubCommand(ModCommand modCommand) {
        subCommands.add(modCommand);
    }


    @Override
    public LiteralArgumentBuilder buildCommand(){
        LiteralArgumentBuilder builder = literal(this.getName()).requires((x)->{
            return x.hasPermission(this.getRequiredPermissionLevel());
        });
        for(ModCommand command: subCommands){
            builder.then(command.buildCommand());
        }
        return builder;
    }
}
