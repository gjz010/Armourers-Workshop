package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.List;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.getPlayer;

public class CommandWardrobeResync extends ModCommand {

    public CommandWardrobeResync(ModCommand parent) {
        super(parent, "resync");
    }

    @Override
    public LiteralArgumentBuilder buildCommand(){
        return literal(this.getName()).then(argument("player", EntityArgument.players()).executes((x)->this.execute(x)));
    }
    // Arguments 1 - <player>
    @Override
    public int execute(CommandContext ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(ctx, "player");
        if (player == null) {
            return 0;
        }
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        IPlayerWardrobeCap wardrobeCap = (IPlayerWardrobeCap) PlayerWardrobeCap.get(player);
        if (skinCapability != null) {
            skinCapability.syncToPlayer(player);
            skinCapability.syncToAllTracking();
        }
        if (wardrobeCap != null) {
            wardrobeCap.syncToPlayer(player);
            wardrobeCap.syncToAllTracking();
        }
        return 0;
    }
}
