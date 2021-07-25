package moe.plushie.armourers_workshop.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkHooks;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandAdminPanel extends ModCommand {

    public CommandAdminPanel(ModCommand parent) {
        super(parent, "admin_panel");
    }

    @Override
    public LiteralArgumentBuilder buildCommand(){
        return literal(this.getName());
    }
    @Override
    public int execute(CommandContext<CommandSource> ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

        //NetworkHooks.openGui(player, ArmourersWorkshop.getInstance(), EnumGuiId.ADMIN_PANEL.ordinal());
        return 0;
    }
}
