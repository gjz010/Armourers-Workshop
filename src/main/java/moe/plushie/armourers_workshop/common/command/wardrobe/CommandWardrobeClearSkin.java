package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.getPlayer;

public class CommandWardrobeClearSkin extends ModCommand {

    public CommandWardrobeClearSkin(ModCommand parent) {
        super(parent, "clear_skin");
    }

    @Override
    public LiteralArgumentBuilder buildCommand(){
        return literal(this.getName()).then(
                argument("player", EntityArgument.players()).then(
                        argument("skin_type", StringArgumentType.string()).then(
                                argument("slot_id", IntegerArgumentType.integer(1, 10)).executes((x)->this.execute(x))
                        )
                ));
    }

    // Arguments 3 - <player> <skin type> <slot id>
    @Override
    public int execute(CommandContext ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(ctx, "player");
        if (player == null) {
            return 0;
        }
        String argSkinType=getString(ctx, "skin_type");
        int argSlotId=getInteger(ctx, "slot_id");
        ModLogger.log(player.getName());


        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(argSkinType);
        if (skinType == null) {
            throw new CommandException(new StringTextComponent("No skin type!"));
        }

        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability != null) {
            skinCapability.clearSkin(skinType, argSlotId - 1);
            skinCapability.syncToPlayer(player);
            skinCapability.syncToAllTracking();
        }
        return 0;
    }
}
